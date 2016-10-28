package edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saulexamples.BasicClassifierWithRandomData.RandomClassifiers.{OppositClassifier, BinaryClassifier}
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
/**
 * Created by Parisa on 10/28/16.
 */
object BinaryConstraints {
  val binaryConstraint =  ConstrainedClassifier.constraint[String]{

    x: String =>
      (BinaryClassifier on x is "-1") ==> (OppositClassifier on x is "-1")
  }
}

object binaryConstrainedClassifier extends ConstrainedClassifier[String,String](BinaryClassifier){
  def subjectTo =  BinaryConstraints.binaryConstraint
  override val solver = new OJalgoHook
}

