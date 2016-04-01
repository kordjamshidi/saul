package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.lbjava.learn.{LinearThresholdUnit, SparseWeightVector}
import edu.illinois.cs.cogcomp.saul.classifier.{ConstrainedClassifier, SparseNetworkLBP}
import edu.illinois.cs.cogcomp.sl.core.{AbstractInferenceSolver, IInstance, IStructure}
import edu.illinois.cs.cogcomp.sl.util.WeightVector
/** Created by Parisa on 12/8/15.
  */
class Saul_SL_Inference[HEAD <: AnyRef](factors: List[ConstrainedClassifier[_,HEAD]]) extends AbstractInferenceSolver {
  val a=factors

  override def getBestStructure(weight: WeightVector, ins: IInstance): IStructure = {

    val myIns = ins.asInstanceOf[Saul_SL_Instance[HEAD]]
    var myStruct: Saul_SL_Label_Structure[HEAD] = new Saul_SL_Label_Structure[HEAD](myIns.ConstraintFactors.toList, myIns.head)
    for (i <- 0 until myIns.ConstraintFactors.size) {
      val c = myIns.ConstraintFactors(i)
      val candis: Seq[_] = c.getCandidates(myIns.head)
      candis.foreach(x =>
        myStruct.labels += myIns.ConstraintFactors(i).classifier.discreteValue(x))
    }
    myStruct
  }

  override def getLoss(ins: IInstance, gold: IStructure, pred: IStructure): Float = { //TODO check
    var l: Float = 0
    val myGold = gold.asInstanceOf[Saul_SL_Label_Structure[HEAD]]
    val myPred = pred.asInstanceOf[Saul_SL_Label_Structure[HEAD]]
    for (i <- 0 until myGold.labels.size) {

      if (myGold.labels(i)!=(myPred.labels(i)))
            l = l+1
    }
    l
  }

  override def getLossAugmentedBestStructure(weight: WeightVector, ins: IInstance, goldStructure: IStructure): IStructure = {

    val myIns = ins.asInstanceOf[Saul_SL_Instance[HEAD]]
    val myStruct: Saul_SL_Label_Structure[HEAD] = new Saul_SL_Label_Structure[HEAD](myIns.ConstraintFactors.toList, myIns.head)

  a.foreach {
    cf=>

        for (i <- 0 until cf.onClassifier.asInstanceOf[SparseNetworkLBP].net.size())
        {
          val w1= cf.onClassifier.asInstanceOf[SparseNetworkLBP].net.get(i).asInstanceOf[LinearThresholdUnit].getParameters.asInstanceOf[LinearThresholdUnit.Parameters].weightVector
          val myFactorJoinlyTrainedWeight= weight.getWeightArray.slice(i,w1.size())
          cf.onClassifier.asInstanceOf[SparseNetworkLBP].net.get(i).asInstanceOf[LinearThresholdUnit].getParameters.asInstanceOf[LinearThresholdUnit.Parameters].weightVector= new SparseWeightVector(Utils.converFarrayToD(myFactorJoinlyTrainedWeight))

        }
  }
  a.foreach {
    cf=>
       cf.getCandidates(myIns.head).foreach {
       x =>
       myStruct.labels += cf.classifier.discreteValue(x)
    }
  }
    myStruct
  }
  //Todo add loss to the objective before calling inference
}
