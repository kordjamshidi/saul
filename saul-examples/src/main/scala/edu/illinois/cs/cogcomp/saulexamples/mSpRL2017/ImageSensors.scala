package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ Image, Segment, SegmentRelation }

object ImageSensors {

  def imageSegmentLink(i: Image, s: Segment): Boolean = {
    i.getID == s.getAssociatedImageID
  }
  def rel_segment(r: SegmentRelation, s: Segment): Boolean = {
    (r.getFirstSegmentId == s.getSegmentId) || (r.getSecondSegmentId == s.getSegmentId)
  }
}