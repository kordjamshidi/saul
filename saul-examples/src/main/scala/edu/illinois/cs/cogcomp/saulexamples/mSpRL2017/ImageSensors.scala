package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ Image, Segment, SegmentRelation }

object ImageSensors {

  def image_segment_link(i: Image, s: Segment): Boolean = {
    i.getImageID == s.getAssociatedImageID
  }
  def rel_segment(r: SegmentRelation, s: Segment): Boolean = {
    (r.getSegment_ID1 == s.getSegmentID) || (r.getSegment_ID2 == s.getSegmentID)
  }
}