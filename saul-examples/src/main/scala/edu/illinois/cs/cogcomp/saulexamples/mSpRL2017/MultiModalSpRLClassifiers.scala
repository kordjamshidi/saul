/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.lbjava.learn.{SparseAveragedPerceptron, SparseNetworkLearner, SupportVectorMachine}
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.datamodel.property.Property
import edu.illinois.cs.cogcomp.saul.learn.SaulWekaWrapper
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.FeatureSets
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.FeatureSets.FeatureSets
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import weka.classifiers.`lazy`.IBk
import weka.classifiers.bayes.NaiveBayes

object MultiModalSpRLClassifiers {
  var featureSet = FeatureSets.WordEmbeddingPlusImage

  def phraseFeatures: List[Property[Phrase]] = phraseFeatures(featureSet)

  def phraseFeatures(featureSet: FeatureSets): List[Property[Phrase]] =
    List(wordForm, headWordFrom, pos, headWordPos, phrasePos, semanticRole, dependencyRelation, subCategorization,
      spatialContext) ++
      (featureSet match {
        case FeatureSets.WordEmbedding => List(tokenVector)
        case FeatureSets.WordEmbeddingPlusImage => List(tokenVector, isTokenAnImageConcept, nearestSegmentConceptVector)
        case _ => List[Property[Phrase]]()
      })

  def relationFeatures: List[Property[Relation]] = relationFeatures(featureSet)

  def relationFeatures(featureSet: FeatureSets): List[Property[Relation]] =
    List(relationWordForm, relationHeadWordForm, relationPos, relationHeadWordPos, relationPhrasePos,
      relationSemanticRole, relationDependencyRelation, relationSubCategorization, relationSpatialContext, distance,
      before, isTrajectorCandidate, isLandmarkCandidate, isIndicatorCandidate) ++
      (featureSet match {
        case FeatureSets.WordEmbedding => List(relationTokensVector)
        case FeatureSets.WordEmbeddingPlusImage => List(relationTokensVector, relationNearestSegmentConceptVector, relationIsTokenAnImageConcept)
        case _ => List[Property[Relation]]()
      })

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

  object SpatialRoleClassifier extends Learnable(phrases) {
    def label = spatialRole

    override lazy val classifier = new SparseNetworkLearner()

    override def feature = using(phraseFeatures)
  }

  object TrajectorRoleClassifier extends Learnable(phrases) {
    def label = trajectorRole

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 2
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = using(phraseFeatures)
  }

  object LandmarkRoleClassifier extends Learnable(phrases) {
    def label = landmarkRole

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 2
      baseLTU = new SparseAveragedPerceptron(p)

    }

    override def feature = using(phraseFeatures)
  }

  object IndicatorRoleClassifier extends Learnable(phrases) {
    def label = indicatorRole

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.thickness = 2
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = using(phraseFeatures(FeatureSets.BaseLine))
  }

  object TrajectorPairClassifier extends Learnable(pairs) {
    def label = isTrajectorRelation

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.positiveThickness = 2
      p.negativeThickness = 1
      //p.thickness = 4
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = using(relationFeatures)
  }

  object LandmarkPairClassifier extends Learnable(pairs) {
    def label = isLandmarkRelation

    override lazy val classifier = new SparseNetworkLearner {
      val p = new SparseAveragedPerceptron.Parameters()
      p.learningRate = .1
      p.positiveThickness = 4
      p.negativeThickness = 1
      baseLTU = new SparseAveragedPerceptron(p)
    }

    override def feature = using(relationFeatures)
  }

}
