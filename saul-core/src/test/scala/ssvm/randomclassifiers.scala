package ssvm

import edu.illinois.cs.cogcomp.lbjava.learn.SparseAveragedPerceptron
import edu.illinois.cs.cogcomp.saul.classifier.Learnable

/** Created by Parisa on 10/14/16.
  */
object randomClassifiers {
  import randomDataModel._
  object bClassifier extends Learnable[String](randomNode) {
    def label = binaryLabel
    override def feature = using(randomProperty)
    override lazy val classifier = new SparseAveragedPerceptron()
    override val useCache = true
  }
}
