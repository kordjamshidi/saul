/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner
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
    var labelCount = 0

    //The features vectors are generated per classifier with local indexes and then the indexes are adjusted globally

    model.Factors.zipWithIndex.foreach {

      case (cf, indF) =>
        val candis: Seq[_] = cf.getCandidates(myX.head)
        val sparseNet = cf.onClassifier.classifier.asInstanceOf[SparseNetworkLearner]
        val fvLocal = new FeatureVectorBuffer()

        candis.foreach {
          (ci) =>
            var localOffset = 0
            //This  block is the conversion from Lbjava to SL feature vectors
            val a = sparseNet.getExampleArray(ci, false)
            var a0 = a(0).asInstanceOf[Array[Int]]
            var a1 = a(1).asInstanceOf[Array[Double]]
            val fvTemp = new FeatureVectorBuffer(a0, a1)
            val lab = myY.labels(labelCount)
            labelCount = labelCount + 1
            //keep the features for the on label (at netI index) and repeat a zero feature vector for the off labels
            for (netI <- 0 until sparseNet.getNetwork.size()) {
              if (netI != 0)
                localOffset = localOffset + model.LTUWeightTemplates(ltuNum + netI - 1).length
              // else localOffset = 0
              if (!sparseNet.getLabelLexicon.lookupKey(netI).valueEquals(lab)) {
                a1 = Array()
                a0 = Array()
                fvLocal.addFeature(new FeatureVectorBuffer(a0, a1), localOffset)
              } else
                fvLocal.addFeature(fvTemp, localOffset)
              //val p = fvLocal.toFeatureVector

            }
        }
        ltuNum = ltuNum + sparseNet.getNetwork.size()

        if (indF > 0)
          factorOffset = factorOffset + model.Factors(indF - 1).onClassifier.classifier.getLexicon.size() * model.Factors(indF - 1).onClassifier.getLabelLexicon.size()

        fv.addFeature(fvLocal, factorOffset)
    }

    fv.toFeatureVector()

  }

}
