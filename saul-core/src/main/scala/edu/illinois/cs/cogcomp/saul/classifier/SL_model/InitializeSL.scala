  package edu.illinois.cs.cogcomp.saul.classifier.SL_model

  import edu.illinois.cs.cogcomp.lbjava.learn.LinearThresholdUnit
  import edu.illinois.cs.cogcomp.saul.classifier.SparseNetworkLBP
  import edu.illinois.cs.cogcomp.sl.core.SLProblem
  import edu.illinois.cs.cogcomp.sl.util.WeightVector

  import scala.collection.JavaConversions._
  /**
   * Created by Parisa on 4/1/16.
   */
  object InitializeSL{


    def apply[HEAD<:AnyRef](sp:SLProblem , model:SaulSLModel[HEAD]): WeightVector =  {
      var myWeight: Array[Float]= Array()
      model.Factors.foreach{
        cf =>
        sp.instanceList.toList.zipWithIndex.foreach {
        case (myIns,ind) => {
         val ins = myIns.asInstanceOf[Saul_SL_Instance[HEAD]]
         //for (i <- 0 until ins.ConstraintFactors.size) {
           val candis: Seq[_] = cf.getCandidates(ins.head)
           val ilearner = cf.onClassifier.asInstanceOf[SparseNetworkLBP]
           val lLexicon = cf.onClassifier.getLabelLexicon
           candis.foreach {
             x =>
               val a = cf.onClassifier.getExampleArray(x)
               val a0 = a(0).asInstanceOf[Array[Int]] //exampleFeatures
               val a1 = a(1).asInstanceOf[Array[Double]] // exampleValues
               val exampleLabels = a(2).asInstanceOf[Array[Int]]
               val labelValues = a(3).asInstanceOf[Array[Double]]
               val label = exampleLabels(0)
               var N = ilearner.net.size();
               if (label >= N || ilearner.net.get(label) == null) {
                 ilearner.iConjuctiveLables = ilearner.iConjuctiveLables | ilearner.getLabelLexicon.lookupKey(label).isConjunctive();
                 var ltu: LinearThresholdUnit = ilearner.getbaseLTU
                 ltu.initialize(ilearner.getnumExamples, ilearner.getnumFeatures);
                 ilearner.net.set(label, ltu);
                 N = label + 1;
               }
           } // for each candidate
         } // for each constraintFactor
       }//end case
     }//for each example
model.Factors.foreach(

x =>
  for (i <- 0 until x.onClassifier.asInstanceOf[SparseNetworkLBP].net.size())
    {
     val w1= x.onClassifier.asInstanceOf[SparseNetworkLBP].net.get(i).asInstanceOf[LinearThresholdUnit].getParameters.asInstanceOf[LinearThresholdUnit.Parameters].weightVector
      for (j<- 0 until w1.size())
        myWeight +:= w1.getWeight(j).asInstanceOf[Float]
        })
 val wv= new WeightVector(myWeight)
 wv
    }//end of apply

  }// end of object
