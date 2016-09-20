package edu.illinois.cs.cogcomp.saul.classifier.infer

import edu.illinois.cs.cogcomp.lbjava.learn.{ LinearThresholdUnit, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

/** Created by Parisa on 9/18/16.
  */
object InitSparseNetwork {
  def apply[HEAD <: AnyRef](node: Node[HEAD], cClassifier: ConstrainedClassifier[_, HEAD]) = {
    val allHeads = node.getTrainingInstances
    //this means we are not reading any model into the SparseNetworks
    // but we forget all the models and go over the data to build the right
    // size for the lexicon and the right number of the ltu s
    cClassifier.onClassifier.classifier.forget()
    val ilearner = cClassifier.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
    val lLexicon = cClassifier.onClassifier.classifier.getLabelLexicon
    allHeads.foreach {
      myInstance =>
        {
          val head = myInstance.asInstanceOf[HEAD]
          val candidates: Seq[_] = cClassifier.getCandidates(head)
          candidates.foreach {
            x =>
              val a = cClassifier.onClassifier.classifier.getExampleArray(x)
              val exampleLabels = a(2).asInstanceOf[Array[Int]]
              val labelValues = a(3).asInstanceOf[Array[Double]]
              val label = exampleLabels(0)
              val N = ilearner.getNetwork.size()
              if (label >= N || ilearner.getNetwork.get(label) == null) {
                val isConjunctiveLabels = ilearner.isUsingConjunctiveLabels | ilearner.getLabelLexicon.lookupKey(label).isConjunctive
                ilearner.setConjunctiveLabels(isConjunctiveLabels)
                val ltu: LinearThresholdUnit = ilearner.getBaseLTU
                ltu.initialize(ilearner.getNumExamples, ilearner.getNumFeatures)
                ilearner.getNetwork.set(label, ltu)
              }
          } // for each candidate
        } // end case
    } // for each example
  } //end f apply
} // end of object