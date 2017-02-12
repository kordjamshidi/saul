package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import java.io.File

import edu.illinois.cs.cogcomp.saul.util.Logging
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.{ClefDocument, SpRLDataReader}
import org.apache.commons.io.{FileUtils, IOUtils}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator._
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory

import scala.collection.JavaConversions._

/**
  * Created by Taher on 2017-02-12.
  */
object WordEmbedding extends App with Logging {

  val vec = build()
  //val vec = load()
  println(vec.wordsNearest("girl", 5))


  private def load() = WordVectorSerializer.readWord2VecModel("data/clef.bin")
  private def build() = {
    val iter = new CustomSentenceIterator("data/SpRL/CLEF/texts/", ".eng")
    iter.setPreProcessor(new SentencePreProcessor() {
      override def preProcess(s: String): String = s.toLowerCase
    })

    val tokenizer = new DefaultTokenizerFactory()
    tokenizer.setTokenPreProcessor(new CommonPreprocessor())

    val vec = new Word2Vec.Builder()
      .minWordFrequency(5)
      .iterations(1000)
      .layerSize(300) // word vector size
      .windowSize(5)
      .iterate(iter)
      .tokenizerFactory(tokenizer)
      .build()
    vec.fit()
    WordVectorSerializer.writeWord2VecModel(vec, "data/clef.bin")
    vec
  }
}

class CustomSentenceIterator(corpusPath: String, extension: String) extends BaseSentenceIterator {

  private val reader = new SpRLDataReader(corpusPath, classOf[ClefDocument], extension)
  reader.readData()
  private val docs = reader.documents
  private val lines = docs.flatMap(_.getDescription.split(";").map(_.trim)).filter(_ != "")
  private var iter = lines.toIterator

  override def nextSentence(): String = {
    val line = this.iter.next
    if (this.preProcessor != null)
      this.preProcessor.preProcess(line)
    else
      line
  }

  override def hasNext: Boolean = iter.hasNext

  override def reset(): Unit = {
    iter = lines.toIterator
  }
}