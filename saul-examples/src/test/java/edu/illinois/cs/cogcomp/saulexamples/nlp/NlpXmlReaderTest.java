package edu.illinois.cs.cogcomp.saulexamples.nlp;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Document;
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Phrase;
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Sentence;
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Taher on 2016-12-18.
 */
public class NlpXmlReaderTest {
    NlpXmlReader reader;
    private List<Document> documents;

    @Before
    public void setup() {
        reader = new NlpXmlReader(getResourcePath("SpRL/2017/test.xml"));
        documents = reader.getDocuments("SCENE");
    }

    @Test
    public void document() {
        assertEquals("Document count", 2, documents.size());
        assertEquals("Document 1 Id", "sc1", documents.get(0).getId());
        assertEquals("Document 2 Id", "sc2", documents.get(1).getId());
        assertEquals("Document 1 test attribute", "test", documents.get(0).getProperty("test"));
    }

    @Test
    public void sentence() {
        String docId = documents.get(0).getId();
        List<Sentence> sentences = reader.getSentences("SENTENCE", docId);
        assertEquals("Document 1 sentence count", 2, sentences.size());
    }

    @Test
    public void phrase() {
        String docId = documents.get(0).getId();
        List<Phrase> phrases = reader.getPhrases("TRAJECTOR", docId);
        assertEquals("Document 1 Trajector phrase count", 3, phrases.size());
    }
    private String getResourcePath(String relativePath) {
        return getClass().getClassLoader().getResource(relativePath).getPath();
    }
}
