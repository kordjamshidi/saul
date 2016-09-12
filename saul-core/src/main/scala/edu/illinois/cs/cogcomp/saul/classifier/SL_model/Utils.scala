/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.SL_model
import Array._
/** Created by Parisa on 4/1/16.
  */
object Utils {
  def converFarrayToD(a: Array[Float]): Array[Double] = {
    var d: Array[Double] = ofDim[Double](a.length)
    for (i <- 0 until a.length) {
      d.update(i, Float.float2double(a(i)))
    }
    d
  }
}
