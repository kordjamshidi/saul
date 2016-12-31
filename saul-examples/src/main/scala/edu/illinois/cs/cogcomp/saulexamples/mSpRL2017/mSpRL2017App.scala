/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.data.ImageReader
import scala.collection.JavaConversions._

object mSpRL2017App  extends App {

  val trainData = new ImageReader("C:/Users/Umar Manzoor/Documents/GitHub/saul/mSprl/images").images.toList

  mSpRL2017DataModel.image populate trainData




}
