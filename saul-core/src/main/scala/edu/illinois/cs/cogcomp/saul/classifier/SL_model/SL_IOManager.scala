package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.sl.core.SLProblem
import edu.illinois.cs.cogcomp.sl.util.Lexiconer

import scala.reflect.ClassTag

/** Created by Parisa on 12/4/15.
  */
object SL_IOManager {
  val lexm: Lexiconer = new Lexiconer()
  def makeSLProblem[HEAD <: AnyRef](dm: DataModel, list: List[ConstrainedClassifier[_, HEAD]], testing: Boolean = false)(implicit t: ClassTag[HEAD]): SLProblem = {
    var sp: SLProblem = new SLProblem()
    var allHeads: Iterable[HEAD] = Iterable[HEAD]()
    if (testing) {
      allHeads = dm.getNodeWithType[HEAD].getTestingInstances
    } else {
      allHeads = dm.getNodeWithType[HEAD].getTrainingInstances
    }

    allHeads.foreach(x =>
      {
        // val l: java.util.List[ConstrainedClassifier[_,HEAD]] =list.asJava
        val ins = new Saul_SL_Instance(x)
        val outs = new Saul_SL_Label_Structure(list, x)
        sp.addExample(ins, outs)
      })
    sp
  }
}
