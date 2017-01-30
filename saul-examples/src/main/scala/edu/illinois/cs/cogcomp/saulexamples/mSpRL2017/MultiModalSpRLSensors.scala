package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Relation, Token}
import edu.illinois.cs.cogcomp.saulexamples.vision.{Image, Segment, SegmentRelation}

object MultiModalSpRLSensors {

  def imageToSegmentMatching(i: Image, s: Segment): Boolean = {
    i.getId == s.getAssociatedImageID
  }

  def segmentRelationToSegmentMatching(r: SegmentRelation, s: Segment): Boolean = {
    (r.getFirstSegmentId == s.getSegmentId || r.getSecondSegmentId == s.getSegmentId) && (r.getImageId == s.getAssociatedImageID)
  }

  def relationToTokenMatching(r: Relation, t: Token): Boolean = {
    (r.containsProperty("TR_RELATION") && t.getPropertyValues("TRAJECTOR_id").contains(r.getArgumentId(0))) ||
      (r.containsProperty("LM_RELATION") && t.getPropertyValues("LANDMARK_id").contains(r.getArgumentId(0))) ||
      t.getPropertyValues("SPATIALINDICATOR_id").contains(r.getArgumentId(1))
  }

  def documentToImageMatching(d: Document, i: Image): Boolean = {
    d.getPropertyFirstValue("IMAGE").endsWith("/" + i.getLabel)
  }
}