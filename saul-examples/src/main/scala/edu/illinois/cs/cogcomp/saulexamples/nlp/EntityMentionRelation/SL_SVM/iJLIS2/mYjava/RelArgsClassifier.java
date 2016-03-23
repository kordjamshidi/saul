package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.mYjava;// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// discrete RelArgsClassifier(ConllRelation r) <- JointER(work_forClassifier)

import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.infer.*;
import edu.illinois.cs.cogcomp.saulexamples.EntityMentionRelation.datastruct.ConllRelation;
import edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2.JointER;


public class RelArgsClassifier extends Classifier
{
  private static final work_forClassifier __work_forClassifier = new work_forClassifier();

  public RelArgsClassifier()
  {
    containingPackage = "";
    name = "RelArgsClassifier";
  }

  public String getInputType() { return "ConllRelation"; }
  public String getOutputType() { return "discrete"; }

  private static String[] __allowableValues = new String[]{ "*", "*" };
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
    if (!(__example instanceof ConllRelation))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'RelArgsClassifier(ConllRelation)' defined on line 35 of ER_Joint.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    ConllRelation head = JointER.findHead((ConllRelation) __example);
    JointER inference = (JointER) InferenceManager.get("JointER", head);

    if (inference == null)
    {
      inference = new JointER(head);
      InferenceManager.put(inference);
    }

    String result = null;

    try { result = inference.valueOf(__work_forClassifier, __example); }
    catch (Exception e)
    {
      System.err.println("LBJ ERROR: Fatal error while evaluating classifier RelArgsClassifier: " + e);
      e.printStackTrace();
      System.exit(1);
    }

    return result;
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof ConllRelation[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'RelArgsClassifier(ConllRelation)' defined on line 35 of ER_Joint.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "RelArgsClassifier".hashCode(); }
  public boolean equals(Object o) { return o instanceof RelArgsClassifier; }
}

