package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.sl.core.SLModel

/**
 * Created by Parisa on 4/1/16.
 */
class SaulSLModel[HEAD<: AnyRef](cls: List[ConstrainedClassifier[_,HEAD]]) extends SLModel {
  var Factors: List[ConstrainedClassifier[_,HEAD]]= cls
}
