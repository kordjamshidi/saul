/** This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */

package edu.illinois.cs.cogcomp.saulexamples.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Umar Manzoor on 26/12/2016.
 */
public class Image {
    private final String label;
    private List<Integer> ImageObjectCodes;
    private List<String> ImageObjectConcepts;

    public Image(String label) {
        this.label = label;
        ImageObjectCodes = new ArrayList<>();
        ImageObjectConcepts = new ArrayList<>();
    }

    public String getLabel()
    {
        return label;
    }

    // Retrieving associated objects of the image.
    public List<Integer> getObjectCodes()
    {
        return Collections.unmodifiableList(ImageObjectCodes);
    }

    // Storing associated objects of the image.
    public void setObjects(List<Integer> ObjectCodes, List<String> ObjectConcepts)
    {
        this.ImageObjectCodes = ObjectCodes;
        this.ImageObjectConcepts = ObjectConcepts;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return label + ", " + ImageObjectCodes + ", " + ImageObjectConcepts;
    }

}
