// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000B4ECFCB2E292A4CCCCB2150FAC7029EA14A25C5A94959A9C52529FA1EC9F979393149A939852999F97A052A9A05DA0E0109A545C9F971E9F549DE69F54A10413DF815070FF2A4F4CCBCCA20BA348495B24D200172979CDF5000000

package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;

import LBJ2.classify.FeatureVector;
import LBJ2.infer.FirstOrderConjunction;
import LBJ2.infer.FirstOrderConstant;
import LBJ2.infer.FirstOrderConstraint;
import LBJ2.infer.ParameterizedConstraint;
import ml.wolfe.examples.parisa.ConllRelation;


public class JointER$subjectto extends ParameterizedConstraint
{
  private static final ml.wolfe.examples.parisa.iJLIS2.PersonWorkFor __PersonWorkFor = new ml.wolfe.examples.parisa.iJLIS2.PersonWorkFor();
  private static final ml.wolfe.examples.parisa.iJLIS2.OrganizationWorkFor __OrganizationWorkFor = new ml.wolfe.examples.parisa.iJLIS2.OrganizationWorkFor();

  public JointER$subjectto() { super("ml.wolfe.examples.parisa.iJLIS2.JointER$subjectto"); }

  public String getInputType() { return "ml.wolfe.examples.parisa.ConllRelation"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof ConllRelation))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Constraint 'JointER$subjectto(ConllRelation)' defined on line 127 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    ConllRelation t = (ConllRelation) __example;

    {
      boolean LBJ2$constraint$result$0;
      {
        boolean LBJ2$constraint$result$1;
        LBJ2$constraint$result$1 = __PersonWorkFor.discreteValue(t).equals("true");
        if (LBJ2$constraint$result$1)
          LBJ2$constraint$result$0 = __OrganizationWorkFor.discreteValue(t).equals("true");
        else LBJ2$constraint$result$0 = false;
      }
      if (!LBJ2$constraint$result$0) return "false";
    }

    return "true";
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof ConllRelation[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'JointER$subjectto(ConllRelation)' defined on line 127 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "JointER$subjectto".hashCode(); }
  public boolean equals(Object o) { return o instanceof JointER$subjectto; }

  public FirstOrderConstraint makeConstraint(Object __example)
  {
    if (!(__example instanceof ConllRelation))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Constraint 'JointER$subjectto(ConllRelation)' defined on line 127 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    ConllRelation t = (ConllRelation) __example;
    FirstOrderConstraint __result = new FirstOrderConstant(true);

    {
        FirstOrderConstraint LBJ2$constraint$result$0 = null;
      {
        FirstOrderConstraint LBJ2$constraint$result$1 = null;
        LBJ2$constraint$result$1 = __PersonWorkFor.makeConstraint(t);
        FirstOrderConstraint LBJ2$constraint$result$2 = null;
        LBJ2$constraint$result$2 = __OrganizationWorkFor.makeConstraint(t);
        LBJ2$constraint$result$0 = new FirstOrderConjunction(LBJ2$constraint$result$1, LBJ2$constraint$result$2);
      }
      __result = new FirstOrderConjunction(__result, LBJ2$constraint$result$0);
    }

    return __result;
  }
}

