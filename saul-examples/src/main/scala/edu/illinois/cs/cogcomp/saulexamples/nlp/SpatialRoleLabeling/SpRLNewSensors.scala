package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Sentence}

/**
  * Created by parisakordjamshidi on 12/25/16.
  */
object SpRLNewSensors {

  def DocToSentence(d:Document,s:Sentence): Boolean ={
    d.getText.contains(s.getText)
  }

}
