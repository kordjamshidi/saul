/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.{ ClassifierUtils, ConstrainedClassifier }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.sl.core._
import edu.illinois.cs.cogcomp.sl.learner._

import scala.collection.JavaConversions._
import scala.reflect._

/** Created by Parisa on 12/3/15.
  */
object StructuredLearning {
  def apply[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]], usePreTrained: Boolean = false)(implicit headTag: ClassTag[HEAD]) =
    {
      trainSSVM[HEAD](node, cls, usePreTrained)
    }

  def trainSSVM[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]], usePreTrained: Boolean)(implicit t: ClassTag[HEAD]): SaulSLModel[HEAD] = {
    val sp = SL_IOManager.makeSLProblem(node, cls)
    val model = Initialize(node, new SaulSLModel(cls), usePreTrained)

    model.infSolver = new Saul_SL_Inference[HEAD](model.Factors.toList, model.LTUWeightTemplates)
    val para = new SLParameters
    //    para.STOP_CONDITION = 0.0001f
    //    para.INNER_STOP_CONDITION= 0.0001f
    //    para.C_FOR_STRUCTURE = 1
    //    para.CHECK_INFERENCE_OPT = false

    model.featureGenerator = new SL_FeatureGenerator(model)
    para.loadConfigFile("../config/DCD.config")
    model.para = para
    val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para);
    model.wv = learner.train(sp, model.wv)
    model.saveModel("SL_ER_Model.txt")
    return model
  }

  def Eval1[T <: AnyRef, H <: AnyRef](cf: ConstrainedClassifier[T, H], sp: SLProblem) = {

    val testExamples: Seq[T] = sp.instanceList.map(x => cf.getCandidates(x.asInstanceOf[Saul_SL_Instance[H]].head)).flatten.distinct
    ClassifierUtils.TestClassifiers.apply1(testExamples, cf)
  }
  def Evaluate[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_ <: AnyRef, HEAD]], myModel: SaulSLModel[HEAD], modelPath: String)(implicit t: ClassTag[HEAD]): Unit = {

    val sp: SLProblem = SL_IOManager.makeSLProblem[HEAD](node, cls, testing = true)
    println("Before weight update:")
    val results1 = for (cf <- myModel.Factors.toList.asInstanceOf[List[ConstrainedClassifier[_ <: AnyRef, HEAD]]]) yield {
      Eval1(cf, sp)
    }
    println("after weight update:")
    myModel.infSolver.asInstanceOf[Saul_SL_Inference[HEAD]].updateWeights(myModel.wv)
    val results = for (cf <- myModel.Factors.toList.asInstanceOf[List[ConstrainedClassifier[_ <: AnyRef, HEAD]]]) yield {
      Eval1(cf, sp)
    }
  }
}

