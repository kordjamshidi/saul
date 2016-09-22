/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.lbjava.learn.{ LinearThresholdUnit, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.classifier.infer.InitSparseNetwork
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.sl.util.WeightVector

import scala.collection.mutable.ListBuffer

/** Created by Parisa on 4/1/16.
  * Here we only make the lbjava lexicons for each onClassifier
  * (i.e. the base classifier of each constraint classifier) based on the features of IInstances
  */
object Initialize {

  def apply[HEAD <: AnyRef](node: Node[HEAD], model: SaulSLModel[HEAD], usePreTrained: Boolean = false): SaulSLModel[HEAD] = {

    var wvLength = 0
    var lt: ListBuffer[Array[Float]] = ListBuffer()

    /*this means we are not reading any model into the SparseNetworks but
     we forget all the models and go over the data to build the right size
     for the lexicon and the right number of the ltu s*/

    if (!usePreTrained)
      model.Factors.foreach {
        cf => InitSparseNetwork(node, cf)
      }
    /*In this step or we have built the lexicon by going over the data in the above block or
we use the loaded lexicons in the case of uesPreTrained == true, the goal is to build
 a global weight vector using all classifiers and initialize it accordingly to have a fixed size*/

    model.Factors.foreach(
      x => {
        val sparseNet = x.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
        val temp = (sparseNet.getLexicon.size())

        for (i <- 0 until sparseNet.getNetwork.size()) {

          val getTheWeight = x.onClassifier.classifier.asInstanceOf[SparseNetworkLearner].getNetwork.get(i).asInstanceOf[LinearThresholdUnit].getWeightVector
          val t = Array.fill[Float](temp)(0)

          if (usePreTrained) { //if we are going to initialize we get the loaded weights otherwise the weights are filled with zeros

            for (j <- 0 until temp)
              t(j) = getTheWeight.getWeight(j).asInstanceOf[Float]

          }
          lt = lt :+ t

          wvLength = wvLength + temp
        }

        println("lexicon size: " + sparseNet.getLexicon.size(), "* label lexicon size:", sparseNet.getLabelLexicon.size())
      }
    )
    // wv = Concatenate_(over factors)Concatenate_(over ltu) => size(wv)=sum_(over factors)sum_(over ltu)(size(ltu_i))

    val myWeight = Array(lt.flatten: _*)
    val wv = new WeightVector(myWeight) // wv this is one unified weight vector of all initialized LTUs
    val m = new SaulSLModel[HEAD](model.Factors.toList, lt) // lt is the list of individual weight vectors
    m.wv = wv
    m
  } //end f apply
} // end of object
