package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Relation
import MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers.{IndicatorRoleClassifier, LandmarkPairClassifier, SpatialRoleClassifier, TrajectorPairClassifier}

import scala.collection.JavaConversions._
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
/**
  * Created by parisakordjamshidi on 2/4/17.
  */
object MultiModalSpRLConstriants {

val integrityTR = ConstrainedClassifier.constraint[Relation]{
  x : Relation =>
    ((TrajectorPairClassifier on x) is "TR_SP") ==>
      (SpatialRoleClassifier on (textRelations(x) ~> relationToFirstArgument).head is "Trajector") and
      (IndicatorRoleClassifier on (textRelations(x) ~> relationToSecondArgument).head is "Indicator")
}

  val integrityLM = ConstrainedClassifier.constraint[Relation]{
    x : Relation =>
      ((LandmarkPairClassifier on x) is "LM_SP") ==>
        (SpatialRoleClassifier on (textRelations(x) ~> relationToFirstArgument).head is "Landmark") and
        (IndicatorRoleClassifier on (textRelations(x) ~> relationToSecondArgument).head is "Indicator")
  }

val allConstraints = ConstrainedClassifier.constraint[Relation]
  {
    x : Relation => integrityLM(x) and integrityTR(x)
  }
}
