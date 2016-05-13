package edu.illinois.cs.cogcomp.saul.classifier

import java.io.File
import java.net.URL

import edu.illinois.cs.cogcomp.core.io.IOUtils
import edu.illinois.cs.cogcomp.lbjava.classify.{ FeatureVector, TestDiscrete }
import edu.illinois.cs.cogcomp.lbjava.learn.Learner.Parameters
import edu.illinois.cs.cogcomp.lbjava.learn._
import edu.illinois.cs.cogcomp.lbjava.parse.{Parser, FoldParser}
import edu.illinois.cs.cogcomp.lbjava.parse.FoldParser.SplitPolicy
import edu.illinois.cs.cogcomp.lbjava.util.ExceptionlessOutputStream
import edu.illinois.cs.cogcomp.saul.TestContinuous
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saul.datamodel.edge.Link
import edu.illinois.cs.cogcomp.saul.datamodel.node.Node
import edu.illinois.cs.cogcomp.saul.datamodel.property.{ PropertyWithWindow, CombinedDiscreteProperty, Property }
import edu.illinois.cs.cogcomp.saul.lbjrelated.LBJLearnerEquivalent
import edu.illinois.cs.cogcomp.saul.parser.{LBJavaParserToIterable, IterableToLBJavaParser}

import org.slf4j.helpers.NOPLogger
import org.slf4j.{ Logger, LoggerFactory }

import scala.reflect.ClassTag

abstract class Learnable[T <: AnyRef](val node: Node[T], val parameters: Parameters = new Learner.Parameters)(implicit tag: ClassTag[T]) extends LBJLearnerEquivalent {
  /** Whether to use caching */
  val useCache = false

  val logging = true
  val logger: Logger = if (logging) LoggerFactory.getLogger(this.getClass) else NOPLogger.NOP_LOGGER;

  var isTraining = false

  def trainingInstances = node.getTrainingInstances

  def getClassNameForClassifier = this.getClass.getCanonicalName

  def getClassSimpleNameForClassifier = this.getClass.getSimpleName

  def feature: List[Property[T]] = node.properties.toList

  /** filter out the label from the features */
  def combinedProperties = if (label != null) new CombinedDiscreteProperty[T](this.feature.filterNot(_.name == label.name))
  else new CombinedDiscreteProperty[T](this.feature)

  def lbpFeatures = combinedProperties.classifier

  /** classifier need to be defined by the user */
  val classifier: Learner

  /** syntactic sugar to create simple calls to the function */
  def apply(example: AnyRef): String = classifier.discreteValue(example: AnyRef)

  /** specifications of the classifier and its model files  */
  classifier.setReadLexiconOnDemand()

  val modelDir = "models" + File.separator
  def lcFilePath(suffix: String = "") = new URL(new URL("file:"), modelDir + getClassNameForClassifier + suffix + ".lc")
  def lexFilePath(suffix: String = "") = new URL(new URL("file:"), modelDir + getClassNameForClassifier + suffix + ".lex")
  IOUtils.mkdir(modelDir)
  classifier.setModelLocation(lcFilePath())
  classifier.setLexiconLocation(lexFilePath())

  // create .lex file if it does not exist
  if (!IOUtils.exists(lexFilePath().getPath)) {
    val lexFile = ExceptionlessOutputStream.openCompressedStream(lexFilePath())
    if (classifier.getCurrentLexicon == null) lexFile.writeInt(0)
    else classifier.getCurrentLexicon.write(lexFile)
    lexFile.close()
  }

  // create .lc file if it does not exist
  if (!IOUtils.exists(lcFilePath().getPath)) {
    val lcFile = ExceptionlessOutputStream.openCompressedStream(lcFilePath())
    classifier.write(lcFile)
    lcFile.close()
  }

  def setExtractor(): Unit = {
    if (feature != null) {
      logger.info("Setting the feature extractors to be {}", lbpFeatures.getCompositeChildren)

      classifier.setExtractor(lbpFeatures)
    } else {
      logger.warn("Warning: no features found!")
    }
  }

