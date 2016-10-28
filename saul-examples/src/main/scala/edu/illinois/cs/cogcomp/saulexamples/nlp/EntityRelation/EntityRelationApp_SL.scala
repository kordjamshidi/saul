/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saul.classifier.JointTrainSparseNetwork
import edu.illinois.cs.cogcomp.saul.classifier.SL_model.StructuredLearning
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._

/** Created by Parisa on 12/8/15.
  */
object EntityRelationApp_SL extends App {
  import EntityRelationDataModel._

  EntityRelationDataModel.populateWithConll() //.populateWithConll()

  // ClassifierUtils.LoadClassifier(EntityRelationApp.jarModelPath, PersonClassifier, OrganizationClassifier, LocationClassifier, WorksForClassifier, LivesInClassifier)

  val cls_base = List(PersonClassifier, OrganizationClassifier, LocationClassifier, LivesInClassifier, WorksForClassifier)

  //ClassifierUtils.TrainClassifiers(10, cls_base)
  //ClassifierUtils.TestClassifiers(cls_base: _*)

  val cls = List(PerConstrainedClassifier, OrgConstrainedClassifier, LocConstrainedClassifier,
    LivesIn_PerOrg_relationConstrainedClassifier, WorksFor_PerOrg_ConstrainedClassifier)

  JointTrainSparseNetwork.train[ConllRelation](pairs, cls, 5, true )

  //ClassifierUtils.TestClassifiers(cls: _*)

  val m = StructuredLearning(pairs, cls, usePreTrained = false)

  println("Structured evaluation.\n")

  // StructuredLearning.Evaluate(pairs, cls, m, "")
  //ClassifierUtils.TestClassifiers(cls_base:_*)
}

