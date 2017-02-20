/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.{File, FileOutputStream, PrintStream, PrintWriter}

import edu.illinois.cs.cogcomp.saul.classifier.Results
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.DataProportion._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalConstrainedClassifiers.{LMPairConstraintClassifier, TRPairConstraintClassifier}
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalPopulateData._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval.{RelationEval, RelationsEvalDocument, SpRLEvaluation, SpRLEvaluator}
import org.apache.commons.io.FileUtils

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

object MultiModalSpRLApp extends App with Logging {

  MultiModalSpRLClassifiers.featureSet = FeatureSets.BaseLine

  val classifiers = List(
    TrajectorRoleClassifier,
    LandmarkRoleClassifier,
    IndicatorRoleClassifier,
    TrajectorPairClassifier,
    LandmarkPairClassifier
  )

  runClassifiers(true, ValidationTrain)
  runClassifiers(false, ValidationTest)

  private def runClassifiers(isTrain: Boolean, proportion: DataProportion) = {

    val resultsDir = s"data/mSpRL/results/"
    FileUtils.forceMkdir(new File(resultsDir))

    val suffix = if (useVectorAverages) "_vecAvg" else ""

    populateData(isTrain, proportion)
    classifiers.foreach(x => {
      x.modelDir = s"models/mSpRL/$featureSet/"
      x.modelSuffix = suffix
    })

    if (isTrain) {
      println("training started ...")
      classifiers.foreach(classifier => {
        classifier.learn(50)
        classifier.save()
      })
    } else {
      println("testing started ...")
      val stream = new FileOutputStream(s"$resultsDir/$featureSet$suffix.txt")
      classifiers.foreach(classifier => {
        classifier.load()
        val results = classifier.test()
        saveResults(stream, s"${classifier.getClassSimpleNameForClassifier}", convertToEval(results))
      })
      val results = testTriplet(resultsDir, isTrain, proportion,
        x => TrajectorPairClassifier(x),
        x => LandmarkPairClassifier(x),
        x => IndicatorRoleClassifier(x)
      )
      saveResults(stream, "triplet", results)

      /*Pair level constraints
      * */
      val trResults = TRPairConstraintClassifier.test()
      saveResults(stream, "TRPair-Constrained", convertToEval(trResults))

      val lmResults = LMPairConstraintClassifier.test()
      saveResults(stream, s"LMPair-Constrained", convertToEval(lmResults))

      val constrainedResults = testTriplet(resultsDir, isTrain, proportion,
        x => TRPairConstraintClassifier(x),
        x => LMPairConstraintClassifier(x),
        x => IndicatorRoleClassifier(x)
      )
      saveResults(stream, s"triplet-constrained", constrainedResults)

      /*Sentence level constraints
     * */

      val trSentenceResults = SentenceLevelConstraintClassifiers.TRConstraintClassifier.test()
      saveResults(stream, "TR-SentenceConstrained", convertToEval(trSentenceResults))

      val lmSentenceResults = SentenceLevelConstraintClassifiers.LMConstraintClassifier.test()
      saveResults(stream, "LM-SentenceConstrained", convertToEval(lmSentenceResults))

      val spSentenceResults = SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier.test()
      saveResults(stream, "SP-SentenceConstrained", convertToEval(spSentenceResults))

      val trPairSentenceResults = SentenceLevelConstraintClassifiers.TRPairConstraintClassifier.test()
      saveResults(stream, "TRPair-SentenceConstrained", convertToEval(trPairSentenceResults))

      val lmPairSentenceResults = SentenceLevelConstraintClassifiers.LMPairConstraintClassifier.test()
      saveResults(stream, "LMPair-SentenceConstrained", convertToEval(lmPairSentenceResults))

      val constrainedPairSentenceResults = testTriplet(resultsDir, isTrain, proportion,
        x => SentenceLevelConstraintClassifiers.TRPairConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.LMPairConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier(x)
      )
      saveResults(stream, "triplet-SentenceConstrained", constrainedPairSentenceResults)

      stream.close()
    }
  }

  //  val constrainedClassifiers =  List(
  //    SentenceLevelConstraintClassifiers.TRConstraintClassifier,
  //    SentenceLevelConstraintClassifiers.LMConstraintClassifier,
  //    SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier,
  //    SentenceLevelConstraintClassifiers.TRPairConstraintClassifier,
  //    SentenceLevelConstraintClassifiers.LMPairConstraintClassifier)

  /*train classifier jointly*/
  // JointTrainSparseNetwork(sentences, constrainedClassifieList, 30, init = true)
  /*test the same list of constrainedclassifiers as before*/


