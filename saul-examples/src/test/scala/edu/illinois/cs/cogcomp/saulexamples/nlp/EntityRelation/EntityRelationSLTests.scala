package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saul.classifier.SL_model._
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel._
import edu.illinois.cs.cogcomp.sl.core.SLParameters
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory
import org.scalatest.{ FlatSpec, Matchers }

/** Created by Parisa on 6/10/16.
  */
class EntityRelationSLTests extends FlatSpec with Matchers {

  populateWithConllSmallSet()

  val cls = List(PerConstrainedClassifier, OrgConstrainedClassifier, LocConstrainedClassifier, LivesIn_PerOrg_relationConstrainedClassifier, WorksFor_PerOrg_ConstrainedClassifier)

  val SLProblem = SL_IOManager.makeSLProblem(pairs, cls)
  "Structured output learning (SL)" should "get correct number of instances." in {
    SLProblem.goldStructureList.size() should be(24)
    SLProblem.instanceList.size() should be(24)
  }

  val model = Initialize(SLProblem, new SaulSLModel(cls))
  "Structured output learning (SL) initialization" should "work." in {
    model.Factors.size should be(5)
    model.LTUWeightTemplates.size should be(10)
    model.wv.getLength should be(1144)
    model.wv.getWeightArray.filter(p => (p > 0.00)).isEmpty should be(true)
  }

  val xGold = SLProblem.instanceList.get(0)
  val yGold = SLProblem.goldStructureList.get(0)
  model.featureGenerator = new SL_FeatureGenerator(model)

  "Structured output learning (SL) Feature extraction" should "work." in {
    model.featureGenerator.getFeatureVector(xGold, yGold).getValues.sum should be(96.0)
  }

  val para = new SLParameters
  para.loadConfigFile("./config/DCD.config")
  model.para = para
  model.infSolver = new Saul_SL_Inference[ConllRelation](model.Factors.toList, model.LTUWeightTemplates, node)
  val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para)

  "Structured output learning's loss" should " be calculate correctly." in {

    val yTest = new Saul_SL_Label_Structure[ConllRelation](cls, yGold.asInstanceOf[Saul_SL_Label_Structure[ConllRelation]].head.asInstanceOf[ConllRelation])
    yTest.labels = for (
      l <- yGold.asInstanceOf[Saul_SL_Label_Structure[ConllRelation]].labels
    ) yield "true"

    model.infSolver.getLoss(xGold, yGold, yGold) should be(0.00)
    model.infSolver.getLoss(xGold, yGold, yTest) >= (0.8) should be(true)
  }

  val weight = learner.train(SLProblem, model.wv)
  "Structured output learning" should " have a correctly working inference module." in {
    val yPredicted = model.infSolver.getBestStructure(model.wv, xGold)
    val yMostViolated = model.infSolver.getLossAugmentedBestStructure(model.wv, xGold, yGold)
    model.infSolver.getLoss(xGold, yGold, yPredicted) should be(0.00)
    model.infSolver.getLoss(xGold, yPredicted, yMostViolated) should be(0.00)
    (yPredicted.asInstanceOf[Saul_SL_Label_Structure[ConllRelation]].equals(yMostViolated.
      asInstanceOf[Saul_SL_Label_Structure[ConllRelation]])) should be(true)
  }

  "Structured output learning (SL)" should "really work in Saul." in {

  }
}
