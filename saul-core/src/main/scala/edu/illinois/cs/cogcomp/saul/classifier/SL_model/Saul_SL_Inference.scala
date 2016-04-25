package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.lbjava.learn.{LinearThresholdUnit, SparseWeightVector}
import edu.illinois.cs.cogcomp.saul.classifier.{ConstrainedClassifier, SparseNetworkLBP}
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.sl.core.{AbstractInferenceSolver, IInstance, IStructure}
import edu.illinois.cs.cogcomp.sl.util.WeightVector

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

/** Created by Parisa on 12/8/15.
  */
class Saul_SL_Inference[HEAD <: AnyRef](factors: List[ConstrainedClassifier[_,HEAD]], ltuTemplates: ListBuffer[Array[Float]], dm:DataModel) (implicit t:ClassTag[HEAD]) extends AbstractInferenceSolver {
  val a=factors
  val dataM=dm
  override def getBestStructure(weight: WeightVector, ins: IInstance): IStructure = {

    val myIns = ins.asInstanceOf[Saul_SL_Instance[HEAD]]
    var myStruct: Saul_SL_Label_Structure[HEAD] = new Saul_SL_Label_Structure[HEAD](factors.toList, myIns.head)
    for (i <- 0 until factors.size) {
      val c = factors(i)
      val candis: Seq[_] = c.getCandidates(myIns.head)
      candis.foreach(x =>
        myStruct.labels += factors(i).classifier.discreteValue(x))
    }
    myStruct
  }

  override def getLoss(ins: IInstance, gold: IStructure, pred: IStructure): Float = { //TODO check
    var TotalLoss: Float = 0
    val myGold = gold.asInstanceOf[Saul_SL_Label_Structure[HEAD]]
    val myPred = pred.asInstanceOf[Saul_SL_Label_Structure[HEAD]]
    var count = 0
    a.foreach {
      x=>
        var localLoss=0
        val oracle: Classifier = x.onClassifier.getLabeler()
        val candidates= x.getCandidates(ins.asInstanceOf[Saul_SL_Instance[HEAD]].head)
        candidates.foreach{
          ci =>
            if (myGold.labels(count)!=(myPred.labels(count)))
               localLoss = localLoss+1
            count=count+1
             }
        if (candidates.size!=0)
         TotalLoss=TotalLoss+localLoss/candidates.size
   }
    TotalLoss=TotalLoss/factors.size

    TotalLoss
  }

  override def getLossAugmentedBestStructure(weight: WeightVector, ins: IInstance, goldStructure: IStructure): IStructure = {

    val myIns = ins.asInstanceOf[Saul_SL_Instance[HEAD]]
    val myStruct: Saul_SL_Label_Structure[HEAD] = new Saul_SL_Label_Structure[HEAD](factors.toList, myIns.head)
    val FactorsNum=a.size
    var ltu_count = 0
  a.foreach {
    cf=>
        for (i <- 0 until cf.onClassifier.asInstanceOf[SparseNetworkLBP].net.size())
        {
          val w1= ltuTemplates(ltu_count)//cf.onClassifier.asInstanceOf[SparseNetworkLBP].net.get(i).asInstanceOf[LinearThresholdUnit].getParameters.asInstanceOf[LinearThresholdUnit.Parameters].weightVector
          print("w1 size\t", w1.size)
          val myFactorJoinlyTrainedWeight= weight.getWeightArray.slice(i,w1.size)
          cf.onClassifier.asInstanceOf[SparseNetworkLBP].net.get(i).asInstanceOf[LinearThresholdUnit].getParameters.asInstanceOf[LinearThresholdUnit.Parameters].weightVector= new SparseWeightVector(Utils.converFarrayToD(myFactorJoinlyTrainedWeight))
        }
      cf.onClassifier.setLossFlag()
      cf.onClassifier.setCandidates(cf.getCandidates(myIns.head).size* FactorsNum)
  println()
  }

/// val newFactors=List[ConstrainedClassifier[_,HEAD]]
//  a.foreach {
//    cf =>
//
//       myStruct.labels ++= cf.lossAugmentedInfer(myIns.head.asInstanceOf[HEAD],cf.getCandidates(myIns.head).size*FactorsNum)
//  }

  a.foreach {
    cf=>
       cf.getCandidates(myIns.head).foreach {
       x =>
       myStruct.labels += cf.classifier.discreteValue(x)
    }
  }
    a.map (x=> x.onClassifier.unsetLossFlag())
    myStruct
  }
    }
