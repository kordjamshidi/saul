package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.Helpers

import java.io.{FileOutputStream, PrintStream, PrintWriter}

import edu.illinois.cs.cogcomp.saul.classifier.Results
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.MultiModalSpRLClassifiers
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.{Phrase, Relation, Token}
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval.{RelationEval, SpRLEvaluation, SpRLEvaluator}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.{break, breakable}

/**
  * Created by taher on 2017-02-28.
  */
object ReportHelper {

  def reportRelationResults(resultsDir: String,
                            resultFilePrefix: String,
                            actual: List[(Relation, RelationEval)],
                            predicted: List[(Relation, RelationEval)]
                           ): Unit = {
    val tp = ListBuffer[(Relation, Relation)]()
    actual.zipWithIndex.foreach { case (a, i) =>
      breakable {
        predicted.zipWithIndex.foreach { case (p, j) =>
          if (a._2.isEqual(p._2)) {
            tp += ((a._1, p._1))
            break()
          }
        }
      }
    }
    val fp = predicted.filterNot(x => tp.exists(_._2 == x._1))
    val fn = actual.filterNot(x => tp.exists(_._1 == x._1))
    actual.foreach(x => {
      x._1.getArguments.foreach(a => a.setText(a.getPropertyFirstValue("headWord"))) // convert phrase text to headword
    })

    var writer = new PrintWriter(s"$resultsDir/${resultFilePrefix}_triplet-fp.txt")
    fp.groupBy(_._1.getArgument(1).asInstanceOf[Token].getDocument.getId).toList.sortBy(_._1).foreach {
      case (key, list) => {
        writer.println(s"===================================== ${key} ==================================")
        list.foreach { case (r, _) =>
          writer.println(s"${r.getArgument(0).getText} -> ${r.getArgument(1).getText} -> ${r.getArgument(2).getText}")
        }
      }
    }
    writer.close()

    writer = new PrintWriter(s"$resultsDir/${resultFilePrefix}_triplet-fn.txt")
    fn.groupBy(_._1.getArgument(1).asInstanceOf[Phrase].getDocument.getId).toList.sortBy(_._1).foreach {
      case (key, list) => {
        writer.println(s"===================================== ${key} ==================================")
        list.foreach { case (r, _) =>
          val args = r.getArguments.map(_.asInstanceOf[Phrase]).toList
          writer.println(s"${r.getId} : ${args(0).getText} -> ${args(1).getText} -> ${args(2).getText}")
        }
      }
    }
    writer.close()

    writer = new PrintWriter(s"$resultsDir/${resultFilePrefix}_triplet-tp.txt")
    tp.groupBy(_._1.getArgument(1).asInstanceOf[Phrase].getDocument.getId).toList.sortBy(_._1).foreach {
      case (key, list) => {
        writer.println(s"===================================== ${key} ==================================")
        list.foreach { case (a, p) =>
          val actualArgs = a.getArguments.toList
          val predictedArgs = p.getArguments.toList
          writer.println(s"${a.getId} : ${actualArgs(0).getText} -> ${actualArgs(1).getText} -> " +
            s"${actualArgs(2).getText}   ${actualArgs(0).getText} -> ${actualArgs(1).getText} -> " +
            s"${actualArgs(2).getText}")
        }
      }
    }
    writer.close()
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
      writer.println(s"(${getArg(0, x)}, ${getArg(1, x)})[${print(x)}] -> ${x.getProperty("RelationType")}")
    )
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

  def reportRoleStats(tokenInstances: List[Token], candidates: List[Token], tagName: String): Unit = {

    val instances = tokenInstances.filter(_.containsProperty(s"${tagName}_id"))
    val actual = instances.map(_.getPropertyValues(s"${tagName}_id").size()).sum
    val missingTokens = instances.toSet.diff(candidates.toSet).toList.map(_.getText.toLowerCase())
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
}
