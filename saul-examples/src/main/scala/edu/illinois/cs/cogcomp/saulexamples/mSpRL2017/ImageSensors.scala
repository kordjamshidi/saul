package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.data.Segment
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Image, Segment}


object ImageSensors {

  def image_segment_link(i: Image, s: Segment): Boolean = {
    i.getImageID == s.getAssociatedImageID
  }
}