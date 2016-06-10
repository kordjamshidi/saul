package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation

import edu.illinois.cs.cogcomp.saul.classifier.ClassifierUtils
import edu.illinois.cs.cogcomp.saul.classifier.SL_model._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationConstrainedClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.EntityRelationDataModel._
import org.slf4j.helpers.NOPLogger
import org.slf4j.{ Logger, LoggerFactory }
/** Created by Parisa on 12/8/15.
  */
object EntityRelationApp_SL extends App {
  val logging = true
  val logger: Logger = if (logging) LoggerFactory.getLogger(this.getClass) else NOPLogger.NOP_LOGGER;

  EntityRelationDataModel.populateWithConllSmallSet()
  //  val cls = List(PerConstrainedClassifier, OrgConstrainedClassifier, LocConstrainedClassifier, LivesIn_PerOrg_relationConstrainedClassifier, WorksFor_PerOrg_ConstrainedClassifier)

  val testRels = pairs.getTestingInstances.toSet.toList
  val testTokens = tokens.getTestingInstances.toSet.toList

  //  val SLProblem = SL_IOManager.makeSLProblem(pairs, cls)
  //  val model = Initialize(SLProblem, new SaulSLModel(cls))
  //  model.featureGenerator = new SL_FeatureGenerator(model)
  //  val para = new SLParameters
  //  para.loadConfigFile("./config/DCD.config")
  //  model.infSolver = new Saul_SL_Inference[ConllRelation](model.Factors.toList, model.LTUWeightTemplates, node)
  //
  //  model.para = para
  //  val learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para)

  //   model.wv = learner.train(SLProblem, model.wv)

  val m = JoinSLtrain(pairs, List(PerConstrainedClassifier, OrgConstrainedClassifier, LocConstrainedClassifier, LivesIn_PerOrg_relationConstrainedClassifier, WorksFor_PerOrg_ConstrainedClassifier))

  //  /* Test SL_ER */
  //  println("Independent Classifiers:")
  //  ClassifierUtils.TrainClassifiers(10, PersonClassifier, OrganizationClassifier, LocationClassifier, WorksForClassifier, LivesInClassifier)
  //  ClassifierUtils.TestClassifiers(PersonClassifier, OrganizationClassifier, LocationClassifier, WorksForClassifier, LivesInClassifier)
  //  println("SL Classifiers:")
  //  PerConstrainedClassifier.test(EntityRelationDataModel.tokens.getTrainingInstances, outputGranularity = 10)
  //  OrgConstrainedClassifier.test(EntityRelationDataModel.tokens.getTrainingInstances, outputGranularity = 10)
  //  LocConstrainedClassifier.test(EntityRelationDataModel.tokens.getTrainingInstances, outputGranularity = 10)
  //  LivesIn_PerOrg_relationConstrainedClassifier.test(EntityRelationDataModel.pairs.getTrainingInstances, outputGranularity = 10)
  //  WorksFor_PerOrg_ConstrainedClassifier.test(EntityRelationDataModel.pairs.getTrainingInstances, outputGranularity = 10)
  //
  ClassifierUtils.TestClassifiers((testTokens, PerConstrainedClassifier), (testTokens, OrgConstrainedClassifier),
    (testTokens, LocConstrainedClassifier))

  ClassifierUtils.TestClassifiers(
    (testRels, WorksFor_PerOrg_ConstrainedClassifier),
    (testRels, LivesIn_PerOrg_relationConstrainedClassifier)
  )

}
