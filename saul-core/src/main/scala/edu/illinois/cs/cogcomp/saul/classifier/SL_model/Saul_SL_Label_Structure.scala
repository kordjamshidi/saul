package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.sl.core.IStructure

import scala.collection.JavaConversions._
/**
 * Created by Parisa on 12/10/15.
 */
class Saul_SL_Label_Structure [_,HEAD] extends IStructure{

  var labels : List[String] = null

  def Saul_SL_Label_java_Structure(l: List[ConstrainedClassifier[_, HEAD]] , obj: HEAD){

    l.foreach( (c:ConstrainedClassifier[_,HEAD]) => {
      labels.add(c.onClassifier.discreteValue())
    })
    }
}

