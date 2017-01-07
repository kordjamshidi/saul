package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Umar Manzoor on 29/12/2016.
 */
public class Segment {
    private int id;
    private int code;
    private String features;
    private String concept;
    private String imageId;

    public Segment(String imageId, int id, int code, String features, String concept) {
        this.imageId = imageId;
        this.id = id;
        this.code = code;
        this.features = features;
        this.concept = concept;
    }

    public String getImageId() {
        return imageId;
    }

    public int getId() {
        return id;
    }

    public String getFeatures() {
        return features;
    }

    public int getCode() {
        return code;
    }

    public String getConcept() {
        return concept;
    }

    @Override
    public String toString() {
        return imageId + ", " + id + ", " + code + ", " + features + ", " + concept;
    }

}
