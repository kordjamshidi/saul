package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.sl.core.IInstance
import scala.collection.JavaConversions._
import scala.reflect.ClassTag

/** Created by Parisa on 12/10/15.
  */
class Saul_SL_Instance[_, HEAD<:AnyRef] extends IInstance {

  val inputFeatures = List() // List<Object[]>
  var factorClassifiers = List() //List<ConstrainedClassifier>

  def apply(l: List[ConstrainedClassifier[_, HEAD]], x: HEAD)(implicit headTag: ClassTag[_])={
    for (c: ConstrainedClassifier[_, HEAD] <- l) {
      val oracle: Classifier = c.onClassifier.getLabeler()
      val cands: Seq[_] = c.getCandidates(x)
      for (ci <- cands) {
        c.classifier.discreteValue(ci) //prediction result
        oracle.discreteValue(ci) // true lable
        // return a Feature values and indexs
        inputFeatures.add(c.onClassifier.getExampleArray(ci))
        factorClassifiers.add(c)
      }
      //                    val a0 = a(0).asInstanceOf[Array[Int]]
      //                    val a1 = a(1).asInstanceOf[Array[Double]]
    }
  }
}

