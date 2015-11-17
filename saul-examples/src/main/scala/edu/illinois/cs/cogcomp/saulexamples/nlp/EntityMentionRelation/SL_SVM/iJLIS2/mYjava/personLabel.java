package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.mYjava;// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B88000000000000000D4D814A02C030154FA234659086F20677D590E244A71886FB25C1662E42544A4EEE64712EAEF2ED7EDB96EC71341CAEE8037B1272F9DA4906955E01FC06F38A03F92E374DB348A40AE7B4B2DC752F5A38491F590D1E1B44ECBFB98A168891EB9F439B01AD3B9F71312FFD448D1552076CF309F19AF60EBF78FC019000000

import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.infer.*;
import edu.illinois.cs.cogcomp.lbjava.io.IOUtilities;
import edu.illinois.cs.cogcomp.lbjava.learn.*;
import edu.illinois.cs.cogcomp.lbjava.parse.*;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRawToken;

import java.util.List;


public class personLabel extends Classifier
{
  public personLabel()
  {
    containingPackage = "";
    name = "personLabel";
  }

  public String getInputType() { return "ConllRawToken"; }
  public String getOutputType() { return "discrete"; }

  private static String[] __allowableValues = new String[]{ "Per", "nPer" };
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
    if (!(__example instanceof ConllRawToken))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'personLabel(ConllRawToken)' defined on line 17 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    String __cachedValue = _discreteValue(__example);

    if (valueIndexOf(__cachedValue) == -1)
    {
      System.err.println("Classifier 'personLabel' defined on line 17 of LALModel.lbj produced '" + __cachedValue  + "' as a feature value, which is not allowable.");
      System.exit(1);
    }

    return __cachedValue;
  }

  private String _discreteValue(Object __example)
  {
    ConllRawToken t = (ConllRawToken) __example;

    if (t.entType.equalsIgnoreCase("Peop"))
    {
      return "Per";
    }
    else
    {
      return "nPer";
    }
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof ConllRawToken[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'personLabel(ConllRawToken)' defined on line 17 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "personLabel".hashCode(); }
  public boolean equals(Object o) { return o instanceof personLabel; }
}

