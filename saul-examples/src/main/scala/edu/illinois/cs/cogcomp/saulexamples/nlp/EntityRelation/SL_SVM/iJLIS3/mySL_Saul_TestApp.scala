package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.SL_SVM.iJLIS3

import edu.illinois.cs.cogcomp.saul.classifier.SL_model.JoinSLtrain
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel

/** Created by Parisa on 12/8/15.
  */
object mySL_Saul_TestApp extends App {

  EntityRelationDataModel.populateWithConllSmallSet()
  JoinSLtrain(EntityRelationDataModel, List(PerConstrainedClassifier,OrgConstrainedClassifier, LocConstrainedClassifier,LivesIn_PerOrg_relationConstrainedClassifier,WorksFor_PerOrg_ConstrainedClassifier))

  /* Test SL_ER */

  PerConstrainedClassifier.test()
  OrgConstrainedClassifier.test()
  LocConstrainedClassifier.test()
  LivesIn_PerOrg_relationConstrainedClassifier.test()
  WorksFor_PerOrg_ConstrainedClassifier.test()
}
