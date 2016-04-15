package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.lbjava.infer.GurobiHook
import edu.illinois.cs.cogcomp.lbjava.learn.{LinearThresholdUnit, SparseWeightVector}
import edu.illinois.cs.cogcomp.saul.classifier.{ConstrainedClassifier, SparseNetworkLBP}
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.sl.core.{AbstractInferenceSolver, IInstance, IStructure}
import edu.illinois.cs.cogcomp.sl.util.WeightVector

import scala.reflect.ClassTag

/** Created by Parisa on 12/8/15.
  */
class Saul_SL_Inference[HEAD <: AnyRef](factors: List[ConstrainedClassifier[_<:AnyRef,HEAD]], dm:DataModel) (implicit t:ClassTag[HEAD]) extends AbstractInferenceSolver {
  val a=factors
  a.head.tType
  val dataM=dm
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
        TotalLoss=TotalLoss+localLoss/candidates.size
   }
    TotalLoss=TotalLoss/factors.size

    TotalLoss
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
  val FactorsNum=a.size
/// val newFactors=List[ConstrainedClassifier[_,HEAD]]
  a.foreach {
    cf =>
      val tempclassifier = new lossAugmentedClassifier(cf.onClassifier, cf.getCandidates(myIns.head).size*FactorsNum)
      cf.getCandidates(myIns.head.asInstanceOf[HEAD]).foreach {
        (example )=>

      //  val  tic=example.asInstanceOf[cf.LEFT]
        val  g1= tempclassifier.scores(example)

         // type t= cf.LEFT
         // type h = HEAD
          val temp1=  t.runtimeClass

         // val b= example.asInstanceOf[cf.LEFT]
          val g2=  cf.onClassifier.scores(example)

         // cf.subjectTo.createInferenceCondition[cf.LEFT](dm, new GurobiHook())
         // cf(example)//.onClassifier.
         //cf.buildWithConstraint(cf.subjectTo.createInferenceCondition(dm, new GurobiHook()))
        //  x: T => buildWithConstraint(subjectTo.createInferenceCondition[T](this.dm, getSolverInstance()).convertToType[T], onClassifier)(x)
      myStruct.labels +=
      cf.buildWithConstraint(cf.subjectTo.createInferenceCondition(dm, new GurobiHook()), tempclassifier)(example.asInstanceOf[cf.LEFT])
         // cf.buildWithConstraint(cf.subjectTo.createInferenceCondition[AnyRef](dm,new GurobiHook()),new lossAugmentedClassifier[cf.LEFT](cf.onClassifier))
//      x.buildWithConstraint(x.subjectTo.createInferenceCondition[_](dm, x.getSolverInstance()).convertToType[_], new lossAugmentedClassifier[_](x.onClassifier))
    //x.buildWithConstraint(x.subjectTo.createInferenceCondition(dm,))
    //  val a = new ConstrainedClassifier[_,HEAD](dm,new lossAugmentedClassifier[_](x.onClassifier)){ def subjectTo = null} //EntityRelationConstraints.relationArgumentConstraints} {}
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
