package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.sl.core.{ IStructure, IInstance, AbstractFeatureGenerator }
import edu.illinois.cs.cogcomp.sl.util.{ FeatureVectorBuffer, IFeatureVector }

/** Created by Parisa on 12/8/15.
  */
class SL_FeatureGenerator[HEAD <: AnyRef] extends AbstractFeatureGenerator {
  override def getFeatureVector(x: IInstance, y: IStructure): IFeatureVector = {

    val myX = x.asInstanceOf[Saul_SL_Instance[HEAD]]
    val myY = y.asInstanceOf[Saul_SL_Label_Structure[HEAD]]
    val fv = new FeatureVectorBuffer()
    var labelCount = 0
    for (i <- 0 until myX.factorClassifiers.size) {
      val c = myX.factorClassifiers(i)
      val candis: Seq[_] = c.getCandidates(myX.head)
      val fvLocal = new FeatureVectorBuffer()
      var previousSize = 0
      previousSize = 0
      for (ci <- candis) {
        previousSize = fvLocal.toFeatureVector().getMaxIdx
        val a = c.onClassifier.getExampleArray(ci)
        val a0 = a(0).asInstanceOf[Array[Int]]
        var a1 = a(1).asInstanceOf[Array[Double]]
        val lab = myY.labels(labelCount)
        labelCount = labelCount + 1
        if (!lab.equals("true")) {
          a1 = Array.fill[Double](a1.length)(0)
        }
        //val fv = new FeatureVectorBuffer(a0, a1)
        fvLocal.addFeature(a0, a1)
      }
      fv.addFeature(fvLocal, previousSize)
    }

    fv.toFeatureVector()
  }

}
