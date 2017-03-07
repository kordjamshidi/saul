/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 * <p>
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import org.apache.commons.lang.StringUtils;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taher on 2016-09-20.
 */
public class SpRLEvaluator {

    public void printEvaluation(List<SpRLEvaluation> eval) {
        printEvaluation(System.out, eval);
    }

    public static void printEvaluation(OutputStream outputStream, List<SpRLEvaluation> eval) {
        PrintStream out = new PrintStream(outputStream, true);
        out.printf("%-20s %-10s %-10s %-10s %-10s %-10s\n",
                "label",
                "Precision",
                "Recall",
                "F1",
                "LCount",
                "PCount"
        );
        out.println(StringUtils.repeat("-", 75));
        for (SpRLEvaluation e : eval) {
            out.printf("%-20s %-10.3f %-10.3f %-10.3f %-10d %-10d\n",
                    e.getLabel(),
                    e.getPrecision(),
                    e.getRecall(),
                    e.getF1(),
                    e.getLabeledCount(),
                    e.getPredictedCount()
            );
        }
        out.println(StringUtils.repeat("-", 75));
    }

    public List<SpRLEvaluation> evaluateRoles(RolesEvalDocument actual, RolesEvalDocument predicted) {
        return evaluateRoles(actual, predicted, new DefaultComparer());
    }

    public List<SpRLEvaluation> evaluateRoles(RolesEvalDocument actual, RolesEvalDocument predicted, EvalComparer comparer) {

        List<SpRLEvaluation> evaluations = new ArrayList<>();

        evaluations.add(evaluate("SP", actual.getSpatialIndicators(), predicted.getSpatialIndicators(), comparer));
        evaluations.add(evaluate("TR", actual.getTrajectors(), predicted.getTrajectors(), comparer));
        evaluations.add(evaluate("LM", actual.getLandmarks(), predicted.getLandmarks(), comparer));

        return evaluations;
    }

    public List<SpRLEvaluation> evaluateRelations(RelationsEvalDocument actual, RelationsEvalDocument predicted) {
        return evaluateRelations(actual, predicted, new DefaultComparer());
    }

    public List<SpRLEvaluation> evaluateRelations(RelationsEvalDocument actual, RelationsEvalDocument predicted,
                                                  EvalComparer comparer) {

        List<SpRLEvaluation> evaluations = new ArrayList<>();
        evaluations.add(evaluate("Relation", actual.getRelations(), predicted.getRelations(), comparer));

        return evaluations;
    }

    private <T extends SpRLEval> SpRLEvaluation evaluate(String label, List<T> actual, List<T> predicted,
                                                         EvalComparer comparer) {
        int tp = 0;
        String positive = "+", negative = "-";
        TestDiscrete tester = new TestDiscrete();

        for (T a : actual) {
            for (T p : predicted) {
                if (comparer.isEqual(a, p)) {
                    tester.reportPrediction(positive, positive);
                    tp++;
                    break;// count each actual occurrence no more than once
                }
            }
        }

        int fp = predicted.size() - tp;
        for (int i = 0; i < fp; i++)
            tester.reportPrediction(positive, negative);

        int fn = actual.size() - tp;
        for (int i = 0; i < fn; i++)
            tester.reportPrediction(negative, positive);

        return new SpRLEvaluation(
                label,
                tester.getPrecision(positive) * 100,
                tester.getRecall(positive) * 100,
                tester.getF1(positive) * 100,
                tester.getLabeled(positive),
                tester.getPredicted(positive)
        );
    }

}
