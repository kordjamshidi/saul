package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.mYjava;// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B88000000000000000D4D81BA020130144F756945908EDF0867702806522C592157490E2BBAB9C12274EFDD0A6175DC0CB16ED525E3B1A062738ADD3BB50939F5BA4FA69BB8720BFE55897F0E8529A05904BE52D449EA4EB4760E3CBF18E0FC1327EDED44D0D7CC0FFD7C3E64DC580D6DDC23A909FFB82CA8A218336E8466CAE70B03EE714B9000000

import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation;


public class workLabel extends Classifier
{
  public workLabel()
  {
    containingPackage = "";
    name = "workLabel";
  }

  public String getInputType() { return "ConllRelation"; }
  public String getOutputType() { return "discrete"; }

  private static String[] __allowableValues = new String[]{ "Works", "nWorks" };
  public static String[] getAllowableValues() { return __allowableValues; }
  public String[] allowableValues() { return __allowableValues; }


  public FeatureVector classify(Object __example)
  {
    return new FeatureVector(featureValue(__example));
  }

  public Feature featureValue(Object __example)
  {
    String result = discreteValue(__example);
    return new DiscretePrimitiveStringFeature(containingPackage, name, "", result, valueIndexOf(result), (short) allowableValues().length);
  }

  public String discreteValue(Object __example)
  {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'workLabel(ConllRelation)' defined on line 88 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);

      String __cachedValue = _discreteValue(__example);

    if (valueIndexOf(__cachedValue) == -1)
    {
      System.err.println("Classifier 'workLabel' defined on line 88 of LALModel.lbj produced '" + __cachedValue  + "' as a feature value, which is not allowable.");
      System.exit(1);
    }

    return __cachedValue;
  }

  private String _discreteValue(Object __example)
  {
    ConllRelation t = (ConllRelation) __example;

    if (t.relType.equalsIgnoreCase("Work_For"))
    {
      return "Works";
    }
    else
    {
      return "nWorks";
    }
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof ConllRelation[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'workLabel(ConllRelation)' defined on line 88 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "workLabel".hashCode(); }
  public boolean equals(Object o) { return o instanceof workLabel; }
}

