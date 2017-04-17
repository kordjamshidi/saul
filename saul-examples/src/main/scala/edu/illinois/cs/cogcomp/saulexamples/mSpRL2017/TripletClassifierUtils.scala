package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.DataProportion._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.{ ReportHelper, SpRLXmlReader }
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ NlpBaseElement, Phrase, Relation }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval._

import scala.collection.JavaConversions._

/** Created by taher on 2017-02-26.
  */
object TripletClassifierUtils {

  import MultiModalSpRLDataModel._

  def test(
    dataPath: String,
    resultsDir: String,
    resultsFilePrefix: String,
    isTrain: Boolean,
    trClassifier: (Relation) => String,
    spClassifier: (Phrase) => String,
    lmClassifier: (Relation) => String
  ): Seq[SpRLEvaluation] = {

    val predicted: List[Relation] = predict(trClassifier, spClassifier, lmClassifier, isTrain)
    val actual = getActualRelationEvalsPhraseBased(dataPath)

    ReportHelper.reportRelationResults(resultsDir, resultsFilePrefix + "_triplet", actual, predicted, new OverlapComparer, 3)
  }

  def predict(
    trClassifier: (Relation) => String,
    spClassifier: (Phrase) => String,
    lmClassifier: (Relation) => String,
    isTrain: Boolean = false
  ): List[Relation] = {
    val instances = if (isTrain) phrases.getTrainingInstances else phrases.getTestingInstances
    val indicators = instances.filter(t => t.getId != dummyPhrase.getId && spClassifier(t) == "Indicator").toList
      .sortBy(x => x.getSentence.getStart + x.getStart)

    indicators.flatMap(sp => {
      val pairs = phrases(sp) <~ relationToSecondArgument
      val trajectorPairs = (pairs.filter(r => trClassifier(r) == "TR-SP") ~> relationToFirstArgument).groupBy(x => x).keys
      if (trajectorPairs.nonEmpty) {
        val landmarkPairs = (pairs.filter(r => lmClassifier(r) == "LM-SP") ~> relationToFirstArgument).groupBy(x => x).keys
        if (landmarkPairs.nonEmpty) {
          trajectorPairs.flatMap(tr => landmarkPairs.map(lm => createRelation(Some(tr), Some(sp), Some(lm))))
            .filter(r => r.getArgumentIds.toList.distinct.size == 3) // distinct arguments
            .toList
        } else {
          List()
        }
      } else {
        List()
      }
    })
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////
  private def getActualRelationEvalsPhraseBased(dataPath: String): List[Relation] = {

    val reader = new SpRLXmlReader(dataPath).reader
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
      r.setArgument(0, tr)
      r.setArgument(1, sp)
      r.setArgument(2, lm)
      r
    }).toList
  }

  private def createRelation(tr: Option[Phrase], sp: Option[Phrase], lm: Option[Phrase]): Relation = {

    val r = new Relation()
    r.setArgument(0, if (tr.nonEmpty) tr.get else dummyPhrase)
    r.setArgumentId(0, r.getArgument(0).getId)
    r.setArgument(1, sp.get)
    r.setArgumentId(1, r.getArgument(1).getId)
    r.setArgument(2, if (lm.nonEmpty) lm.get else dummyPhrase)
    r.setArgumentId(2, r.getArgument(2).getId)
    r.setParent(sp.get.getSentence)
    r
  }

}

