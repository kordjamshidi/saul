/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.Eval;

import javax.xml.bind.annotation.*;

/**
 * Created by Taher on 2016-09-19.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Relation")
public class RelationEval implements SpRLEval {

    private final RoleEval tr;
    private final RoleEval sp;
    private final RoleEval lm;

    public RelationEval() {
        this(-1, -1, -1, -1, -1, -1);
    }

    public RelationEval(int trajectorStart, int trajectorEnd, int spatialIndicatorStart, int spatialIndicatorEnd, int landmarkStart, int landmarkEnd) {
        this.trajectorStart = trajectorStart;
        this.trajectorEnd = trajectorEnd;
        this.landmarkStart = landmarkStart;
        this.landmarkEnd = landmarkEnd;
        this.spatialIndicatorStart = spatialIndicatorStart;
        this.spatialIndicatorEnd = spatialIndicatorEnd;
        tr = new RoleEval(trajectorStart, trajectorEnd);
        lm = new RoleEval(landmarkStart, landmarkEnd);
        sp = new RoleEval(spatialIndicatorStart, spatialIndicatorEnd);
    }

    @XmlAttribute(name = "trajectorStart", required = true)
    private int trajectorStart;
    @XmlAttribute(name = "trajectorEnd", required = true)
    private int trajectorEnd;

    @XmlAttribute(name = "landmarkStart", required = true)
    private int landmarkStart;
    @XmlAttribute(name = "landmarkEnd", required = true)
    private int landmarkEnd;

    @XmlAttribute(name = "spatialIndicatorStart", required = true)
    private int spatialIndicatorStart;
    @XmlAttribute(name = "spatialIndicatorEnd", required = true)
    private int spatialIndicatorEnd;

    public int getTrajectorStart() {
        return trajectorStart;
    }

    public void setTrajectorStart(int trajectorStart) {
        this.trajectorStart = trajectorStart;
    }

    public int getTrajectorEnd() {
        return trajectorEnd;
    }

    public void setTrajectorEnd(int trajectorEnd) {
        this.trajectorEnd = trajectorEnd;
    }

    public int getLandmarkStart() {
        return landmarkStart;
    }

    public void setLandmarkStart(int landmarkStart) {
        this.landmarkStart = landmarkStart;
    }

    public int getLandmarkEnd() {
        return landmarkEnd;
    }

    public void setLandmarkEnd(int landmarkEnd) {
        this.landmarkEnd = landmarkEnd;
    }

    public int getSpatialIndicatorStart() {
        return spatialIndicatorStart;
    }

    public void setSpatialIndicatorStart(int spatialIndicatorStart) {
        this.spatialIndicatorStart = spatialIndicatorStart;
    }

    public int getSpatialIndicatorEnd() {
        return spatialIndicatorEnd;
    }

    public void setSpatialIndicatorEnd(int spatialIndicatorEnd) {
        this.spatialIndicatorEnd = spatialIndicatorEnd;
    }

    public boolean contains(RelationEval p) {
        return sp.contains(p.sp) && tr.contains(p.tr) && lm.contains(p.lm);
    }

    @Override
    public boolean isEqual(SpRLEval b) {
        if (b == null)
            return false;
        if (!b.getClass().equals(getClass()))
            return false;
        RelationEval obj = (RelationEval) b;
        return contains(obj);
    }
}
