package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.mYjava;// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B88000000000000000B49CC2E4E2A4D294DA652FF2A4752D1505AC301D5BA09F549EE398949A93A1EC9F97939314985E129F9D9A97A052A9A063ABA05DA09996A0A152A79A97521259509AA79A585A98935CE99E979F549AEC985C9AA1063C435318A0186679615E920454CA51A6512537A83519522F0E235B00A5116788D8000000

import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRawToken;


public class orgLabel extends Classifier
{
  public orgLabel()
  {
    containingPackage = "";
    name = "orgLabel";
  }

  public String getInputType() { return "ConllRawToken"; }
  public String getOutputType() { return "discrete"; }

  private static String[] __allowableValues = new String[]{ "Org", "nOrg" };
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
      System.err.println("Classifier 'orgLabel(ConllRawToken)' defined on line 64 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);

      String __cachedValue = _discreteValue(__example);

    if (valueIndexOf(__cachedValue) == -1)
    {
      System.err.println("Classifier 'orgLabel' defined on line 64 of LALModel.lbj produced '" + __cachedValue  + "' as a feature value, which is not allowable.");
      System.exit(1);
    }

    return __cachedValue;
  }

  private String _discreteValue(Object __example)
  {
    ConllRawToken t = (ConllRawToken) __example;

    if (t.entType.equalsIgnoreCase("Org"))
    {
      return "Org";
    }
    else
    {
      return "nOrg";
    }
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof ConllRawToken[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'orgLabel(ConllRawToken)' defined on line 64 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "orgLabel".hashCode(); }
  public boolean equals(Object o) { return o instanceof orgLabel; }
}

