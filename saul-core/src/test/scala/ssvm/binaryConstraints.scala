package ssvm

/**
 * Created by Parisa on 10/21/16.
 */

import edu.illinois.cs.cogcomp.infer.ilp.OJalgoHook
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import ssvm.randomClassifiers.bClassifier

object binaryConstraints {

 val binaryConstraint =  ConstrainedClassifier.constraint[String]{

   x: String => bClassifier on x is "-1"
 }
}
object binaryConstrainedClassifier extends ConstrainedClassifier[String,String](bClassifier){
  def subjectTo = binaryConstraints.binaryConstraint
  override val solver = new OJalgoHook
}