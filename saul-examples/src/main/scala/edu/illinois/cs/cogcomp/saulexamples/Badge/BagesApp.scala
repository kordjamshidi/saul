package edu.illinois.cs.cogcomp.saulexamples.Badge

/**
  * Created by Parisa on 9/13/16.
  */

import edu.illinois.cs.cogcomp.saul.classifier.{JointTrain, JointTrainSparseNetwork}
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeConstraintClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeDataModel._

import scala.collection.JavaConversions._
object BadgesApp extends App{

  val allNamesTrain= new BadgeReader("data/badges/badges.train").badges
  val allNamesTest= new BadgeReader("data/badges/badges.test").badges

  badge.populate(allNamesTrain)
  badge.populate(allNamesTest,false)

/*Test the join training with SparsePerceptron*/

  BadgeClassifier.test()
  BadgeOppositClassifier.test()
  JointTrain.train(BadgeDataModel.badge,List(badgeConstrainedClassifier,oppositBadgeConstrainedClassifier),5)
  badgeConstrainedClassifier.test()
  BadgeClassifier.test()
  oppositBadgeConstrainedClassifier.test()

/*Test the joinTraining with SparseNetwork*/

  BadgeClassifierMulti.test()
  BadgeOppositClassifierMulti.test()
  JointTrainSparseNetwork.train(badge,List(badgeConstrainedClassifierMulti,oppositBadgeConstrainedClassifierMulti),5,true)
  badgeConstrainedClassifierMulti.test()
  BadgeClassifierMulti.test()
  oppositBadgeConstrainedClassifierMulti.test()

}