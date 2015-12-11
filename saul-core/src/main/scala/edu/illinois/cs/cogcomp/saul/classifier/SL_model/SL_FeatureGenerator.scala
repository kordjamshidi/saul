package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.sl.core.{ IStructure, IInstance, AbstractFeatureGenerator }
import edu.illinois.cs.cogcomp.sl.util.{ FeatureVectorBuffer, IFeatureVector }

/** Created by Parisa on 12/8/15.
  */
class SL_FeatureGenerator[_,HEAD] extends AbstractFeatureGenerator {
  override def getFeatureVector(x: IInstance, y: IStructure): IFeatureVector = {

    val myX = x.asInstanceOf[Saul_SL_java_Instance[HEAD,_]]
    val myY = x.asInstanceOf[Saul_SL_Label_java_Structure[HEAD,_]]
    var fv = new FeatureVectorBuffer()

    for (i <- 0 until myX.factorClassifiers.size()) {
      val a = myX.factorClassifiers.get(i).onClassifier.getExampleArray()
      val a0 = a(0).asInstanceOf[Array[Int]]
      var a1 = a(1).asInstanceOf[Array[Double]]
      val lab = myY.labels.get(i)
      if (!lab.equals("true")) {
        a1 = Array.fill[Double](a1.length)(0)
      }
      val fv = new FeatureVectorBuffer(a0, a1)
      fv.addFeature(a0, a1)
    }
    fv.toFeatureVector()
  }
}
