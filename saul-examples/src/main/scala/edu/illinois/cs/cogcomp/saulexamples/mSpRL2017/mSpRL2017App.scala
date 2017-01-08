/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.mSpRL2017

import edu.illinois.cs.cogcomp.saulexamples.data.ImageReader
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.mSpRL2017DataModel._
import edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.ImageClassifiers._
import scala.collection.JavaConversions._

object mSpRL2017App extends App {

  val imageReaderTraining = new ImageReader("data/msprl/train")
  val imageReaderTest = new ImageReader("data/msprl/test")

  val imageListTrain = imageReaderTraining.getImages()
  val segementListTrain = imageReaderTraining.getSegments()
  val relationListTrain = imageReaderTraining.getSegmentsRelations()

  images.populate(imageListTrain)
  segments.populate(segementListTrain)
  relation.populate(relationListTrain)


  val imageListTest = imageReaderTest.getImages()
  val segementListTest = imageReaderTest.getSegments()
  val relationListTest = imageReaderTest.getSegmentsRelations()

  images.populate(imageListTest)
  segments.populate(segementListTest)
  relation.populate(relationListTest)

  ImageSVMClassifier.learn(5)
  ImageSVMClassifier.test(segementListTest)

  ImageClassifierWeka.learn(5)
  ImageClassifierWeka.test(segementListTest)

//  print((images() ~> image_segment).size)
//  print ((relation()~>relationsToSegments).size)

}