  def setLabeler(): Unit = {
    if (label != null) {
      val oracle = Property.entitiesToLBJFeature(label)
      logger.info("Setting the labeler to be '{}", oracle)

      classifier.setLabeler(oracle)
    }
  }

  // set parameters for classifier
  setExtractor()
  setLabeler()

  def removeModelFiles(): Unit = {
    IOUtils.rm(lcFilePath().getPath)
    IOUtils.rm(lexFilePath().getPath)
    createFiles()
  }

  /** This function prints a summary of the classifier
    */
  def printlnModel(): Unit = {
    classifier.write(System.out)
  }

  def save(): Unit = {
    removeModelFiles()
    val dummyClassifier = new SparsePerceptron()
    classifier.setExtractor(dummyClassifier)
    classifier.setLabeler(dummyClassifier)
    classifier.write(lcFilePath().getPath, lexFilePath().getPath)

    // after saving, get rid of the dummyClassifier in the classifier.
    setExtractor()
    setLabeler()
  }

  def createFiles(): Unit = {
    // create the model directory if it does not exist
    IOUtils.mkdir(modelDir)
    // create .lex file if it does not exist
    if (!IOUtils.exists(lexFilePath().getPath)) {
      val lexFile = ExceptionlessOutputStream.openCompressedStream(lexFilePath())
      if (classifier.getCurrentLexicon == null) lexFile.writeInt(0)
      else classifier.getCurrentLexicon.write(lexFile)
      lexFile.close()
    }

    // create .lc file if it does not exist
    if (!IOUtils.exists(lcFilePath().getPath)) {
      val lcFile = ExceptionlessOutputStream.openCompressedStream(lcFilePath())
      classifier.write(lcFile)
      lcFile.close()
    }
  }

  /** Loads the model and lexicon for the classifier. Looks up in the local file system
    * and the files are not found, looks up in the classpath JARs.
    *
    * @param lcFile The path of the model file
    * @param lexFile The path of the lexicon file
    */
  def load(lcFile: String, lexFile: String): Unit = {
    if (IOUtils.exists(lcFile)) {
      logger.info("Reading model file {} from local path.", IOUtils.getFileName(lcFile))
      classifier.readModel(lcFile)
    } else {
      val modelResourcesUrls = IOUtils.lsResources(getClass, lcFile)
      if (modelResourcesUrls.size() == 1) {
        logger.info("Reading model file {} from classpath.", IOUtils.getFileName(lcFile))
        classifier.readModel(modelResourcesUrls.get(0))
      } else logger.error("Cannot find model file: {}", lcFile)
    }
    if (IOUtils.exists(lcFile)) {
      logger.info("Reading lexicon file {} from local path.", IOUtils.getFileName(lexFile))
      classifier.readLexicon(lexFile)
    } else {
      val lexiconResourcesUrls = IOUtils.lsResources(getClass, lexFile)
      if (lexiconResourcesUrls.size() == 1) {
        logger.info("Reading lexicon file {} from classpath.", IOUtils.getFileName(lexFile))
        classifier.readLexicon(lexiconResourcesUrls.get(0))
      } else logger.error("Cannot find lexicon file {}", lexFile)
    }

    setExtractor()
    setLabeler()
  }

  def load(lcFile: URL, lexFile: URL): Unit = {
    load(lcFile.getPath, lexFile.getPath)
  }

  def load(): Unit = {
    load(lcFilePath().getPath, lexFilePath().getPath)
  }

  def learn(iteration: Int): Unit = {
    createFiles()
    isTraining = true
    if (useCache) {
      if (node.derivedInstances.isEmpty) {
        logger.error("No cached data found. Please use \"dataModel.load(filepath)\" \n" +
          "If you don't have any cache saved, use \"datamodel.deriveInstances()\" to extract it, " +
          "and then save it with \"datamodel.write(filePath)\"  ")
      }
      learnWithDerivedInstances(iteration, node.derivedInstances.values)
    } else {
      learn(iteration, this.trainingInstances)
      classifier.doneLearning()
    }
    isTraining = false
  }

