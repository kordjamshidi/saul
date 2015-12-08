package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.lbjava.learn.LinearThresholdUnit
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.sl.core.{ SLModel, SLParameters }
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory

import scala.reflect.ClassTag

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

  def trainSSVM[HEAD <: AnyRef](dm: DataModel, cls: List[ConstrainedClassifier[_, HEAD]]): Unit = {

    val model = new SLModel
    val allHeads = dm.getNodeWithType[HEAD].getTrainingInstances
    val sp = SL_IOManager.makeSLProblem(dm, cls)
    //    model.infSolver = new iERjavaInferencePL
    val para = new SLParameters
    para.C_FOR_STRUCTURE = 1
    para.CHECK_INFERENCE_OPT = false

    //    model.config = new util.HashMap();
    model.para = para
    //    model.featureGenerator = new ERFeatureGenerator(lexm)
    //    model.lm=lexm
    model.lm.setAllowNewFeatures(true)
    para.TOTAL_NUMBER_FEATURE = 3 * model.lm.getNumOfFeature
    para.loadConfigFile("./config/DCD.config")
    val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para);

    //    println("num?:"+(model.featureGenerator).asInstanceOf[ERFeatureGenerator].getlexicon().getNumOfFeature)
    println("num?:" + model.lm.getNumOfFeature)

    //    model.wv = learner.train(sp)
    //    model.saveModel(modelname);

    allHeads foreach {
      h =>
        {
          cls.foreach {
            case c: ConstrainedClassifier[_, HEAD] => {

              type C = c.LEFT

              val typedC = c.asInstanceOf[ConstrainedClassifier[_, HEAD]]

              val oracle = typedC.onClassifier.getLabeler

              typedC.getCandidates(h) foreach {
                x =>
                  {

                    def trainOnce() = {

                      val result = typedC.classifier.discreteValue(x)
                      val trueLabel = oracle.discreteValue(x)

                      if (result.equals("true") && trueLabel.equals("false")) {
                        val a = typedC.onClassifier.getExampleArray(x)
                        val a0 = a(0).asInstanceOf[Array[Int]]
                        val a1 = a(1).asInstanceOf[Array[Double]]

                        typedC.onClassifier.asInstanceOf[LinearThresholdUnit].promote(a0, a1, 0.1)
                      } else {

                        if (result.equals("false") && trueLabel.equals("true")) {
                          val a = typedC.onClassifier.getExampleArray(x)
                          val a0 = a(0).asInstanceOf[Array[Int]]
                          val a1 = a(1).asInstanceOf[Array[Double]]
                          typedC.onClassifier.asInstanceOf[LinearThresholdUnit].demote(a0, a1, 0.1)
                        } else {
                        }
                      }

                    }

                    trainOnce()
                  }
              }
            }
          }
        }
    }
    train(dm, cls, it - 1)

  }
}
