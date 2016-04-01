package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS3

import edu.illinois.cs.cogcomp.saul.classifier.SL_model.JoinSLtrain
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationConstrainedClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationDataModel

/** Created by Parisa on 12/8/15.
  */
object mySL_Saul_TestApp extends App {
  EntityRelationDataModel.populateWithConll()
  JoinSLtrain(EntityRelationDataModel, List(perConstraintClassifier,orgConstraintClassifier, locConstraintClassifier,liveIn_P_O_relationClassifier,work_P_O_relationClassifier))
}
