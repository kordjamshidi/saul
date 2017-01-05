package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.ImageSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ Image, Segment, SegmentRelation }

import scala.io.Source

object mSpRL2017DataModel extends DataModel {

  val image = node[Image]
  val segment = node[Segment]

  val relation = node[SegmentRelation]

  val image_segment = edge(image, segment)
  // Linking associated Segments with Images
  image_segment.addSensor(image_segment_link _)

  // Here we will create relationships between different segments
  // I am planning to use Relation class

  val relToSg = edge(relation, segment)

  relToSg.addSensor(rel_segment _)

  val image_lable = property(image) {

    x: Image => x.getLabel

  }

  val image_id = property(image) {

    x: Image => x.getImageID
  }

  val segment_lable = property(segment) {

    x: Segment => x.getSegmentConcept

  }

  val segment_id = property(segment) {

    x: Segment => x.getSegmentCode
  }

  val segment_features = property(segment) {

    x: Segment => x.getSegmentFeatures
  }
}