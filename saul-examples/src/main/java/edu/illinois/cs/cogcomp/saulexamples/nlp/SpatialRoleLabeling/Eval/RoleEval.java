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
@XmlRootElement(name = "Role")
public class RoleEval implements SpRLEval {

    public RoleEval() {
        setEnd(-1);
        setStart(-1);
    }

    public RoleEval(int start, int end) {
        this.setStart(start);
        this.setEnd(end);
    }

    @XmlAttribute(name = "start", required = true)
    private int start;
    @XmlAttribute(name = "end", required = true)
    private int end;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean contains(RoleEval b) {
        return b != null && start <= b.getStart() && end >= b.getEnd();
    }

    public boolean contains(int x) {
        return start <= x && x <= end;
    }

    public boolean overlaps(RoleEval b) {
        return b != null &&
                (contains(b.start) || contains(b.end) || b.contains(start) || b.contains(end));

    }

    @Override
    public boolean isEqual(SpRLEval b) {
        if(b == null)
            return false;
        if(!b.getClass().equals(getClass()))
            return false;
        RoleEval obj = (RoleEval) b;
        return contains(obj);
    }
}
