/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.{File, FileOutputStream}

import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.{FeatureSets, ReportHelper}
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalPopulateData._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.mSpRLConfigurator._
import org.apache.commons.io.FileUtils

object MultiModalSpRLApp extends App with Logging {

  var useConstraints = true
  expName match {
    case "BM" =>
      MultiModalSpRLClassifiers.featureSet = FeatureSets.BaseLine
      useConstraints = false
    case "BM+C" =>
      MultiModalSpRLClassifiers.featureSet = FeatureSets.BaseLine
    case "BM+C+E" =>
      MultiModalSpRLClassifiers.featureSet = FeatureSets.WordEmbedding
    case "BM+C+E+I" =>
      MultiModalSpRLClassifiers.featureSet = FeatureSets.WordEmbeddingPlusImage
  }
  MultiModalSpRLDataModel.useVectorAverages = false

  val classifiers = List(
    TrajectorRoleClassifier,
    LandmarkRoleClassifier,
    IndicatorRoleClassifier,
    TrajectorPairClassifier,
    LandmarkPairClassifier
  )
  classifiers.foreach(x => {
    x.modelDir = s"models/mSpRL/$featureSet/"
    x.modelSuffix = suffix
  })
  FileUtils.forceMkdir(new File(resultsDir))

  populateRoleDataFromAnnotatedCorpus()

  if (isTrain) {
    println("training started ...")
    TrajectorRoleClassifier.learn(iterations)
    TrajectorRoleClassifier.save()

    IndicatorRoleClassifier.learn(iterations)
    IndicatorRoleClassifier.save()

    LandmarkRoleClassifier.learn(iterations)
    LandmarkRoleClassifier.save()

    populatePairDataFromAnnotatedCorpus(x => IndicatorRoleClassifier(x) == "Indicator")
    ReportHelper.saveCandidateList(true, pairs.getTestingInstances.toList)

    TrajectorPairClassifier.learn(iterations)
    TrajectorPairClassifier.save()

    LandmarkPairClassifier.learn(iterations)
    LandmarkRoleClassifier.save()
  }

  else {

    println("testing started ...")
    val stream = new FileOutputStream(s"$resultsDir/$expName$suffix.txt")

    TrajectorRoleClassifier.load()
    LandmarkRoleClassifier.load()
    IndicatorRoleClassifier.load()
    TrajectorPairClassifier.load()
    LandmarkPairClassifier.load()
    populatePairDataFromAnnotatedCorpus(x => IndicatorRoleClassifier(x) == "Indicator")
    ReportHelper.saveCandidateList(false, pairs.getTestingInstances.toList)

    if (!useConstraints) {
      val trResults = TrajectorRoleClassifier.test()
      ReportHelper.saveEvalResults(stream, s"TR", trResults)

      val lmResults = LandmarkRoleClassifier.test()
      ReportHelper.saveEvalResults(stream, s"LM", lmResults)

      val spResults = IndicatorRoleClassifier.test()
      ReportHelper.saveEvalResults(stream, s"SP", spResults)

      val trPairResults = TrajectorPairClassifier.test()
      ReportHelper.saveEvalResults(stream, s"TRPair", trPairResults)

      val lmPairResults = LandmarkPairClassifier.test()
      ReportHelper.saveEvalResults(stream, s"LMPair", lmPairResults)

      val results = TripletClassifierUtils.test(testFile, resultsDir, featureSet.toString, isTrain,
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
      ReportHelper.saveAsXml(triplets, trajectors, indicators, landmarks, s"$resultsDir/${expName}${suffix}.xml")

    }

    if(useConstraints) {

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

      val constrainedPairSentenceResults = TripletClassifierUtils.test(testFile, resultsDir, featureSet.toString, isTrain,
        x => SentenceLevelConstraintClassifiers.TRPairConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.LMPairConstraintClassifier(x))
      ReportHelper.saveEvalResults(stream, "triplet-SentenceConstrained", constrainedPairSentenceResults)

      val triplets = TripletClassifierUtils.predict(
        x => SentenceLevelConstraintClassifiers.TRPairConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.LMPairConstraintClassifier(x))
      val trajectors = phrases.getTestingInstances.filter(x => SentenceLevelConstraintClassifiers.TRConstraintClassifier(x) == "Trajector").toList
      val landmarks = phrases.getTestingInstances.filter(x => SentenceLevelConstraintClassifiers.LMConstraintClassifier == "Landmark").toList
      val indicators = phrases.getTestingInstances.filter(x => SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier(x) == "Indicator").toList
      ReportHelper.saveAsXml(triplets, trajectors, indicators, landmarks, s"$resultsDir/${expName}${suffix}.xml")
    }

    stream.close()
  }

}

