/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saul.classifier.SL_model._
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel._
import edu.illinois.cs.cogcomp.sl.core.SLParameters
import org.scalatest.{ FlatSpec, Matchers }

/** Created by Parisa on 6/10/16.
  */
class EntityRelationSLTests extends FlatSpec with Matchers {

  /*this lines of setting are used in all the tests below them*/

  populateWithConllSmallSet()

  val cls = List(PerConstrainedClassifier, OrgConstrainedClassifier, LocConstrainedClassifier, LivesIn_PerOrg_relationConstrainedClassifier, WorksFor_PerOrg_ConstrainedClassifier)
  val cls_base = List(PersonClassifier, OrganizationClassifier, LocationClassifier, WorksForClassifier, LivesInClassifier)

  val SLProblem = SL_IOManager.makeSLProblem(pairs, cls)
  val para = new SLParameters
  para.loadConfigFile("../config/DCD.config")

  val xGold = SLProblem.instanceList.get(0)
  val yGold = SLProblem.goldStructureList.get(0)

  /*Tests*/

  "Structured output learning (SL)" should "get correct number of instances." in {
    SLProblem.goldStructureList.size() should be(24)
    SLProblem.instanceList.size() should be(24)
  }

  "Structured output learning (SL) initialization" should "work." in {
    val model = Initialize(pairs, new SaulSLModel(cls), usePreTrained = false)
    model.Factors.size should be(5)
    model.LTUWeightTemplates.size should be(10)
    model.wv.getLength should be(1144)
    model.wv.getWeightArray.filter(p => (p > 0.00)).isEmpty should be(true)
  }
  "Structured output learning (SL) without initialization using trained models" should "work." in {
    ClassifierUtils.LoadClassifier(EntityRelationApp.jarModelPath, cls_base: _*)
    val model = Initialize(pairs, new SaulSLModel(cls), usePreTrained = true)
    model.Factors.size should be(5)
    model.LTUWeightTemplates.size should be(10)
    model.wv.getLength should be(171582)
    (model.wv.getWeightArray.filter(p => (p > 0.00)).length < 20000) should be(true)
    //    model.featureGenerator.getFeatureVector(xGold, yGold).getValues.sum should be(96.0)
  }

  //  val a = sparseNet.getExampleArray(ci, false)
  //  var a0 = a(0).asInstanceOf[Array[Int]]
  //  var a1 = a(1).asInstanceOf[Array[Double]]
  //  val fvTemp = new FeatureVectorBuffer(a0, a1)

  "Structured output learning (SL) Feature extraction" should "work." in {
    //  val model = Initialize(pairs, new SaulSLModel(cls), usePreTrained = false)

  }

  "Structured output learning's loss" should " be calculate correctly." in {
    val model = Initialize(pairs, new SaulSLModel(cls), usePreTrained = false)
    model.para = para
    model.infSolver = new Saul_SL_Inference[ConllRelation](model.Factors.toList, model.LTUWeightTemplates)
    //val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para)

    val yTest = new Saul_SL_Label_Structure[ConllRelation](cls, yGold.asInstanceOf[Saul_SL_Label_Structure[ConllRelation]].head.asInstanceOf[ConllRelation])
    yTest.labels = for (
      l <- yGold.asInstanceOf[Saul_SL_Label_Structure[ConllRelation]].labels
    ) yield "true"

    model.infSolver.getLoss(xGold, yGold, yGold) should be(0.00)
    model.infSolver.getLoss(xGold, yGold, yTest) >= (0.8) should be(true)
  }

  // val weight = learner.train(SLProblem, model.wv)
  "Structured output learning" should " have a correctly working inference module." in {
    //    val model = Initialize(pairs, new SaulSLModel(cls), usePreTrained = false)
    //    val yPredicted = model.infSolver.getBestStructure(model.wv, xGold)
    //    val yMostViolated = model.infSolver.getLossAugmentedBestStructure(model.wv, xGold, yGold)
    //    //  model.infSolver.getLoss(xGold, yGold, yPredicted) should be(0.00)
    //    // model.infSolver.getLoss(xGold, yPredicted, yMostViolated) should be(0.00)
    //    (yPredicted.asInstanceOf[Saul_SL_Label_Structure[ConllRelation]].equals(yMostViolated.
    //      asInstanceOf[Saul_SL_Label_Structure[ConllRelation]])) should be(true)
  }

  "Structured output learning (SL)" should "really work in Saul." in {

  }
}
