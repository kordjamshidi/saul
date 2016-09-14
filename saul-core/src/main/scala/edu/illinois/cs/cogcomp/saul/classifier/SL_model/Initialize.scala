/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.lbjava.learn.{ LinearThresholdUnit, SparseNetworkLearner }
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
          val ilearner = cf.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
          val lLexicon = cf.onClassifier.classifier.getLabelLexicon
          sp.instanceList.toList.zipWithIndex.foreach {

            case (myIns, ind) => {
              val ins = myIns.asInstanceOf[Saul_SL_Instance[HEAD]]
              val candis: Seq[_] = cf.getCandidates(ins.head)
              candis.foreach {
                x =>
                  val a = cf.onClassifier.classifier.getExampleArray(x)
                  val a0 = a(0).asInstanceOf[Array[Int]] //exampleFeatures
                  val a1 = a(1).asInstanceOf[Array[Double]] // exampleValues
                  val exampleLabels = a(2).asInstanceOf[Array[Int]]
                  val labelValues = a(3).asInstanceOf[Array[Double]]
                  val label = exampleLabels(0)
                  var N = ilearner.getNetwork.size()
                  if (label >= N || ilearner.getNetwork.get(label) == null) {
                    val isConjunctiveLabels = ilearner.isUsingConjunctiveLabels | ilearner.getLabelLexicon.lookupKey(label).isConjunctive
                    ilearner.setConjunctiveLabels(isConjunctiveLabels)
                    val ltu: LinearThresholdUnit = ilearner.getBaseLTU
                    ltu.initialize(ilearner.getNumExamples, ilearner.getNumFeatures)
                    ilearner.getNetwork.set(label, ltu)
                    N = label + 1
                  }
              } // for each candidate
            } // for each constraintFactor
          } // for each example
        //   print("weight vector size:" + ilearner.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getParameters.asInstanceOf[LinearThresholdUnit.Parameters].weightVector.size())
        //   println("lexicon size:" + ilearner.getLexicon.size())

      } //for each factor

    model.Factors.foreach(
      x => {

        val sparseNet = x.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
        val temp = (sparseNet.getLexicon.size())

        for (i <- 0 until sparseNet.getNetwork.size()) {

          val getTheWeight = x.onClassifier.classifier.asInstanceOf[SparseNetworkLearner].getNetwork.get(i).asInstanceOf[LinearThresholdUnit].getWeightVector
          val t = Array.fill[Float](temp)(0)

          if (initialize) {

            for (j <- 0 until temp)
              t(j) = getTheWeight.getWeight(j).asInstanceOf[Float]

          }
          lt = lt :+ t

          wvLength = wvLength + temp
        }

        println("lexicon size: " + sparseNet.getLexicon.size(), "* label lexicon size:", sparseNet.getLabelLexicon.size())
      }
    )
    val myWeight = Array(lt.flatten: _*)
    val wv = new WeightVector(myWeight) // wv this is one unified weight vector of all initialized LTUs
    val m = new SaulSLModel[HEAD](model.Factors.toList, lt) // lt is the list of individual weight vectors
    m.wv = wv
    m
  } //end f apply
} // end of object
