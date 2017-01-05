package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2017;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taher on 2016-10-17.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SpRL")
public class SpRL2017Document {
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
}
