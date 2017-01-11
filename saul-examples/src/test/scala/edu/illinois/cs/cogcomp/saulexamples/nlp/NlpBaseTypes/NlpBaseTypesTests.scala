package edu.illinois.cs.cogcomp.saulexamples.nlp.NlpBaseTypes

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, Sentence}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.NlpBaseTypesSensors._
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Taher on 2017-01-11.
  */
class NlpBaseTypesTests extends FlatSpec with Matchers {

  "Extracted features for 'I received the book.'" should "be correct" in {
    val text = "I received the book."
    val document = new Document("doc1", 0, text.length, text)
    val sentence = new Sentence(document, "s1", 0, text.length, text )
    val tokens = sentenceToTokenGenerating(sentence)

    tokens.length should be(4)

    tokens(0).getText should be("I")
    tokens(1).getText should be("received")
    tokens(2).getText should be("the")
    tokens(3).getText should be("book")

    getPos(tokens(0)).mkString should be("PRP")
    getPos(tokens(1)).mkString should be("VBD")
    getPos(tokens(2)).mkString should be("DT")
    getPos(tokens(3)).mkString should be("NN")

    getLemma(tokens(0)).mkString should be("i")
    getLemma(tokens(1)).mkString should be("receive")
    getLemma(tokens(2)).mkString should be("the")
    getLemma(tokens(3)).mkString should be("book")

    getDependencyRelation(tokens(0)).mkString should be("nsubj")
    getDependencyRelation(tokens(1)).mkString should be("")
    getDependencyRelation(tokens(2)).mkString should be("det")
    getDependencyRelation(tokens(3)).mkString should be("dobj")


  }
}
