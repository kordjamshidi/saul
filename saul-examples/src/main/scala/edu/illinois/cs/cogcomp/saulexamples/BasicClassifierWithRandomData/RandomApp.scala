/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData

import edu.illinois.cs.cogcomp.saul.classifier.JointTrainSparseNetwork
import edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData.BinaryConstraints.{binaryConstrainedClassifier, oppositBinaryConstrainedClassifier}
import edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData.RandomClassifiers.{BinaryClassifier, OppositClassifier}

object RandomApp extends App{

  import RandomDataModel._
  for (i <- 1 to 100) {
    randomNode.addInstance(i.toString)
  }
  val examples = randomNode.getAllInstances
  val graphCacheFile = "models/temp.model"
  RandomDataModel.deriveInstances()
  RandomDataModel.write(graphCacheFile)
  BinaryClassifier.learn(30)
  BinaryClassifier.test(examples)
  OppositClassifier.learn(30)
  OppositClassifier.test(examples)
  binaryConstrainedClassifier.test(examples)
  val ccl= List(binaryConstrainedClassifier,oppositBinaryConstrainedClassifier)
  JointTrainSparseNetwork.train(randomNode,ccl ,10, false)
  binaryConstrainedClassifier.test(examples)
}
