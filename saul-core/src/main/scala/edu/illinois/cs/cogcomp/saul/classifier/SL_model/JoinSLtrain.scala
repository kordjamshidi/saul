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
    cls: List[ConstrainedClassifier[_<:AnyRef, HEAD]]
  )(
    implicit
    headTag: ClassTag[HEAD]
  ) = {

    trainSSVM[HEAD](dm, cls)
  }

  def trainSSVM[HEAD <: AnyRef](dm: DataModel, cls: List[ConstrainedClassifier[_<:AnyRef, HEAD]])(implicit t: ClassTag[HEAD]): Unit = {
    val sp = SL_IOManager.makeSLProblem(dm, cls)
    val model = Initialize(sp, new SaulSLModel(cls))
    model.infSolver = new Saul_SL_Inference[HEAD](model.Factors, dm)
    val para = new SLParameters
    para.C_FOR_STRUCTURE = 1
    para.CHECK_INFERENCE_OPT = false
    model.para = para
    model.featureGenerator = new SL_FeatureGenerator(model)
    para.loadConfigFile("./config/DCD.config")
    val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para);
    model.wv = learner.train(sp)
  }
}
