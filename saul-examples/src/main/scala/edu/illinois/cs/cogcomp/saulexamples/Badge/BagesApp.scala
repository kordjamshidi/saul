package edu.illinois.cs.cogcomp.saulexamples.Badge

/** Created by Parisa on 9/13/16.
  */

import edu.illinois.cs.cogcomp.saul.classifier.SimpleSparseNetwork
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeClassifiers._
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeDataModel._

import scala.collection.JavaConversions._
object BadgesApp extends App {

  val allNamesTrain = new BadgeReader("data/badges/badges.train").badges
  val allNamesTest = new BadgeReader("data/badges/badges.test").badges

  badge.populate(allNamesTrain)
  badge.populate(allNamesTest, false)

  /*Test the join training with SparsePerceptron*/

  //  BadgeClassifier.test()
  //  BadgeOppositClassifier.test()
  //  JointTrain.train(BadgeDataModel.badge,List(badgeConstrainedClassifier,oppositBadgeConstrainedClassifier),5)
  //  badgeConstrainedClassifier.test()
  //  BadgeClassifier.test()
  //  oppositBadgeConstrainedClassifier.test()

  /*Test the joinTraining with SparseNetwork*/

  // BadgeClassifierMulti.test()
  //BadgeOppositClassifierMulti.test()
  //val cls=List(badgeConstrainedClassifierMulti,oppositBadgeConstrainedClassifierMulti)

  //  ClassifierUtils.InitializeClassifiers(badge, cls: _*)

  // JointTrainSparseNetwork.train(badge,cls,5,false)
  // badgeConstrainedClassifierMulti.test()

  //oppositBadgeConstrainedClassifierMulti.test()
  SimpleSparseNetwork.train(badge, BadgeClassifierMulti, 5, false)
  BadgeClassifierMulti.test()
}