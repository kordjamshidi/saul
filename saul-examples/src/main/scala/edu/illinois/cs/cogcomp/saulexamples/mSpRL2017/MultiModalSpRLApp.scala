/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.{File, FileOutputStream}

import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.DataProportion._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.{FeatureSets, ImageReaderHelper, SpRLXmlReader, ReportHelper}
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalConstrainedClassifiers.{LMPairConstraintClassifier, TRPairConstraintClassifier}
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalPopulateData._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import org.apache.commons.io.FileUtils
import mSpRLConfigurator._

object MultiModalSpRLApp extends App with Logging {

  MultiModalSpRLClassifiers.featureSet = FeatureSets.BaseLineWithImage
  MultiModalSpRLDataModel.useVectorAverages = false

  val classifiers = List(
    TrajectorRoleClassifier,
    LandmarkRoleClassifier,
    IndicatorRoleClassifier,
    TrajectorPairClassifier,
    LandmarkPairClassifier
  )

  FileUtils.forceMkdir(new File(resultsDir))

  val suffix = if (useVectorAverages) "_vecAvg" else ""

  val trainFileName = "newSprl2017_validation_train.xml"
  val testFileName = "newSprl2017_validation_test.xml"
  //runClassifiers(true, dataPath + trainFileName, Train)
  runClassifiers(true, dataPath + testFileName, Test)

  private def runClassifiers(isTrain: Boolean, textDataPath: String, imageDataProportion: DataProportion) = {

    lazy val xmlReader = new SpRLXmlReader(textDataPath)
    lazy val imageReader = new ImageReaderHelper(dataPath,trainFileName, testFileName, imageDataProportion)

    val populateImages = featureSet == FeatureSets.WordEmbeddingPlusImage || featureSet == FeatureSets.BaseLineWithImage
    populateRoleDataFromAnnotatedCorpus(xmlReader, imageReader, isTrain, populateImages)

    classifiers.foreach(x => {
      x.modelDir = s"models/mSpRL/$featureSet/"
      x.modelSuffix = suffix
    })

    if (isTrain) {
      println("training started ...")
      var pairsPopulated = false
      classifiers.foreach(classifier => {
        if (classifier == TrajectorPairClassifier || classifier == LandmarkPairClassifier) {
          if (!pairsPopulated) {
            populatePairDataFromAnnotatedCorpus(xmlReader, isTrain, x => SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier(x) == "Indicator")
            ReportHelper.saveCandidateList(true, pairs.getTrainingInstances.toList)
            pairsPopulated = true
          }
        }
        classifier.learn(50)
        classifier.save()
      })
    } else {
      println("testing started ...")
      val stream = new FileOutputStream(s"$resultsDir/$featureSet$suffix.txt")

      var pairsPopulated = false
      classifiers.foreach(classifier => {
        classifier.load()
        if (classifier == TrajectorPairClassifier || classifier == LandmarkPairClassifier) {
          if (!pairsPopulated) {
            populatePairDataFromAnnotatedCorpus(xmlReader, isTrain, x => SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier(x) == "Indicator")
            ReportHelper.saveCandidateList(false, pairs.getTestingInstances.toList)
            pairsPopulated = true
          }
          val predicted = pairs.getTestingInstances.filter(x => classifier(x) != "None").toList
          val results = PairClassifierUtils.evaluate(predicted, textDataPath, resultsDir, featureSet.toString, isTrain,
            classifier == TrajectorPairClassifier)
          ReportHelper.saveEvalResults(stream, s"${classifier.getClassSimpleNameForClassifier}-xml", results)
        }
        val results = classifier.test()
        ReportHelper.saveEvalResults(stream, s"${classifier.getClassSimpleNameForClassifier}", results)
      })

      val allCandidateResults = TripletClassifierUtils.test(textDataPath, resultsDir, "all-candidates", isTrain,
        _ => "TR-SP",
        _ => "Indicator",
        _ => "LM-SP")
      ReportHelper.saveEvalResults(stream, "triplet-all-candidates", allCandidateResults)

      val groundTruthResults = TripletClassifierUtils.test(textDataPath, resultsDir, "ground-truth", isTrain,
        r => isTrajectorRelation(r),
        t => indicatorRole(t),
        r => isLandmarkRelation(r))
      ReportHelper.saveEvalResults(stream, "triplet-ground-truth", groundTruthResults)

      val results = TripletClassifierUtils.test(textDataPath, resultsDir, featureSet.toString, isTrain,
        x => TrajectorPairClassifier(x),
        x => IndicatorRoleClassifier(x),
        x => LandmarkPairClassifier(x))
      ReportHelper.saveEvalResults(stream, "triplet", results)

      /*Pair level constraints
      * */
      val trResults = TRPairConstraintClassifier.test()
      ReportHelper.saveEvalResults(stream, "TRPair-Constrained", trResults)

      val lmResults = LMPairConstraintClassifier.test()
      ReportHelper.saveEvalResults(stream, s"LMPair-Constrained", lmResults)

      val constrainedResults = TripletClassifierUtils.test(textDataPath, resultsDir, featureSet.toString, isTrain,
        x => TRPairConstraintClassifier(x),
        x => IndicatorRoleClassifier(x),
        x => LMPairConstraintClassifier(x))
      ReportHelper.saveEvalResults(stream, s"triplet-constrained", constrainedResults)

      /*Sentence level constraints
     * */

      val trSentenceResults = SentenceLevelConstraintClassifiers.TRConstraintClassifier.test()
      ReportHelper.saveEvalResults(stream, "TR-SentenceConstrained", trSentenceResults)

      val lmSentenceResults = SentenceLevelConstraintClassifiers.LMConstraintClassifier.test()
      ReportHelper.saveEvalResults(stream, "LM-SentenceConstrained", lmSentenceResults)

      val spSentenceResults = SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier.test()
      ReportHelper.saveEvalResults(stream, "SP-SentenceConstrained", spSentenceResults)

      val trPairSentenceResults = SentenceLevelConstraintClassifiers.TRPairConstraintClassifier.test()
      ReportHelper.saveEvalResults(stream, "TRPair-SentenceConstrained", trPairSentenceResults)

      val lmPairSentenceResults = SentenceLevelConstraintClassifiers.LMPairConstraintClassifier.test()
      ReportHelper.saveEvalResults(stream, "LMPair-SentenceConstrained", lmPairSentenceResults)

      val constrainedPairSentenceResults = TripletClassifierUtils.test(textDataPath, resultsDir, featureSet.toString, isTrain,
        x => SentenceLevelConstraintClassifiers.TRPairConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.LMPairConstraintClassifier(x))
      ReportHelper.saveEvalResults(stream, "triplet-SentenceConstrained", constrainedPairSentenceResults)

      stream.close()
    }
  }

  //  val constrainedClassifiers =  List(
  //  SentenceLevelConstraintClassifiers.TRConstraintClassifier,
  //    SentenceLevelConstraintClassifiers.LMConstraintClassifier,
  //    SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier,
  //    SentenceLevelConstraintClassifiers.TRPairConstraintClassifier,
  //    SentenceLevelConstraintClassifiers.LMPairConstraintClassifier)

  /*train classifier jointly*/
  // JointTrainSparseNetwork(sentences, constrainedClassifieList, 30, init = true)
  /*test the same list of constrainedclassifiers as before*/

}

