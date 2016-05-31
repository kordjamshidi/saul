package edu.illinois.cs.cogcomp.saul.datamodel.property

import java.util

import edu.illinois.cs.cogcomp.lbjava.classify.{ Classifier, FeatureVector }
import edu.illinois.cs.cogcomp.saul.datamodel.property.features.ClassifierContainsInLBP

import scala.reflect.ClassTag

case class CombinedDiscreteProperty[T <: AnyRef](
  atts: List[Property[T]]
)(implicit val tag: ClassTag[T]) extends TypedProperty[T, List[_]] {

  override val sensor: (T) => List[_] = {
    t: T => atts.map(att => att.sensor(t))
  }

  val name = "combined++" + atts.map(x => { x.name }).mkString("+")

  val packageName = "LBP_Package"

  override def makeClassifierWithName(n: String): Classifier = new ClassifierContainsInLBP {
    this.containingPackage = packageName
    this.name = n

    override def getOutputType: String = "mixed%"

    def classify(instance: AnyRef): FeatureVector = {
      val t: T = instance.asInstanceOf[T]
      val featureVector = new FeatureVector()
      atts.foreach(_.addToFeatureVector(t, featureVector))
      featureVector
    }

    override def classify(examples: Array[AnyRef]): Array[FeatureVector] = {
      super.classify(examples)
    }

    //  override def scores(example: AnyRef): ScoreSet = {
    //    val cand_num=1
    ////    if (cand_num==0)
    ////      print("There is no relevant component of this type in the head to be classified.")
    //    val cf= classifier.asInstanceOf[SparseNetworkLBP]
    //    val gold = cf.getLabeler.discreteValue(example)
    //    val lLexicon = cf.getLabelLexicon
    //    val resultS: ScoreSet = classifier.scores(example)//new ScoreSet
    //    for (i <- 0 until lLexicon.size()) {
    //      if (lLexicon.lookupKey(i).valueEquals(gold))
    //        resultS.put(lLexicon.lookupKey(i).getStringValue, resultS.getScore(lLexicon.lookupKey(i).getStringValue).score - (1/(cand_num)) )
    //      else
    //        resultS.put(lLexicon.lookupKey(i).getStringValue, resultS.getScore(lLexicon.lookupKey(i).getStringValue).score + (1/(cand_num)) )
    //    }
    //    resultS
    //  }

    override def getCompositeChildren: util.LinkedList[_] = {
      val result: util.LinkedList[Classifier] = new util.LinkedList[Classifier]()
      atts.foreach(x => result.add(x.classifier))
      result
    }

    override def discreteValue(example: AnyRef): String = {
      atts.head(example.asInstanceOf[T]).asInstanceOf[String]
    }
  }

  override def addToFeatureVector(instance: T, featureVector: FeatureVector): FeatureVector = {
    atts.foreach(_.addToFeatureVector(instance, featureVector))
    featureVector
  }

  def addToFeatureVector(instance: T, featureVector: FeatureVector, nameOfClassifier: String): FeatureVector = {
    featureVector.addFeatures(makeClassifierWithName(nameOfClassifier).classify(instance))
    featureVector
  }
}
