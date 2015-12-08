package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.RewriteBasicModel.entityRelationClassifiers._

object entityRelationConstraints {

  val Per_Org = ConstrainedClassifier.constraintOf[ConllRelation] {
    x: ConllRelation =>
      {
        (((WorkForClassifier on x) isTrue) ==>
          (((OrgClassifier on x.e2) isTrue) &&&
            ((PersonClassifier on x.e1) isTrue))) &&& (
              ((LivesInClassifier on x) isTrue) ==> (
                ((PersonClassifier on x.e1) isTrue)
                &&& ((LocationClassifier on x.e2) isTrue)
              )
            ) &&& ((WorkForClassifier on x isTrue) ==> (LivesInClassifier on x isNotTrue)) &&& ((LivesInClassifier on x isTrue) ==> (WorkForClassifier on x isNotTrue))
      }
  }

  val LiveInConstrint = ConstrainedClassifier.constraintOf[ConllRelation] {
    x: ConllRelation =>
      {
        ((LivesInClassifier on x) isTrue) ==> (
          ((PersonClassifier on x.e1) isTrue)
          &&& ((LocationClassifier on x.e2) isTrue)
        )
      }
  }

  val PersonWorkFor = ConstrainedClassifier.constraintOf[ConllRelation] {
    x: ConllRelation =>
      {
        ((WorkForClassifier on x) isTrue) ==>
          ((PersonClassifier on x.e1) isTrue)
      }
  }
}
