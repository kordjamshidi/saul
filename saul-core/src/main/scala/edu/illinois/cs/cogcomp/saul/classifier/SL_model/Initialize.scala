package edu.illinois.cs.cogcomp.saul.classifier.SL_model
import edu.illinois.cs.cogcomp.lbjava.learn.LinearThresholdUnit
import edu.illinois.cs.cogcomp.saul.classifier.SparseNetworkLBP
import edu.illinois.cs.cogcomp.sl.core.SLProblem
import edu.illinois.cs.cogcomp.sl.util.WeightVector

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/** Created by Parisa on 4/1/16.
  * Here we only make the lbjava lexicons for each onClassifier
  * (i.e. the base classifier of each constraint classifier) based on the features of IInstances
  */
object Initialize {

  def apply[HEAD <: AnyRef](sp: SLProblem, model: SaulSLModel[HEAD], initialize: Boolean = false): SaulSLModel[HEAD] = {

    var wvLength = 0
    var lt: ListBuffer[Array[Float]] = ListBuffer()

    if (!initialize)
      model.Factors.foreach {
        cf =>
          cf.onClassifier.classifier.forget()
          sp.instanceList.toList.zipWithIndex.foreach {
            case (myIns, ind) => {
              val ins = myIns.asInstanceOf[Saul_SL_Instance[HEAD]]
              val candis: Seq[_] = cf.getCandidates(ins.head)
              val ilearner = cf.onClassifier.classifier.asInstanceOf[SparseNetworkLBP]
              val lLexicon = cf.onClassifier.classifier.getLabelLexicon
              candis.foreach {
                x =>
                  val a = cf.onClassifier.classifier.getExampleArray(x)
                  val a0 = a(0).asInstanceOf[Array[Int]] //exampleFeatures
                  val a1 = a(1).asInstanceOf[Array[Double]] // exampleValues
                  val exampleLabels = a(2).asInstanceOf[Array[Int]]
                  val labelValues = a(3).asInstanceOf[Array[Double]]
                  val label = exampleLabels(0)
                  var N = ilearner.getNetwork.size();
                  if (label >= N || ilearner.getNetwork.get(label) == null) {
                    ilearner.iConjuctiveLables = ilearner.iConjuctiveLables | ilearner.getLabelLexicon.lookupKey(label).isConjunctive();
                    var ltu: LinearThresholdUnit = ilearner.getbaseLTU
                    ltu.initialize(ilearner.getnumExamples, ilearner.getnumFeatures);
                    ilearner.getNetwork.set(label, ltu);
                    print("weight vector size:" + ilearner.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getParameters.asInstanceOf[LinearThresholdUnit.Parameters].weightVector.size())
                    println("lexicon size:" + ilearner.getLexicon.size())

                    N = label + 1;
                  }
              } // for each candidate
            } // for each constraintFactor
          } // for each example
      } //for each factor

    model.Factors.foreach(
      x => {

        val sparseNet = x.onClassifier.classifier.asInstanceOf[SparseNetworkLBP]
        val temp = (sparseNet.getLexicon.size())

        for (i <- 0 until sparseNet.getNetwork.size()) {
          val t = Array.fill[Float](temp)(0)
          if (initialize) {
            val getTheWeight = x.onClassifier.classifier.asInstanceOf[SparseNetworkLBP].getNetwork.get(i).asInstanceOf[LinearThresholdUnit].getWeightVector
            for (j <- 0 until temp) {

              t(j) = getTheWeight.getWeight(j).asInstanceOf[Float]
              // if (t(j)==0) {print(j," zero/")}
            }
          }

          lt = lt :+ t
          wvLength = wvLength + temp
        }

        println(sparseNet.getLexicon.size(), "*", sparseNet.getLabelLexicon.size())
      }
    )
    val myWeight = Array(lt.flatten: _*)
    val wv = new WeightVector(myWeight) // wv this is one unified weight vector of all initialized LTUs
    val m = new SaulSLModel[HEAD](model.Factors.toList, lt) // lt is the list of individual weight vectors
    m.wv = wv
    m
  } //end f apply
} // end of object
