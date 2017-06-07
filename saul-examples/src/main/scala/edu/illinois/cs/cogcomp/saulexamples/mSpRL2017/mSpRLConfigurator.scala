package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.FeatureSets
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.mSpRLConfigurator.useConstraints

/** Created by parisakordjamshidi on 3/23/17.
  */
object mSpRLConfigurator {
  val resultsDir = "data/mSpRL/results/"
  val imageDataPath = "data/mSprl/saiapr_tc-12/"
  val modelDir = "models/mSpRL/"
  val spatialIndicatorLex = "data/mSprl/spatialIndicator.lex"
  val trainFile = "data/mSprl/saiapr_tc-12/newSpRL2017_train.xml"
  val testFile = "data/mSprl/saiapr_tc-12/newSpRL2017_gold.xml"
  val suffix = ""
  val model = FeatureSets.WordEmbedding
  val isTrain = true
  val jointTrain = true
  val skipIndividualClassifiersTraining = true  /* When using joint train, it will ignore individual classifiers
                                                 * training and loads them from the disk*/
  val iterations = 50
  val useConstraints = true
  val populateImages = model == FeatureSets.WordEmbeddingPlusImage || model == FeatureSets.BaseLineWithImage
}
