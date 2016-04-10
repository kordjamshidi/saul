package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.sl.core.SLModel

import scala.collection.mutable.ListBuffer

/**
 * Created by Parisa on 4/1/16.
 */
class SaulSLModel[HEAD<: AnyRef](cls: List[ConstrainedClassifier[_<:AnyRef,HEAD]], listBuffer: ListBuffer[Array[Float]]=ListBuffer()) extends SLModel {
  var Factors: List[ConstrainedClassifier[_<:AnyRef,HEAD]]= cls
  var LtuTemplates: ListBuffer[Array[Float]]= listBuffer

}
