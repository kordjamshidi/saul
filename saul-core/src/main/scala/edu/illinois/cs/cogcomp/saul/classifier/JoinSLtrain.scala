package edu.illinois.cs.cogcomp.saul.classifier

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.sl.core.{SLModel, SLParameters}
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory

import scala.reflect.ClassTag

/**
 * Created by Parisa on 12/3/15.
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

  def trainSSVM[HEAD <: AnyRef](dm: DataModel, cls: List[ConstrainedClassifier[_, HEAD]]): Unit = {

    val model = new SLModel
//    val sp = ERIOManager.readXY(cr,0,10)
//    model.infSolver = new iERjavaInferencePL
    val para = new SLParameters
    para.C_FOR_STRUCTURE = 1
    para.CHECK_INFERENCE_OPT = false

//    model.config = new util.HashMap();
    model.para = para
//    model.featureGenerator = new ERFeatureGenerator(lexm)
//    model.lm=lexm
    model.lm.setAllowNewFeatures(true)
    para.TOTAL_NUMBER_FEATURE = 3*model.lm.getNumOfFeature
    para.loadConfigFile("./config/DCD.config")
    val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para);

//    println("num?:"+(model.featureGenerator).asInstanceOf[ERFeatureGenerator].getlexicon().getNumOfFeature)
    println("num?:"+model.lm.getNumOfFeature)

//    model.wv = learner.train(sp)
//    model.saveModel(modelname);
  }
}
