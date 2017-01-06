# Saul NLP Base Types
Built in hierarchy of common constituents for processing text and NLP tasks
and a relation class that connects those constituents.
Nlp base types help to design data models in Saul and provide many built in feature 
extractors and helpers.

## Hierarchy
All classes of the hierarchy assumed to have a unique **id** throughout the entire corpora,
a **text**, and also character based span which shows the **start** index and exclusive 
**end** index of each constituent.

On the top level of the hierarchy we have the [`Document`](Document.java) class.
Each document contains many [`Sentences`](Segment.java) which in turn can 
have many [`Phrases`](Phrases) and finally each phrase have many [`Tokens`](Token.java). 
Note that, you can omit one or more of these constituents for specific usages.

### Properties
We can specify additional properties for all hierarchy classes using
`setPropertyValue`. This function adds a value to the list of values for that property. 
The value list can be retrieved by `getPropertyValues` function. 
And there is the `getPropertyFirstValue` which returns the first 
value of the list for that property.

## Relation
Data modeling in Saul usually requires having edges between the model's nodes. 
[Relations](Relation.java) help to have a container that holds the information 
needed to construct those edges.

Each relation should have a unique **Id** and two or more **argumentId** which determine
the Id of the constituents that used in this relation. Additional properties can be added
using `setProperty` function.
