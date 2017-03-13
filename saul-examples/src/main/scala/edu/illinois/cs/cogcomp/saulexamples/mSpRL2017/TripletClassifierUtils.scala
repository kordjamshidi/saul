package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.{ FileOutputStream, PrintStream, PrintWriter }

import edu.illinois.cs.cogcomp.saul.classifier.Results
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.DataProportion._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.{ ReportHelper, XmlReaderHelper }
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ NlpBaseElement, Phrase, Relation, Token }
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors.getHeadword
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval._

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.{ break, breakable }

/** Created by taher on 2017-02-26.
  */
object TripletClassifierUtils {

  import MultiModalSpRLDataModel._

  def test(
    dataDir: String,
    resultsDir: String,
    resultsFilePrefix: String,
    isTrain: Boolean,
    proportion: DataProportion,
    trClassifier: (Relation) => String,
    spClassifier: (Phrase) => String,
    lmClassifier: (Relation) => String
  ): Seq[SpRLEvaluation] = {

    val predicted: List[(Relation, RelationEval)] = predictWithEval(trClassifier, spClassifier, lmClassifier, isTrain)
    val actual = getActualRelationEvalsPhraseBased(dataDir, proportion)

    val comparer = new EvalComparer {
      override def isEqual(a: SpRLEval, b: SpRLEval) = a.asInstanceOf[RelationEval].overlaps(b.asInstanceOf[RelationEval])
    }

    ReportHelper.reportRelationResults(resultsDir, resultsFilePrefix, actual, predicted, comparer)

    val evaluator = new SpRLEvaluator()
    val actualEval = new RelationsEvalDocument(actual.map(_._2))
    val predictedEval = new RelationsEvalDocument(predicted.map(_._2))
    val results = evaluator.evaluateRelations(actualEval, predictedEval, comparer)
    evaluator.printEvaluation(results)
    results
  }

  def predict(
    trClassifier: Relation => String,
    lmClassifier: Relation => String,
    spClassifier: (Phrase) => String,
    isTrain: Boolean = false
  ): List[Relation] = predictWithEval(trClassifier, spClassifier, lmClassifier, isTrain).map(_._1)

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////
  private def predictWithEval(
    trClassifier: (Relation) => String,
    spClassifier: (Phrase) => String,
    lmClassifier: (Relation) => String,
    isTrain: Boolean
  ): List[(Relation, RelationEval)] = {
    val instances = if (isTrain) phrases.getTrainingInstances else phrases.getTestingInstances
    val indicators = instances.filter(t => t.getId != dummyPhrase.getId && spClassifier(t) == "Indicator").toList
      .sortBy(x => x.getSentence.getStart + x.getStart)

    indicators.flatMap(sp => {
      val pairs = phrases(sp) <~ relationToSecondArgument
      val trajectorPairs = (pairs.filter(r => trClassifier(r) == "TR-SP") ~> relationToFirstArgument).groupBy(x => x).keys
      if (trajectorPairs.nonEmpty) {
        val landmarkPairs = (pairs.filter(r => lmClassifier(r) == "LM-SP") ~> relationToFirstArgument).groupBy(x => x).keys
        if (landmarkPairs.nonEmpty) {
          trajectorPairs.flatMap(tr => landmarkPairs.map(lm => getRelationEval(Some(tr), Some(sp), Some(lm)))).toList
        } else {
          List() //trajectorPairs.map(tr => getRelationEval(Some(tr), Some(sp), None)).toList
        }
      } else {
        List()
      }
    })
  }

