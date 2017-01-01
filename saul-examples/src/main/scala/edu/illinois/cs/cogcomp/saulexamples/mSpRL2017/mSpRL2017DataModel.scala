package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.{Image, Segment};
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.ImageSensors._
import scala.io.Source

object mSpRL2017DataModel extends DataModel{

  val image= node[Image]
  val segment = node[Segment]

  val image_segment= edge(image,segment)

  image_segment.addSensor(image_segment_link _)

  val image_lable = property(image){

    x: Image => x.getLabel

  }
}