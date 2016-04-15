package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.sl.core.IInstance
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector
import scala.collection.mutable.ListBuffer

/** Created by Parisa on 12/10/15.
  * Here we only make the lbjava lexicons for each onClassifier
  * (i.e. the base classifier of each constraint classifier) based on the features of IInstances
  */
case class Saul_SL_Instance[HEAD <: AnyRef](l: List[ConstrainedClassifier[_, HEAD]], x: HEAD) extends IInstance {
  var ConstraintFactors: ListBuffer[ConstrainedClassifier[_, HEAD]] =ListBuffer()
  var fv: IFeatureVector = null;
  val head: HEAD = x
  def apply = {
    l.foreach {
      (c: ConstrainedClassifier[_, HEAD]) =>
     ConstraintFactors += (c)
   }
  }
}

