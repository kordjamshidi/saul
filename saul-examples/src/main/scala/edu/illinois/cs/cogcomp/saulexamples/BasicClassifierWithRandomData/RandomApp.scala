/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData

import edu.illinois.cs.cogcomp.lbjava.learn.SparsePerceptron
import edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData.BinaryConstraints.{ binaryConstrainedClassifier, oppositBinaryConstrainedClassifier }
import edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData.RandomClassifiers.{ BinaryClassifier, OppositClassifier }

object RandomApp extends App {

  import RandomDataModel._
  for (i <- 1 to 100) {
    randomNode.addInstance(i.toString)
  }
  val examples = randomNode.getAllInstances
  BinaryClassifier.classifier.asInstanceOf[SparsePerceptron].setInitialWeight(0.01)
  OppositClassifier.classifier.asInstanceOf[SparsePerceptron].setInitialWeight(0.02)

  // val graphCacheFile = "models/temp.model"
  //RandomDataModel.deriveInstances()
  // RandomDataModel.write(graphCacheFile)
  //BinaryClassifier.learn(30)
  BinaryClassifier.test(examples)
  //OppositClassifier.learn(30)
  //OppositClassifier.test(examples)
  //binaryConstrainedClassifier.test(examples)
  val ccl = List(binaryConstrainedClassifier, oppositBinaryConstrainedClassifier)

  // JointTrain.train(randomNode,ccl ,10)

  //binaryConstrainedClassifier.classifier.asInstanceOf[SparsePerceptron].setInitialWeight(0.01)

  BinaryClassifier.test(examples)

  OppositClassifier.test(examples)
  binaryConstrainedClassifier.test(examples)
  oppositBinaryConstrainedClassifier.test(examples)

}
