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

  private val tokenFeatures = List(wordForm, pos, semanticRole, dependencyRelation, subCategorization, spatialContext,
    isTokenAnImageConcept, nearestSegmentConceptVector, tokenVector)

  private val relationFeatures = List(relationWordForm, relationPos, relationSemanticRole, relationDependencyRelation,
    relationSubCategorization, relationSpatialContext, relationIsTokenAnImageConcept, relationTokensVector, distance, before)

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

  object SpatialRoleClassifier extends Learnable(tokens) {
    def label = spatialRole

    override lazy val classifier = new SupportVectorMachine()

    override def feature = using(tokenFeatures)
  }

  object TrajectorRoleClassifier extends Learnable(tokens) {
    def label = trajectorRole

    override lazy val classifier = new SparseNetworkLearner()

    override def feature = using(tokenFeatures)
  }

  object LandmarkRoleClassifier extends Learnable(tokens) {
    def label = landmarkRole

    override lazy val classifier = new SupportVectorMachine()

    override def feature = using(tokenFeatures)
  }

  object IndicatorRoleClassifier extends Learnable(tokens) {
    def label = indicatorRole

    override lazy val classifier = new SupportVectorMachine()

    override def feature = using(tokenFeatures)
  }

  object TrajectorPairClassifier extends Learnable(textRelations) {
    def label = relationType

    override lazy val classifier = new SparseNetworkLearner()

    override def feature = using(relationFeatures)
  }

  object LandmarkPairClassifier extends Learnable(textRelations) {
    def label = relationType

    override lazy val classifier = new SparseNetworkLearner()

    override def feature = using(relationFeatures)
  }

}
