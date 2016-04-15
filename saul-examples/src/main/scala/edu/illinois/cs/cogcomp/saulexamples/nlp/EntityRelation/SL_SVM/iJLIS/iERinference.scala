
package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.SL_SVM.iJLIS

import java.util.{ ArrayList, List }

import edu.illinois.cs.cogcomp.sl.core.{ AbstractInferenceSolver, IInstance, IStructure }
import edu.illinois.cs.cogcomp.sl.util.WeightVector

/** Created by parisakordjamshidi on 20/09/14.
  */

class iERinference extends AbstractInferenceSolver {
  /** This is for training
    */

  override def getLoss(iInstance: IInstance, gold: IStructure, pred: IStructure): Float = {
    val igold: ERiStructure = gold.asInstanceOf[ERiStructure];
    val ipred: ERiStructure = pred.asInstanceOf[ERiStructure];
    var loss: Float = 0
    if (ipred.nodeValues == igold.nodeValues)
      loss = 0
    else
      loss = 1

    loss
  }

  /** This is for prediction.
    */
  def getBestStructure(wv: WeightVector, input: IInstance): IStructure = {
    val qi: ERqInstance = input.asInstanceOf[ERqInstance]
    val options: List[ERiStructure] = getOptions(qi)
    var max: Double = Double.NegativeInfinity
    var score: Double = .0
    var maxC: ERiStructure = null
    import scala.collection.JavaConversions._
    for (c <- options) {
      val a = new ERFeatureGenerator();
      val f = a.getFeatureVector(c.qi, c)
      score = wv.dotProduct(f)
      if (score > max) {
        max = score
        maxC = c
      }
    }
    return maxC
  }

  override def getLossAugmentedBestStructure(wv: WeightVector, input: IInstance, gold: IStructure): IStructure = {

    val goldStruct: ERiStructure = gold.asInstanceOf[ERiStructure]
    val qi: ERqInstance = input.asInstanceOf[ERqInstance]
    val options: List[ERiStructure] = getOptions(qi)
    var max: Double = Double.NegativeInfinity
    var score: Double = .0
    var maxC: ERiStructure = null
    var loss: Double = .0
    System.out.println
    System.out.println(gold)
    import scala.collection.JavaConversions._
    for (c <- options) {
      val a = new ERFeatureGenerator();
      val f = a.getFeatureVector(c.qi, c)
      score = wv.dotProduct(f)
      loss = getLoss(null, c, goldStruct)
      score += loss
      System.out.println(c)
      System.out.println(f)
      System.out.println(score)
      if (score > max) {
        max = score
        maxC = c
      }
    }
    return maxC
    //  val a= new edu.illinois.cs.cogcomp.core.datastructures.Pair[IStructure, java.lang.Double](maxC.asInstanceOf[IStructure], new lang.Double(getLoss(maxC, goldStruct)))

    //return a
  }

  def getOptions(qi: ERqInstance): List[ERiStructure] = {
    val options: List[ERiStructure] = new ArrayList[ERiStructure]
    //for (i <- NodeLabel.values) {
    // for (j <- NodeLabel.values) {
    // for (k <- NodeLabel.values) {
    // val labelOptions: List[Nodevalues]= new ArrayList[Nodevalues]()
    // val a:Nodevalues=new Nodevalues()
    // a.a=true
    //labelOptions.add(a)
    //a.a=false
    //labelOptions.add(a)
    //labelOptions.add(k)
    options.add(new ERiStructure(qi, true))
    options.add(new ERiStructure(qi, false))

    return options
  }
  override def clone(): iERinference = {
    return new iERinference();
  }

}

