package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Image, Segment, SegmentRelation}

object ImageSensors {

  def imageSegmentLink(i: Image, s: Segment): Boolean = {
    i.getId == s.getImageId
  }

  def relationSegmentLink(r: SegmentRelation, s: Segment): Boolean = {
    (r.getFirstSegmentId == s.getId) || (r.getSecondSegmentId == s.getId)
  }
}