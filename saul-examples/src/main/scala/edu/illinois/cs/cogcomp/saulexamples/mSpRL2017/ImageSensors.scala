package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.data.{Image, Segment}

/**
  * Created by parisakordjamshidi on 12/31/16.
  */
object ImageSensors {

  def image_segment_link(i: Image, s: Segment): Boolean = {
    i.getLabel == s.getSegmentCode
  }
}
