/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.{File, FileOutputStream}

import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.DataProportion._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalPopulateData._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import org.apache.commons.io.FileUtils

import scala.collection.JavaConversions._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalConstrainedClassifiers.{LMPairConstraintClassifier, TRPairConstraintClassifier}

object MultiModalSpRLApp extends App with Logging {

  MultiModalSpRLClassifiers.featureSet = FeatureSets.BaseLine
  MultiModalSpRLDataModel.useVectorAverages = false

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

    populateDataFromAnnotatedCorpus(isTrain, proportion, featureSet == FeatureSets.WordEmbeddingPlusImage)
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
        TripletClassifierUtils.saveResults(stream, s"${classifier.getClassSimpleNameForClassifier}",
          TripletClassifierUtils.convertToEval(results))
      })
      val results = TripletClassifierUtils.test(resultsDir, isTrain,
        proportion, x => TrajectorPairClassifier(x),
        x => IndicatorRoleClassifier(x),
        x => LandmarkPairClassifier(x)
      )
      TripletClassifierUtils.saveResults(stream, "triplet", results)

      /*Pair level constraints
      * */
      val trResults = TRPairConstraintClassifier.test()
      TripletClassifierUtils.saveResults(stream, "TRPair-Constrained", TripletClassifierUtils.convertToEval(trResults))

      val lmResults = LMPairConstraintClassifier.test()
      TripletClassifierUtils.saveResults(stream, s"LMPair-Constrained", TripletClassifierUtils.convertToEval(lmResults))

      val constrainedResults = TripletClassifierUtils.test(resultsDir, isTrain, proportion,
        x => TRPairConstraintClassifier(x),
        x => IndicatorRoleClassifier(x),
        x => LMPairConstraintClassifier(x)
      )
      TripletClassifierUtils.saveResults(stream, s"triplet-constrained", constrainedResults)

      /*Sentence level constraints
     * */

      val trSentenceResults = SentenceLevelConstraintClassifiers.TRConstraintClassifier.test()
      TripletClassifierUtils.saveResults(stream, "TR-SentenceConstrained",
        TripletClassifierUtils.convertToEval(trSentenceResults))

      val lmSentenceResults = SentenceLevelConstraintClassifiers.LMConstraintClassifier.test()
      TripletClassifierUtils.saveResults(stream, "LM-SentenceConstrained",
        TripletClassifierUtils.convertToEval(lmSentenceResults))

      val spSentenceResults = SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier.test()
      TripletClassifierUtils.saveResults(stream, "SP-SentenceConstrained",
        TripletClassifierUtils.convertToEval(spSentenceResults))

      val trPairSentenceResults = SentenceLevelConstraintClassifiers.TRPairConstraintClassifier.test()
      TripletClassifierUtils.saveResults(stream, "TRPair-SentenceConstrained",
        TripletClassifierUtils.convertToEval(trPairSentenceResults))

      val lmPairSentenceResults = SentenceLevelConstraintClassifiers.LMPairConstraintClassifier.test()
      TripletClassifierUtils.saveResults(stream, "LMPair-SentenceConstrained",
        TripletClassifierUtils.convertToEval(lmPairSentenceResults))

      val constrainedPairSentenceResults = TripletClassifierUtils.test(resultsDir, isTrain,
        proportion, x => SentenceLevelConstraintClassifiers.TRPairConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.IndicatorConstraintClassifier(x),
        x => SentenceLevelConstraintClassifiers.LMPairConstraintClassifier(x)
      )
      TripletClassifierUtils.saveResults(stream, "triplet-SentenceConstrained", constrainedPairSentenceResults)

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

}

