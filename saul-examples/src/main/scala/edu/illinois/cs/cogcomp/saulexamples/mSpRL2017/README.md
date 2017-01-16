# Spatial Role Labeling using Images

In this example, we have used CLEF image dataset Available at (http://www.imageclef.org/SIAPRdata) for the task of spatial role labeling using images.

The CLEF dataset contains segmentation masks, visual features, labels for the regions and the hierarchical annotation for regions. 

For further information about these attributes please refer to CLEF dataset README.txt 

## mSpRL using Saul
In this example, we implemented and compared the performance of SVM and Naive Bayes for classifying the image segments using the feature set provided by CLEF image dataset using Saul.

### Data representation and preparation

The CLEF image data is made available in different files, we have used the following files in this project 1) wlist.txt, 2) labels.txt, 3) features.txt, 4) spatial_rels mat (Matlab) files 5) training.mat and 6) testing.mat.

`wlist.txt` file contains the vocabulary used for annotation, the format of the file is Id followed by label.

29	branch

30	bridge

These Id are used in other files for referring the associated label.

`label.txt` file contains the codes of segments / objects found in the image, the format of the file is ImageId followed by segment sequence number followed by segment code.
  
25	1	29

25	2	60

25	3	31

means that segments / objects "1", "2" and "3" in image "25" (.jpg) have associated the labels 29 ("branch"), 60 ("cloud") and 31 ("building") respectively.

`feature.txt` file contains visual features extracted for each segment / object of the image. The visual features were extracted using code from Peter Carbonetto [3] and are the following: "region area, width and height of the region, mean and standard deviation in the x and y axis, boundary/area, convexity, average, standard deviation and skewness in RGB and CIE-Lab color spaces, for a total of 27 attributes". the format of the file is as follow:

112	1	0.4417593         0.775     0.8583333     0.5560012     0.6382412      0.656913     0.7427811    0.01397768     0.3359084      119.3438      111.6017      105.8651      67.57657      64.81628      66.64162     0.3635511     0.5000695     0.7093432      68.94983      1.267164      3.321504      17.48109      8.993122      8.798025    -0.0448975     0.1654533      1.945854	120
112	2	0.0189062     0.127083     0.219444     0.932729      0.59748      0.11265     0.170704    0.0624426     0.322059      69.2314      76.0888      70.3459      32.4674      33.4975       32.245      1.57548      1.40818      1.56848      59.4284     -3.34258      1.99991      10.3577      2.65751      1.48654     0.785716    -0.310416      0.87871	204

The file contains 30 columns, columns 1 and 2 are as in the `labels.txt` file, columns 3-29 are the values of the extracted visual features, the last column is as the third column in `labels.txt`. 

`spatial_rels` is a directory containing information of spatial relationships in the images. These relations are stored in a matlab file, with the same name of the image from which they were extracted.  Each file contains three matrices namely topo, x_rels and y_rels. The matrice topo stores the topological relations (i.e. 1: adjacent and 2: disjoint), x_rels matice stores the direction relations with reference to the x axis (i.e. 3: x-aligned and 4: beside) and y_rels matrice stores the direction relations with reference to the y axis (i.e. 5: y-aligned, 6: above and 7: below). 


`training.mat` file contains the list of images (14000 in total) to be used in the training.
  
`testing.mat` file contains the list of images (4000 in total) to be used in the testing.

We need basic data structures to load these data and feed them to the Saul application, therefore, we developed [`Image`](../../../../../../../../java/edu/illinois/cs/cogcomp/nlp/BaseTypes/Image.java), [`Segment`](../../../../../../../../java/edu/illinois/cs/cogcomp/saulexamples/nlp/BaseTypes/Segment.java), and [`SegmentRelation`](../../../../../../../../java/edu/illinois/cs/cogcomp/saulexamples/nlp/BaseTypes/SegmentRelation.java) classes for this purpose.

