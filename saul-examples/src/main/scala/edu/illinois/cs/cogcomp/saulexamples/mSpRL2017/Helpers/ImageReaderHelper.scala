package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers

import edu.illinois.cs.cogcomp.saulexamples.data.CLEFImageReader
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers.DataProportion._
import edu.illinois.cs.cogcomp.saulexamples.vision.{Image, Segment, SegmentRelation}

import scala.collection.JavaConversions._

/** Created by taher on 2017-02-28.
  */
class ImageReaderHelper(dataDir: String, trainFileName: String, testFileName: String, proportion: DataProportion) {

  lazy val reader = new CLEFImageReader(dataDir, trainFileName, testFileName, false)

  def getImageRelationList: List[SegmentRelation] = {

    proportion match {
      case Train => reader.trainingRelations.toList
      case Test => reader.testRelations.toList
      case All => reader.trainingRelations.toList ++ reader.testRelations
    }
  }

  def getSegmentList: List[Segment] = {

    proportion match {
      case Train => reader.trainingSegments.toList
      case Test => reader.testSegments.toList
      case All => reader.trainingSegments.toList ++ reader.testSegments
    }
  }

  def getImageList: List[Image] = {

    proportion match {
      case Train => reader.trainingImages.toList
      case Test => reader.testImages.toList
      case All => reader.trainingImages.toList ++ reader.testImages
    }
  }
}