  def learn(iteration: Int = 10, parser: Parser): Unit = {
    val trainingIterable = new LBJavaParserToIterable[T](parser)
    learn(iteration, trainingIterable)
  }

  def learn(iteration: Int, data: Iterable[T]): Unit = {
    createFiles()

    if (logger.isInfoEnabled) {
      val oracle = Property.entitiesToLBJFeature(label)
      logger.info("==> Learning using the feature extractors to be {}", lbpFeatures.getCompositeChildren)
      logger.info("==> Learning using the labeler to be '{}'", oracle)
      logger.info(classifier.getExtractor.getCompositeChildren.toString)
      logger.info(classifier.getLabeler.toString)
      logger.info(s"Learnable: Learn with data of size ${data.size}")
      logger.info(s"Training: $iteration iterations remain.")
    }

    isTraining = true

    (iteration to 1 by -1).foreach(remainingIteration => {
      if (remainingIteration % 10 == 0)
        logger.info("Training: {} iterations remain.", remainingIteration)

      node.clearPropertyCache()
      data.foreach(classifier.learn)
    })

    classifier.doneLearning()
    isTraining = false
  }

  def learnWithDerivedInstances(numIterations: Int, featureVectors: Iterable[FeatureVector]): Unit = {
    isTraining = true
    val propertyNameSet = feature.map(_.name).toSet
    (0 until numIterations).foreach { _ =>
      featureVectors.foreach {
        fullFeatureVector =>
          val featureVector = new FeatureVector()
          val numFeatures = fullFeatureVector.size()
          (0 until numFeatures).foreach {
            featureIndex =>
              val feature = fullFeatureVector.getFeature(featureIndex)
              val propertyName = feature.getGeneratingClassifier
              if (label != null && label.name.equals(propertyName)) {
                featureVector.addLabel(feature)
              } else if (propertyNameSet.contains(propertyName)) {
                featureVector.addFeature(feature)
              }
          }
          classifier.learn(featureVector)
      }
    }
    classifier.doneLearning()
    isTraining = false
  }

  def forget() = this.classifier.forget()

  case class Result(label: String, f1: Double, precision: Double, recall: Double)

  /** Test with the test data, retrieve internally
 *
    * @return List of [[Learnable.Result]]
    */
  def test(): Seq[Result] = {
    val testData = node.getTestingInstances
    test(testData)
  }

  /** Test with given data, use internally
    *
    * @param testData if the collection of data is not given it is derived from the data model based on its type
    * @param prediction it is the property that we want to evaluate it if it is null then the prediction of the classifier is the default
    * @param groundTruth it is the property that we want to evaluate the prediction against it, if it is null then the gold label derived from the classifier is used
    * @param exclude it is the label that we want to exclude fro evaluation, this is useful for evaluating the multi-class classifiers when we need to measure overall F1 instead of accuracy and we need to exclude the negative class
    * @return List of [[Learnable.Result]]
    */
  def test(testData: Iterable[T], prediction: Property[T] = null, groundTruth: Property[T] = null, exclude: String = ""): Seq[Result] = {
    isTraining = false
    val testParser = new IterableToLBJavaParser[T](testData)
    test(testParser)
  }

  def test(testParser: Parser, prediction: Property[T] = null, groundTruth: Property[T] = null, exclude: String = ""): Seq[Result] = {
    testParser.reset()
    val tester = if (prediction == null && groundTruth == null)
      TestDiscrete.testDiscrete(classifier, classifier.getLabeler, testParser)
    else
      TestDiscrete.testDiscrete(prediction.classifier, groundTruth.classifier, testParser)
    if (!exclude.isEmpty) {
      tester.addNull(exclude)
    }
    tester.printPerformance(System.out)
    tester.getLabels.map { label => Result(label, tester.getF1(label), tester.getPrecision(label), tester.getRecall(label)) }
  }

