package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers.{ LandmarkPairClassifier, LandmarkRoleClassifier, TrajectorPairClassifier, TrajectorRoleClassifier }
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.SentenceLevelConstraints._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ Relation, Sentence, Token }
import MultiModalSpRLDataModel.{ sentences, _ }

/** Created by parisakordjamshidi on 2/9/17.
  */
object SentenceLevelConstraintClassifiers {

  val erSolver = new OJalgoHook

  object TRPairConstraintClassifier extends ConstrainedClassifier[Relation, Sentence](TrajectorPairClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    override val pathToHead = Some(-sentenceToRelations)
  }
  object LMPairConstraintClassifier extends ConstrainedClassifier[Relation, Sentence](LandmarkPairClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    override val pathToHead = Some(-sentenceToRelations)
  }

  object LMConstraintClassifier extends ConstrainedClassifier[Token, Sentence](LandmarkRoleClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    override val pathToHead = Some(-sentenceToToken)
  }

  object TRConstraintClassifier extends ConstrainedClassifier[Token, Sentence](TrajectorRoleClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    override val pathToHead = Some(-sentenceToToken)
  }
}
