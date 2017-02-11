package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.lbjava.infer.{FirstOrderConstant, FirstOrderConstraint}
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Relation, Sentence}
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
/**
  * Created by parisakordjamshidi on 2/9/17.
  */
object SentenceLevelConstraints {
  val integrityTR = ConstrainedClassifier.constraint[Sentence] {
    var a: FirstOrderConstraint = null
    s: Sentence =>
      a = new FirstOrderConstant(true)
      (sentences(s) ~> sentenceToToken <~ relationToFirstArgument).foreach
      {
        x=>
        a= a and (((TrajectorPairClassifier on x) is "TR-SP") ==>
        (TrajectorRoleClassifier on (pairs(x) ~> relationToFirstArgument).head is "Trajector") and
        (IndicatorRoleClassifier on (pairs(x) ~> relationToSecondArgument).head is "Indicator"))
     }
      a
  }

  val integrityLM = ConstrainedClassifier.constraint[Sentence] {
    var a: FirstOrderConstraint = null
    s: Sentence =>
      a = new FirstOrderConstant(true)
      (sentences(s) ~> sentenceToToken <~ relationToFirstArgument).foreach{
        x=>
      ((LandmarkPairClassifier on s) is "LM-SP") ==>
        (LandmarkRoleClassifier on (pairs(x) ~> relationToFirstArgument).head is "Landmark") and
        (IndicatorRoleClassifier on (pairs(x) ~> relationToSecondArgument).head is "Indicator")
  }
  a
  }

  val allConstraints = ConstrainedClassifier.constraint[Sentence] {

    x: Sentence => integrityLM(x) and integrityTR(x)
  }

}
