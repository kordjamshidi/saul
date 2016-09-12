/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.classifier.infer

import edu.illinois.cs.cogcomp.lbjava.infer.{ ParameterizedConstraint }
import edu.illinois.cs.cogcomp.lbjava.learn.{ IdentityNormalizer, Learner, Normalizer }
import edu.illinois.cs.cogcomp.saul.constraint.LfsConstraint
import edu.illinois.cs.cogcomp.infer.ilp.ILPSolver
import scala.reflect.ClassTag

abstract class InferenceCondition[INPUT <: AnyRef, HEAD <: AnyRef](solver: ILPSolver, normalize: Normalizer = new IdentityNormalizer)(
  implicit
  val inputTag: ClassTag[INPUT],
  val headTag: ClassTag[HEAD]
  ) {
  def subjectTo: LfsConstraint[HEAD]

  def transfer(t: HEAD): JointTemplate[HEAD] = {
    new JointTemplate[HEAD](t, solver, normalize) {
      // TODO: Define this function
      override def getSubjectToInstance: ParameterizedConstraint = {
        subjectTo.transfer
      }
      //verbosity = 1
      // TODO: override other functions that needed here
      override def getNormalizer(c: Learner): Normalizer = {
        normalize
      }
    }
  }

  def apply(head: HEAD): JointTemplate[HEAD] = {
    this.transfer(head)
  }

  val outer = this
  def convertToType[T <: AnyRef](implicit tag: ClassTag[T]): InferenceCondition[T, HEAD] = this.asInstanceOf[InferenceCondition[T, HEAD]]
}
