// Modifying this comment will cause the next execution of LBJava to overwrite this file.
// F1B88000000000000000D4C814A02C034144FA234659086F20577D590D594F2013E825CFCF8629254A4EEEDA222EA606ED3ED56CC121B0713D7C066703AFEDA0981A7F76A8DE2AA8C9CFC34CB3515C1E0B7C281FA0B5A1A691E5F063C7E4E52F1F6A131B3F996F3137E6317B6F4941FD7A5450523FF18EF84D512E3B0EA6D8000000

import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.infer.*;
import edu.illinois.cs.cogcomp.lbjava.io.IOUtilities;
import edu.illinois.cs.cogcomp.lbjava.learn.*;
import edu.illinois.cs.cogcomp.lbjava.parse.*;
import java.util.List;


public class locLabel extends Classifier
{
  public locLabel()
  {
    containingPackage = "";
    name = "locLabel";
  }

  public String getInputType() { return "ConllRawToken"; }
  public String getOutputType() { return "discrete"; }

  private static String[] __allowableValues = new String[]{ "Loc", "nLoc" };
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
      System.err.println("Classifier 'locLabel(ConllRawToken)' defined on line 40 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    String __cachedValue = _discreteValue(__example);

    if (valueIndexOf(__cachedValue) == -1)
    {
      System.err.println("Classifier 'locLabel' defined on line 40 of LALModel.lbj produced '" + __cachedValue  + "' as a feature value, which is not allowable.");
      System.exit(1);
    }

    return __cachedValue;
  }

  private String _discreteValue(Object __example)
  {
    ConllRawToken t = (ConllRawToken) __example;

    if (t.entType.equalsIgnoreCase("Loc"))
    {
      return "Loc";
    }
    else
    {
      return "nLoc";
    }
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof ConllRawToken[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'locLabel(ConllRawToken)' defined on line 40 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "locLabel".hashCode(); }
  public boolean equals(Object o) { return o instanceof locLabel; }
}

