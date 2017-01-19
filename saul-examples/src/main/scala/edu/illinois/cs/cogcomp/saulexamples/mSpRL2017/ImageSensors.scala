package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.vision.{Image, Segment, SegmentRelation}

object ImageSensors {

  def imageSegmentLink(i: Image, s: Segment): Boolean = {
    i.getId == s.getAssociatedImageID
  }
  def rel_segment(r: SegmentRelation, s: Segment): Boolean = {
    (r.getFirstSegmentId == s.getSegmentId || r.getSecondSegmentId == s.getSegmentId) && (r.getImageId == s.getAssociatedImageID)
  }
}