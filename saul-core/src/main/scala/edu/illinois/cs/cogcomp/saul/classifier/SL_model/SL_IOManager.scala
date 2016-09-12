/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.sl.core.SLProblem
import edu.illinois.cs.cogcomp.sl.util.Lexiconer

import scala.reflect.ClassTag

/** Created by Parisa on 12/4/15.
  */
object SL_IOManager {
  val lexm: Lexiconer = new Lexiconer()
  def makeSLProblem[HEAD <: AnyRef](node: Node[HEAD], list: List[ConstrainedClassifier[_, HEAD]], testing: Boolean = false)(implicit t: ClassTag[HEAD]): SLProblem = {
    var sp: SLProblem = new SLProblem()
    var allHeads: Iterable[HEAD] = Iterable[HEAD]()
    if (testing) {
      allHeads = node.getTestingInstances
    } else {
      allHeads = node.getTrainingInstances
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
