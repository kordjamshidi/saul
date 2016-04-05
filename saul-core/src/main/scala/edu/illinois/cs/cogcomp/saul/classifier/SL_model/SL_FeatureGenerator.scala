package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.sl.core.{ IStructure, IInstance, AbstractFeatureGenerator }
import edu.illinois.cs.cogcomp.sl.util.{ FeatureVectorBuffer, IFeatureVector }

/** Created by Parisa on 12/8/15.
  */
class SL_FeatureGenerator[HEAD <: AnyRef](factors: List[ConstrainedClassifier[_,HEAD]]) extends AbstractFeatureGenerator {
  override def getFeatureVector(x: IInstance, y: IStructure): IFeatureVector = {

    val myX = x.asInstanceOf[Saul_SL_Instance[HEAD]]
    val myY = y.asInstanceOf[Saul_SL_Label_Structure[HEAD]]
    val fv = new FeatureVectorBuffer()
    var labelCount = 0
    var previousSize = 0
    var previousSize2 = 0
    factors.foreach{
      cf =>
      val candis: Seq[_] = cf.getCandidates(myX.head)
      val fvLocal = new FeatureVectorBuffer()
      previousSize=previousSize+previousSize2
      candis.foreach {
        ci=>
        val a = cf.onClassifier.getExampleArray((ci),true)
        val a0 = a(0).asInstanceOf[Array[Int]]
        var a1 = a(1).asInstanceOf[Array[Double]]
        val lab = myY.labels(labelCount)
        labelCount = labelCount + 1
        if (!lab.equals("true")) {
          a1 = Array.fill[Double](a1.length)(0)
        }
        //val fv = new FeatureVectorBuffer(a0, a1)
        fvLocal.addFeature(a0, a1)
        previousSize2 = fvLocal.toFeatureVector().getMaxIdx
      }
      fv.addFeature(fvLocal, previousSize)
    }

    fv.toFeatureVector()
  }

}
