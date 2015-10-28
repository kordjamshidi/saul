// Modifying this comment will cause the next execution of LBJ2 to overwrite this file.
// F1B88000000000000000D9F8BCE43C04C054F75CACA8E2843C3ABA2D50AAA59A212515065C609991735B29A7646B348788F77644FF08DEDB2D9B7E646B0A4E40B7DE058FB0D939CA70433E3039E5CA39C034B83D37AE940C76077790301AA042DEE10FD86081D85A38D883BF7E690D745238386A3180D40F7086E6F57726E8218A52C858B55D3F88E83F07EABE0943753385430A964986031BF11E923AA1DDFB3926741F19430567D42FD76782BBD2617F51435F5D21CF8C1A7123B21CD4DB852CF08399F6FF7A295963F1EA81C1AA8C6DFB07EAA489AB27FC6F3A7E1D1EAB960842EF20ED1E217F54100000
package edu.illinois.cs.cogcomp.saulexamples.nlp.EntityMentionRelation.SL_SVM.iJLIS2;

//import util.ClassUtils;
//import util.ExceptionlessInputStream;
//import util.ExceptionlessOutputStream;

//import LBJ.learn.SparseAveragedPerceptron;

import LBJ2.classify.*;
import LBJ2.learn.*;
import LBJ2.parse.ArrayFileParser;
import LBJ2.parse.Parser;
import LBJ2.util.ClassUtils;
import LBJ2.util.ExceptionlessInputStream;
import LBJ2.util.ExceptionlessOutputStream;
import ml.wolfe.examples.parisa.ConllRawToken;
//import learn.*;
public class OrganizationClassifier extends SparseAveragedPerceptron
{
  private static java.net.URL _lcFilePath;
  private static java.net.URL _lexFilePath;

  static
  {
    _lcFilePath = OrganizationClassifier.class.getResource("OrganizationClassifier.lc");

    if (_lcFilePath == null)
    {
      System.err.println("ERROR: Can't locate OrganizationClassifier.lc in the class path.");
      System.exit(1);
    }

    _lexFilePath = OrganizationClassifier.class.getResource("OrganizationClassifier.ex");

    if (_lexFilePath == null)
    {
      System.err.println("ERROR: Can't locate OrganizationClassifier.lex in the class path.");
      System.exit(1);
    }
  }

  private static void loadInstance()
  {
    if (instance == null)
    {
      instance = (OrganizationClassifier) readLearner(_lcFilePath);
      instance.readLexiconOnDemand(_lexFilePath);
    }
  }

  public static Parser getParser() { return new ArrayFileParser("OrganizationClassifier.ex"); }
  public static Parser getTestParser() { return new ArrayFileParser("OrganizationClassifier.test.ex"); }
  public static boolean isTraining;
  public static OrganizationClassifier instance;

  public static OrganizationClassifier getInstance()
  {
    loadInstance();
    return instance;
  }

  private OrganizationClassifier(boolean b)
  {
    super(new Parameters());
    containingPackage = "ml.wolfe.examples.parisa.iJLIS2";
    name = "OrganizationClassifier";
    setEncoding(null);
    setLabeler(new orgLabel());
    setExtractor(new EntityFeatures());
    isClone = false;
  }

  public static TestingMetric getTestingMetric() { return null; }


  private boolean isClone;

  public void unclone() { isClone = false; }

  public OrganizationClassifier()
  {
    super("ml.wolfe.examples.parisa.iJLIS2.OrganizationClassifier");
    isClone = true;
  }

  public OrganizationClassifier(String modelPath, String lexiconPath) { this(new Parameters(), modelPath, lexiconPath); }
  public OrganizationClassifier(Parameters p, String modelPath, String lexiconPath)
  {
    super(p);
    try {
      lcFilePath = new java.net.URL("file:" + modelPath);
      lexFilePath = new java.net.URL("file:" + lexiconPath);
    }
    catch (Exception e) {
      System.err.println("ERROR: Can't create model or lexicon URL: " + e);
      e.printStackTrace();
      System.exit(1);
    }

    if (new java.io.File(modelPath).exists()) {
      readModel(lcFilePath);
      readLexiconOnDemand(lexFilePath);
    }
    else {
      containingPackage = "ml.wolfe.examples.parisa.iJLIS2";
      name = "OrganizationClassifier";
      setLabeler(new orgLabel());
      setExtractor(new EntityFeatures());
    }

    isClone = false;
  }

