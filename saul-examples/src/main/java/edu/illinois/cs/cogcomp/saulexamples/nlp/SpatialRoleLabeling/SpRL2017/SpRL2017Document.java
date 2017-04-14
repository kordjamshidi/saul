/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2017;

import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLXmlDocument;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taher on 2016-10-17.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SpRL")
public class SpRL2017Document implements SpRLXmlDocument {
    @XmlElement(name = "SCENE", required = true)
    private List<Scene> Scenes;

    public SpRL2017Document() {
        Scenes = new ArrayList<>();
    }

    public List<Scene> getScenes() {
        return Scenes;
    }

    public void setScenes(List<Scene> scenes) {
        Scenes = scenes;
    }

    @XmlTransient
    protected String filename;
    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
