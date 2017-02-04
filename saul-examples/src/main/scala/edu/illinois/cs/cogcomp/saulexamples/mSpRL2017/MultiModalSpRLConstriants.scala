package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Relation
import MultiModalSpRLDataModel._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers.{IndicatorRoleClassifier, SpatialRoleClassifier, TrajectorPairClassifier}

import scala.collection.JavaConversions._
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
/**
  * Created by parisakordjamshidi on 2/4/17.
  */
object MultiModalSpRLConstriants {

val integrity = ConstrainedClassifier.constraint[Relation]{
  x : Relation =>

    ((TrajectorPairClassifier on x) is "TR_SP") ==>
      (SpatialRoleClassifier on (textRelations(x) ~> relationToFirstArgument) is "Trajector") and (IndicatorRoleClassifier on (textRelations(x) ~> relationToSecondArgument) is "Indicator")
}
}
