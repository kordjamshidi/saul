/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.SL_model

import edu.illinois.cs.cogcomp.sl.core.IInstance

/** Created by Parisa on 12/10/15.
  */
case class Saul_SL_Instance[HEAD <: AnyRef](x: HEAD) extends IInstance {
  val head: HEAD = x
}

