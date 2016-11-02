package edu.illinois.cs.cogcomp.saul.classifier

/** Created by Parisa on 11/2/16.
  */

import edu.illinois.cs.cogcomp.lbjava.learn.{ LinearThresholdUnit, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import org.slf4j.{ Logger, LoggerFactory }

import scala.reflect.ClassTag

object SimpleSparseNetwork {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  var difference = 0
  def apply[HEAD <: AnyRef](node: Node[HEAD], cls: Learnable[HEAD], init: Boolean)(implicit headTag: ClassTag[HEAD]) = {
    train[HEAD](node, cls, 1, init)
  }

  def apply[HEAD <: AnyRef](node: Node[HEAD], cls: Learnable[HEAD], it: Int, init: Boolean)(implicit headTag: ClassTag[HEAD]) = {
    train[HEAD](node, cls, it, init)
  }

  @scala.annotation.tailrec
  def train[HEAD <: AnyRef](node: Node[HEAD], currentClassifier: Learnable[HEAD], it: Int, init: Boolean)(implicit headTag: ClassTag[HEAD]): Unit = {
    // forall members in collection of the head (dm.t) do
    logger.info("Training iteration: " + it)
    if (it == 0) {
      // Done
      println("difference=", difference)
    } else {
      val allHeads = node.getTrainingInstances
      difference = 0
      allHeads.zipWithIndex.foreach {
        case (candidate, idx) =>

          //  if (idx % 5000 == 0)
              logger.info(s"Training: $idx examples inferred.")
              val oracle = currentClassifier.getLabeler
              val baseClassifier = currentClassifier.classifier.asInstanceOf[SparseNetworkLearner]

              {
                def trainOnce() = {
                  val result = currentClassifier.classifier.discreteValue(candidate)
                  val trueLabel = oracle.discreteValue(candidate)
                  val lLexicon = currentClassifier.getLabelLexicon
                  var LTU_actual: Int = 0
                  var LTU_predicted: Int = 0
                  for (i <- 0 until lLexicon.size()) {
                    if (lLexicon.lookupKey(i).valueEquals(result))
                      LTU_predicted = i
                    if (lLexicon.lookupKey(i).valueEquals(trueLabel))
                      LTU_actual = i
                  }

                  // The idea is that when the prediction is wrong the LTU of the actual class should be promoted
                  // and the LTU of the predicted class should be demoted.
                  if (!result.equals(trueLabel)) //equals("true") && trueLabel.equals("false")   )
                  {
                    val a = currentClassifier.getExampleArray(candidate,true)
                    val a0 = a(0).asInstanceOf[Array[Int]] //exampleFeatures
                    val a1 = a(1).asInstanceOf[Array[Double]] // exampleValues
                    val exampleLabels = a(2).asInstanceOf[Array[Int]]
                    val label = exampleLabels(0)
                    var N = baseClassifier.getNetwork.size

                    if (label >= N || baseClassifier.getNetwork.get(label) == null) {
                      val conjugateLabels = baseClassifier.isUsingConjunctiveLabels | baseClassifier.getLabelLexicon.lookupKey(label).isConjunctive
                      baseClassifier.setConjunctiveLabels(conjugateLabels)

                      val ltu: LinearThresholdUnit = baseClassifier.getBaseLTU
                      ltu.initialize(baseClassifier.getNumExamples, baseClassifier.getNumFeatures)
                      baseClassifier.getNetwork.set(label, ltu)
                      N = label + 1
                    }

                    // test push
                    val ltu_actual = baseClassifier.getLTU(LTU_actual).asInstanceOf[LinearThresholdUnit]
                    val ltu_predicted = baseClassifier.getLTU(LTU_predicted).asInstanceOf[LinearThresholdUnit]

                    if (ltu_actual != null)
                      ltu_actual.promote(a0, a1, 0.1)
                    if (ltu_predicted != null)
                      ltu_predicted.demote(a0, a1, 0.1)
                  }
                }

                trainOnce()
              }
          }
      train(node, currentClassifier, it - 1, false)
      }

    }

}

