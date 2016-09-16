The `SL_model` package aims at providing the possibility of designing structured output prediction models, based on generalized linear models such as structured SVMs and structured Perceptrons.
For implementating any structured learning problem in SL, we need to implement the following classes, [see here.](http://cogcomp.cs.illinois.edu/software/illinois-sl/)
<ul>
<li>The input structure, x. This should implement the IInstance interface.</li>

<li>The output structure, y. This should implement the IStructure interface.</li>

<li>A procedure to compute the feature vector Φ(x,y). For this you need to extned the AbstractFeatureGenerator class and override its getFeatureVector method.</li>

<li>A procedure InferenceSolver to perform the loss-augmented inference,

argmaxy′wTΦ(x,y′)+Δ(y,y′)

For this you need to extend the AbstractInferenceSolver class.</li>

<li>At test time, we need to solve

argmaxy′wTΦ(x,y′)
We will call this the MAP inference problem. For this we can just set Δ(y,y′) to zero in the loss-augmented inference solver.</li>

</ul>

Now here in Saul-SL, what we do is that the way we define our model is based on Saul's conceptual abstraction rather than the SL abstraction. In other words Saul user writes the program in terms of Classifiers and Constraints and the above mentioned modules are provided by Saul.
-The input structure is the collection of the input components of each individual classifier.
-The output structure is the collection of the labels
-The feature vector is the concatination of input-feature * output-label for each individual  classifier and a global join feature function is built automatically based on that.
-The inference is done using the constraints that express the correlations between the Classifiers.

This view implements the idea of collective classification in the framework of structured output prediction models and provides the possibility of using global first order constraints and domain knowledge easily in the structured learning model.

 The underlying inference is performed using ILP techniques.






