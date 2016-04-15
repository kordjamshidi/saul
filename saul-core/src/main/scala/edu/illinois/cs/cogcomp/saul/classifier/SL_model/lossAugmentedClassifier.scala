package edu.illinois.cs.cogcomp.saul.classifier.SL_model
import java.io.PrintStream

import edu.illinois.cs.cogcomp.lbjava.classify.{FeatureVector, ScoreSet}
import edu.illinois.cs.cogcomp.lbjava.learn.Learner
import edu.illinois.cs.cogcomp.saul.classifier.SparseNetworkLBP

import scala.reflect.ClassTag

/**
 * Created by Parisa on 4/1/16.
 */
class lossAugmentedClassifier[T<:AnyRef](c:Learner, cand_num: Int=1)(implicit t:ClassTag[T] ) extends Learner("lossAugmentedClassifier") {
    override def getInputType: String = { "dummy"}

    override def allowableValues: Array[String] = c.allowableValues()//{ Array[String]("false", "true") }

    override def equals(o: Any): Boolean = { getClass == o.getClass }

    /** The reason for true to be -1 is because the internal optimization by default finds the maximizer, while in this
      * problem we are looking for a minimizer
      */
    override def scores(example: AnyRef): ScoreSet = {
       if (cand_num==0)
          print("There is no relevant component of this type in the head to be classified.")
       val cf= c.asInstanceOf[SparseNetworkLBP]
       val gold = cf.getLabeler.discreteValue(example)
       val lLexicon = cf.getLabelLexicon
       val resultS: ScoreSet = c.scores(example)//new ScoreSet
       for (i <- 0 until lLexicon.size()) {
         if (lLexicon.lookupKey(i).valueEquals(gold))
           resultS.put(lLexicon.lookupKey(i).getStringValue, resultS.getScore(lLexicon.lookupKey(i).getStringValue).score - (1/(cand_num)) )
         else
           resultS.put(lLexicon.lookupKey(i).getStringValue, resultS.getScore(lLexicon.lookupKey(i).getStringValue).score + (1/(cand_num)) )
       }
      resultS
    }

    override def write(printStream: PrintStream): Unit = ???

    override def scores(ints: Array[Int], doubles: Array[Double]): ScoreSet = ???

    override def classify(ints: Array[Int], doubles: Array[Double]): FeatureVector = ???

    override def learn(ints: Array[Int], doubles: Array[Double], ints1: Array[Int], doubles1: Array[Double]): Unit = ???
  }
