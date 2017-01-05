/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.data.ImageReader
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.mSpRL2017DataModel._
import scala.collection.JavaConversions._

object mSpRL2017App extends App {

  val image_reader = new ImageReader("data/msprl")

  val imageList = image_reader.getImages()
  val segementList = image_reader.getSegments()
  val relationList = image_reader.getSegmentsRelations()

  image.populate(imageList)
  segment.populate(segementList)
  relation.populate(relationList)
  print((image() ~> image_segment).size)

}
