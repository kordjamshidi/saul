package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Relation, Token}
import MultiModalSpRLConstriants._
import MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers.{LandmarkPairClassifier, LandmarkRoleClassifier, TrajectorPairClassifier, TrajectorRoleClassifier}
/**
  * Created by parisakordjamshidi on 2/4/17.
  */
object MultiModalContrainedClassifiers {

 // import SRLApps.srlDataModelObject._
  val erSolver = new OJalgoHook

  object TRPairConstraintClassifier extends ConstrainedClassifier[Relation, Relation](TrajectorPairClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    //override val pathToHead = Some()
  }
  object LMPairConstraintClassifier extends ConstrainedClassifier[Relation, Relation](LandmarkPairClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    //override val pathToHead = Some()
  }

  object lmConstraintClassifier extends ConstrainedClassifier[Token, Relation](LandmarkRoleClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    //override val pathToHead = Some()
  }

  object trConstraintClassifier extends ConstrainedClassifier[Token, Relation](TrajectorRoleClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    //override val pathToHead = Some()
  }
}
