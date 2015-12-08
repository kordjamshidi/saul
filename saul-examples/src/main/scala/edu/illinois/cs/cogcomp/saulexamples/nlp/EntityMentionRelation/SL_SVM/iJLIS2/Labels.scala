package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2

/** Created by parisakordjamshidi on 23/09/14.
  */
class Labels {
  var E1Label: String = "";
  var E2Label: String = "";
  var RelLabel: String = "";
  // val labels: util.ArrayList[String]=new  util.ArrayList[String]
  def set(x: String, y: String, z: String) {
    E1Label = x;
    E2Label = y;
    RelLabel = z;
  }
  def LinirizLabels(): collection.mutable.MutableList[collection.mutable.MutableList[Boolean]] =
    {
      var a: collection.mutable.MutableList[Boolean] = scala.collection.mutable.MutableList(E1Label.toLowerCase.contains("peop"))
      a += E2Label.contains("peop")
      var b: collection.mutable.MutableList[collection.mutable.MutableList[Boolean]] = collection.mutable.MutableList(a)

      a = collection.mutable.MutableList(E1Label.toLowerCase.contains("org"))
      a += E2Label.toLowerCase.contains("org")

      b += a

      a = collection.mutable.MutableList(RelLabel.toLowerCase.contains("work"))
      a += E2Label.toLowerCase.contains("work")

      b += a

      //a+=RelLabel.contains("work_for")
      //E1Label="e"
      b
    }

}
