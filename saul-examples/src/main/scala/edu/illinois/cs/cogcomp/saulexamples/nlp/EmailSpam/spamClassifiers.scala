package edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam

import edu.illinois.cs.cogcomp.lbjava.learn.{ SupportVectorMachine, SparseNetworkLearner }
import edu.illinois.cs.cogcomp.saul.classifier.Learnable
import edu.illinois.cs.cogcomp.saul.constraint.ConstraintTypeConversion._
import edu.illinois.cs.cogcomp.saulexamples.data.Document
import edu.illinois.cs.cogcomp.saulexamples.nlp.EmailSpam.spamDataModel._

object SpamClassifiers {
  object SpamClassifier extends Learnable[Document](spamDataModel) {
    def label = spamLabel
    override lazy val classifier = new SupportVectorMachine()
    override def feature = using(wordFeature)
  }

  object SpamClassifierWithCache extends Learnable[Document](spamDataModel) {
    def label = spamLabel
    override lazy val classifier = new SupportVectorMachine()
    override def feature = using(wordFeature)
    override val useCache = true
  }

  object DeserializedSpamClassifier extends Learnable[Document](spamDataModel) {
    def label = spamLabel
    override lazy val classifier = new SupportVectorMachine()
    override def feature = using(wordFeature)
  }
}
