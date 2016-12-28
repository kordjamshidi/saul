package edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes;

/**
 * Created by Taher on 2016-12-18.
 */
public class Sentence extends NlpBasedElement {

    private Document document;

    public Sentence() {
    }

    public Sentence(Document document, String id, Integer start, Integer end, String text) {
        super(id, start, end, text);
        this.setDocument(document);
    }

    @Override
    public NlpBaseElementTypes getType() {
        return NlpBaseElementTypes.Sentence;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
