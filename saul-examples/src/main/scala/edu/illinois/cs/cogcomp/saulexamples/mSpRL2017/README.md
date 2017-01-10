# Spatial Role Labeling using Images

In this example, we have used CLEF image dataset Available at (http://www.imageclef.org/SIAPRdata) for the task of spatial role labeling using images.

The CLEF dataset contains segmentation masks, visual features, labels for the regions and the hierarchical annotation for regions. 

For further information about these attributes please refer to CLEF dataset README.txt 

## mSpRL using Saul
In this example, we implemented and compared the performance of SVM and Naive Bayes for classifying the image segments using the feature set provided by CLEF image dataset using Saul.

### Data representation and preparation

The CLEF image data is made available in different files, we need basic data structures to load these data and feed them to the Saul application. [`Image`](../../../../../../../../java/edu/illinois/cs/cogcomp/nlp/BaseTypes/Image.java), [`Segment`](../../../../../../../../java/edu/illinois/cs/cogcomp/saulexamples/nlp/BaseTypes/Segment.java), and [`SegmentRelation`](../../../../../../../../java/edu/illinois/cs/cogcomp/saulexamples/nlp/BaseTypes/SegmentRelation.java) classes are used for this purpose.

In `Image` class, all revelant information about image will be stored, in `Segment` class all information about segments such as concept, features etc will be stored and in `SegmentRelation` class information about segment to segment relationship is stored.     

The dataset is a collection of txt files, use image / segment / relation codes so we developed image reader for populating appropiate files and also converting image / segment / relation codes to corresponding concepts.

So, the next step is to populate image / segment / relations.

```scala
val imageReaderTraining = new ImageReader("data/msprl/train")
  val imageReaderTest = new ImageReader("data/msprl/test")

  val imageListTrain = imageReaderTraining.getImages()
  val segementListTrain = imageReaderTraining.getSegments()
  val relationListTrain = imageReaderTraining.getSegmentsRelations()

  images.populate(imageListTrain)
  segments.populate(segementListTrain)
  relation.populate(relationListTrain)


  val imageListTest = imageReaderTest.getImages()
  val segementListTest = imageReaderTest.getSegments()
  val relationListTest = imageReaderTest.getSegmentsRelations()

  images.populate(imageListTest, false)
  segments.populate(segementListTest, false)
  relation.populate(relationListTest, false)
 ```
`ImageReader` is the data reader which loads data from the dataset. `getImages` and `getSegments` returns all the images and segments available in the dataset respectively. 
`getSegmentRelations` returns the relations amoung different segments. 


### Defining the `DataModel`
In order to identify spatial relations, images and segments are populated using image reader, the relation `imageSegmentLink` is used to generate relationship between image and its associated segments.
The relation `rel_segment` is used to generate relationship amoung different segments of the image, like above, below, adjanct. 
See [`mSpRL2017DataModel`](mSpRL2017DataModel.scala)

```scala
  // data model
  val images = node[Image]
  val segments = node[Segment]
  val relation = node[SegmentRelation]

  val image_segment = edge(images, segments)

  val relationsToSegments = edge(relation, segments)
  
```

### Sensors
Next step is to determine sensors:
```scala
  // sensors
    image_segment.addSensor(imageSegmentLink _)
    
    relationsToSegments.addSensor(rel_segment _)
```
The [`imageSegmentLink`](ImageSensors.scala) sensor, generates relationship between image and its associated segements. The [`rel_segment`](ImageSensors.scala) sensor, generates relationship between different segments. 

### Features
Now we can specify the features, all features are constructed using `property` method of `DataModel`. The classifier tries to predict segmentLable, segmentLable is a string like Building, Tree, Group of People etc:
```scala
  // classifier labels
  val imageId = property(images) {
    x: Image => x.getID
  }

  val segmentLable = property(segments) {
    x: Segment => x.getSegmentConcept
  }

  val segmentId = property(segments) {
    x: Segment => x.getSegmentCode
  }
```

### Classification
We used SVM and Naive Bayes classifiers for classifying image segments using the CLEF features. Each feature in CLEF feature set consists of 27 double values, these values are used for classifying the segment. Defining the classifier is straightforward using Saul:

```scala
object ImageClassifiers {
  object ImageSVMClassifier extends Learnable(segments) {
    def label = segmentLable
    override lazy val classifier = new SupportVectorMachine()
    override def feature = using(segmentFeatures)
  }

  object ImageClassifierWeka extends Learnable(segments) {
    def label = segmentLable
    override lazy val classifier = new SaulWekaWrapper(new NaiveBayes())
    override def feature = using(segmentFeatures)
  }
}
```
We extend `Learnable` class of Saul and specify the type of classifier we want. Next the target label for classification is determined by implementing `label` property and finally the set of features needed for classification is provided.
You can find this implementation in [`ImageClassifiers`](ImageClassifiers.scala)

## Test results for mSpRL-2017 data
The mSpRL-2017 dataset is a subset of IAPR TC-12 Benchmark.
mSpRL-2017 data is avaiable at http://www.cs.tulane.edu/~pkordjam/mSpRL_CLEF_lab.htm#data
00 folder of the dataset is used as training data, 01 folder of the dataset is used for testing data. 
<pre>
 Results using SVM Classifier
   
   Label         Precision Recall   F1   LCount PCount
----------------------------------------------------------
ancent-building          0.000  0.000  0.000      2      0
apple                    0.000  0.000  0.000      0      5
bed                      0.000  0.000  0.000    200      0
bench                    0.000  0.000  0.000      4      0
bird                     0.000  0.000  0.000      4      0
boat                     0.000  0.000  0.000      5      1
bottle                   0.000  0.000  0.000      2      0
branch                   0.000  0.000  0.000      5      0
building                 0.000  0.000  0.000     63      0
bus                      0.000  0.000  0.000      1      0
bush                     0.000  0.000  0.000     16     13
cabin                    0.000  0.000  0.000     10      0
cactus                   0.000  0.000  0.000     36      0
can                      0.000  0.000  0.000      1      7
car                      0.000  0.000  0.000     28      4
castle                   0.000  0.000  0.000      1      0
chair                    0.000  0.000  0.000     43      1
child                    0.000  0.000  0.000      3      0
child-boy                1.748 35.714  3.333     14    286
child-girl               0.459 10.000  0.877     10    218
chimney                  0.000  0.000  0.000      3      0
church                   0.000  0.000  0.000      1      0
city                     0.000  0.000  0.000      5      0
cloth                    0.000  0.000  0.000     21      0
cloud                   22.430 47.059 30.380     51    107
column                   0.000  0.000  0.000     31      0
construction             0.000  0.000  0.000      7      0
couple-of-persons        0.000  0.000  0.000      7      0
crocodile                0.000  0.000  0.000      1      0
curtain                  0.000  0.000  0.000     86      0
door                     0.000  0.000  0.000     56      0
eagle                    0.000  0.000  0.000      2      0
edifice                  0.000  0.000  0.000     35      0
fabric                   0.000  0.000  0.000     14      0
face-of-person           1.047 69.231  2.062     13    860
fence                    0.000  0.000  0.000      9      0
flag                     0.000  0.000  0.000     17      0
flock-of-birds           0.000  0.000  0.000      2      0
floor                    0.000  0.000  0.000     18      0
floor-carpet             0.000  0.000  0.000     33      0
floor-other              0.000  0.000  0.000     55      0
floor-wood               0.000  0.000  0.000     20      0
flowerbed                0.000  0.000  0.000      1      0
fountain                 0.000  0.000  0.000      2      0
fruit                    0.000  0.000  0.000      1      0
furniture-other          0.000  0.000  0.000      1      0
glacier                  0.000  0.000  0.000      1      0
grass                    0.000  0.000  0.000     40      0
ground                   1.911 18.750  3.468     32    314
group-of-persons         0.000  0.000  0.000     14     10
hand-of-person           0.000  0.000  0.000      5      0
handcraft                0.000  0.000  0.000      3      0
hat                      0.000  0.000  0.000     17     52
head-of-person           0.000  0.000  0.000      6      0
highway                  0.000  0.000  0.000      1      0
hill                     0.000  0.000  0.000     22      0
house                    0.000  0.000  0.000     50      0
hut                      0.000  0.000  0.000      7      0
ice                      0.000  0.000  0.000      3      0
lake                     0.000  0.000  0.000      6      0
lamp                     0.000  0.000  0.000     99      0
landscape-nature         0.000  0.000  0.000      5      0
leaf                     0.000  0.000  0.000      5      0
lizard                   0.000  0.000  0.000      2      0
llama                    0.000  0.000  0.000      2      0
man                      0.000  0.000  0.000     11      0
monument                 0.000  0.000  0.000      1      0
mountain                 0.000  0.000  0.000     34      0
non-wooden-furniture     0.000  0.000  0.000     41      0
ocean                    0.000  0.000  0.000     10      0
orange                   0.000  0.000  0.000     16      0
painting                 0.000  0.000  0.000     47      0
palm                     0.000  0.000  0.000     23      5
paper                    0.000  0.000  0.000      1      0
person                   0.000  0.000  0.000      6      1
plant                    0.000  0.000  0.000     62      0
plant-pot                0.000  0.000  0.000      8      0
public-sign              0.000  0.000  0.000     29      0
railroad                 0.000  0.000  0.000      1      0
road                     0.000  0.000  0.000      7      0
rock                     2.299 20.000  4.124     30    261
roof                     0.000  0.000  0.000     20      0
sand-beach               0.000  0.000  0.000     14     43
sand-dessert             0.000  0.000  0.000      1      0
ship                     0.000  0.000  0.000      6      0
sidewalk                 0.000  0.000  0.000     20      0
sky                      0.000  0.000  0.000     15      0
sky-blue                31.795 65.957 42.907     94    195
sky-light               62.963 25.373 36.170     67     27
sky-night                0.000  0.000  0.000      1      3
snake                    0.000  0.000  0.000      1      0
snow                     0.000  0.000  0.000      4      0
stairs                   0.000  0.000  0.000      7      0
statue                   0.000  0.000  0.000      3      0
steam                    0.000  0.000  0.000      2      0
street                   0.000  0.000  0.000     18      0
swimming-pool           25.000  9.524 13.793     21      8
table                    0.000  0.000  0.000     22      0
telephone                0.000  0.000  0.000     11      0
tire                     0.000  0.000  0.000      0     12
toy                      0.000  0.000  0.000      3      0
trash                    0.000  0.000  0.000      0      2
tree                     0.000  0.000  0.000     60      0
trees                   13.333  4.878  7.143     41     15
trunk                    0.000  0.000  0.000      4      2
umbrella                 0.000  0.000  0.000     19      0
vegetation               1.724  2.174  1.923     46     58
vehicles-with-tires      0.000  0.000  0.000      6      0
wall                     1.724  0.500  0.775    200     58
water                    0.000  0.000  0.000     22      0
water-reflection         0.000  0.000  0.000      3      0
waves                    0.000  0.000  0.000      3      0
window                   0.000  0.000  0.000    192     18
woman                    0.000  0.000  0.000      5      0
wood                     0.000  0.000  0.000      8      0
wooden-furniture         0.000  0.000  0.000     71     11
----------------------------------------------------------
Accuracy                 5.237   -      -      -      2597

</pre>

<pre>

 Results using Naive Bayes Classifier

 Label         Precision Recall   F1   LCount PCount
----------------------------------------------------------
ancent-building          0.000  0.000  0.000      2      0
balloon                  0.000  0.000  0.000      0     48
bed                      0.000  0.000  0.000    200      0
bench                    0.000  0.000  0.000      4      0
bird                     0.000  0.000  0.000      4      0
boat                     0.000  0.000  0.000      5      0
book                     0.000  0.000  0.000      0     44
bottle                   0.000  0.000  0.000      2      0
branch                   0.000  0.000  0.000      5     58
building                 0.000  0.000  0.000     63     13
bus                      0.000  0.000  0.000      1      0
bush                     9.091  6.250  7.407     16     11
cabin                    0.000  0.000  0.000     10      0
cactus                   0.000  0.000  0.000     36      0
can                      0.000  0.000  0.000      1      0
car                      5.714  7.143  6.349     28     35
castle                   0.000  0.000  0.000      1      0
chair                    0.000  0.000  0.000     43     40
child                    0.000  0.000  0.000      3      3
child-boy                3.509 28.571  6.250     14    114
child-girl               0.935 10.000  1.709     10    107
chimney                  0.000  0.000  0.000      3      0
church                   0.000  0.000  0.000      1      3
city                     2.000 20.000  3.636      5     50
cloth                    0.000  0.000  0.000     21     12
cloud                    5.263  1.961  2.857     51     19
column                   0.000  0.000  0.000     31      0
construction             1.818 14.286  3.226      7     55
couple-of-persons        0.000  0.000  0.000      7     16
crocodile                0.000  0.000  0.000      1      0
curtain                  0.000  0.000  0.000     86      0
door                     3.333  1.786  2.326     56     30
eagle                    0.000  0.000  0.000      2      0
edifice                  0.000  0.000  0.000     35     60
fabric                   0.000  0.000  0.000     14      0
face-of-person           9.231 46.154 15.385     13     65
fence                    0.000  0.000  0.000      9      0
flag                     0.000  0.000  0.000     17      8
flock-of-birds           0.000  0.000  0.000      2      0
floor                    0.000  0.000  0.000     18      0
floor-carpet             0.000  0.000  0.000     33      0
floor-other              9.091  7.273  8.081     55     44
floor-wood               0.000  0.000  0.000     20      0
flowerbed                0.000  0.000  0.000      1      0
fountain                 0.000  0.000  0.000      2      0
fruit                    0.000  0.000  0.000      1      0
furniture-other          0.000  0.000  0.000      1      0
glacier                  0.000  0.000  0.000      1      0
grass                    6.667  2.500  3.636     40     15
ground                   2.874 15.625  4.854     32    174
group-of-persons         0.000  0.000  0.000     14     36
hand-of-person           0.000  0.000  0.000      5     45
handcraft                0.000  0.000  0.000      3      0
hat                     12.000 17.647 14.286     17     25
head-of-person           0.000  0.000  0.000      6     99
highway                  0.000  0.000  0.000      1      0
hill                     2.500  4.545  3.226     22     40
house                    0.000  0.000  0.000     50      3
humans                   0.000  0.000  0.000      0      6
hut                      0.000  0.000  0.000      7      0
ice                      0.000  0.000  0.000      3      0
jewelry                  0.000  0.000  0.000      0      5
lake                     0.000  0.000  0.000      6      0
lamp                     0.000  0.000  0.000     99      0
landscape-nature         0.000  0.000  0.000      5      0
leaf                     0.000  0.000  0.000      5      0
lizard                   0.000  0.000  0.000      2      0
llama                    0.000  0.000  0.000      2      0
man                      0.300  9.091  0.581     11    333
monument                 0.000  0.000  0.000      1      0
mountain                 0.000  0.000  0.000     34     22
non-wooden-furniture     0.000  0.000  0.000     41      0
ocean                    0.000  0.000  0.000     10      0
orange                   0.000  0.000  0.000     16      5
painting                 0.000  0.000  0.000     47      7
palm                     0.000  0.000  0.000     23     37
paper                    0.000  0.000  0.000      1     52
person                   0.000  0.000  0.000      6     53
plant                    0.000  0.000  0.000     62      0
plant-pot                0.000  0.000  0.000      8      0
public-sign              0.000  0.000  0.000     29      9
railroad                 0.000  0.000  0.000      1      0
road                     0.000  0.000  0.000      7      3
rock                     4.902 16.667  7.576     30    102
roof                     0.000  0.000  0.000     20      0
sand-beach               0.000  0.000  0.000     14      0
sand-dessert             0.000  0.000  0.000      1      0
ship                     0.000  0.000  0.000      6      0
sidewalk                11.364 25.000 15.625     20     44
sky                      0.000  0.000  0.000     15      1
sky-blue                42.373 53.191 47.170     94    118
sky-light               55.556 52.239 53.846     67     63
sky-night                0.000  0.000  0.000      1      0
snake                    0.000  0.000  0.000      1      0
snow                     0.000  0.000  0.000      4      0
stairs                   0.000  0.000  0.000      7      0
statue                   0.000  0.000  0.000      3      0
steam                    0.000  0.000  0.000      2      0
street                  12.500  5.556  7.692     18      8
swimming-pool            0.000  0.000  0.000     21      0
table                    0.000  0.000  0.000     22      4
telephone                0.000  0.000  0.000     11      0
tower                    0.000  0.000  0.000      0     22
toy                      0.000  0.000  0.000      3     13
tree                     1.923  1.667  1.786     60     52
trees                   10.417 12.195 11.236     41     48
trunk                    0.000  0.000  0.000      4      2
umbrella                 0.000  0.000  0.000     19      0
vegetation               9.091  2.174  3.509     46     11
vehicles-with-tires      0.000  0.000  0.000      6      0
wall                    25.758  8.500 12.782    200     66
water                    6.000 13.636  8.333     22     50
water-reflection         0.000  0.000  0.000      3      0
waves                    0.000  0.000  0.000      3      0
window                  15.294  6.771  9.386    192     85
woman                    0.000  0.000  0.000      5     19
wood                     0.000  0.000  0.000      8     17
wooden-furniture         2.941  2.817  2.878     71     68
----------------------------------------------------------
Accuracy                 6.585   -      -      -      2597
</pre>
## Running
To run the main app with default properties:

```
sbt "project saulExamples" "runMain edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.mSpRL2017App"
```

## References
[1] Kordjamshidi, Parisa, Steven Bethard, and Marie-Francine Moens. "SemEval-2012 task 3: Spatial role labeling." Proceedings of the First Joint Conference on Lexical and Computational Semantics. Association for Computational Linguistics, 2012.

[2] Roberts, Kirk, and Sanda M. Harabagiu. "UTD-SpRL: A joint approach to spatial role labeling." Proceedings of the First Joint Conference on Lexical and Computational Semantics. Association for Computational Linguistics, 2012.
