/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData

import edu.illinois.cs.cogcomp.lbjava.learn.SparsePerceptron
import edu.illinois.cs.cogcomp.saul.classifier.Learnable

object RandomClassifiers {
  import RandomDataModel._
  object BinaryClassifier extends Learnable[String](randomNode) {
    def label = randomLabel
    override def feature = using(randomProperty)
    override lazy val classifier = new SparsePerceptron()
    //override val useCache = true
  }

object OppositClassifier extends Learnable[String](randomNode) {
    def label = oppositRandomLabel
    override def feature = using(randomProperty)
    override lazy val classifier = new SparsePerceptron()
    //override val useCache = true
  }
}
