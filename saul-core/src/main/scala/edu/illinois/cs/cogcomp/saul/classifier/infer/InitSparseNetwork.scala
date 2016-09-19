package edu.illinois.cs.cogcomp.saul.classifier.infer

import edu.illinois.cs.cogcomp.lbjava.learn.{ LinearThresholdUnit, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node

/** Created by Parisa on 9/18/16.
  */
object InitSparseNetwork {
  def apply[HEAD <: AnyRef](node: Node[HEAD], cf: ConstrainedClassifier[_, HEAD]) = {
    val allHeads = node.getTrainingInstances
    //this means we are not reading any model into the SparseNetworks but we forget all the models and go over the data to build the right size for the lexicon and the right number of the ltu s
    cf.onClassifier.classifier.forget()
    val ilearner = cf.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
    val lLexicon = cf.onClassifier.classifier.getLabelLexicon
    allHeads.zipWithIndex.foreach {
      case (myIns, ind) => {
        val ins = myIns.asInstanceOf[HEAD]
        val candis: Seq[_] = cf.getCandidates(ins)
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
      } // end case
    } // for each example
    //   print("weight vector size:" + ilearner.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getParameters.asInstanceOf[LinearThresholdUnit.Parameters].weightVector.size())
    //   println("lexicon size:" + ilearner.getLexicon.size())
  } //end f apply
} // end of object