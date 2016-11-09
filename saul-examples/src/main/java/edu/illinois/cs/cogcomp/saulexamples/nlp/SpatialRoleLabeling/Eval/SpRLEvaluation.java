/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

/**
 * Created by Taher on 2016-09-20.
 */
public class SpRLEvaluation {
    private final String label;
    private final double precision;
    private final double recall;
    private final double f1;
    private final int labeledCount;
    private final int predictedCount;
    public SpRLEvaluation(String label, double precision, double recall, double f1, int labeledCount, int predictedCount) {
        this.label = label;
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
        this.labeledCount = labeledCount;
        this.predictedCount = predictedCount;
    }

    public String getLabel() {
        return label;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public int getLabeledCount() {
        return labeledCount;
    }

    public int getPredictedCount() {
        return predictedCount;
    }

    public double getF1() {
        return f1;
    }
}
