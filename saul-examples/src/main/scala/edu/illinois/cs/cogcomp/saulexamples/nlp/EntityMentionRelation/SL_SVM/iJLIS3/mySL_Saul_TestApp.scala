package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS3

import edu.illinois.cs.cogcomp.saul.classifier.SL_model.JoinSLtrain
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationConstrainedClassifiers.perConstraintClassifier
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationDataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.EntityRelationClassifiers.personClassifier
/** Created by Parisa on 12/8/15.
  */
object mySL_Saul_TestApp extends App {
  EntityRelationDataModel.populateWithConll()
  personClassifier.learn(1)
  JoinSLtrain(EntityRelationDataModel, List(perConstraintClassifier))
}
