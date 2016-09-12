/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier
import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.sl.core.IStructure

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/** Created by Parisa on 12/10/15.
  */
class Saul_SL_Label_Structure[HEAD <: AnyRef](l: List[ConstrainedClassifier[_, HEAD]], x: HEAD) extends IStructure {

  var labels: ListBuffer[String] = ListBuffer()
  val head = x
  l.foreach { (c: ConstrainedClassifier[_, HEAD]) =>
    {
      val oracle: Classifier = c.onClassifier.classifier.getLabeler()
      val candis: Seq[_] = c.getCandidates(x)
      candis.foreach {
        ci =>
          labels.add(oracle.discreteValue(ci))

      }
    }
  }
  def equals(a: Saul_SL_Label_Structure[HEAD]): Boolean = {
    a.labels.equals(labels)
  }
}