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
    //var lexm: Lexiconer = new Lexiconer()
    val model = new SaulSLModel(cls)
    val sp = SL_IOManager.makeSLProblem(dm, cls)
    model.infSolver = new Saul_SL_Inference(model.Factors, dm)
    val para = new SLParameters
    para.C_FOR_STRUCTURE = 1
    para.CHECK_INFERENCE_OPT = false
    //    model.config = new util.HashMap();
    model.para = para
    model.featureGenerator = new SL_FeatureGenerator(model.Factors)
    // model.lm=lexm
    // model.lm.setAllowNewFeatures(false)
    //  para.TOTAL_NUMBER_FEATURE = 3 * model.lm.getNumOfFeature
    para.loadConfigFile("./config/DCD.config")
    val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para);

    //    println("num?:"+(model.featureGenerator).asInstanceOf[ERFeatureGenerator].getlexicon().getNumOfFeature)
    //println("num?:" + model.lm.getNumOfFeature)
    InitializeSL(sp,model)
    model.wv = learner.train(sp)

//    model.saveModel("modelname.SAUL");


  }
}
