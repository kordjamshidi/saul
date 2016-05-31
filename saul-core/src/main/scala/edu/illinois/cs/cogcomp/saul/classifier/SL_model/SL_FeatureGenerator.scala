package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.saul.classifier.SparseNetworkLBP
import edu.illinois.cs.cogcomp.sl.core.{ AbstractFeatureGenerator, IInstance, IStructure }
import edu.illinois.cs.cogcomp.sl.util.{ FeatureVectorBuffer, IFeatureVector }

/** Created by Parisa on 12/8/15.
  */
class SL_FeatureGenerator[HEAD <: AnyRef](model: SaulSLModel[HEAD]) extends AbstractFeatureGenerator {
  override def getFeatureVector(x: IInstance, y: IStructure): IFeatureVector = {

    val myX = x.asInstanceOf[Saul_SL_Instance[HEAD]]
    val myY = y.asInstanceOf[Saul_SL_Label_Structure[HEAD]]
    val fv = new FeatureVectorBuffer()
    var ltuNum = 0
    var factorOffset = 0
    model.Factors.zipWithIndex.foreach {

      case (cf, indF) =>
        val candis: Seq[_] = cf.getCandidates(myX.head)
        val sparseNet = cf.onClassifier.asInstanceOf[SparseNetworkLBP]
        val fvLocal = new FeatureVectorBuffer()
        var localOffset = 0
        candis.zipWithIndex.foreach {
          case (ci, indC) =>
            val a = sparseNet.getExampleArray(ci, false)
            var a0 = a(0).asInstanceOf[Array[Int]]
            var a1 = a(1).asInstanceOf[Array[Double]]
            val fvTemp = new FeatureVectorBuffer(a0, a1)
            val lab = myY.labels(indC)
            for (netI <- 0 until sparseNet.net.size()) {
              if (netI != 0)
                localOffset = localOffset + model.LtuTemplates(ltuNum + netI - 1).length
              else localOffset = 0
              if (!sparseNet.getLabelLexicon.lookupKey(netI).valueEquals(lab)) {
                a1 = Array()
                a0 = Array()
                fvLocal.addFeature(new FeatureVectorBuffer(a0, a1), localOffset)
              } else
                fvLocal.addFeature(fvTemp, localOffset)
              val p = fvLocal.toFeatureVector

            }
        }
        ltuNum = ltuNum + sparseNet.net.size()

        if (indF > 0)
          factorOffset = factorOffset + model.Factors(indF - 1).onClassifier.getLexicon.size() * model.Factors(indF - 1).onClassifier.getLabelLexicon.size()

        fv.addFeature(fvLocal, factorOffset)
    }

    fv.toFeatureVector()

  }

}
