/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 * <p>
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

import org.apache.commons.lang.StringUtils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Taher on 2016-09-20.
 */
public class SpRLEvaluator {

    public static void printEvaluation(List<SpRLEvaluation> eval) {
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
        return evaluateRoles(actual, predicted, new ExactComparer());
    }

    public List<SpRLEvaluation> evaluateRoles(RolesEvalDocument actual, RolesEvalDocument predicted, EvalComparer comparer) {

        List<SpRLEvaluation> evaluations = new ArrayList<>();

        evaluations.add(evaluate("SP", actual.getSpatialIndicators(), predicted.getSpatialIndicators(), comparer));
        evaluations.add(evaluate("TR", actual.getTrajectors(), predicted.getTrajectors(), comparer));
        evaluations.add(evaluate("LM", actual.getLandmarks(), predicted.getLandmarks(), comparer));

        return evaluations;
    }

    public List<SpRLEvaluation> evaluateRelations(RelationsEvalDocument actual, RelationsEvalDocument predicted) {
        return evaluateRelations(actual, predicted, new ExactComparer());
    }

    public List<SpRLEvaluation> evaluateRelations(RelationsEvalDocument actual, RelationsEvalDocument predicted,
                                                  EvalComparer comparer) {

        List<SpRLEvaluation> evaluations = new ArrayList<>();
        evaluations.add(evaluate("Relation", actual.getRelations(), predicted.getRelations(), comparer));

        return evaluations;
    }

    private <T extends SpRLEval> SpRLEvaluation evaluate(String label, List<T> actualList, List<T> predictedList,
                                                         EvalComparer comparer) {
        int tp = 0;
        List<T> actual = distinct(actualList);
        List<T> predicted = distinct(predictedList);
        int predictedCount = predicted.size();
        int actualCount = actual.size();

        while (actual.size() > 0) {
            T a = actual.get(0);
            for (T p : predicted) {
                if (comparer.isEqual(a, p)) {
                    tp++;
                    predicted.remove(p);
                    break;
                }
            }
            actual.remove(a);
        }

        int fp = predictedCount - tp;
        int fn = actualCount - tp;
        double precision = tp == 0 ? 0 : (double) tp / (tp + fp) * 100;
        double recall = tp == 0 ? 0 : (double) tp / (tp + fn) * 100;
        double f1 = precision == 0 || recall == 0 ? 0 : 2 * precision * recall / (precision + recall);

        return new SpRLEvaluation(
                label,
                precision,
                recall,
                f1,
                actualCount,
                predictedCount
        );
    }

    private <T extends SpRLEval> List<T> distinct(List<T> l) {
        HashSet<T> set = new HashSet<T>();
        List<T> newList = new ArrayList<T>();
        set.add(l.get(0));
        for (T i : l) {
            if (!set.contains(i))
                set.add(i);
        }
        newList.addAll(set);
        return newList;
    }

}
