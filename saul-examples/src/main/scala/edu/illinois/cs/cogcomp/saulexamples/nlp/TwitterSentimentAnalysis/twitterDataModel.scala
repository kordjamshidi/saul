/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.TwitterSentimentAnalysis

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Document, NlpBaseElement, Sentence, Token}
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors
import edu.illinois.cs.cogcomp.saulexamples.twitter.datastructures.Tweet
import sensors._
import edu.illinois.cs.cogcomp.saulexamples.nlp.LanguageBaseTypeSensors._

import scala.collection.JavaConversions._
/** Created by guest on 10/2/16.
  */
object twitterDataModel extends DataModel {

  val tweet = node[Tweet]
  val documents = node[Document]
  val sentences = node[Sentence]
  val tokens =node[Token]

  val tweetToDoc = edge(tweet,documents)
  tweetToDoc.addSensor(generateDocFromTweet _)

  val docToSentence = edge(documents,sentences)
  docToSentence.addSensor(documentToSentenceGenerating _)

  val sentToToken = edge(sentences,tokens)

  sentToToken.addSensor(sentenceToTokenGenerating _)

  val tokenText = property(tokens) {
    x: Token => x.getText
  }

  val tokenFeatures = property (tweet) {
    x: Tweet =>
     val a =  (tweet(x) ~>tweetToDoc~>docToSentence~>sentToToken) prop tokenText
   a.toList
  }

  val WordFeatures = property(tweet) {
    x: Tweet =>
      val a = x.getWords.toList
      a
  }

  val BigramFeatures = property(tweet) {
    x: Tweet => x.getWords.toList.sliding(2).map(_.mkString("-")).toList
  }

  val Label = property(tweet) {
    x: Tweet => x.getSentimentLabel
  }
}
