/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.Badge

import edu.illinois.cs.cogcomp.lbjava.learn.{LinearThresholdUnit, SparseNetworkLearner}
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeClassifiers.{BadgeClassifierMulti, BadgeOppositClassifierMulti}
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeDataModel._
import edu.illinois.cs.cogcomp.saulexamples.Badge.BadgeReader
import edu.illinois.cs.cogcomp.sl.util.WeightVector
import org.scalatest.{FlatSpec, Matchers}

import scala.Array._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

class weightTest extends FlatSpec with Matchers {

  val allNamesTrain = new BadgeReader("/Users/Parisa/iSoftware/Github_forked/ParisaVersion/ParisaPublicVersion/saul/data/badges/badges.train").badges
  val allNamesTest = new BadgeReader("/Users/Parisa/iSoftware/Github_forked/ParisaVersion/ParisaPublicVersion/saul/data/badges/badges.test").badges

  badge.populate(allNamesTrain)
  badge.populate(allNamesTest,false)
  BadgeClassifierMulti.learn(2)
  BadgeOppositClassifierMulti.learn(2)
  BadgeClassifierMulti.test()
  BadgeOppositClassifierMulti.test()
  var fullWeightList: ListBuffer[Array[Float]] = ListBuffer()
  var wvLength = 0
  "update weight" should "work" in {
    List(BadgeClassifierMulti,BadgeOppositClassifierMulti).foreach{
     x => {
       val sparseNet = x.classifier.asInstanceOf[SparseNetworkLearner]
       val lexiconSize = (sparseNet.getLexicon.size())

       for (i <- 0 until sparseNet.getNetwork.size()) {
         val fullWeights = Array.fill[Float](lexiconSize)(0)
         val trainedWeighs = x.classifier.asInstanceOf[SparseNetworkLearner].getNetwork.get(i).asInstanceOf[LinearThresholdUnit].getWeightVector
        println("joint before ",  trainedWeighs,", s=", trainedWeighs.size())

         for (j <- 0 until lexiconSize)
           fullWeights(j) = trainedWeighs.getWeight(j).asInstanceOf[Float]

         fullWeightList = fullWeightList :+ fullWeights

         wvLength = wvLength + lexiconSize
       }

       println("lexicon size: " + sparseNet.getLexicon.size(), "* label lexicon size:", sparseNet.getLabelLexicon.size())
     }
   }

    // wv = Concatenate_(over factors)Concatenate_(over ltu) => size(wv)=sum_(over factors)sum_(over ltu)(size(ltu_i))

    val myWeight = Array(fullWeightList.flatten: _*)
    val wv = new WeightVector(myWeight) // wv this is one unified weight vector of all initialized LTUs
    wvLength should be (72)

    BadgeClassifierMulti.test()

    var ltu_count = 0
    var offset = 0
    List(BadgeClassifierMulti,BadgeOppositClassifierMulti).foreach {
        cf =>
          for (i <- 0 until cf.classifier.asInstanceOf[SparseNetworkLearner].getNetwork.size()) {
            val ltuSize = cf.classifier.asInstanceOf[SparseNetworkLearner].getLexicon.size()//.net.get(i).asInstanceOf[LinearThresholdUnit].getParameters.asInstanceOf[LinearThresholdUnit.Parameters].weightVector
            //  print("w", ltu_count, " size:\t", ltuSize.size)
            val myLTUJointlyTrainedWeight = wv.getWeightArray.slice(offset, offset + ltuSize)
            // var count = 0
            // for (count <- cf.onClassifier.classifier.asInstanceOf[SparseNetworkLBP].getNetwork.get(i).asInstanceOf[LinearThresholdUnit].getWeightVector.size() until ltuSize.size)
            //   myLTUJointlyTrainedWeight = myLTUJointlyTrainedWeight :+ 0.asInstanceOf[Float]
            // if (cf.onClassifier.classifier.asInstanceOf[SparseNetworkLBP].getNetwork.get(i).asInstanceOf[LinearThresholdUnit].getWeightVector.size()!= ltuSize.size) {
            //    println("size mismatch!", cf.onClassifier.classifier.asInstanceOf[SparseNetworkLBP].getNetwork.get(i).asInstanceOf[LinearThresholdUnit].getWeightVector.size(), ",", ltuSize.size)
            //              }
            val exampleFeatureIndexes = ofDim[Int](myLTUJointlyTrainedWeight.length)
            val weightVector = cf.classifier.asInstanceOf[SparseNetworkLearner].getLTU(i).asInstanceOf[LinearThresholdUnit].getWeightVector
            weightVector.clear()

            // val  exampleFeatureIndexes = cf.onClassifier.classifier.asInstanceOf[SparseNetworkLBP].getLexicon.getMap.values.toArray.map(_.asInstanceOf[Int])//.toArray//.toArray().asInstanceOf[Array[Int]]
            for (featureIndex <- myLTUJointlyTrainedWeight.indices) {
              exampleFeatureIndexes(featureIndex) = featureIndex
              weightVector.setWeight(featureIndex, myLTUJointlyTrainedWeight(featureIndex))
              //cf.onClassifier.classifier.asInstanceOf[SparseNetworkLBP].getLexicon.;
            }

            //.getParameters.asInstanceOf[LinearThresholdUnit.Parameters]
            // cf.onClassifier.classifier.asInstanceOf[SparseNetworkLBP].getLTU(i).getWeightVector.scaledAdd(exampleFeatureIndexes, Utils.converFarrayToD(myLTUJointlyTrainedWeight), 1.0)
            offset = offset + ltuSize//ltuTemplates(ltu_count).length
            ltu_count = ltu_count + 1
            val trainedWeighs2 = cf.classifier.asInstanceOf[SparseNetworkLearner].getNetwork.get(i).asInstanceOf[LinearThresholdUnit].getWeightVector
            println("after joint ",  trainedWeighs2, ", s=", trainedWeighs2.size())
          }
      }

    BadgeClassifierMulti.test()


  }

//  "weights initializer" should "join multiple weight vectors" in {
//    var lt: ListBuffer[Array[Float]] = ListBuffer()
//    var wvLength = 0
//    // Add 3 factors (size 3, 4, 7)
//    val length1 = 3
//    val length2 = 4
//    val length3 = 7
//    val t1: Array[Float] = Array(4.0f, 6.0f, 8.0f)
//    wvLength = wvLength + length1
//    val t2: Array[Float] = Array(-8.0f, -6.0f, -4.0f, 0.0f)
//    wvLength = wvLength + length2
//    val t3: Array[Float] = Array(14.0f, 16.0f, 18.0f, 24.0f, 35.5f, 78.32f, 567.865f)
//    wvLength = wvLength + length3
//
//    // Append the individual vectors to the buffer
//    lt = lt :+ t1
//    lt = lt :+ t2
//    lt = lt :+ t3
//
//    // Create the complete weight vector
//    val myWeight = Array(lt.flatten: _*)
//    val wv = new WeightVector(myWeight)
//    wv.getWeightArray should be(Array(4.0f, 6.0f, 8.0f, -8.0f, -6.0f, -4.0f, 0.0f, 14.0f, 16.0f, 18.0f, 24.0f, 35.5f, 78.32f, 567.865f))
//  }
//
//  "weight update" should "distribute the joint weight vector" in {
//    var lt: ListBuffer[Array[Float]] = ListBuffer()
//    var wvLength = 0
//    // Add 3 factors (size 3, 4, 7)
//    val t1: Array[Float] = Array(4.0f, 6.0f, 8.0f)
//    wvLength = wvLength + t1.length
//    val t2: Array[Float] = Array(-8.0f, -6.0f, -4.0f, 0.0f)
//    wvLength = wvLength + t2.length
//    val t3: Array[Float] = Array(14.0f, 16.0f, 18.0f, 24.0f, 35.5f, 78.32f, 567.865f)
//    wvLength = wvLength + t3.length
//
//    // Append the individual vectors to the buffer
//    lt = lt :+ t1
//    lt = lt :+ t2
//    lt = lt :+ t3
//
//    // Create the complete weight vector
//    val myWeight = Array(lt.flatten: _*)
//    val wv = new WeightVector(myWeight)
//
//    var offset = 0
//    val factorWeight1 = wv.getWeightArray.slice(offset, offset + t1.length)
//    factorWeight1 should be(Array(4.0f, 6.0f, 8.0f))
//    offset = offset + t1.length
//
//    val factorWeight2 = wv.getWeightArray.slice(offset, offset + t2.length)
//    factorWeight2 should be(Array(-8.0f, -6.0f, -4.0f, 0.0f))
//    offset = offset + t2.length
//
//    val factorWeight3 = wv.getWeightArray.slice(offset, offset + t3.length)
//    factorWeight3 should be(Array(14.0f, 16.0f, 18.0f, 24.0f, 35.5f, 78.32f, 567.865f))
//  }
//  "featureVector operations" should "work" in {
//    val fvGlobal = new FeatureVectorBuffer()
//    val fv = new FeatureVectorBuffer()
//    val a0: Array[Int] = Array(1, 2, 3)
//    val a1: Array[Double] = Array(0.1, 0.2, 0.3)
//    val fvTemp = new FeatureVectorBuffer(a0, a1)
//    fvGlobal.addFeature(fvTemp)
//    fvTemp.toFeatureVector.getIndices should be(Array(1, 2, 3))
//    fvGlobal.addFeature(fvTemp, 3)
//    fvGlobal.addFeature(fvTemp, 3)
//    fvGlobal.shift(1)
//    fvGlobal.toFeatureVector.getIndices should be(Array(2, 3, 4, 5, 6, 7))
//  }
//
//
//  // Testing the original functions with real classifiers
//  "integration test" should "work" in {
//    // Initialize toy model
//    import testModel._
//    object TestClassifier extends Learnable(tokens) {
//      def label = testLabel
//
//      override def feature = using(word)
//
//      override lazy val classifier = new SparseNetworkLearner()
//    }
//    object TestBiClassifier extends Learnable(tokens) {
//      def label = testLabel
//
//      override def feature = using(word, biWord)
//
//      override lazy val classifier = new SparseNetworkLearner()
//    }
//    object TestConstraintClassifier extends ConstrainedClassifier[String, String](TestClassifier) {
//      def subjectTo = ConstrainedClassifier.constraint { _ => new FirstOrderConstant(true) }
//
//      //override val pathToHead = Some(-iEdge)
//      // override def filter(t: String, h: String): Boolean = t.equals(h)
//      override val solver = new OJalgoHook
//    }
//    object TestBiConstraintClassifier extends ConstrainedClassifier[String, String](TestBiClassifier) {
//      def subjectTo = ConstrainedClassifier.constraint { _ => new FirstOrderConstant(true) }
//
//      // override val pathToHead = Some(-iEdge)
//      //override def filter(t: String, h: String): Boolean = t.equals(h)
//      override val solver = new OJalgoHook
//    }
//
//    val words = List("this", "is", "a", "test")
//    tokens.populate(words)
//
//    val cls = List(TestConstraintClassifier, TestBiConstraintClassifier)
//    // This should combine the weights
//    val m = StructuredLearning(tokens, cls)
//
//    val clNet1 = TestConstraintClassifier.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
//    val clNet2 = TestBiConstraintClassifier.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
//    val wv1 = clNet1.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getWeightVector
//    val wv2 = clNet2.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getWeightVector
//
//    m.Factors.size should be(2)
//
//    wv1.size() should be(4)
//    wv2.size() should be(8)
//    // Combined size should be 12
//    m.wv.getLength should be(12)
//
//    // This should distribute the weights
//    m.infSolver.asInstanceOf[Saul_SL_Inference[String]].updateWeights(m.wv)
//
//    val wv1After = clNet1.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getWeightVector
//    val wv2After = clNet2.getNetwork.get(0).asInstanceOf[LinearThresholdUnit].getWeightVector
//
//    //Everything should be the same
//    wv1After.size() should be(4)
//    wv2After.size() should be(8)
//    // Combined size should be 12
//    m.wv.getLength should be(12)
//  }
//
//  object testModel extends DataModel {
//    val tokens = node[String]
//    val iEdge = edge(tokens, tokens)
//    val testLabel = property(tokens) { x: String => x.equals("candidate") }
//    val word = property(tokens) { x: String => x }
//    val biWord = property(tokens) { x: String => x + "-" + x }
//  }

}