  public String getInputType() { return "ml.wolfe.examples.parisa.ConllRawToken"; }
  public String getOutputType() { return "discrete"; }

  public void learn(Object example)
  {
    if (isClone)
    {
      if (!(example instanceof ConllRawToken || example instanceof Object[]))
      {
        String type = example == null ? "null" : example.getClass().getName();
        System.err.println("Classifier 'OrganizationClassifier(ConllRawToken)' defined on line 74 of LALModel.lbj received '" + type + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

      loadInstance();
      instance.learn(example);
      return;
    }

    if (example instanceof Object[])
    {
      Object[] a = (Object[]) example;
      if (a[0] instanceof int[])
      {
        super.learn((int[]) a[0], (double[]) a[1], (int[]) a[2], (double[]) a[3]);
        return;
      }
    }

    super.learn(example);
  }

  public void learn(Object[] examples)
  {
    if (isClone)
    {
      if (!(examples instanceof ConllRawToken[] || examples instanceof Object[][]))
      {
        String type = examples == null ? "null" : examples.getClass().getName();
        System.err.println("Classifier 'OrganizationClassifier(ConllRawToken)' defined on line 74 of LALModel.lbj received '" + type + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

      loadInstance();
      instance.learn(examples);
      return;
    }

    super.learn(examples);
  }

  public Feature featureValue(Object __example)
  {
    if (isClone)
    {
      if (!(__example instanceof ConllRawToken || __example instanceof Object[]))
      {
        String type = __example == null ? "null" : __example.getClass().getName();
        System.err.println("Classifier 'OrganizationClassifier(ConllRawToken)' defined on line 74 of LALModel.lbj received '" + type + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

      loadInstance();
      return instance.featureValue(__example);
    }

    if (__example instanceof Object[])
    {
      Object[] a = (Object[]) __example;
      if (a[0] instanceof int[])
        return super.featureValue((int[]) a[0], (double[]) a[1]);
    }

      Feature __result;
    __result = super.featureValue(__example);
    return __result;
  }

  public FeatureVector classify(Object __example)
  {
    return new FeatureVector(featureValue(__example));
  }

  public String discreteValue(Object __example)
  {
    return featureValue(__example).getStringValue();
  }

  public FeatureVector[] classify(Object[] examples)
  {
    if (isClone)
    {
      if (!(examples instanceof ConllRawToken[] || examples instanceof Object[][]))
      {
        String type = examples == null ? "null" : examples.getClass().getName();
        System.err.println("Classifier 'OrganizationClassifier(ConllRawToken)' defined on line 74 of LALModel.lbj received '" + type + "' as input.");
        new Exception().printStackTrace();
        System.exit(1);
      }

      loadInstance();
      return instance.classify(examples);
    }

     FeatureVector[] result = super.classify(examples);
    return result;
  }

  public static void main(String[] args)
  {
    String testParserName = null;
    String testFile = null;
     Parser testParser = getTestParser();

    try
    {
      if (!args[0].equals("null"))
        testParserName = args[0];
      if (args.length > 1) testFile = args[1];

      if (testParserName == null && testParser == null)
      {
        System.err.println("The \"testFrom\" clause was not used in the learning classifier expression that");
        System.err.println("generated this classifier, so a parser and input file must be specified.\n");
        throw new Exception();
      }
    }
    catch (Exception e)
    {
      System.err.println("usage: ml.wolfe.examples.parisa.iJLIS2.OrganizationClassifier \\");
      System.err.println("           <parser> <input file> [<null label> [<null label> ...]]\n");
      System.err.println("     * <parser> must be the fully qualified class name of a Parser, or \"null\"");
      System.err.println("       to use the default as specified by the \"testFrom\" clause.");
      System.err.println("     * <input file> is the relative or absolute path of a file, or \"null\" to");
      System.err.println("       use the parser arguments specified by the \"testFrom\" clause.  <input");
      System.err.println("       file> can also be non-\"null\" when <parser> is \"null\" (when the parser");
      System.err.println("       specified by the \"testFrom\" clause has a single string argument");
      System.err.println("       constructor) to use an alternate file.");
      System.err.println("     * A <null label> is a label (or prediction) that should not count towards");
      System.err.println("       overall precision and recall assessments.");
      System.exit(1);
    }

    if (testParserName == null && testFile != null && !testFile.equals("null"))
      testParserName = testParser.getClass().getName();
    if (testParserName != null)
      testParser = ClassUtils.getParser(testParserName, new Class[]{String.class}, new String[]{testFile});
    OrganizationClassifier classifier = new OrganizationClassifier();
      TestDiscrete tester = new TestDiscrete();
    for (int i = 2; i < args.length; ++i)
      tester.addNull(args[i]);
      TestDiscrete.testDiscrete(tester, classifier, classifier.getLabeler(), testParser, true, 0);
  }

  public int hashCode() { return "OrganizationClassifier".hashCode(); }
  public boolean equals(Object o) { return o instanceof OrganizationClassifier; }

  public void write(java.io.PrintStream a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.write(a0);
      return;
    }

    super.write(a0);
  }

  public void write(ExceptionlessOutputStream a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.write(a0);
      return;
    }

    super.write(a0);
  }

  public void initialize(int a0, int a1)
  {
    if (isClone)
    {
      loadInstance();
      instance.initialize(a0, a1);
      return;
    }

    super.initialize(a0, a1);
  }

  public void read(ExceptionlessInputStream a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.read(a0);
      return;
    }

    super.read(a0);
  }

  public Learner.Parameters getParameters()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getParameters();
    }

    return super.getParameters();
  }

  public void learn(int[] a0, double[] a1, int[] a2, double[] a3)
  {
    if (isClone)
    {
      loadInstance();
      instance.learn(a0, a1, a2, a3);
      return;
    }

    super.learn(a0, a1, a2, a3);
  }

  public void setParameters(SparseAveragedPerceptron.Parameters a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setParameters(a0);
      return;
    }

    super.setParameters(a0);
  }

