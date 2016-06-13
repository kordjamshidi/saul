package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.{ClassifierUtils, ConstrainedClassifier}
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.sl.core._
import edu.illinois.cs.cogcomp.sl.learner._

import scala.collection.JavaConversions._
import scala.reflect._

/** Created by Parisa on 12/3/15.
  */
object StructuredLearning {
  def apply[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]])(implicit headTag: ClassTag[HEAD]) =
    {
      trainSSVM[HEAD](node, cls)
    }

  def trainSSVM[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]])(implicit t: ClassTag[HEAD]): SaulSLModel[HEAD] = {
    val sp = SL_IOManager.makeSLProblem(node, cls)
    val model = Initialize(sp, new SaulSLModel(cls))
    model.infSolver = new Saul_SL_Inference[HEAD](model.Factors.toList, model.LTUWeightTemplates, node)
    val para = new SLParameters
    //    para.STOP_CONDITION = 0.0001f
    //    para.INNER_STOP_CONDITION= 0.0001f
    //    para.C_FOR_STRUCTURE = 1
    //    para.CHECK_INFERENCE_OPT = false
    model.para = para
    model.featureGenerator = new SL_FeatureGenerator(model)
    para.loadConfigFile("./config/DCD.config")
    val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para);
    model.wv = learner.train(sp, model.wv)
    model.saveModel("SL_ER_Model.txt")
    return model
  }
  def Evaluate[HEAD <: AnyRef](node: Node[HEAD], cls: List[ConstrainedClassifier[_, HEAD]], modelPath: String)(implicit t: ClassTag[HEAD]): Unit = {

    val myModel = SLModel.loadModel(modelPath).asInstanceOf[SaulSLModel[HEAD]]
    val sp: SLProblem = SL_IOManager.makeSLProblem[HEAD](node, cls, testing = true)
    myModel.asInstanceOf[Saul_SL_Inference].updateWeights(myModel.wv)
    val il = for {
      cf <- myModel.Factors.toList
      testExamples = for (candList <- sp.instanceList) yield
      cf.getCandidates(candList.asInstanceOf[Saul_SL_Instance[HEAD]].head).flatten.distinct
      } yield (testExamples, cf)

     ClassifierUtils.TestClassifiers(il.toSeq)

  }
}

