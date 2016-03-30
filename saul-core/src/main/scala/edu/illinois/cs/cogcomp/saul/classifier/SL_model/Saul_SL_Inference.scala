package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.sl.core.{ AbstractInferenceSolver, IInstance, IStructure }
import edu.illinois.cs.cogcomp.sl.util.WeightVector

/** Created by Parisa on 12/8/15.
  */
class Saul_SL_Inference[HEAD <: AnyRef] extends AbstractInferenceSolver {

  override def getBestStructure(weight: WeightVector, ins: IInstance): IStructure = {
    val myIns = ins.asInstanceOf[Saul_SL_Instance[HEAD]]
    var myStruct: Saul_SL_Label_Structure[HEAD] = new Saul_SL_Label_Structure[HEAD](myIns.factorClassifiers.toList, myIns.head)
    for (i <- 0 until myIns.factorClassifiers.size) {
      val c = myIns.factorClassifiers(i)
      val candis: Seq[_] = c.getCandidates(myIns.head)
      candis.foreach(x =>
        myStruct.labels += myIns.factorClassifiers(i).classifier.discreteValue(x))
    }
    myStruct
  }

  override def getLoss(ins: IInstance, gold: IStructure, pred: IStructure): Float = { //TODO check
    var l: Float = 0
    val myGold = gold.asInstanceOf[Saul_SL_Label_Structure[HEAD]]
    val myPred = pred.asInstanceOf[Saul_SL_Label_Structure[HEAD]]
    for (i <- 0 until myGold.labels.size) {
      l = l + myGold.labels(i).equals(myPred.labels(i)).asInstanceOf[Float]
    }
    l
  }

  override def getLossAugmentedBestStructure(weight: WeightVector, ins: IInstance, goldStructure: IStructure): IStructure = {

    val myIns = ins.asInstanceOf[Saul_SL_Instance[HEAD]]
    // update the classifiers with the current wight modle
    var myStruct: Saul_SL_Label_Structure[HEAD] = new Saul_SL_Label_Structure[HEAD](myIns.factorClassifiers.toList, myIns.head)
    for (i <- 0 until myIns.factorClassifiers.size) {
      val c = myIns.factorClassifiers(i)
      val candis: Seq[_] = c.getCandidates(myIns.head)
      candis.foreach(x =>
        myStruct.labels += myIns.factorClassifiers(i).classifier.discreteValue(x))
    }
    myStruct
  }
  //Todo add loss to the objective before calling inference
}