In `Image` class, all revelant information about image will be stored, in `Segment` class all information about segments such as concept, features etc will be stored and in `SegmentRelation` class information about segment to segment relationship is stored.     

As discussed above image dataset is a collection of different files, therefore, we developed CLEFImageReader for populating appropiate files and also converting image / segment / relation codes to corresponding concepts.

So, the next step is to populate image / segment / relations.

```scala
val CLEFDataset = new CLEFImageReader("/saiaprtc12ok/benchmark/saiapr_tc-12")

  val imageListTrain = CLEFDataset.trainingImages
  val segementListTrain = CLEFDataset.trainingSegments
  val relationListTrain = CLEFDataset.trainingRelations

  images.populate(imageListTrain)
  segments.populate(segementListTrain)
  relation.populate(relationListTrain)


  val imageListTest = CLEFDataset.testImages
  val segementListTest = CLEFDataset.testSegments
  val relationListTest = CLEFDataset.testRelations

  images.populate(imageListTest, false)
  segments.populate(segementListTest, false)
  relation.populate(relationListTest, false)
 ```
`CLEFImageReader` is the data reader which loads data from the dataset. `CLEFDataset.trainingImages` and `CLEFDataset.trainingSegments` returns all the training images and segments available in the dataset respectively. `CLEFDataset.trainingRelations` returns the relations amoung different segments. 
 
 `CLEFDataset.testImages` and `CLEFDataset.testSegments` returns all the training images and segments available in the dataset respectively. `CLEFDataset.testRelations` returns the relations amoung different segments.


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
The IAPR TC-12 Benchmark dataset is used in this example.

Total Training Images: 14000

Total Segments in Training Images: 70086

Total Relations Created Using Training Segments: 1274046

Total Test Images: 4000

Total Segments in Test Images: 19612

Total Relations Created Using Test Segments: 356538

Note: The training or test relation created in this example are not playing any role in classification, however, they will play very important role in Multi-Model Spatial Role Labeling

<pre>
 Results using SVM Classifier
   
        Label          Precision Recall   F1   LCount PCount
