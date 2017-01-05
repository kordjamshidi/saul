package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.ImageSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ Image, Segment, SegmentRelation }

import scala.io.Source

object mSpRL2017DataModel extends DataModel {

  val images = node[Image]
  val segment = node[Segment]

  val relation = node[SegmentRelation]

  val image_segment = edge(images, segment)
  // Linking associated Segments with Images
  image_segment.addSensor(imageSegmentLink _)

  // Here we will create relationships between different segments
  // I am planning to use Relation class

  val relationsToSegments = edge(relation, segment)

  relationsToSegments.addSensor(rel_segment _)

  val imageLable = property(images) {

    x: Image => x.getLabel

  }

  val imageId = property(images) {

    x: Image => x.getID
  }

  val segmentLable = property(segment) {

    x: Segment => x.getSegmentConcept

  }

  val segmentId = property(segment) {

    x: Segment => x.getSegmentCode
  }

  val segmentFeatures = property(segment) {

    x: Segment => x.getSegmentFeatures
  }
}