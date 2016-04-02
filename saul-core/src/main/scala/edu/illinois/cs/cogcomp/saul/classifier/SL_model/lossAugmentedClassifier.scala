package edu.illinois.cs.cogcomp.saul.classifier.SL_model
import java.io.PrintStream

import edu.illinois.cs.cogcomp.lbjava.classify.{FeatureVector, ScoreSet}
import edu.illinois.cs.cogcomp.lbjava.learn.Learner

/**
 * Created by Parisa on 4/1/16.
 */
class lossAugmentedClassifier[T](c:Learner) extends Learner("lossAugmentedClassifier") {
    override def getInputType: String = { "dummy"}

    override def allowableValues: Array[String] = c.allowableValues()//{ Array[String]("false", "true") }

    override def equals(o: Any): Boolean = { getClass == o.getClass }

    /** The reason for true to be -1 is because the internal optimization by default finds the maximizer, while in this
      * problem we are looking for a minimizer
      */
    override def scores(example: AnyRef): ScoreSet = {
      val result: ScoreSet = c.scores(example)//new ScoreSet
      result
    }

    override def write(printStream: PrintStream): Unit = ???

    override def scores(ints: Array[Int], doubles: Array[Double]): ScoreSet = ???

    override def classify(ints: Array[Int], doubles: Array[Double]): FeatureVector = ???

    override def learn(ints: Array[Int], doubles: Array[Double], ints1: Array[Int], doubles1: Array[Double]): Unit = ???
  }