------------------------------------------------------------
air-vehicles               0.000  0.000  0.000      1      0
airplane                   0.000  0.000  0.000     21      0
ancent-building            0.000  0.000  0.000     21      0
animal                     0.000  0.000  0.000      2      0
antelope                   0.000  0.000  0.000      2      0
apple                      0.000  0.000  0.000      1      0
arctic                     0.000  0.000  0.000      1      0
baby                       0.000  0.000  0.000      4      0
ball                       0.000  0.000  0.000      6      0
balloon                    0.000  0.000  0.000     23      0
beach                      0.000  0.000  0.000     10      0
bear                       0.000  0.000  0.000      2      0
bed                        0.000  0.000  0.000    143      0
bench                      0.000  0.000  0.000     25      0
bicycle                    0.000  0.000  0.000     77      0
bird                       0.000  0.000  0.000     59      0
boat                       0.000  0.000  0.000     71      0
boat-rafting               0.000  0.000  0.000      9      0
bobcat-wildcat             0.000  0.000  0.000      1      0
book                       0.000  0.000  0.000     14      0
bottle                     0.000  0.000  0.000     87      0
branch                     0.000  0.000  0.000     82      1
bridge                     0.000  0.000  0.000     30      0
building                   0.000  0.000  0.000    297      0
bull                       0.000  0.000  0.000      2      0
bus                        0.000  0.000  0.000     28      0
bush                       0.000  0.000  0.000    139      0
butterfly                  0.000  0.000  0.000      1      0
cabin                      0.000  0.000  0.000     17      0
cactus                     0.000  0.000  0.000     78      0
camel                      0.000  0.000  0.000      1      0
camera                     0.000  0.000  0.000     17      0
can                        0.000  0.000  0.000     14      0
canine                     0.000  0.000  0.000      1      0
cannon                     0.000  0.000  0.000      3      0
car                        0.000  0.000  0.000    216      0
castle                     0.000  0.000  0.000     44      0
cello                      0.000  0.000  0.000      1      0
chair                      0.000  0.000  0.000    122      0
child                      0.000  0.000  0.000     17      0
child-boy                  0.000  0.000  0.000    117      0
child-girl                 0.000  0.000  0.000    116      4
chimney                    0.000  0.000  0.000      1      0
church                     0.000  0.000  0.000     84      0
church-interior            0.000  0.000  0.000      5      0
city                       0.000  0.000  0.000    147      0
clock                      0.000  0.000  0.000     10      0
cloth                      0.000  0.000  0.000    104      0
cloud                     20.309 28.548 23.734    599    842
column                     0.000  0.000  0.000     74      0
construction               0.000  0.000  0.000      9      0
construction-other         0.000  0.000  0.000     40      0
coral                      0.000  0.000  0.000     14      0
couple-of-persons          0.000  0.000  0.000    248      0
cow                        0.000  0.000  0.000      5      0
crab                       0.000  0.000  0.000      6      0
crocodile                  0.000  0.000  0.000      6      0
cup                        0.000  0.000  0.000     30      0
curtain                    0.000  0.000  0.000    101      0
deer                       0.000  0.000  0.000      3      0
desk                       0.000  0.000  0.000     12      0
dessert                    0.000  0.000  0.000      7      0
dish                      27.273  3.846  6.742     78     11
diver                      0.000  0.000  0.000      2      0
dog                        0.000  0.000  0.000      8      0
dolphin                    0.000  0.000  0.000      1      0
door                       0.000  0.000  0.000    128      0
eagle                      0.000  0.000  0.000     10      0
edifice                    0.000  0.000  0.000     73      0
entity                     0.000  0.000  0.000      2      0
fabric                     0.000  0.000  0.000    211      0
face-of-person             8.046  8.046  8.046    174    174
fence                      0.000  0.000  0.000    115      0
fire                       0.000  0.000  0.000     11      0
firework                   0.000  0.000  0.000      4      0
fish                       0.000  0.000  0.000      9      0
flag                      20.253 25.397 22.535     63     79
flamingo                   0.000  0.000  0.000      5      0
flock-of-birds             0.000  0.000  0.000      7      0
floor                      0.000  0.000  0.000    228      0
floor-carpet               0.000  0.000  0.000     28      0
floor-other                0.000  0.000  0.000    152      0
floor-tennis-court         0.000  0.000  0.000     34      0
floor-wood                 0.000  0.000  0.000     65      0
flower                     0.000  0.000  0.000     41      8
flowerbed                  0.000  0.000  0.000     45      4
food                       0.000  0.000  0.000     29      0
fountain                   0.000  0.000  0.000     25      0
fowl-hen                   0.000  0.000  0.000      1      0
fruit                      0.000  0.000  0.000     56      3
furniture                  0.000  0.000  0.000     17      0
furniture-other            0.000  0.000  0.000     35      0
generic-objects            0.000  0.000  0.000     55      0
giraffe                    0.000  0.000  0.000      8      0
glacier                    0.000  0.000  0.000     24      0
glass                      0.000  0.000  0.000     72      0
grapes                     0.000  0.000  0.000      2      0
grass                     23.891 73.844 36.101    627   1938
ground                    13.583 70.396 22.771    733   3799
ground-vehicles            0.000  0.000  0.000      5      0
group-of-persons          14.128 67.844 23.386    793   3808
guitar                     0.000  0.000  0.000      2      0
hand-of-person             0.000  0.000  0.000     60      0
handcraft                  0.000  0.000  0.000     26      0
hat                        0.000  0.000  0.000    155      0
head-of-person             0.000  0.000  0.000     95      0
helicopter                 0.000  0.000  0.000      1      0
herd-of-mammals            0.000  0.000  0.000     26      0
highway                    0.000  0.000  0.000    102      0
hill                       0.000  0.000  0.000    168      0
horn                       0.000  0.000  0.000      1      0
horse                      0.000  0.000  0.000     69      0
house                      0.000  0.000  0.000    264      0
humans                     0.000  0.000  0.000      6      0
hut                        0.000  0.000  0.000     47      0
ice                        0.000  0.000  0.000     12      0
iguana                     0.000  0.000  0.000      6      0
island                     0.000  0.000  0.000     12      0
jewelry                    0.000  0.000  0.000      6      0
kangaroo                   0.000  0.000  0.000     13      0
kitchen-pot                0.000  0.000  0.000     56      0
koala                      0.000  0.000  0.000      3      0
lake                       0.000  0.000  0.000    116      0
lamp                       7.143  1.325  2.235    151     28
landscape-nature           0.000  0.000  0.000     12      0
leaf                       0.000  0.000  0.000     32      1
lighthouse                 0.000  0.000  0.000     12      0
lizard                     0.000  0.000  0.000      4      0
llama                      0.000  0.000  0.000     39      0
log                        0.000  0.000  0.000      2      0
mammal                     0.000  0.000  0.000     10      0
mammal-other               0.000  0.000  0.000     17      0
man                        9.570 24.531 13.769    799   2048
man-made-other             0.000  0.000  0.000      6      0
monkey                     0.000  0.000  0.000      4      0
monument                   0.000  0.000  0.000     32      0
motorcycle                 0.000  0.000  0.000     11      0
mountain                  14.044 11.910 12.889    487    413
mural-carving              0.000  0.000  0.000     14      0
mushroom                   0.000  0.000  0.000      1      0
musical-instrument         0.000  0.000  0.000     17      0
non-wooden-furniture       0.000  0.000  0.000     59      0
ocean                     14.646  7.733 10.122    375    198
ocean-animal               0.000  0.000  0.000      2      0
orange                     0.000  0.000  0.000      4      4
pagoda                     0.000  0.000  0.000     12      0
painting                   0.000  0.000  0.000    112      0
palm                       0.000  0.000  0.000    129      0
paper                      0.000  0.000  0.000     46      0
parrot                     0.000  0.000  0.000      3      0
penguin                    0.000  0.000  0.000     11      0
person                     0.000  0.000  0.000    258      0
person-related-objects     0.000  0.000  0.000      7      0
piano                      0.000  0.000  0.000      1      0
pigeon                     0.000  0.000  0.000      8      0
plant                      0.000  0.000  0.000    201      6
plant-pot                  0.000  0.000  0.000     54      0
primate                    0.000  0.000  0.000      3      0
public-sign                0.000  0.000  0.000    134      0
pyramid                    0.000  0.000  0.000      5      0
rabbit                     0.000  0.000  0.000      1      0
rafter                     0.000  0.000  0.000     23      0
railroad                   0.000  0.000  0.000     14      0
reptile                    0.000  0.000  0.000      2      0
river                     25.000  1.242  2.367    161      8
road                       0.000  0.000  0.000     63      0
rock                       3.883  3.155  3.481    634    515
rodent                     0.000  0.000  0.000      7      0
roof                       0.000  0.000  0.000     90      0
ruin-archeological         0.000  0.000  0.000     68      0
sand-beach                 0.000  0.000  0.000    157      0
sand-dessert               0.000  0.000  0.000    102      0
saxophone                  0.000  0.000  0.000      1      0
school-of-fishes           0.000  0.000  0.000      1      0
screen                     0.000  0.000  0.000      5      0
seal                       0.000  0.000  0.000     39      0
semaphore                  0.000  0.000  0.000      4      0
sheep                      0.000  0.000  0.000      2      0
ship                       0.000  0.000  0.000     43      0
shore                      0.000  0.000  0.000     19      0
sidewalk                   0.000  0.000  0.000    180      0
sky                       34.843 32.065 33.396    552    508
sky-blue                  36.044 91.285 51.682   1136   2877
sky-light                 48.023 68.919 56.604    370    531
sky-night                 28.571  3.333  5.970     60      7
sky-red-sunset-dusk       29.630 13.333 18.391     60     27
smoke                      0.000  0.000  0.000      4      0
snake                      0.000  0.000  0.000      1      0
snow                       0.000  0.000  0.000    101      0
squirrel                   0.000  0.000  0.000      2      0
stairs                     0.000  0.000  0.000     57      0
statue                     0.000  0.000  0.000     49      0
steam                      0.000  0.000  0.000     17      0
street                     0.000  0.000  0.000    205      0
sun                       62.500 83.333 71.429     18     24
surfboard                  0.000  0.000  0.000     17      0
swimming-pool              0.000  0.000  0.000     29      7
table                      0.000  0.000  0.000    105      0
telephone                  0.000  0.000  0.000     12      0
tiger                      0.000  0.000  0.000      1      0
tire                       0.000  0.000  0.000     23      0
tower                      0.000  0.000  0.000     35      0
toy                        0.000  0.000  0.000     12      0
train                      0.000  0.000  0.000     26      1
trash                      0.000  0.000  0.000     10      0
tree                       0.000  0.000  0.000    263      0
trees                     18.928 30.571 23.380    543    877
trunk                      0.000  0.000  0.000     98      0
turtle                     0.000  0.000  0.000      4      0
umbrella                   0.000  0.000  0.000     32      0
vegetable                  0.000  0.000  0.000      8      0
vegetation                14.375 16.312 15.282    564    640
vehicle                    0.000  0.000  0.000      6      0
vehicles-with-tires        0.000  0.000  0.000     10      0
viola                      0.000  0.000  0.000      1      0
violin                     0.000  0.000  0.000      1      0
volcano                    0.000  0.000  0.000     10      0
wall                       9.174  1.969  3.241    508    109
water                      0.000  0.000  0.000    139      0
water-reflection           0.000  0.000  0.000     60      4
water-vehicles             0.000  0.000  0.000      3      0
waterfall                 50.000  3.448  6.452     58      4
waves                      0.000  0.000  0.000     20      0
whale                      0.000  0.000  0.000      4      0
window                     0.000  0.000  0.000    333      0
woman                      5.814  0.986  1.686    507     86
wood                       0.000  0.000  0.000     85      0
wooden-furniture          16.667  2.655  4.580    113     18
------------------------------------------------------------
Accuracy                  19.376   -      -      -     19612

