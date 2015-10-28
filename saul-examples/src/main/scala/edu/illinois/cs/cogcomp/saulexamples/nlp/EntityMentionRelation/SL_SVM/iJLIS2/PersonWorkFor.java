// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000B4ECFCB2E292A4CCCCB2158084D2A2ECFCB0FCF2AC67BCF22D07ECFCBC9C90A4DC94C29CCCFC35821D458A658270A45C7A5E71937E426171766A566A6196005CDACA41A4A8A43551C6DE0A6202B4BE5AA12C5585B24D20001EE1FFCBE6000000
package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;

import LBJ2.classify.FeatureVector;
import LBJ2.infer.*;
import ml.wolfe.examples.parisa.ConllRelation;


public class PersonWorkFor extends ParameterizedConstraint
{
  private static final work_forClassifier __work_forClassifier = new work_forClassifier();
  private static final PersonClassifier __PersonClassifier = new PersonClassifier();

  public PersonWorkFor() { super("ml.wolfe.examples.parisa.iJLIS2.PersonWorkFor"); }

  public String getInputType() { return "ml.wolfe.examples.parisa.ConllRelation"; }

  public String discreteValue(Object __example)
  {
    if (!(__example instanceof ConllRelation))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Constraint 'PersonWorkFor(ConllRelation)' defined on line 113 of LALModel.lbj received '" + type + "' as input.");
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
          LBJ2$constraint$result$0 = ("" + (__PersonClassifier.discreteValue(t.e1))).equals("" + (true));
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
      System.err.println("Classifier 'PersonWorkFor(ConllRelation)' defined on line 113 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "PersonWorkFor".hashCode(); }
  public boolean equals(Object o) { return o instanceof PersonWorkFor; }

  public FirstOrderConstraint makeConstraint(Object __example)
  {
    if (!(__example instanceof ConllRelation))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Constraint 'PersonWorkFor(ConllRelation)' defined on line 113 of LALModel.lbj received '" + type + "' as input.");
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
        LBJ2$constraint$result$2 = new FirstOrderEqualityWithValue(true, new FirstOrderVariable(__PersonClassifier, t.e1), "" + (true));
        LBJ2$constraint$result$0 = new FirstOrderImplication(LBJ2$constraint$result$1, LBJ2$constraint$result$2);
      }
      __result = new FirstOrderConjunction(__result, LBJ2$constraint$result$0);
    }

    return __result;
  }
}

