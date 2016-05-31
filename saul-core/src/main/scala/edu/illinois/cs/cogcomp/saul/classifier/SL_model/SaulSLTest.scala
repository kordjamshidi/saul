package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.sl.core.IStructure

import scala.collection.JavaConversions._
import scala.reflect.ClassTag

/** Created by Parisa on 4/1/16.
  */

object SaulSLTest {

  def evaluate[HEAD <: AnyRef](goldIstructure: List[IStructure], predictedIstructure: List[IStructure], model: SaulSLModel[HEAD]) = {
    model.Factors.foreach {
      x =>
        {
        }
    }
  }
  def apply[HEAD <: AnyRef](dm: DataModel, cls: List[ConstrainedClassifier[_, HEAD]], model: SaulSLModel[HEAD], inference: Saul_SL_Inference[HEAD])(implicit t: ClassTag[HEAD]): Unit = {
    val sp = SL_IOManager.makeSLProblem(dm, cls, true)
    var a: List[IStructure] = List[IStructure]()
    sp.instanceList.toList.foreach {
      ins =>
        a = model.infSolver.getBestStructure(model.wv, ins) :: a
    }
    evaluate(a, sp.goldStructureList.toList, model)
  }

}