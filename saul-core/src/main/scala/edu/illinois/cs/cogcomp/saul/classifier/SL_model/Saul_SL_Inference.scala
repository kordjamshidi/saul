package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.sl.core.{ AbstractInferenceSolver, IInstance, IStructure }
import edu.illinois.cs.cogcomp.sl.util.WeightVector

/** Created by Parisa on 12/8/15.
  */
class Saul_SL_Inference[_,HEAD] extends AbstractInferenceSolver {

  override def getBestStructure(weight: WeightVector, ins: IInstance): IStructure = {
    val myIns = ins.asInstanceOf[Saul_SL_java_Instance[_,HEAD]]
    var myStruct: Saul_SL_Label_java_Structure [_,HEAD]= ???
    for (i <- 0 until myIns.factorClassifiers.size()) {

      myStruct.labels.add(myIns.factorClassifiers.get(i).onClassifier.discreteValue(myIns.inputFeatures.get(i)))
    }
    myStruct
  }

  override def getLoss(ins: IInstance, gold: IStructure, pred: IStructure): Float = {
    var l: Float = 0
    val myGold = gold.asInstanceOf[Saul_SL_Label_java_Structure[_,HEAD]]
    val myPred = pred.asInstanceOf[Saul_SL_Label_java_Structure[_,HEAD]]
    for (i <- 0 until myGold.labels.size()) {
      l = l + myGold.labels.get(i).equals(myPred.labels.get(i)).asInstanceOf[Float]
    }
    l
  }

  override def getLossAugmentedBestStructure(weight: WeightVector, ins: IInstance, goldStructure: IStructure): IStructure = {

    val myIns = ins.asInstanceOf[Saul_SL_java_Instance[HEAD,_]]
    var myStruct: Saul_SL_Label_java_Structure[HEAD,_] = ???
    for (i <- 0 until myIns.factorClassifiers.size()) {

      myStruct.labels.add(myIns.factorClassifiers.get(i).onClassifier.discreteValue(myIns.inputFeatures.get(i)))

    }
    myStruct
  }

}
