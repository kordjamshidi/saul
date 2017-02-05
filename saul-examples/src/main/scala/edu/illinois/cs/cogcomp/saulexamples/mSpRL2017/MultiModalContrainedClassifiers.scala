package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Relation
import MultiModalSpRLConstriants._
import MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers.TrajectorPairClassifier
/**
  * Created by parisakordjamshidi on 2/4/17.
  */
object MultiModalContrainedClassifiers {

 // import SRLApps.srlDataModelObject._
  val erSolver = new OJalgoHook

  object argTRConstraintClassifier extends ConstrainedClassifier[Relation, Relation](TrajectorPairClassifier) {
    def subjectTo = allConstraints
    override val solver = erSolver
    //override val pathToHead = Some()
  }
}
