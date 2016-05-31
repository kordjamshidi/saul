package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.sl.core.IInstance

/** Created by Parisa on 12/10/15.
  */
case class Saul_SL_Instance[HEAD <: AnyRef](x: HEAD) extends IInstance {
  val head: HEAD = x
}

