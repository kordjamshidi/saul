package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Phrase, Relation, Sentence}

/**
  * Created by parisakordjamshidi on 12/25/16.
  */
object SpRLNewSensors {

  def DocToSentence(d: Document, s: Sentence): Boolean = {
    d.getId == s.getDocumentId
  }

  def RelToTr(r: Relation, p: Phrase): Boolean = {
    r.getProperty("trajector_id") == p.getId
  }

  def RelToLm(r: Relation, p: Phrase): Boolean = {
    r.getProperty("landmark_id") == p.getId
  }
  def RelToSp(r: Relation, p: Phrase): Boolean = {
    r.getProperty(" spatial_indicator_id") == p.getId
  }
}




