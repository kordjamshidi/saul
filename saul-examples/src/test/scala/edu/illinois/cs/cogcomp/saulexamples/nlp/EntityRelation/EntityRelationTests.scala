package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saul.classifier.SL_model._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel._
import org.scalatest._

class EntityRelationTests extends FlatSpec with Matchers {
  val minScore = 0.3
  populateWithConllSmallSet()
  val cls = List(PerConstrainedClassifier, OrgConstrainedClassifier, LocConstrainedClassifier, LivesIn_PerOrg_relationConstrainedClassifier, WorksFor_PerOrg_ConstrainedClassifier)

  "entity classifier " should " should work. " in {
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      PersonClassifier, OrganizationClassifier, LocationClassifier
    )
    val scores = PersonClassifier.test() ++ OrganizationClassifier.test() ++ LocationClassifier.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }

  "independent relation classifier " should " should work. " in {
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier
    )
    val scores = WorksForClassifier.test() ++ LivesInClassifier.test() ++
      LocatedInClassifier.test() ++ OrgBasedInClassifier.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }

  "pipeline relation classifiers " should " should work. " in {
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifierPipeline, LivesInClassifierPipeline
    )
    val scores = WorksForClassifierPipeline.test() ++ LivesInClassifierPipeline.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }

  "L+I entity-relation classifiers " should " work. " in {
    ClassifierUtils.LoadClassifier(
      EntityRelationApp.jarModelPath,
      PersonClassifier, OrganizationClassifier, LocationClassifier,
      WorksForClassifier, LivesInClassifier, LocatedInClassifier, OrgBasedInClassifier
    )
    val scores = PerConstrainedClassifier.test() ++ WorksFor_PerOrg_ConstrainedClassifier.test()
    scores.foreach { case (label, score) => (score._1 > minScore) should be(true) }
  }
  val SLProblem = SL_IOManager.makeSLProblem(pairs, cls)
  "Structured output learning (SL)" should "get correct number of instances." in {
    SLProblem.goldStructureList should be(24)
    SLProblem.instanceList should be(24)
  }

  val model = Initialize(SLProblem, new SaulSLModel(cls))
  "Structured output learning (SL)" should "initialize correctly." in {
    model.Factors.size should be(5)
    model.LTUWeightTemplates should be(10)
    model.wv.getLength should be(1144)
  }

  model.featureGenerator = new SL_FeatureGenerator(model)
  "Structured output learning (SL)" should "do feature extraction correctly." in {
    model.featureGenerator.getFeatureVector(SLProblem.instanceList.get(0), SLProblem.goldStructureList.get(0)).getValues.sum should be(96.0)
  }

  "Structured output learning (SL)" should "really work in Saul." in {
    val M = JoinSLtrain(pairs, cls)
    M.featureGenerator.getFeatureVector(SLProblem.instanceList.get(0), SLProblem.goldStructureList.get(0))
  }
}