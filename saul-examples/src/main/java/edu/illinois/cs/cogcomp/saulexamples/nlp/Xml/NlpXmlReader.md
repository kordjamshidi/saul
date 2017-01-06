# NLP Xml Reader
An [Xml reader](NlpXmlReader.java) that facilitates reading data from xml files into Saul's NLP 
[BaseTypes](../BaseTypes/BaseTyps.md).

##Tag Names
The reader uses `"DOCUMENT"`, `"SENTENCE"`, `"PHRASE"`, and `"TOKEN"` as default tag names 
for the corresponding base types. You can change them either by the constructor or by the
setters provided in the reader's class. Note that you can read a type from multiple tags by
changing the tag name just before reading the data.

The properties or tags that determine `start`, `end`, `id` and `text` of each constituent
are predefined in the reader and can be changed using the corresponding setters as well.

##Read a list of constituents
Using `getDocuments`, `getSentences`, `getPhrases` and `getTokens` we can retrieve any of 
the base types in the xml file. For each type except the document there is another function
`getXXXByParentId` which retrieves list of a constituent which contained in another (Parent)constituent.

`getRelations` and `getRelationsByParentId` work similar to aforementioned functions, but need
to determine the property name of argument ids of the relation as well.

## Adding properties to the constituents from other tags
In many scenarios we need to add properties from various tags. We can do that by providing
the tag names when using `getXXXs` or `getXXXByParentId` when we have the constituents in 
the xml file. But when we have generated the constituents from another source and want to 
add properties from a tag, we can use `addPropertiesFromTag`. This function uses the parent Id
of the constituents(`documentId` for sentence, and `sentenceId` for `Token` and `Phrase`) to find
the context for each constituent to be retrieved.

### Matching
adding properties from a tag requires matching between constituents and tags. The default
matching strategy is [`ExactMatching`](XmlExachMatching.java). 

Built in matching strategies:
- [`ExactMatching`](XmlExachMatching.java): adds tag's properties if the tag's span
 exactly matches with the constituent's span 
- [`InclusionMatching`](XmlInclusionMatching.java): adds tag's properties if the tag's span
 includes the constituent's span
- [`PartOfMatching`](XmlPartOfMatching.java): adds tag's properties if the tag's span is
a part of constituent span, in other word if the constituent's span includes the tag's span
- [`OverlapMatching`](XmlOverlapMatching.java): adds tag's properties if the tag's span
and the constituent's span are overlapping
- [`HeadwordMatching`](../../../../../../../../scala/edu/illinois/cs/cogcomp/saulexamples/nlp/SpatialRoleLabeling/XmlMatchings.scala):
 adds tag's properties if the tag's span contains the headwords span of the constituent

You can create your own matching strategy by implementing [`IXmlSpanMatching`](IXmlSpanMatching.java) interface.

## An example in scala
Suppose we have this xml file: 
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<SpRL>
    <SCENE id="sc2" test="test">
        <DOCNO>annotations/01/1069.eng</DOCNO>
        <IMAGE>images/01/1069.jpg</IMAGE>
        <SENTENCE id="s603" start="130" end="344">
            <TEXT>Interior view of a room with a large bed with red bedcovers , a white 
            wooden desk and chair below a TV fixed in the corner , a white fridge and a 
            glass door with a wooden frame leading onto a veranda and garden .
            </TEXT>
            <SPATIALINDICATOR id="S3" start="109" end="111" text="in"/>
            <SPATIALINDICATOR id="S4" start="92" end="97" text="below"/>
            <SPATIALINDICATOR id="S5" start="177" end="189" text="leading onto"/>
            <LANDMARK id="L3" start="112" end="122" text="the corner"/>
            <LANDMARK id="L4" start="98" end="102" text="a TV"/>
            <LANDMARK id="L5" start="192" end="199" text="veranda"/>
            <LANDMARK id="L6" start="204" end="210" text="garden"/>
            <TRAJECTOR id="T4" start="98" end="102" text="a TV"/>
            <TRAJECTOR id="T5" start="70" end="81" text="wooden desk"/>
            <TRAJECTOR id="T6" start="86" end="91" text="chair"/>
            <TRAJECTOR id="T7" start="144" end="156" text="a glass door"/>
            <TESTPROP id="TP3" first_value="1" second_value="T1" start="62" end="72"/>
            <MATCH id="inc1" start="70" end="82"/>
            <MATCH id="inc2" start="143" end="157"/>
            <MATCH id="p1" start="100" end="101"/>
            <MATCH id="p2" start="98" end="100"/>
            <MATCH id="p3" start="100" end="102"/>
            <MATCH id="o1" start="93" end="99"/>
            <MATCH id="e1" start="144" end="156"/>
            <RELATION id="SR4" trajector_id="T4" landmark_id="L3" spatial_indicator_id="S3" general_type="region"
                      specific_type="RCC8" RCC8_value="EC" FoR="intrinsic"/>
            <RELATION id="SR5" trajector_id="T5" landmark_id="L4" spatial_indicator_id="S4" general_type="direction"
                      specific_type="relative" RCC8_value="below" FoR="intrinsic"/>
            <RELATION id="SR6" trajector_id="T6" landmark_id="L4" spatial_indicator_id="S4" general_type="direction"
                      specific_type="relative" RCC8_value="below" FoR="intrinsic"/>
            <RELATION id="SR7" trajector_id="T7" landmark_id="L5" spatial_indicator_id="S5" general_type="region"
                      specific_type="relative" RCC8_value="DC" FoR="intrinsic"/>
            <RELATION id="SR8" trajector_id="T7" landmark_id="L6" spatial_indicator_id="S5" general_type="region"
                      specific_type="relative" RCC8_value="DC" FoR="intrinsic"/>
        </SENTENCE>
    </SCENE>
</SpRL>
```
we can read the hierarchy and the relation list by:

```scala
  val reader = new NlpXmlReader("path_to_the_xml_file.xml", "SCENE", "SENTENCE", null, null)
  val documentList = reader.getDocuments()
  val sentencesList = reader.getSentences()

  reader.setPhraseTagName("TRAJECTOR")// set the phrase tag name before reading as phrase list
  val trajectorList = reader.getPhrases() // reading trajectors as a list of phrases
  reader.setPhraseTagName("LANDMARK")
  val landmarkList = reader.getPhrases()
  reader.setPhraseTagName("SPATIALINDICATOR")
  val spIndicatorList = reader.getPhrases()

  //the relation contains three arguments which their ids are determined by the specified strings
  val relationList = reader.getRelations("RELATION", "trajector_id", "spatial_indicator_id", "landmark_id")

  val phraseList = getPhrasesFromSomewhere()// generating the phrases from other sources
  reader.addPropertiesFromTag("TRAJECTOR", phraseList, XmlMatchings.headwordMatching)
  reader.addPropertiesFromTag("LANDMARK", phraseList, new XmlPartOfMatching)
  reader.addPropertiesFromTag("SPATIALINDICATOR", phraseList, new XmlPartOfMatching)
```