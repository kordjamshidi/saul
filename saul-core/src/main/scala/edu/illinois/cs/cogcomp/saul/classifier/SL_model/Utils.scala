package edu.illinois.cs.cogcomp.saul.classifier.SL_model

/**
 * Created by Parisa on 4/1/16.
 */
object Utils {
 def converFarrayToD(a: Array[Float]):Array[Double]={
  var d:Array[Double]=Array[Double]()
   a.foreach(x=>
     d :+ Float.float2double(x))
 d
 }
}
