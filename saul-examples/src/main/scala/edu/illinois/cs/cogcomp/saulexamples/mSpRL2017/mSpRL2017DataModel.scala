package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.ImageSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Image, Segment, SegmentRelation}

import scala.io.Source

object mSpRL2017DataModel extends DataModel {

  val images = node[Image]
  val segments = node[Segment]

  val relations = node[SegmentRelation]

  val imagesToSegments = edge(images, segments)
  // Linking associated Segments with Images
  imagesToSegments.addSensor(imageSegmentLink _)

  // Here we will create relationships between different segments
  // I am planning to use Relation class

  val relationsToSegments = edge(relations, segments)

  relationsToSegments.addSensor(relationSegmentLink _)

  val imageLabel = property(images) {

    x: Image => x.getLabel

  }

  val imageId = property(images) {

    x: Image => x.getId
  }

  val segmentLabel = property(segments) {

    x: Segment => x.getConcept

  }

  val segmentId = property(segments) {

    x: Segment => x.getCode
  }

  val segmentFeatures = property(segments) {

    x: Segment => x.getFeatures
  }
}