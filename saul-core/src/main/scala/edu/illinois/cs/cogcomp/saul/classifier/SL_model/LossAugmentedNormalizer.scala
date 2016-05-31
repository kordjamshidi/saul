package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet
import edu.illinois.cs.cogcomp.lbjava.learn.{ Learner, Normalizer }
import edu.illinois.cs.cogcomp.saul.classifier.SparseNetworkLBP

/** Created by Parisa on 4/18/16.
  */
class LossAugmentedNormalizer(cand_num: Int, c: Learner, example: AnyRef) extends Normalizer {
  /** Simply returns the argument.
    *
    * @param scores The set of scores to normalize.
    * @return The normalized set of scores.
    */
  def normalize(scores: ScoreSet): ScoreSet = {
    if (cand_num == 0)
      print("There is no relevant component of this type in the head to be classified.")
    val cf = c.asInstanceOf[SparseNetworkLBP]
    val gold = cf.getLabeler.discreteValue(example)
    val lLexicon = cf.getLabelLexicon

    val resultS: ScoreSet = cf.scores(example) //new ScoreSet
    for (i <- 0 until lLexicon.size()) {
      if (lLexicon.lookupKey(i).valueEquals(gold))
        resultS.put(lLexicon.lookupKey(i).getStringValue, resultS.getScore(lLexicon.lookupKey(i).getStringValue).score - (1 / (cand_num)))
      else
        resultS.put(lLexicon.lookupKey(i).getStringValue, resultS.getScore(lLexicon.lookupKey(i).getStringValue).score + (1 / (cand_num)))
    }
    resultS
  }
}
