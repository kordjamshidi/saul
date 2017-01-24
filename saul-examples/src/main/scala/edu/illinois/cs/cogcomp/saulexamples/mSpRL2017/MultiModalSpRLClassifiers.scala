/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.lbjava.learn.{SparseNetworkLearner, SupportVectorMachine}
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.learn.SaulWekaWrapper
import weka.classifiers.`lazy`.IBk
import weka.classifiers.bayes.NaiveBayes
import MultiModalSpRLDataModel._

object MultiModalSpRLClassifiers {
  object ImageSVMClassifier extends Learnable(segments) {
    def label = segmentLabel
    override lazy val classifier = new SupportVectorMachine()
    override def feature = using(segmentFeatures)
  }

  object ImageClassifierWeka extends Learnable(segments) {
    def label = segmentLabel
    override lazy val classifier = new SaulWekaWrapper(new NaiveBayes())
    override def feature = using(segmentFeatures)
  }

  object ImageClassifierWekaIBK extends Learnable(segments) {
    def label = segmentLabel
    override lazy val classifier = new SaulWekaWrapper(new IBk())
    override def feature = using(segmentFeatures)
  }
  //Here we add the trajector, landmark and indicator classifiers
  // in addition to two pair classifiers indicator-tr amd indicator-lm.

}
