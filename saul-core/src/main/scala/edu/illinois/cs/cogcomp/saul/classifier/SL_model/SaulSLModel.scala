package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import java.io.{ BufferedOutputStream, FileOutputStream, ObjectOutputStream }

import edu.illinois.cs.cogcomp.saul.classifier.ConstrainedClassifier
import edu.illinois.cs.cogcomp.sl.core.{ SLModel, SLParameters }

import scala.collection.mutable.ListBuffer

/** Created by Parisa on 4/1/16.
  */
class SaulSLModel[HEAD <: AnyRef](cls: List[ConstrainedClassifier[_, HEAD]], listBuffer: ListBuffer[Array[Float]] = ListBuffer()) extends SLModel with Serializable {
  var LtuTemplates: ListBuffer[Array[Float]] = listBuffer
  var Factors: ListBuffer[ConstrainedClassifier[_, HEAD]] = ListBuffer()
  cls.foreach {
    (c: ConstrainedClassifier[_, HEAD]) =>
      Factors += (c)
  }

  override def saveModel(fileName: String): Unit = {
    numFeatuerBit = SLParameters.HASHING_MASK
    val oos: ObjectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))
    oos.writeObject(this.wv)
    oos.close
    // logger.info("Done!")
  }

}
