/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes._
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml._
import SpRLNewSensors._

import scala.collection.JavaConversions._

/**
  * Created by parisakordjamshidi on 12/22/16.
  */
object SpRLNewGenerationDataModel extends DataModel {


  /*
  Nodes
   */
  val documents = node[Document]
  val sentences = node[Sentence]
  val tokens = node[Token]
  val phrases = node[Phrase]
  val relations = node[Relation]

  /*
  Edges
   */

  val docTosen = edge(documents, sentences)
  docTosen.addSensor(DocToSentenceMatching _)

  val sentenceToPhrase = edge(sentences, phrases)
  sentenceToPhrase.addSensor(SentencePhraseGenerating _)

  //
  val relToTr = edge(relations, phrases)
  relToTr.addSensor(RelToTrMatching _)


  val relToLm = edge(relations, phrases)

  relToLm.addSensor(RelToLmMatching _)

  val relToSp = edge(relations, phrases)
  relToSp.addSensor(RelToSpMatching _)
  /*
     Properties
  */

  val testPhraseProperty = property(phrases) {
    x: Phrase => x.getPropertyValue("TESTPROP_first_value")
  }
  val testDocumentProperty = property(documents) {
    x: Document => x.getPropertyValue("test")
  }

  val pos = property(phrases) {
    x: Phrase => getPos(x).mkString(",")
  }
  val lemma = property(phrases) {
    x: Phrase => getLemma(x).mkString(",")
  }

  val isTrajector = property(phrases) {
    x: Phrase => x.getPropertyValues("TRAJECTOR_id").nonEmpty
  }
  val isLandmark = property(phrases) {
    x: Phrase =>
      x.getPropertyValue("LANDMARK_id") != null
  }

  //val isSpIndicator = property(phrases)


  // when we have the annotation in the xml then we need to just use a matching sensor
  // docTosen.addSensor(a_matchingSensor)

  //when we want to use our NLP tools then we use generating sensors
  // sentenceToToken.addSensor(a_generatingSensor)
  //  val spPairs = join(tokens,tokens)
  // val spTriplets = join(spPairs,spatialIndicators)
  //  val isTrajector= property(tokens){
  //    x: Token => x.hasXmlAttribute("trajector") // or  x.hastag("trajector")
  // each base type has two methods of hasXMLTag or hasXMLAttribute that recieves a string as input.
  //  }
  //  val lemma = property (tokens) {
  //    x:Token => TextAnnotationSensor(x)
  //  }
}

object SpRLApp2 extends App {

  import SpRLNewGenerationDataModel._

  val reader = new NlpXmlReader("./saul-examples/src/test/resources/SpRL/2017/test.xml", "SCENE", "SENTENCE", null, null)
  val documentList = reader.getDocuments()
  val sentencesList = reader.getSentences()

  reader.setPhraseTagName("TRAJECTOR")
  val trajectorList = reader.getPhrases()
  reader.setPhraseTagName("LANDMARK")
  val landmarkList = reader.getPhrases()
  reader.setPhraseTagName("SPATIALINDICATOR")
  val spIndicatorList = reader.getPhrases()

  val relationList = reader.getRelations("RELATION", "trajector_id", "spatial_indicator_id", "landmark_id")

  documents.populate(documentList)
  sentences.populate(sentencesList)

  //reader.addPropertiesFromTag("TRAJECTOR", phrases().toList, new XmlPartOfMatching)
  reader.addPropertiesFromTag("TRAJECTOR", phrases().toList, XmlMatchings.headwordMatching)
  reader.addPropertiesFromTag("LANDMARK", phrases().toList, new XmlPartOfMatching)
  reader.addPropertiesFromTag("SPATIALINDICATOR", phrases().toList, new XmlPartOfMatching)
  relations.populate(relationList)

  val trCandidates = null :: phrases().filter(x => getPos(x).contains("NN") && x.getPropertyValues("TRAJECTOR_id").isEmpty).toList
  val spCandidates = phrases().filter(x => getPos(x).contains("IN") && x.getPropertyValues("SPATIALINDICATOR_id").isEmpty).toList
  val lmCandidates = null :: phrases().filter(x => getPos(x).contains("NN") && x.getPropertyValues("LANDMARK_id").isEmpty).toList
  val candidateRelations = getCandidateRelations[Phrase](args => args.filter(_ != null).groupBy(_.getSentence.getId).size <= 1, trCandidates, spCandidates, lmCandidates)
  relations.populate(candidateRelations)

  println(candidateRelations.size)
  println(relations().size)
  println("trajectors in the model:", relations() ~> relToTr, "actual trajectors:", trajectorList)
  println("trajectors in the model:", relations() ~> relToTr prop isTrajector, "actual trajectors:", trajectorList)
  println("landmarks in the model:", relations() ~> relToLm, "actual landmarks:", landmarkList)
  println("indicators in the model:", relations() ~> relToSp, "actual indicators:", spIndicatorList)

  val d = documents() ~> docTosen
  println("sentence num:", d.size, "should be equal to", sentencesList.length, "should be equal to", sentences().size)

  println("phrase 1 pos tags:" + pos(phrases().head))
  println("phrase 1 lemma :" + lemma(phrases().head))
  println("phrease 1 sentence:" + (phrases(phrases().head) <~ sentenceToPhrase).head.getText)
  println("number of sentences connected to the phrases:", phrases() <~ sentenceToPhrase size, "sentences:", sentences().size)


  private def getCandidateRelations[T <: NlpBaseElement](jointFilter: List[T] => Boolean, argumentInstances: List[T]*): List[Relation] = {
    if (argumentInstances.length < 2) {
      List.empty
    }
    else {
      crossProduct(argumentInstances.seq.toList)
        .filter(jointFilter)
        .map(args => {
          val r = new Relation()
          args.zipWithIndex.filter(x => x._1 != null).foreach {
            case (a, i) => {
              r.setArgumentId(i, a.getId)
              r.setId(r.getId + "[" + i + ", " + a.getId + "]")
            }
          }
          r
        }).filter(r => distinctArguments(r))
    }
  }

  def distinctArguments(r: Relation): Boolean = {
    (for (i <- Range(0, r.getArgumentsCount); j <- Range(0, r.getArgumentsCount)) yield (i, j))
      .forall { case (i, j) =>
        i == j || r.getArgumentId(i) != r.getArgumentId(j) || (r.getArgumentId(i) == null && r.getArgumentId(j) == null)
      }
  }

  def crossProduct[T](input: List[List[T]]): List[List[T]] = input match {
    case Nil => Nil
    case head :: Nil => head.map(_ :: Nil)
    case head :: tail => for (elem <- head; sub <- crossProduct(tail)) yield elem :: sub
  }


}
