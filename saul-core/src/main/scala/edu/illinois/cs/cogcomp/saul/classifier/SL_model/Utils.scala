package edu.illinois.cs.cogcomp.saul.classifier.SL_model
import Array._
/**
 * Created by Parisa on 4/1/16.
 */
object Utils {
 def converFarrayToD(a: Array[Float]):Array[Double]={
  var d:Array[Double]=ofDim[Double](a.length)
   for (i<-0 until a.length){
     d.update(i,Float.float2double(a(i)))
   }
  d
 }
}
