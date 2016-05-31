package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.sl.core._
import edu.illinois.cs.cogcomp.sl.learner._

import scala.reflect._

/** Created by Parisa on 12/3/15.
  */
object JoinSLtrain {
  def apply[HEAD <: AnyRef](
    dm: DataModel,
    cls: List[ConstrainedClassifier[_, HEAD]]
  )(
    implicit
    headTag: ClassTag[HEAD]
  ) = {

    trainSSVM[HEAD](dm, cls)
  }

  def trainSSVM[HEAD <: AnyRef](dm: DataModel, cls: List[ConstrainedClassifier[_, HEAD]])(implicit t: ClassTag[HEAD]): Unit = {
    val sp = SL_IOManager.makeSLProblem(dm, cls)
    val model = Initialize(sp, new SaulSLModel(cls))
    model.infSolver = new Saul_SL_Inference[HEAD](model.Factors.toList, model.LtuTemplates, dm)
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
  }
  def TestSSVM[HEAD <: AnyRef](dm: DataModel, cls: List[ConstrainedClassifier[_, HEAD]], modelPath: String)(implicit t: ClassTag[HEAD]): Unit = {
    val myModel = SLModel.loadModel(modelPath).asInstanceOf[SaulSLModel[HEAD]]
    val sp: SLProblem = SL_IOManager.makeSLProblem[HEAD](dm, cls, testing = true)

    // val sp.instanceList.toList.map(x=> myModel.infSolver.getBestStructure(myModel.wv,x.asInstanceOf[Saul_SL_Instance].head.asInstanceOf[Saul_SL_Instance]))
    //    def apply {
    //
    //      l.foreach { (c: ConstrainedClassifier[_, HEAD]) =>
    //      {
    //        val oracle: Classifier = c.onClassifier.getLabeler()
    //        val candis: Seq[_] = c.getCandidates(x)
    //        candis.foreach {
    //          ci =>
    //            labels.add(oracle.discreteValue(ci))
    //        }
    //      }
    //      }
    //    }

  }
}
