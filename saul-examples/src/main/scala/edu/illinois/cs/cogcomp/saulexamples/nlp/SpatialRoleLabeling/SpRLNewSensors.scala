package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Phrase, Relation, Sentence}

/**
  * Created by parisakordjamshidi on 12/25/16.
  */
object SpRLNewSensors {

  def DocToSentence(d: Document, s: Sentence): Boolean = {
    d.getId == s.getProperty("")
  }

  def RelToTr(r: Relation, p: Phrase): Boolean = {
    r.getProperty("trajector_id") == p.getId
  }
}
