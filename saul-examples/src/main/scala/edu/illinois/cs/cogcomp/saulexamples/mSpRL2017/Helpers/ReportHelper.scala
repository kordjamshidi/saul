package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers

import java.io.{ FileOutputStream, PrintStream, PrintWriter }

import edu.illinois.cs.cogcomp.saul.classifier.Results
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLDataModel.dummyPhrase
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{ NlpBaseElement, Phrase, Relation, Token }
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval._

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.{ break, breakable }

/** Created by taher on 2017-02-28.
  */
object ReportHelper {

  def reportRelationResults(
    resultsDir: String,
    resultFilePrefix: String,
    a: List[Relation],
    p: List[Relation],
    comparer: EvalComparer
  ) = {
    val actual = a.map(r => (r, getRelationEval(r)))
    val predicted = p.map(r => (r, getRelationEval(r)))

    val tp = ListBuffer[(Relation, Relation)]()
    actual.foreach { a =>
      breakable {
        predicted.foreach { p =>
          if (comparer.isEqual(a._2, p._2)) {
            tp += ((a._1, p._1))
            break()
          }
        }
      }
    }
    val fp = predicted.filterNot(x => tp.exists(_._2 == x._1))
    val fn = actual.filterNot(x => tp.exists(_._1 == x._1))

    var writer = new PrintWriter(s"$resultsDir/${resultFilePrefix}-fp.txt")
    fp.groupBy(x => getDocumentId(x._1.getArgument(1))).toList.sortBy(_._1).foreach {
      case (key, list) => {
        writer.println(s"===================================== ${key} ==================================")
        list.foreach {
          case (r, _) =>
            writer.println(s"${r.getArgument(0).getText} -> ${r.getArgument(1).getText} -> ${r.getArgument(2).getText}")
        }
      }
    }
    writer.close()

    writer = new PrintWriter(s"$resultsDir/${resultFilePrefix}-fn.txt")
    fn.groupBy(x => getDocumentId(x._1.getArgument(1))).toList.sortBy(_._1).foreach {
      case (key, list) => {
        writer.println(s"===================================== ${key} ==================================")
        list.foreach {
          case (r, _) =>
            val args = r.getArguments.toList
            writer.println(s"${r.getId} : ${args(0).getText} -> ${args(1).getText} -> ${args(2).getText}")
        }
      }
    }
    writer.close()

    writer = new PrintWriter(s"$resultsDir/${resultFilePrefix}-tp.txt")
    tp.groupBy(x => getDocumentId(x._1.getArgument(1))).toList.sortBy(_._1).foreach {
      case (key, list) => {
        writer.println(s"===================================== ${key} ==================================")
        list.foreach {
          case (a, p) =>
            val actualArgs = a.getArguments.toList
            val predictedArgs = p.getArguments.toList
            writer.println(s"${a.getId} : ${actualArgs(0).getText} -> ${actualArgs(1).getText} -> " +
              s"${actualArgs(2).getText}   ${predictedArgs(0).getText} -> ${predictedArgs(1).getText} -> " +
              s"${predictedArgs(2).getText}")
        }
      }
    }
    writer.close()

    val evaluator = new SpRLEvaluator()
    val actualEval = new RelationsEvalDocument(actual.map(_._2))
    val predictedEval = new RelationsEvalDocument(predicted.map(_._2))
    val results = evaluator.evaluateRelations(actualEval, predictedEval, comparer)
    evaluator.printEvaluation(results)
    results
  }

  def saveCandidateList(isTrain: Boolean, candidateRelations: List[Relation]): Unit = {

    def getArg(i: Int, r: Relation) = r.getArgument(i).getText.toLowerCase

    def print(r: Relation) = {
      MultiModalSpRLClassifiers.relationFeatures(FeatureSets.BaseLine)
        .map(prop => printVal(prop(r))).mkString(" | ")
    }

    def printVal(v: Any) = {
      v match {
        case x: List[_] => x.mkString(", ")
        case _ => v.toString
      }
    }

    val name = if (isTrain) "Train" else "Test"
    val writer = new PrintWriter(s"data/mSprl/results/RoleCandidates-${name}.txt")
    candidateRelations.foreach(x =>
      writer.println(s"(${getArg(0, x)}, ${getArg(1, x)})[${print(x)}] -> ${x.getProperty("RelationType")}"))
    writer.close()
  }