</pre>

<pre>

 Results using Naive Bayes Classifier

        Label          Precision Recall    F1   LCount PCount
-------------------------------------------------------------
air-vehicles               0.000   0.000  0.000      1     11
airplane                   4.225  42.857  7.692     21    213
ancent-building           25.000   4.762  8.000     21      4
animal                     0.000   0.000  0.000      2      3
antelope                   0.901  50.000  1.770      2    111
ape                        0.000   0.000  0.000      0     20
apple                      0.000   0.000  0.000      1      6
arctic                     0.000   0.000  0.000      1     80
baby                       0.000   0.000  0.000      4      0
ball                       0.267  16.667  0.525      6    375
balloon                   50.000   4.348  8.000     23      2
beach                      0.000   0.000  0.000     10      0
bear                       0.000   0.000  0.000      2      0
beaver                     0.000   0.000  0.000      0     10
bed                       30.000  16.783 21.525    143     80
bench                      0.000   0.000  0.000     25      0
bicycle                   11.538   3.896  5.825     77     26
bird                       5.882   3.390  4.301     59     34
boat                       0.000   0.000  0.000     71      2
boat-rafting               6.667  11.111  8.333      9     15
bobcat-wildcat             0.000   0.000  0.000      1      0
book                       0.000   0.000  0.000     14      2
bottle                     0.000   0.000  0.000     87     26
branch                     0.000   0.000  0.000     82      2
bridge                     5.000   3.333  4.000     30     20
building                  21.212   2.357  4.242    297     33
bull                       0.000   0.000  0.000      2      0
bus                        0.000   0.000  0.000     28     13
bush                       7.660  26.619 11.897    139    483
butterfly                  0.000   0.000  0.000      1     30
cabin                      0.000   0.000  0.000     17      0
cactus                   100.000   1.282  2.532     78      1
camel                      0.000   0.000  0.000      1     34
camera                     0.820  23.529  1.584     17    488
can                        0.000   0.000  0.000     14      2
canine                     0.000   0.000  0.000      1     36
cannon                     0.000   0.000  0.000      3     13
car                       14.286   6.019  8.469    216     91
castle                    11.628  11.364 11.494     44     43
cello                      0.000   0.000  0.000      1      9
chair                     33.333   0.820  1.600    122      3
child                      0.000   0.000  0.000     17      0
child-boy                 20.000   1.709  3.150    117     10
child-girl                15.000  15.517 15.254    116    120
chimney                    0.000   0.000  0.000      1      6
church                     0.000   0.000  0.000     84      6
church-interior            1.705  60.000  3.315      5    176
city                       8.960  21.088 12.576    147    346
clock                      1.167  30.000  2.247     10    257
cloth                     14.286   2.885  4.800    104     21
cloud                     23.704  16.027 19.124    599    405
column                     0.000   0.000  0.000     74      0
construction               0.000   0.000  0.000      9      0
construction-other         0.000   0.000  0.000     40      0
coral                      9.302  85.714 16.783     14    129
couple-of-persons          0.000   0.000  0.000    248      1
cow                        0.000   0.000  0.000      5      3
crab                       0.000   0.000  0.000      6     80
crocodile                  0.000   0.000  0.000      6     29
cup                        0.000   0.000  0.000     30      0
curtain                   35.714   4.950  8.696    101     14
deer                       0.000   0.000  0.000      3     39
desk                       0.000   0.000  0.000     12      1
dessert                    4.000  14.286  6.250      7     25
dish                      28.571   7.692 12.121     78     21
diver                      5.556  50.000 10.000      2     18
dog                        0.719  12.500  1.361      8    139
dolphin                    0.000   0.000  0.000      1      7
door                       6.452  12.500  8.511    128    248
eagle                      4.167  20.000  6.897     10     48
edifice                    0.000   0.000  0.000     73      0
entity                     0.000   0.000  0.000      2      0
fabric                    18.182   0.948  1.802    211     11
face-of-person            21.888  58.621 31.875    174    466
feline                     0.000   0.000  0.000      0     34
fence                      0.000   0.000  0.000    115      3
fire                       4.839  27.273  8.219     11     62
firework                   0.000   0.000  0.000      4     14
fish                       0.000   0.000  0.000      9      7
flag                      20.354  36.508 26.136     63    113
flamingo                   0.000   0.000  0.000      5    121
flock-of-birds             0.000   0.000  0.000      7     16
floor                     10.000   0.439  0.840    228     10
floor-carpet               3.704  14.286  5.882     28    108
floor-other                0.000   0.000  0.000    152     10
floor-tennis-court        28.169  58.824 38.095     34     71
floor-wood                 8.197   7.692  7.937     65     61
flower                    10.345  14.634 12.121     41     58
flowerbed                 11.765  13.333 12.500     45     51
food                       0.000   0.000  0.000     29      0
fountain                   0.000   0.000  0.000     25      2
fowl-hen                   0.000   0.000  0.000      1      4
fruit                      3.846   3.571  3.704     56     52
furniture                  0.000   0.000  0.000     17      0
furniture-other            0.000   0.000  0.000     35      0
generic-objects            0.000   0.000  0.000     55      0
giraffe                    2.941  25.000  5.263      8     68
glacier                    6.494  41.667 11.236     24    154
glass                      0.000   0.000  0.000     72      6
goat                       0.000   0.000  0.000      0      4
grapes                     0.000   0.000  0.000      2      0
grass                     48.244  41.627 44.692    627    541
ground                    29.243  19.509 23.404    733    489
ground-vehicles            0.000   0.000  0.000      5     13
group-of-persons          41.142  44.515 42.762    793    858
guitar                     0.000   0.000  0.000      2     29
hand-of-person             6.167  23.333  9.756     60    227
handcraft                  0.000   0.000  0.000     26      0
hat                       18.750   1.935  3.509    155     16
hawk                       0.000   0.000  0.000      0     19
head-of-person             5.848  10.526  7.519     95    171
helicopter                 0.000   0.000  0.000      1     51
herd-of-mammals            0.000   0.000  0.000     26      0
highway                   13.881  48.039 21.538    102    353
hill                      14.286   3.571  5.714    168     42
horn                       0.000   0.000  0.000      1      0
horse                     12.245   8.696 10.169     69     49
house                      8.333   0.379  0.725    264     12
humans                     0.000   0.000  0.000      6      0
hut                        0.000   0.000  0.000     47      0
ice                        2.041   8.333  3.279     12     49
iguana                     0.000   0.000  0.000      6    132
insect                     0.000   0.000  0.000      0     18
island                     1.970  33.333  3.721     12    203
jewelry                    0.000   0.000  0.000      6      1
kangaroo                   1.802  15.385  3.226     13    111
kitchen-pot                0.000   0.000  0.000     56      0
koala                      5.357 100.000 10.169      3     56
lake                       8.108   2.586  3.922    116     37
lamp                      25.000   2.649  4.790    151     16
landscape-nature           0.000   0.000  0.000     12      8
leaf                      25.926  43.750 32.558     32     54
lighthouse                 0.000   0.000  0.000     12      0
lion                       0.000   0.000  0.000      0     30
lizard                     0.000   0.000  0.000      4      7
llama                      3.571   5.128  4.211     39     56
log                        0.000   0.000  0.000      2     16
mammal                    50.000  10.000 16.667     10      2
mammal-other               0.000   0.000  0.000     17      0
man                       26.908  17.647 21.315    799    524
man-made                   0.000   0.000  0.000      0      2
man-made-other             0.000   0.000  0.000      6      0
monkey                     0.000   0.000  0.000      4      3
monument                   0.000   0.000  0.000     32      0
motorcycle                 0.000   0.000  0.000     11      2
mountain                  34.688  26.283 29.907    487    369
mural-carving              3.167  50.000  5.957     14    221
mushroom                   0.000   0.000  0.000      1     14
musical-instrument         0.000   0.000  0.000     17     78
non-wooden-furniture       0.000   0.000  0.000     59      1
ocean                     29.925  32.000 30.928    375    401
ocean-animal               0.000   0.000  0.000      2     35
orange                     3.846  50.000  7.143      4     52
pagoda                     3.571  25.000  6.250     12     84
painting                  26.087   5.357  8.889    112     23
palm                      36.842   5.426  9.459    129     19
paper                      6.522   6.522  6.522     46     46
parrot                     0.000   0.000  0.000      3     39
penguin                    1.327  54.545  2.592     11    452
person                     5.172   1.163  1.899    258     58
person-related-objects     0.000   0.000  0.000      7      0
piano                      4.000 100.000  7.692      1     25
pigeon                     1.770  25.000  3.306      8    113
plant                      0.000   0.000  0.000    201      1
plant-pot                  0.000   0.000  0.000     54      0
primate                    0.000   0.000  0.000      3      6
public-sign               60.000   2.239  4.317    134      5
pyramid                    0.000   0.000  0.000      5      2
rabbit                     0.000   0.000  0.000      1     39
rafter                     9.302  52.174 15.789     23    129
railroad                   0.000   0.000  0.000     14     32
reptile                    0.000   0.000  0.000      2      8
river                      0.000   0.000  0.000    161      4
road                       0.000   0.000  0.000     63      0
rock                      22.222   1.262  2.388    634     36
rodent                     0.000   0.000  0.000      7     22
roof                      33.333   2.222  4.167     90      6
ruin-archeological         4.025  19.118  6.650     68    323
sand-beach                 7.240  10.191  8.466    157    221
sand-dessert              10.084  23.529 14.118    102    238
saxophone                  0.000   0.000  0.000      1      0
school-of-fishes           0.000   0.000  0.000      1     12
screen                     0.000   0.000  0.000      5      0
seal                       3.070  17.949  5.243     39    228
semaphore                  0.000   0.000  0.000      4    252
sheep                      0.000   0.000  0.000      2      2
ship                       0.000   0.000  0.000     43      3
shore                     25.000  10.526 14.815     19      8
sidewalk                  19.355   3.333  5.687    180     31
sky                       37.687  54.891 44.690    552    804
sky-blue                  70.068  63.468 66.605   1136   1029
sky-light                 61.358  63.514 62.417    370    383
sky-night                 29.518  81.667 43.363     60    166
sky-red-sunset-dusk       36.250  48.333 41.429     60     80
smoke                      1.282  25.000  2.439      4     78
snake                      0.000   0.000  0.000      1     61
snow                       7.252  18.812 10.468    101    262
squirrel                   0.000   0.000  0.000      2      0
stairs                     0.000   0.000  0.000     57      5
statue                     0.000   0.000  0.000     49      3
steam                     10.345  17.647 13.043     17     29
street                    17.722   6.829  9.859    205     79
sun                        8.861  77.778 15.909     18    158
surfboard                  0.000   0.000  0.000     17     16
swimming-pool             26.923  48.276 34.568     29     52
table                      0.000   0.000  0.000    105      1
telephone                  3.279  16.667  5.479     12     61
tiger                      0.000   0.000  0.000      1      0
tire                       0.000   0.000  0.000     23      9
tower                      6.667   2.857  4.000     35     15
toy                        0.000   0.000  0.000     12      1
train                      4.040  15.385  6.400     26     99
trash                      0.000   0.000  0.000     10      2
tree                      14.246  19.392 16.425    263    358
trees                     23.377  19.890 21.493    543    462
trunk                     17.778  16.327 17.021     98     90
turtle                     0.000   0.000  0.000      4      7
umbrella                   0.000   0.000  0.000     32      4
vegetable                  0.000   0.000  0.000      8     28
vegetation                31.924  26.773 29.122    564    473
vehicle                    0.000   0.000  0.000      6      6
vehicles-with-tires        1.124  10.000  2.020     10     89
viola                      0.000   0.000  0.000      1      0
violin                     0.000   0.000  0.000      1      0
volcano                    0.000   0.000  0.000     10      7
wall                      46.154   3.543  6.581    508     39
water                      0.000   0.000  0.000    139      0
water-reflection          16.667  11.667 13.725     60     42
water-vehicles             0.000   0.000  0.000      3      4
waterfall                  8.311  53.448 14.385     58    373
waves                      1.948  15.000  3.448     20    154
whale                      0.000   0.000  0.000      4      7
window                    43.750   4.204  7.671    333     32
woman                     20.833   0.986  1.883    507     24
wood                       0.000   0.000  0.000     85     10
wooden-furniture           6.533  11.504  8.333    113    199
-------------------------------------------------------------
Accuracy                  19.004    -      -      -     19612

</pre>
## Running
To run the main app with default properties:

```
sbt "project saulExamples" "runMain edu.illinois.cs.cogcomp.saulexamples.mSpRL2017.mSpRL2017App"
```

## References
[1] Kordjamshidi, Parisa, Steven Bethard, and Marie-Francine Moens. "SemEval-2012 task 3: Spatial role labeling." Proceedings of the First Joint Conference on Lexical and Computational Semantics. Association for Computational Linguistics, 2012.

[2] Roberts, Kirk, and Sanda M. Harabagiu. "UTD-SpRL: A joint approach to spatial role labeling." Proceedings of the First Joint Conference on Lexical and Computational Semantics. Association for Computational Linguistics, 2012.

[3] http://www.cs.ubc.ca/~pcarbo/