  public double score(int[] a0, double[] a1)
  {
    if (isClone)
    {
      loadInstance();
      return instance.score(a0, a1);
    }

    return super.score(a0, a1);
  }

  public void promote(int[] a0, double[] a1, double a2)
  {
    if (isClone)
    {
      loadInstance();
      instance.promote(a0, a1, a2);
      return;
    }

    super.promote(a0, a1, a2);
  }

  public void demote(int[] a0, double[] a1, double a2)
  {
    if (isClone)
    {
      loadInstance();
      instance.demote(a0, a1, a2);
      return;
    }

    super.demote(a0, a1, a2);
  }

  public void forget()
  {
    if (isClone)
    {
      loadInstance();
      instance.forget();
      return;
    }

    super.forget();
  }

  public double getLearningRate()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getLearningRate();
    }

    return super.getLearningRate();
  }

  public void setLearningRate(double a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setLearningRate(a0);
      return;
    }

    super.setLearningRate(a0);
  }

  public void setThreshold(double a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setThreshold(a0);
      return;
    }

    super.setThreshold(a0);
  }

  public FeatureVector classify(int[] a0, double[] a1)
  {
    if (isClone)
    {
      loadInstance();
      return instance.classify(a0, a1);
    }

    return super.classify(a0, a1);
  }

  public Feature featureValue(int[] a0, double[] a1)
  {
    if (isClone)
    {
      loadInstance();
      return instance.featureValue(a0, a1);
    }

    return super.featureValue(a0, a1);
  }

  public java.lang.String discreteValue(int[] a0, double[] a1)
  {
    if (isClone)
    {
      loadInstance();
      return instance.discreteValue(a0, a1);
    }

    return super.discreteValue(a0, a1);
  }

  public void setParameters(LinearThresholdUnit.Parameters a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setParameters(a0);
      return;
    }

    super.setParameters(a0);
  }

  public double score(java.lang.Object a0)
  {
    if (isClone)
    {
      loadInstance();
      return instance.score(a0);
    }

    return super.score(a0);
  }

  public void setLabeler(Classifier a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setLabeler(a0);
      return;
    }

    super.setLabeler(a0);
  }

  public double getInitialWeight()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getInitialWeight();
    }

    return super.getInitialWeight();
  }

  public void setInitialWeight(double a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setInitialWeight(a0);
      return;
    }

    super.setInitialWeight(a0);
  }

  public double getThreshold()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getThreshold();
    }

    return super.getThreshold();
  }

  public double getPositiveThickness()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getPositiveThickness();
    }

    return super.getPositiveThickness();
  }

  public void setPositiveThickness(double a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setPositiveThickness(a0);
      return;
    }

    super.setPositiveThickness(a0);
  }

  public double getNegativeThickness()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getNegativeThickness();
    }

    return super.getNegativeThickness();
  }

  public void setNegativeThickness(double a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setNegativeThickness(a0);
      return;
    }

    super.setNegativeThickness(a0);
  }

  public void setThickness(double a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setThickness(a0);
      return;
    }

    super.setThickness(a0);
  }

  public double computeLearningRate(int[] a0, double[] a1, double a2, boolean a3)
  {
    if (isClone)
    {
      loadInstance();
      return instance.computeLearningRate(a0, a1, a2, a3);
    }

    return super.computeLearningRate(a0, a1, a2, a3);
  }

  public boolean shouldPromote(boolean a0, double a1, double a2, double a3)
  {
    if (isClone)
    {
      loadInstance();
      return instance.shouldPromote(a0, a1, a2, a3);
    }

    return super.shouldPromote(a0, a1, a2, a3);
  }

  public boolean shouldDemote(boolean a0, double a1, double a2, double a3)
  {
    if (isClone)
    {
      loadInstance();
      return instance.shouldDemote(a0, a1, a2, a3);
    }

    return super.shouldDemote(a0, a1, a2, a3);
  }

  public ScoreSet scores(int[] a0, double[] a1)
  {
    if (isClone)
    {
      loadInstance();
      return instance.scores(a0, a1);
    }

    return super.scores(a0, a1);
  }

  public void write(java.lang.String a0, java.lang.String a1)
  {
    if (isClone)
    {
      loadInstance();
      instance.write(a0, a1);
      return;
    }

    super.write(a0, a1);
  }

  public void save()
  {
    if (isClone)
    {
      loadInstance();
      instance.save();
      return;
    }

    super.save();
  }

  public void read(java.lang.String a0, java.lang.String a1)
  {
    if (isClone)
    {
      loadInstance();
      instance.read(a0, a1);
      return;
    }

    super.read(a0, a1);
  }

  public void learn(FeatureVector a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.learn(a0);
      return;
    }

    super.learn(a0);
  }

  public void learn(FeatureVector[] a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.learn(a0);
      return;
    }

    super.learn(a0);
  }

  public FeatureVector[] classify(java.lang.Object[][] a0)
  {
    if (isClone)
    {
      loadInstance();
      return instance.classify(a0);
    }

    return super.classify(a0);
  }

  public FeatureVector[] classify(FeatureVector[] a0)
  {
    if (isClone)
    {
      loadInstance();
      return instance.classify(a0);
    }

    return super.classify(a0);
  }

  public FeatureVector classify(FeatureVector a0)
  {
    if (isClone)
    {
      loadInstance();
      return instance.classify(a0);
    }

    return super.classify(a0);
  }

  public Feature featureValue(FeatureVector a0)
  {
    if (isClone)
    {
      loadInstance();
      return instance.featureValue(a0);
    }

    return super.featureValue(a0);
  }

  public java.lang.String discreteValue(FeatureVector a0)
  {
    if (isClone)
    {
      loadInstance();
      return instance.discreteValue(a0);
    }

    return super.discreteValue(a0);
  }

  public double realValue(int[] a0, double[] a1)
  {
    if (isClone)
    {
      loadInstance();
      return instance.realValue(a0, a1);
    }

    return super.realValue(a0, a1);
  }

  public double realValue(FeatureVector a0)
  {
    if (isClone)
    {
      loadInstance();
      return instance.realValue(a0);
    }

    return super.realValue(a0);
  }

  public void setParameters(Learner.Parameters a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setParameters(a0);
      return;
    }

    super.setParameters(a0);
  }

  public ScoreSet scores(java.lang.Object a0)
  {
    if (isClone)
    {
      loadInstance();
      return instance.scores(a0);
    }

    return super.scores(a0);
  }

  public ScoreSet scores(FeatureVector a0)
  {
    if (isClone)
    {
      loadInstance();
      return instance.scores(a0);
    }

    return super.scores(a0);
  }

  public Learner emptyClone()
  {
    if (isClone)
    {
      loadInstance();
      return instance.emptyClone();
    }

    return super.emptyClone();
  }

  public java.lang.Object[] getExampleArray(java.lang.Object a0, boolean a1)
  {
    if (isClone)
    {
      loadInstance();
      return instance.getExampleArray(a0, a1);
    }

    return super.getExampleArray(a0, a1);
  }

  public java.lang.Object[] getExampleArray(java.lang.Object a0)
  {
    if (isClone)
    {
      loadInstance();
      return instance.getExampleArray(a0);
    }

    return super.getExampleArray(a0);
  }

  public void readLexiconOnDemand(java.net.URL a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.readLexiconOnDemand(a0);
      return;
    }

    super.readLexiconOnDemand(a0);
  }

  public void readLexiconOnDemand(java.lang.String a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.readLexiconOnDemand(a0);
      return;
    }

    super.readLexiconOnDemand(a0);
  }

  public Classifier getLabeler()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getLabeler();
    }

    return super.getLabeler();
  }

  public void setExtractor(Classifier a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setExtractor(a0);
      return;
    }

    super.setExtractor(a0);
  }

  public Classifier getExtractor()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getExtractor();
    }

    return super.getExtractor();
  }

  public void setLexicon(Lexicon a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setLexicon(a0);
      return;
    }

    super.setLexicon(a0);
  }

  public Lexicon getLexicon()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getLexicon();
    }

    return super.getLexicon();
  }

  public void setLabelLexicon(Lexicon a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setLabelLexicon(a0);
      return;
    }

    super.setLabelLexicon(a0);
  }

  public Lexicon getLabelLexicon()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getLabelLexicon();
    }

    return super.getLabelLexicon();
  }

  public void setEncoding(java.lang.String a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setEncoding(a0);
      return;
    }

    super.setEncoding(a0);
  }

  public void setModelLocation(java.lang.String a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setModelLocation(a0);
      return;
    }

    super.setModelLocation(a0);
  }

  public void setModelLocation(java.net.URL a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setModelLocation(a0);
      return;
    }

    super.setModelLocation(a0);
  }

  public java.net.URL getModelLocation()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getModelLocation();
    }

    return super.getModelLocation();
  }

  public void setLexiconLocation(java.net.URL a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setLexiconLocation(a0);
      return;
    }

    super.setLexiconLocation(a0);
  }

  public void setLexiconLocation(java.lang.String a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.setLexiconLocation(a0);
      return;
    }

    super.setLexiconLocation(a0);
  }

  public java.net.URL getLexiconLocation()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getLexiconLocation();
    }

    return super.getLexiconLocation();
  }

  public void countFeatures(Lexicon.CountPolicy a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.countFeatures(a0);
      return;
    }

    super.countFeatures(a0);
  }

  public Lexicon getLexiconDiscardCounts()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getLexiconDiscardCounts();
    }

    return super.getLexiconDiscardCounts();
  }

  public void doneLearning()
  {
    if (isClone)
    {
      loadInstance();
      instance.doneLearning();
      return;
    }

    super.doneLearning();
  }

  public void doneWithRound()
  {
    if (isClone)
    {
      loadInstance();
      instance.doneWithRound();
      return;
    }

    super.doneWithRound();
  }

  public int getPrunedLexiconSize()
  {
    if (isClone)
    {
      loadInstance();
      return instance.getPrunedLexiconSize();
    }

    return super.getPrunedLexiconSize();
  }

  public void saveModel()
  {
    if (isClone)
    {
      loadInstance();
      instance.saveModel();
      return;
    }

    super.saveModel();
  }

  public void saveLexicon()
  {
    if (isClone)
    {
      loadInstance();
      instance.saveLexicon();
      return;
    }

    super.saveLexicon();
  }

  public void writeModel(java.lang.String a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.writeModel(a0);
      return;
    }

    super.writeModel(a0);
  }

  public void writeLexicon(java.lang.String a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.writeLexicon(a0);
      return;
    }

    super.writeLexicon(a0);
  }

  public void readModel(java.lang.String a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.readModel(a0);
      return;
    }

    super.readModel(a0);
  }

  public void readModel(java.net.URL a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.readModel(a0);
      return;
    }

    super.readModel(a0);
  }

  public void readLexicon(java.net.URL a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.readLexicon(a0);
      return;
    }

    super.readLexicon(a0);
  }

  public void readLexicon(java.lang.String a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.readLexicon(a0);
      return;
    }

    super.readLexicon(a0);
  }

  public void readLabelLexicon(ExceptionlessInputStream a0)
  {
    if (isClone)
    {
      loadInstance();
      instance.readLabelLexicon(a0);
      return;
    }

    super.readLabelLexicon(a0);
  }

  public Lexicon demandLexicon()
  {
    if (isClone)
    {
      loadInstance();
      return instance.demandLexicon();
    }

    return super.demandLexicon();
  }

  public static class Parameters extends SparseAveragedPerceptron.Parameters
  {
    public Parameters()
    {
      learningRate = 0.1;
      thickness = 3.5;
    }
  }
}