  def saveEvalResults(stream: FileOutputStream, caption: String, results: Results): Unit =
    saveEvalResults(stream, caption, convertToEval(results))

  def saveEvalResults(stream: FileOutputStream, caption: String, results: Seq[SpRLEvaluation]): Unit = {
    val writer = new PrintStream(stream, true)
    writer.println("===========================================================================")
    writer.println(s" ${caption}")
    writer.println("---------------------------------------------------------------------------")
    SpRLEvaluator.printEvaluation(stream, results)
    writer.println()
  }

  def reportRoleStats(instances: List[NlpBaseElement], candidates: List[NlpBaseElement], tagName: String): Unit = {

    val roleInstances = instances.filter(_.containsProperty(s"${tagName}_id"))
    val actual = roleInstances.map(_.getPropertyValues(s"${tagName}_id").size()).sum
    val missingTokens = roleInstances.toSet.diff(candidates.toSet).toList.map(_.getText.toLowerCase())
    val missing = actual - candidates.map(_.getPropertyValues(s"${tagName}_id").size()).sum

    println(s"Candidate ${tagName}: ${candidates.size}")
    println(s"Actual ${tagName}: $actual")
    println(s"Missing ${tagName} in the candidates: $missing (${missingTokens.mkString(", ")})")
  }

  def reportRelationStats(candidateRelations: List[Relation], goldTrajectorRelations: List[Relation],
    goldLandmarkRelations: List[Relation]): Unit = {

    val missedTrSp = goldTrajectorRelations.size - candidateRelations.count(_.getProperty("RelationType") == "TR-SP")
    println(s"actual TR-SP: ${goldTrajectorRelations.size}")
    println(s"Missing TR-SP in the candidates: $missedTrSp")
    val missingTrRelations = goldTrajectorRelations
      .filterNot(r => candidateRelations.exists(x => x.getProperty("RelationType") == "TR-SP" && x.getId == r.getId))
      .map(_.getId)
    println(s"missing relations from TR-SP: (${missingTrRelations.mkString(", ")})")

    val missedLmSp = goldLandmarkRelations.size - candidateRelations.count(_.getProperty("RelationType") == "LM-SP")
    println(s"actual LM-SP: ${goldLandmarkRelations.size}")
    println(s"Missing LM-SP in the candidates: $missedLmSp")
    val missingLmRelations = goldLandmarkRelations
      .filterNot(r => candidateRelations.exists(x => x.getProperty("RelationType") == "LM-SP" && x.getId == r.getId))
      .map(_.getId)
    println(s"missing relations from LM-SP: (${missingLmRelations.mkString(", ")})")
  }

  private def convertToEval(r: Results): Seq[SpRLEvaluation] = r.perLabel
    .map(x => new SpRLEvaluation(x.label, x.precision * 100, x.recall * 100, x.f1 * 100, x.labeledSize, x.predictedSize))

  private def getRelationEval(r: Relation): RelationEval = {
    val tr = r.getArgument(0)
    val sp = r.getArgument(1)
    val lm = r.getArgument(2)
    val offset = sp match {
      case x: Token => x.getSentence.getStart
      case x: Phrase => x.getSentence.getStart
    }
    val lmStart = if (notNull(lm)) offset + lm.getStart else -1
    val lmEnd = if (notNull(lm)) offset + lm.getEnd else -1
    val trStart = if (notNull(tr)) offset + tr.getStart else -1
    val trEnd = if (notNull(tr)) offset + tr.getEnd else -1
    val spStart = offset + sp.getStart
    val spEnd = offset + sp.getEnd
    new RelationEval(trStart, trEnd, spStart, spEnd, lmStart, lmEnd)
  }

  private def notNull(t: NlpBaseElement) = {
    t != null && t.getId != dummyPhrase.getId && t.getStart >= 0
  }

  private def getDocumentId(e: NlpBaseElement) = {
    e match {
      case x: Token => x.getDocument.getId
      case x: Phrase => x.getDocument.getId
      case _ => e.asInstanceOf[Token].getDocument.getId
    }
  }
}
