package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.sl.core.IInstance
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector

import scala.collection.mutable.ListBuffer

/** Created by Parisa on 12/10/15.
  * Here we only make the lbjava lexicons for each onClassifier
  * (i.e. the base classifier of each constraint classifier) based on the features of IInstances
  */
case class Saul_SL_Instance[HEAD <: AnyRef](l: List[ConstrainedClassifier[_, HEAD]], x: HEAD) extends IInstance {

  // val inputFeatures:ListBuffer[Array[Object]]= ListBuffer() // List<Object[]>
  var factorClassifiers: ListBuffer[ConstrainedClassifier[_, HEAD]] = ListBuffer()
  //List<ConstrainedClassifier>
  var fv: IFeatureVector = null;
  val head: HEAD = x
  def apply = {
    l.foreach {
      (c: ConstrainedClassifier[_, HEAD]) =>
        //val oracle: Classifier = c.onClassifier.getLabeler()
        val cands: Seq[_] = c.getCandidates(x)
        for (ci <- cands) {
          // c.classifier.discreteValue(ci) //prediction result
          // oracle.discreteValue(ci) // true lable
          // return a Feature values and indexs
          val t = c.onClassifier.getExampleArray(ci, true)
          //   val l: java.util.List[String]= c.onClassifier.getCurrentLexicon.getMap.keys.map(x=> x.toString).toList
          // inputFeatures.add(t)

          //  print("")
          // fv = SUtils.makeFeatures(l).toFeatureVector();

        } // yield inputFeatures
        //                    val a0 = a(0).asInstanceOf[Array[Int]]
        factorClassifiers += (c) //TODO probably we need to remove this, it seems to be redundant
      //                    val a1 = a(1).asInstanceOf[Array[Double]]
    }
  }
}

