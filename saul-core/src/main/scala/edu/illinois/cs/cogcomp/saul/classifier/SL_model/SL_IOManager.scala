package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.sl.core.SLProblem

import scala.reflect.ClassTag

/** Created by Parisa on 12/4/15.
  */
object SL_IOManager {
  def makeSLProblem[HEAD <: AnyRef](dm: DataModel, list: List[ConstrainedClassifier[_, HEAD]])(implicit t: ClassTag[HEAD]): SLProblem = {
    var sp: SLProblem = new SLProblem()
    val allHeads = dm.getNodeWithType[HEAD].getAllInstances
    allHeads.foreach(x => {
      val ins = new Saul_SL_java_Instance(list.asInstanceOf[ConstrainedClassifier[_,HEAD]], x)
      val outs = new Saul_SL_Label_java_Structure(list.asInstanceOf, x)
      sp.addExample(ins, outs)
    })
    sp
  }
}
