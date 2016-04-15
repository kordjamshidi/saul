package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityRelation.SL_SVM.iJLIS3

import scala.reflect.ClassTag

object Main {
 case class typeHolder  [HEAD](a: List[TwoTypeClass[_,HEAD]], x: HEAD){
   val head: HEAD = x
 }

abstract  class TwoTypeClass[T , HEAD ](val value: String)(implicit val xTag: ClassTag[T],
                                                implicit val yTag: ClassTag[HEAD]) {
    type L = T
    type R = HEAD

    def gen(head: HEAD): Seq[T]={
     val s:Seq[T] = null
      s
    }
  }


  def creator[HEAD](a : TwoTypeClass[_,HEAD ])( x: typeHolder) : Unit = {

    def subCreator(x:typeHolder)={
      x.head
    }
  }
    def main(args: Array[String]){
      val x: String= null
      def funtionOfType[HEAD](a : List[TwoTypeClass[_,HEAD]])(x:typeHolder) : Unit = {
        a.foreach({
        cf =>
           val t=cf.gen(x.head.asInstanceOf[HEAD])
            t.foreach {
              x=> creator(cf)_

            }

      })
    }
  }
}