  /** Test with real-valued (continuous) data. Runs Spearman's and Pearson's correlations.
    *
    * @param testData The continuous data to test on
    */
  def testContinuous(testData: Iterable[T] = null): Unit = {
    isTraining = false
    val testReader = new IterableToLBJavaParser[T](if (testData == null) node.getTestingInstances else testData)
    testReader.reset()
    new TestContinuous(classifier, classifier.getLabeler, testReader)
  }

  @scala.annotation.tailrec
  private final def chunkData(ts: List[Iterable[T]], i: Int, curr: Int, acc: (Iterable[T], Iterable[T])): (Iterable[T], Iterable[T]) = {
    ts match {
      case head :: more =>
        acc match {
          case (train, test) =>
            if (i == curr) {
              // we found the test part
              chunkData(more, i, curr + 1, (train, head))
            } else {
              chunkData(more, i, curr + 1, (head ++ train, test))
            }
        }
      case Nil => acc
    }
  }

  /** Run k fold cross validation.
 *
    * @param k number of folds
    * @param splitPolicy strategy to split the instances into k folds; it can be set to [[SplitPolicy.random]],
    *                    [[SplitPolicy.sequential]], [[SplitPolicy.kth]] or [[SplitPolicy.manual]].
    */
  def crossValidation(k: Int, splitPolicy: SplitPolicy = SplitPolicy.random) = {
    val testReader = new IterableToLBJavaParser[T](trainingInstances)
    val foldParser = new FoldParser(testReader, k, splitPolicy, 0, false)

    val a = (0 until k).map{ fold =>
      foldParser.setPivot(fold)
      foldParser.setFromPivot(false)
      this.learn(10, foldParser.getParser)
      foldParser.setFromPivot(true)
      this.test(foldParser.getParser)
    }
  }

  /** Label property for users classifier */
  def label: Property[T]

  def using(properties: Property[T]*): List[Property[T]] = {
    properties.toList
  }

  def using(properties: List[Property[T]]): List[Property[T]] = {
    using(properties: _*)
  }

  // TODO Move the window properties out of Learner class.
  /** A windows of properties
    *
    * @param before always negative (or 0)
    * @param after always positive (or 0)
    */
  def windowWithin[U <: AnyRef](datamodel: DataModel, before: Int, after: Int, properties: List[Property[T]])(implicit uTag: ClassTag[U], tTag: ClassTag[T]) = {
    val fromTag = tTag
    val toTag = uTag

    val fls = datamodel.EDGES.filter(r => r.from.tag.equals(fromTag) && r.to.tag.equals(toTag)).map(_.forward.asInstanceOf[Link[T, U]]) ++
      datamodel.EDGES.filter(r => r.to.tag.equals(fromTag) && r.from.tag.equals(toTag)).map(_.backward.asInstanceOf[Link[T, U]])

    getWindowWithFilters(before, after, fls.map(e => (t: T) => e.neighborsOf(t).head), properties)
  }

  def window(before: Int, after: Int)(properties: List[Property[T]]): Property[T] = {
    getWindowWithFilters(before, after, Nil, properties)
  }

  private def getWindowWithFilters(before: Int, after: Int, filters: Iterable[T => Any], properties: List[Property[T]]): Property[T] = {
    new PropertyWithWindow[T](node, before, after, filters, properties)
  }

  def nextWithIn[U <: AnyRef](datamodel: DataModel, properties: List[Property[T]])(implicit uTag: ClassTag[U]): Property[T] = {
    this.windowWithin[U](datamodel, 0, 1, properties.toList)
  }

  def prevWithIn[U <: AnyRef](datamodel: DataModel, property: Property[T]*)(implicit uTag: ClassTag[U]): Property[T] = {
    this.windowWithin[U](datamodel, -1, 0, property.toList)
  }

  def nextOf(properties: List[Property[T]]): Property[T] = {
    window(0, 1)(properties)
  }

  def prevOf(properties: List[Property[T]]): Property[T] = {
    window(0, 1)(properties)
  }

}
