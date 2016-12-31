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
    private String img_id;

    public Image(String label) {
        this.label = label;
        // for the time being name / id is same.
        this.img_id = label;
    }

    public String getImageID()
    {
        return img_id;
    }

    public void setImageID(String ID)
    {
        img_id = ID;
    }

}
