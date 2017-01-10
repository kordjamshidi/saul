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

  val imageReader = new ImageReader("./saul-examples/src/test/resources/mSprl")

  val imageList = imageReader.getImages()
  val segementList = imageReader.getSegments()
  val relationList = imageReader.getSegmentsRelations()

  images.populate(imageList)
  segments.populate(segementList)
  relations.populate(relationList)
  print((images() ~> imagesToSegments).size)
  print((relations() ~> relationsToSegments).size)

}
