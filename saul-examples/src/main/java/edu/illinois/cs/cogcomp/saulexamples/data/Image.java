/** This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */

package edu.illinois.cs.cogcomp.saulexamples.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Umar Manzoor on 26/12/2016.
 */
public class Image {
    private final String label;
    public List<Segment> associatedObjects;

    public Image(String label) {
        this.label = label;
        associatedObjects = new ArrayList<>();
    }

    public String getLabel()
    {
        return label;
    }

    // Retrieving associated objects of the image.
    public List<Segment> getObjectCodes()
    {
        return Collections.unmodifiableList(associatedObjects);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return label + ", " + associatedObjects.toString();
    }
}
