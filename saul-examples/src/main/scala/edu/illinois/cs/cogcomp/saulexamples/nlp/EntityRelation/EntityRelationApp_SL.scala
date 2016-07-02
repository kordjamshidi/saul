package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saul.classifier.SL_model._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel._
/** Created by Parisa on 12/8/15.
  */
object EntityRelationApp_SL extends App {

  EntityRelationDataModel.populateWithConllSmallSet()

  ClassifierUtils.LoadClassifier(EntityRelationApp.jarModelPath, PersonClassifier, OrganizationClassifier, LocationClassifier, WorksForClassifier, LivesInClassifier)

  val cls = List(PerConstrainedClassifier, OrgConstrainedClassifier, LocConstrainedClassifier,
    LivesIn_PerOrg_relationConstrainedClassifier, WorksFor_PerOrg_ConstrainedClassifier)

  ClassifierUtils.TestClassifiers(cls: _*)

  val m = StructuredLearning(pairs, cls, initialize = false)
  println("Structured evaluation.\n")
  StructuredLearning.Evaluate(pairs, cls, m, "")
  //ClassifierUtils.TestClassifiers(cls:_*)
}

