package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.ImageSensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ Image, Segment, SegmentRelation }

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.breakOut
import scala.util._
import scala.io.Source

object mSpRL2017DataModel extends DataModel {

  val images = node[Image]
  val segments = node[Segment]
  val relation = node[SegmentRelation]

//  val image_segment = edge(images, segments)
  // Linking associated Segments with Images
//  image_segment.addSensor(imageSegmentLink _)

  // Linking associated Segments with Segments
//  val relationsToSegments = edge(relation, segments)
//  relationsToSegments.addSensor(rel_segment _)

  val imageLabel = property(images) {

    x: Image => x.getLabel

  }

  val imageId = property(images) {

    x: Image => x.getId
  }

  val segmentLable = property(segments) {

    x: Segment => x.getSegmentConcept

  }

  val segmentId = property(segments) {

    x: Segment => x.getSegmentCode
  }

  val segmentFeatures = property(segments) {

    x: Segment => val splitfeatures = x.getSegmentFeatures.split(" ").toList

      val features = splitfeatures.map(_.toDouble)

      features
  }
}