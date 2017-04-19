/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.{File, FileOutputStream}

import edu.illinois.cs.cogcomp.core.utilities.XmlModel
import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.DataProportion._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.{FeatureSets, ImageReaderHelper, ReportHelper, SpRLXmlReader}
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalConstrainedClassifiers.{LMPairConstraintClassifier, TRPairConstraintClassifier}
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalPopulateData._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval.{OverlapComparer, SpRLEvaluator, XmlSpRLEvaluator}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2017.SpRL2017Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLDataReader
import org.apache.commons.io.FileUtils

import scala.collection.JavaConversions._
import mSpRLConfigurator._

import scala.util.Random

object MultiModalSpRLApp extends App with Logging {

  val settings = {
    var model = FeatureSets.BaseLine.toString
    var fold = ""
    var train = s"$dataPath$fold/newSpRL2017_validation_train.xml"
    var test = s"$dataPath$fold/newSpRL2017_validation_test.xml"
    args.sliding(2, 2).toList.collect {
      case Array("-t", x) => train = x
      case Array("-g", x) => test = x
      case Array("-f", x) => fold = x.toLowerCase()
      case Array("-m", x) => model = x
    }
    Map("train" -> train, "test" -> test, "model" -> model, "fold" -> fold)
  }

  MultiModalSpRLClassifiers.featureSet = FeatureSets.withName(settings("model"))
  MultiModalSpRLDataModel.useVectorAverages = false

  val classifiers = List(
    TrajectorRoleClassifier,
    LandmarkRoleClassifier,
    IndicatorRoleClassifier,
    TrajectorPairClassifier,
    LandmarkPairClassifier
  )

  FileUtils.forceMkdir(new File(resultsDir))

  //create10Folds()

  val fold = settings("fold")
  val suffix = if (useVectorAverages) "_vecAvg_" + fold else if (fold == "") "" else s"_$fold"

  val trainFile = settings("train")
  val testFile = settings("test")
  runClassifiers(true, trainFile, Train)
  runClassifiers(false, testFile, Test)


  private def runClassifiers(isTrain: Boolean, textDataPath: String, imageDataProportion: DataProportion) = {

    lazy val xmlReader = new SpRLXmlReader(textDataPath)
    lazy val imageReader = new ImageReaderHelper(dataPath, trainFile, testFile, imageDataProportion)

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
            populatePairDataFromAnnotatedCorpus(xmlReader, isTrain, x => IndicatorRoleClassifier(x) == "Indicator")
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
            populatePairDataFromAnnotatedCorpus(xmlReader, isTrain, x => IndicatorRoleClassifier(x) == "Indicator")
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

      val triplets = TripletClassifierUtils.predict(
        x => TrajectorPairClassifier(x),
        x => IndicatorRoleClassifier(x),
        x => LandmarkPairClassifier(x))
      val trajectors = phrases.getTestingInstances.filter(x => TrajectorRoleClassifier(x) == "Trajector").toList
      val landmarks = phrases.getTestingInstances.filter(x => LandmarkRoleClassifier(x) == "Landmark").toList
      val indicators = phrases.getTestingInstances.filter(x => IndicatorRoleClassifier(x) == "Indicator").toList
      ReportHelper.saveAsXml(triplets, trajectors, indicators, landmarks, s"$resultsDir/${featureSet}${suffix}.xml")

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

  private def create10Folds(): Unit = {
    val reader = new SpRLDataReader(dataPath, classOf[SpRL2017Document])
    reader.readData()
    val doc = reader.documents.find(_.getFilename == "newSprl2017_all.xml").get
    val foldSize = Math.ceil(doc.getScenes.length / 10.0).toInt
    val folds = doc.getScenes.sortBy(x => Random.nextGaussian()).zipWithIndex.groupBy(x => x._2 / foldSize)
    folds.foreach(f => {
      val test = new SpRL2017Document
      test.setScenes(f._2.map(_._1))
      val train = new SpRL2017Document
      train.setScenes(folds.filter(_._1 != f._1).flatMap(_._2.map(_._1)).toList)
      FileUtils.forceMkdir(new File(dataPath + s"fold${f._1 + 1}"))
      XmlModel.write(test, dataPath + s"fold${f._1 + 1}/test.xml")
      XmlModel.write(train, dataPath + s"fold${f._1 + 1}/train.xml")
    })
  }
}

