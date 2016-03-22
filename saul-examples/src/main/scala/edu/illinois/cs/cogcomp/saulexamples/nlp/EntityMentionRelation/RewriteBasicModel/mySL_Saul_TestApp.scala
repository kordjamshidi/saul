package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel
import edu.illinois.cs.cogcomp.saul.classifier.SL_model.JoinSLtrain
//import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.IndependentTraining._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationConstraintClassifiers.PerConstraintClassifier

/** Created by Parisa on 12/8/15.
  */
object mySL_Saul_TestApp extends App {
  //populate_ER_graph
  JoinSLtrain(entityRelationBasicDataModel, List(PerConstraintClassifier))

}
