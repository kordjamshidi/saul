package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.SL_SVM.iJLIS3

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saul.classifier.SL_model.JoinSLtrain
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel

/** Created by Parisa on 12/8/15.
  */
object mySL_Saul_TestApp extends App {

  EntityRelationDataModel.populateWithConllSmallSet()
  JoinSLtrain(EntityRelationDataModel, List(PerConstrainedClassifier,OrgConstrainedClassifier, LocConstrainedClassifier,LivesIn_PerOrg_relationConstrainedClassifier,WorksFor_PerOrg_ConstrainedClassifier))

  /* Test SL_ER */
 println("Independent Classifiers:")
  ClassifierUtils.TrainClassifiers(10,PersonClassifier, OrganizationClassifier, LocationClassifier, WorksForClassifier, LivesInClassifier)
  ClassifierUtils.TestClassifiers(PersonClassifier, OrganizationClassifier, LocationClassifier, WorksForClassifier, LivesInClassifier)
 println("SL Classifiers:")
  PerConstrainedClassifier.test(EntityRelationDataModel.tokens.getTrainingInstances,outputGranularity = 10)
  OrgConstrainedClassifier.test(EntityRelationDataModel.tokens.getTrainingInstances,outputGranularity = 10)
  LocConstrainedClassifier.test(EntityRelationDataModel.tokens.getTrainingInstances,outputGranularity = 10)
  LivesIn_PerOrg_relationConstrainedClassifier.test(EntityRelationDataModel.pairs.getTrainingInstances,outputGranularity = 10)
  WorksFor_PerOrg_ConstrainedClassifier.test(EntityRelationDataModel.pairs.getTrainingInstances,outputGranularity = 10)
}
