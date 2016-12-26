package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-18.
 */
public class Sentence extends NlpBaseElement {

    private String documentId;

    public Sentence() {
    }

    public Sentence(String documentId, String id, Integer start, Integer end, String text) {
        super(id, start, end, text);
        this.setDocumentId(documentId);
    }

    @Override
    public NlpBaseElementTypes getType() {
        return NlpBaseElementTypes.Sentence;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
