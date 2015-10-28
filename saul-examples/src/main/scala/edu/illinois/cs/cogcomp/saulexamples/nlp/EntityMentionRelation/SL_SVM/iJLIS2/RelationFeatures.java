// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000B49CC2E4E2A4D294555580A4DC94C29CCCFC37B4D4C292D2A4D26D07ECFCBC9C1898A24986A28D8EA245B2417A6E517AA2868258BF709B81A5929286B2498E51BE1054B4242F3B1827A79A939A9B04E936986498E597E715A876A81A6AE51464152617AA6A285924941596AA53A892644C99264896A4D200F49FA670EB000000

package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;

import LBJ2.classify.Classifier;
import LBJ2.classify.DiscretePrimitiveStringFeature;
import LBJ2.classify.FeatureVector;
import ml.wolfe.examples.parisa.ConllRelation;

public class RelationFeatures extends Classifier
{
  public RelationFeatures()
  {
    containingPackage = "ml.wolfe.examples.parisa.iJLIS2";
    name = "RelationFeatures";
  }

  public String getInputType() { return "ml.wolfe.examples.parisa.ConllRelation"; }
  public String getOutputType() { return "discrete%"; }

  public FeatureVector classify(Object __example)
  {
    if (!(__example instanceof ConllRelation))
    {
      String type = __example == null ? "null" : __example.getClass().getName();
      System.err.println("Classifier 'RelationFeatures(ConllRelation)' defined on line 15 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    ConllRelation t = (ConllRelation) __example;
     FeatureVector __result;
    __result = new  FeatureVector();
    String __id;
    String __value;

    __id = "" + (("WORD1:" + t.s.sentTokens.elementAt(t.wordId1).phrase));
    __value = "true";
    __result.addFeature(new DiscretePrimitiveStringFeature(this.containingPackage, this.name, __id, __value, valueIndexOf(__value), (short) 0));
    __id = "" + (("WORD2:" + t.s.sentTokens.elementAt(t.wordId2).phrase));
    __value = "true";
    __result.addFeature(new  DiscretePrimitiveStringFeature(this.containingPackage, this.name, __id, __value, valueIndexOf(__value), (short) 0));
    return __result;
  }

  public  FeatureVector[] classify(Object[] examples)
  {
    if (!(examples instanceof ConllRelation[]))
    {
      String type = examples == null ? "null" : examples.getClass().getName();
      System.err.println("Classifier 'RelationFeatures(ConllRelation)' defined on line 15 of LALModel.lbj received '" + type + "' as input.");
      new Exception().printStackTrace();
      System.exit(1);
    }

    return super.classify(examples);
  }

  public int hashCode() { return "RelationFeatures".hashCode(); }
  public boolean equals(Object o) { return o instanceof RelationFeatures; }
}

