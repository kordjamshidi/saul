// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000B4ECFCB2E292A4CCCCB2150FF2A4F4CCBCCAA4C29CCCFCB0FCF2AC67BCF22D07ECFCBC9C90A4DC10B0A24986A245B249305A2E3D2FB8C93721B8B8333D2335B843082E6565A052545A9AA06B67826E02B22DB45328BA3B658A500087F6B885A7000000
package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;
import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;
import edu.illinois.cs.cogcomp.lbjava.infer.*;


import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation;
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.OrganizationClassifier;
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.work_forClassifier;

public class OrganizationWorkFor extends ParameterizedConstraint
{
  private static final work_forClassifier __work_forClassifier = new work_forClassifier();
  private static final OrganizationClassifier __OrganizationClassifier = new OrganizationClassifier();

  public OrganizationWorkFor() { super("ml.wolfe.examples.parisa.iJLIS2.OrganizationWorkFor"); }

  public String getInputType() { return "ml.wolfe.examples.parisa.ConllRelation"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof ConllRelation))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Constraint 'OrganizationWorkFor(ConllRelation)' defined on line 118 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    ConllRelation t = (ConllRelation) __example;

    {
      boolean LBJ2$constraint$result$0;
      {
        boolean LBJ2$constraint$result$1;
        LBJ2$constraint$result$1 = ("" + (__work_forClassifier.discreteValue(t))).equals("" + (true));
        if (LBJ2$constraint$result$1)
          LBJ2$constraint$result$0 = ("" + (__OrganizationClassifier.discreteValue(t.e2))).equals("" + (true));
        else LBJ2$constraint$result$0 = true;
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
      System.err.println("Classifier 'OrganizationWorkFor(ConllRelation)' defined on line 118 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "OrganizationWorkFor".hashCode(); }
  public boolean equals(Object o) { return o instanceof OrganizationWorkFor; }

  public FirstOrderConstraint makeConstraint(Object __example)
  {
    if (!(__example instanceof ConllRelation))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Constraint 'OrganizationWorkFor(ConllRelation)' defined on line 118 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    ConllRelation t = (ConllRelation) __example;
      FirstOrderConstraint __result = new FirstOrderConstant(true);

    {
        FirstOrderConstraint LBJ2$constraint$result$0 = null;
      {
          FirstOrderConstraint LBJ2$constraint$result$1 = null;
        LBJ2$constraint$result$1 = new FirstOrderEqualityWithValue(true, new FirstOrderVariable(__work_forClassifier, t), "" + (true));
          FirstOrderConstraint LBJ2$constraint$result$2 = null;
        LBJ2$constraint$result$2 = new FirstOrderEqualityWithValue(true, new FirstOrderVariable(__OrganizationClassifier, t.e2), "" + (true));
        LBJ2$constraint$result$0 = new FirstOrderImplication(LBJ2$constraint$result$1, LBJ2$constraint$result$2);
      }
      __result = new FirstOrderConjunction(__result, LBJ2$constraint$result$0);
    }

    return __result;
  }
}

