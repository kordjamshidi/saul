/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.{File, FileOutputStream, PrintStream, PrintWriter}

import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.data.CLEFImageReader
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalConstrainedClassifiers.{LMPairConstraintClassifier, TRPairConstraintClassifier}
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval.{RelationEval, RelationsEvalDocument, SpRLEvaluation, SpRLEvaluator}
import MultiModalSpRLDataModel._
import DataProportion._
import MultiModalPopulateData._
import edu.illinois.cs.cogcomp.saul.classifier.Results
import org.apache.commons.io.FileUtils

import scala.collection.JavaConversions._

object imageApp extends App {

  val readFullData = false
  val CLEFDataset = new CLEFImageReader("data/mSprl/saiapr_tc-12", readFullData)

  val imageListTrain = CLEFDataset.trainingImages
  val segmentListTrain = CLEFDataset.trainingSegments
  val relationListTrain = CLEFDataset.trainingRelations

  images.populate(imageListTrain)
  segments.populate(segmentListTrain)
  segmentRelations.populate(relationListTrain)

  val imageListTest = CLEFDataset.testImages
  val segementListTest = CLEFDataset.testSegments
  val relationListTest = CLEFDataset.testRelations

  images.populate(imageListTest, false)
  segments.populate(segementListTest, false)
  segmentRelations.populate(relationListTest, false)

  ImageSVMClassifier.learn(5)
  ImageSVMClassifier.test(segementListTest)

  ImageClassifierWeka.learn(5)
  ImageClassifierWeka.test(segementListTest)
}

object combinedPairApp extends App with Logging {

  MultiModalSpRLClassifiers.featureSet = FeatureSets.WordEmbeddingPlusImage

  val classifiers = List(
    TrajectorRoleClassifier,
    LandmarkRoleClassifier,
    IndicatorRoleClassifier,
    TrajectorPairClassifier,
    LandmarkPairClassifier
  )

  //runClassifiers(true, Train)
  runClassifiers(false, Test)

  private def runClassifiers(isTrain: Boolean, proportion: DataProportion) = {

    val resultsDir = s"data/mSpRL/results/"
    FileUtils.forceMkdir(new File(resultsDir))

    populateData(isTrain, proportion)
    classifiers.foreach(_.modelDir = s"models/mSpRL/$featureSet/")

    if (isTrain) {
      println("training started ...")
      classifiers.foreach(classifier => {
        classifier.learn(50)
        classifier.save()
      })
    } else {
      println("testing started ...")
      val stream = new FileOutputStream(s"$resultsDir/$featureSet.txt")
      classifiers.foreach(classifier => {
        classifier.load()
        val results = classifier.test()
        saveResults(stream, s"${classifier.getClassSimpleNameForClassifier}", convertToEval(results))
      })
      val results = testTriplet(isTrain, proportion,
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

      val constrainedResults = testTriplet(isTrain, proportion,
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

      val constrainedPairSentenceResults = testTriplet(isTrain, proportion,
        x => SentenceLevelConstraintClassifiers.TRPairConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.LMPairConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier(x)
      )
      saveResults(stream, "triplet-SentenceConstrained", constrainedPairSentenceResults)

      stream.close()
    }
  }

  private def testTriplet(isTrain: Boolean, proportion: DataProportion,
                          trClassifier: Relation => String,
                          lmClassifier: Relation => String,
                          spClassifier: Token => String
                         ): Seq[SpRLEvaluation] = {

    val tokenInstances = if (isTrain) tokens.getTrainingInstances else tokens.getTestingInstances
    val indicators = tokenInstances.filter(t => spClassifier(t) == "Indicator").toList
      .sortBy(x => x.getSentence.getStart + x.getStart)

    val relations = indicators.flatMap(sp => {
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
    val predictedRelations = new RelationsEvalDocument(relations)
    val actualRelations = new RelationsEvalDocument(getActualRelationEvalsTokenBased(isTrain))
    val evaluator = new SpRLEvaluator()
    val results = evaluator.evaluateRelations(actualRelations, predictedRelations)
    evaluator.printEvaluation(results)
    results
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

  private def getActualRelationEvalsPhraseBased(isTrain: Boolean): List[RelationEval] = {
    val reader = getXmlReader(isTrain)
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

  private def getActualRelationEvalsTokenBased(isTrain: Boolean): List[RelationEval] = {
    val reader = getXmlReader(isTrain)
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
      new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd)
    }).toList
  }

  private def getRelationEval(tr: Option[Token], sp: Option[Token], lm: Option[Token]): RelationEval = {
    val offset = sp.get.getSentence.getStart
    val lmStart = if (notNull(lm)) offset + lm.get.getStart else -1
    val lmEnd = if (notNull(lm)) offset + lm.get.getEnd else -1
    val trStart = if (notNull(tr)) offset + tr.get.getStart else -1
    val trEnd = if (notNull(tr)) offset + tr.get.getEnd else -1
    val spStart = offset + sp.get.getStart
    val spEnd = offset + sp.get.getEnd
    new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd)
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

