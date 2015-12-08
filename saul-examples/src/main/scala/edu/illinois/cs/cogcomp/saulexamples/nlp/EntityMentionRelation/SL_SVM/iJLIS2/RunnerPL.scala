package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2

import java.util

import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.reader.Conll04_ReaderNew
import edu.illinois.cs.cogcomp.sl.core._
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory
import edu.illinois.cs.cogcomp.sl.util.{ Lexiconer, WeightVector }

object RunnerPL {

  var lexm = new Lexiconer()
  def trainSSVM(modelname: String, cr: Conll04_ReaderNew): Unit = {

    val model = new SLModel
    val sp = ERIOManager.readXY(cr, 0, 10)
    model.infSolver = new iERjavaInferencePL
    val para = new SLParameters
    para.C_FOR_STRUCTURE = 1
    para.CHECK_INFERENCE_OPT = false

    model.config = new util.HashMap();
    model.para = para
    model.featureGenerator = new ERFeatureGenerator(lexm)
    model.lm = lexm
    model.lm.setAllowNewFeatures(true)
    para.TOTAL_NUMBER_FEATURE = 3 * model.lm.getNumOfFeature
    para.loadConfigFile("./config/DCD.config")
    val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para);

    println("num?:" + (model.featureGenerator).asInstanceOf[ERFeatureGenerator].getlexicon().getNumOfFeature)
    println("num?:" + model.lm.getNumOfFeature)

    model.wv = learner.train(sp)
    model.saveModel(modelname);
  }

  def testSequenceSSVM(model_name: String, cr: Conll04_ReaderNew) {
    val model: SLModel = SLModel.loadModel(model_name)
    val learned_wv: WeightVector = model.wv
    val inference_proc: iERjavaInferencePL = model.infSolver.asInstanceOf[iERjavaInferencePL]
    //  cr=null

    val sp = ERIOManager.readXY(cr, 11, 20)
    printTestACC(sp, inference_proc, learned_wv)
  }
  private def printTestACC(sp: SLProblem, s_finder: AbstractInferenceSolver, ssvm_wv: WeightVector) {
    var tp: Double = 0
    var fp: Double = 0
    var tn: Double = 0
    var fn: Double = 0
    var total: Double = 0.0

    var i: Int = 0
    while (i < sp.instanceList.size) {
      val gold: ERiStructurePL = sp.goldStructureList.get(i).asInstanceOf[ERiStructurePL]
      val prediction: ERiStructurePL = s_finder.getBestStructure(ssvm_wv, sp.instanceList.get(i)).asInstanceOf[ERiStructurePL]
      System.out.println(prediction)
      //{
      var j: Int = 0

      // while (j < gold.nodeValues.size) {
      // {
      val tempGold = gold.Rlables.LinirizLabels()
      val tempPred = prediction.Rlables.LinirizLabels()
      while (j < tempGold.size) {
        var k: Int = 0
        while (k < tempGold(j).size) {
          val pred: Boolean = tempPred(j)(k) //prediction.nodeValues
          val goldval: Boolean = tempGold(j)(k) //d.nodeValues
          if (goldval == pred) {
            if (pred == true) {
              tp += 1.0
            } else {
              tn += 1.0
            }
          } else {
            if (pred == false) {
              fp += 1.0
            } else {
              fn += 1.0
            }
          }
          total += 1.0
          k = k + 1
        }
        j = j + 1
      }
      i = i + 1

    }
    val precision: Double = tp / (tp + fp)
    val recall: Double = tp / (tp + fn)
    val F1: Double = 2 * precision * recall / (precision + recall)
    println("=========================")
    println("      P        R     F   ")
    // println(String.format("      %-5s %-5s %-5s", "P", "R", "F1"))
    println(precision, recall, F1) //String.format("VALIANT : %4.3f %4.3f %4.3f", precision, recall, F1))
    println("=========================")
    println("Acc = " + (tp + tn) / total)
  }

  def main(args: Array[String]) {
    var cr = new Conll04_ReaderNew("./data/EntityMentionRelation/conll04.corp", "Pair")

    val modelname: String = "mytest1.ssvm.model"
    trainSSVM(modelname, cr)
    System.out.println("\n=== NOW TESTING ===")
    testSequenceSSVM(modelname, cr)
  }

}
