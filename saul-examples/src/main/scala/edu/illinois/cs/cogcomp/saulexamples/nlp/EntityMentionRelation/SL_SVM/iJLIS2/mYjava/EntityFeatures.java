package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.mYjava;// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B88000000000000000B49CC2E4E2A4D294555507DCB29CC29A47B4D4C292D2A4D26D07ECFCBC9C90A4C2F09CFCE4DC35821D450B1D558A6582E4DCB2E455821DB00FF0650B258292A2D45B658A5000617B321944000000

import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.infer.*;
import edu.illinois.cs.cogcomp.lbjava.io.IOUtilities;
import edu.illinois.cs.cogcomp.lbjava.learn.*;
import edu.illinois.cs.cogcomp.lbjava.parse.*;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRawToken;

import java.util.List;


public class EntityFeatures extends Classifier
{
  public EntityFeatures()
  {
    containingPackage = "";
    name = "EntityFeatures";
  }

  public String getInputType() { return "ConllRawToken"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof ConllRawToken))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'EntityFeatures(ConllRawToken)' defined on line 9 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    ConllRawToken t = (ConllRawToken) __example;

    FeatureVector __result;
    __result = new FeatureVector();
    String __id;
    String __value;

    __id = "" + (t.POS);
    __value = "true";
    __result.addFeature(new DiscretePrimitiveStringFeature(this.containingPackage, this.name, __id, __value, valueIndexOf(__value), (short) 0));
    return __result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof ConllRawToken[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'EntityFeatures(ConllRawToken)' defined on line 9 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "EntityFeatures".hashCode(); }
  public boolean equals(Object o) { return o instanceof EntityFeatures; }
}