  private def testTriplet(resultsDir: String,
                          isTrain: Boolean,
                          proportion: DataProportion,
                          trClassifier: Relation => String,
                          lmClassifier: Relation => String,
                          spClassifier: Token => String
                         ): Seq[SpRLEvaluation] = {

    val tokenInstances = if (isTrain) tokens.getTrainingInstances else tokens.getTestingInstances
    val indicators = tokenInstances.filter(t => spClassifier(t) == "Indicator").toList
      .sortBy(x => x.getSentence.getStart + x.getStart)

    val predicted = indicators.flatMap(sp => {
      val pairs = tokens(sp) <~ relationToSecondArgument
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
    val actual = getActualRelationEvalsTokenBased(proportion)
    reportRelationResults(resultsDir, actual, predicted)

    val evaluator = new SpRLEvaluator()
    val actualEval = new RelationsEvalDocument(actual.map(_._2))
    val predictedEval = new RelationsEvalDocument(predicted.map(_._2))
    val results = evaluator.evaluateRelations(actualEval, predictedEval)
    evaluator.printEvaluation(results)
    results
  }

  private def reportRelationResults(resultsDir: String,
                                    actual: List[(Relation, RelationEval)],
                                    predicted: List[(Relation, RelationEval)]
                                   ) = {
    val tp = ListBuffer[(Relation, Relation)]()
    actual.zipWithIndex.foreach { case (a, i) =>
      breakable {
        predicted.zipWithIndex.foreach { case (p, j) =>
          if (a._2.isEqual(p._2)) {
            tp += ((a._1, p._1))
            break()
          }
        }
      }
    }
    val fp = predicted.filterNot(x => tp.exists(_._2 == x._1))
    val fn = actual.filterNot(x => tp.exists(_._1 == x._1))
    actual.foreach(x => {
      x._1.getArguments.foreach(a => a.setText(a.getPropertyFirstValue("head"))) // convert phrase text to headword
    })

    var writer = new PrintWriter(s"$resultsDir/${featureSet}_triplet-fp.txt")
    fp.groupBy(_._1.getArgument(1).asInstanceOf[Token].getDocument.getId).toList.sortBy(_._1).foreach {
      case (key, list) => {
        writer.println(s"===================================== ${key} ==================================")
        list.foreach { case (r, _) =>
          writer.println(s"${r.getArgument(0).getText} -> ${r.getArgument(1).getText} -> ${r.getArgument(2).getText}")
        }
      }
    }
    writer.close()

    writer = new PrintWriter(s"$resultsDir/${featureSet}_triplet-fn.txt")
    fn.groupBy(_._1.getArgument(1).asInstanceOf[Phrase].getDocument.getId).toList.sortBy(_._1).foreach {
      case (key, list) => {
        writer.println(s"===================================== ${key} ==================================")
        list.foreach { case (r, _) =>
          val args = r.getArguments.map(_.asInstanceOf[Phrase]).toList
          writer.println(s"${r.getId} : ${args(0).getText} -> ${args(1).getText} -> ${args(2).getText}")
        }
      }
    }
    writer.close()

    writer = new PrintWriter(s"$resultsDir/${featureSet}_triplet-tp.txt")
    tp.groupBy(_._1.getArgument(1).asInstanceOf[Phrase].getDocument.getId).toList.sortBy(_._1).foreach {
      case (key, list) => {
        writer.println(s"===================================== ${key} ==================================")
        list.foreach { case (a, p) =>
          val actualArgs = a.getArguments.map(_.asInstanceOf[Phrase]).toList
          val predictedArgs = p.getArguments.map(_.asInstanceOf[Phrase]).toList
          writer.println(s"${a.getId} : ${actualArgs(0).getText} -> ${actualArgs(1).getText} -> " +
            s"${actualArgs(2).getText}   ${actualArgs(0).getText} -> ${actualArgs(1).getText} -> " +
            s"${actualArgs(2).getText}")
        }
      }
    }
    writer.close()
  }

  private def convertToEval(r: Results) = r.perLabel
    .map(x => new SpRLEvaluation(x.label, x.precision * 100, x.recall * 100, x.f1 * 100, x.labeledSize, x.predictedSize))

  private def saveResults(stream: FileOutputStream, caption: String, results: Seq[SpRLEvaluation]) = {
    val writer = new PrintStream(stream, true)
    writer.println("===========================================================================")
    writer.println(s" ${caption}")
    writer.println("---------------------------------------------------------------------------")
    SpRLEvaluator.printEvaluation(stream, results)
    writer.println()
  }

  private def getActualRelationEvalsPhraseBased(proportion: DataProportion): List[RelationEval] = {

    def get(proportion: DataProportion) = {
      val reader = getXmlReader(proportion)
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
        new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd)
      }).toList
    }

    proportion match {
      case All => get(Train) ++ get(Test)
      case x => get(x)
    }
  }

  private def getActualRelationEvalsTokenBased(proportion: DataProportion): List[(Relation, RelationEval)] = {

    def get(proportion: DataProportion) = {
      val reader = getXmlReader(proportion)
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
        (r, new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd))
      }).toList
    }

    proportion match {
      case All => get(Train) ++ get(Test)
      case x => get(x)
    }
  }

  private def getRelationEval(tr: Option[Token], sp: Option[Token], lm: Option[Token]): (Relation, RelationEval) = {
    val offset = sp.get.getSentence.getStart
    val lmStart = if (notNull(lm)) offset + lm.get.getStart else -1
    val lmEnd = if (notNull(lm)) offset + lm.get.getEnd else -1
    val trStart = if (notNull(tr)) offset + tr.get.getStart else -1
    val trEnd = if (notNull(tr)) offset + tr.get.getEnd else -1
    val spStart = offset + sp.get.getStart
    val spEnd = offset + sp.get.getEnd
    val r = new Relation()
    r.setArgument(0, if (tr.nonEmpty) tr.get else dummyToken)
    r.setArgument(1, sp.get)
    r.setArgument(2, if (lm.nonEmpty) lm.get else dummyToken)
    val eval = new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd)
    (r, eval)
  }

  private def notNull(t: Option[Token]) = {
    t.nonEmpty && t.get.getId != dummyToken.getId && t.get.getStart >= 0
  }

  private def getHeadSpan(p: Phrase): (Int, Int) = {
    if (p.getStart == -1)
      return (-1, -1)

    val offset = p.getSentence.getStart + p.getStart
    val (_, trHeadStart, trHeadEnd) = getHeadword(p.getText)

    (offset + trHeadStart, offset + trHeadEnd)
  }

  private def getSpan(p: Phrase): (Int, Int) = {
    if (p.getStart == -1)
      return (-1, -1)

    val offset = p.getSentence.getStart

    (offset + p.getStart, offset + p.getEnd)
  }

}

