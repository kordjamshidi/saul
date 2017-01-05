package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling

import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.ImageReader
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Image

import scala.collection.JavaConversions._

/** Created by parisakordjamshidi on 12/26/16.
  */
object SpRL_image_DataModel extends DataModel {

  val images = node[Image]

  val image_lables = property(images) {
    x: Image => x.getLabel
  }
}
object SpRL_image_App extends App {

  val reader = new ImageReader("") /*what should be read here?
 I guess we need to read the list of the files in the 00/images directory
 We need to keep the file name of each image as its name for our later reference also to connect ot the documents
 that contain the text part*/

}