  private def getActualRelationEvalsPhraseBased(dataDir: String, proportion: DataProportion): List[(Relation, RelationEval)] = {

    val reader = new XmlReaderHelper(dataDir, proportion).reader
    val relations = reader.getRelations("RELATION", "trajector_id", "spatial_indicator_id", "landmark_id")

    reader.setPhraseTagName("TRAJECTOR")
    val trajectors = reader.getPhrases().map(x => x.getId -> x).toMap

    reader.setPhraseTagName("LANDMARK")
    val landmarks = reader.getPhrases().map(x => x.getId -> x).toMap

    reader.setPhraseTagName("SPATIALINDICATOR")
    val indicators = reader.getPhrases().map(x => x.getId -> x).toMap

    relations.map(r => {
      val tr = trajectors(r.getArgumentId(0))
      val sp = indicators(r.getArgumentId(1))
      val lm = landmarks(r.getArgumentId(2))
      val (trStart: Int, trEnd: Int) = getSpan(tr)
      val (spStart: Int, spEnd: Int) = getSpan(sp)
      val (lmStart: Int, lmEnd: Int) = getSpan(lm)
      r.setArgument(0, tr)
      r.setArgument(1, sp)
      r.setArgument(2, lm)
      (r, new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd))
    }).toList
  }

  private def getActualRelationEvalsTokenBased(dataDir: String, proportion: DataProportion): List[(Relation, RelationEval)] = {

    val reader = new XmlReaderHelper(dataDir, proportion).reader
    val relations = reader.getRelations("RELATION", "trajector_id", "spatial_indicator_id", "landmark_id")

    reader.setPhraseTagName("TRAJECTOR")
    val trajectors = reader.getPhrases().map(x => x.getId -> x).toMap

    reader.setPhraseTagName("LANDMARK")
    val landmarks = reader.getPhrases().map(x => x.getId -> x).toMap

    reader.setPhraseTagName("SPATIALINDICATOR")
    val indicators = reader.getPhrases().map(x => x.getId -> x).toMap

    relations.map(r => {
      val tr = trajectors(r.getArgumentId(0))
      val sp = indicators(r.getArgumentId(1))
      val lm = landmarks(r.getArgumentId(2))
      val (trStart: Int, trEnd: Int) = getHeadSpan(tr)
      val (spStart: Int, spEnd: Int) = getHeadSpan(sp)
      val (lmStart: Int, lmEnd: Int) = getHeadSpan(lm)
      r.setArgument(0, tr)
      r.setArgument(1, sp)
      r.setArgument(2, lm)
      (r, new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd))
    }).toList
  }

  private def getRelationEval(tr: Option[NlpBaseElement], sp: Option[NlpBaseElement], lm: Option[NlpBaseElement]): (Relation, RelationEval) = {
    val offset = sp.get match {
      case x: Token => x.getSentence.getStart
      case x: Phrase => x.getSentence.getStart
    }
    val lmStart = if (notNull(lm)) offset + lm.get.getStart else -1
    val lmEnd = if (notNull(lm)) offset + lm.get.getEnd else -1
    val trStart = if (notNull(tr)) offset + tr.get.getStart else -1
    val trEnd = if (notNull(tr)) offset + tr.get.getEnd else -1
    val spStart = offset + sp.get.getStart
    val spEnd = offset + sp.get.getEnd
    val r = new Relation()
    r.setArgument(0, if (tr.nonEmpty) tr.get else dummyPhrase)
    r.setArgumentId(0, r.getArgument(0).getId)
    r.setArgument(1, sp.get)
    r.setArgumentId(1, r.getArgument(1).getId)
    r.setArgument(2, if (lm.nonEmpty) lm.get else dummyPhrase)
    r.setArgumentId(2, r.getArgument(2).getId)
    val eval = new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd)
    (r, eval)
  }

  private def notNull(t: Option[NlpBaseElement]) = {
    t.nonEmpty && t.get.getId != dummyPhrase.getId && t.get.getStart >= 0
  }

  private def getHeadSpan(p: Phrase): (Int, Int) = {
    if (p.getStart == -1)
      return (-1, -1)

    val offset = p.getSentence.getStart + p.getStart
    val (_, headStart, headEnd) = getHeadword(p.getText)
    (offset + headStart, offset + headEnd)
  }

  private def getSpan(p: Phrase): (Int, Int) = {
    if (p.getStart == -1)
      return (-1, -1)

    val offset = p.getSentence.getStart

    (offset + p.getStart, offset + p.getEnd)
  }
}

