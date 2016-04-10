package edu.illinois.cs.cogcomp.saul.classifier.SL_model
import edu.illinois.cs.cogcomp.lbjava.learn.LinearThresholdUnit
import edu.illinois.cs.cogcomp.saul.classifier.SparseNetworkLBP
import edu.illinois.cs.cogcomp.sl.core.SLProblem
import edu.illinois.cs.cogcomp.sl.util.WeightVector

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
 * Created by Parisa on 4/1/16.
 */
object InitializeSL{


  def apply[HEAD<:AnyRef](sp:SLProblem , model:SaulSLModel[HEAD]): SaulSLModel[HEAD] =  {

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
    var wvLength=0
    var lt: ListBuffer[Array[Float]] = ListBuffer()
    model.Factors.foreach(
      x => {

        val sparseNet= x.onClassifier.asInstanceOf[SparseNetworkLBP]
        val temp= (sparseNet.getLexicon.size())

        for (i <- 0 until sparseNet.net.size()) {
          val t=  Array.fill[Float](temp)(0)
          lt= lt:+ t
          wvLength = wvLength + temp
        }
        println(sparseNet.getLexicon.size(), "*",sparseNet.getLabelLexicon.size())
      })
    val myWeight = Array.fill[Float](wvLength)(0)
    val wv= new WeightVector(myWeight)
    val m= new SaulSLModel[HEAD](model.Factors,lt)
    m
  }//end f apply
}// end of object
