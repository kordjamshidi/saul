package edu.illinois.cs.cogcomp.saulexamples.nlp;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Sentence;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2013.SpRL2013Document;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRL2015.SpRL2015Document;
import edu.illinois.cs.cogcomp.saulexamples.nlp.SpatialRoleLabeling.SpRLDataReader;
import edu.illinois.cs.cogcomp.saulexamples.nlp.Xml.NlpXmlReader;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Taher on 2016-12-18.
 */
public class NlpXmlReaderTest {
    NlpXmlReader reader;

    @Before
    public void setup() throws Exception {
        reader = new NlpXmlReader(getResourcePath("SpRL/2017/test.xml"), "SCENE");
    }

    @Test
    public void document() throws Exception {
        assertEquals("Document count", 2, reader.getDocuments().size());
        assertEquals("Document 1 Id", "sc1", reader.getDocuments().get(0).getId());
        assertEquals("Document 2 Id", "sc2", reader.getDocuments().get(1).getId());
        assertEquals("Document 1 test attribute", "test", reader.getDocuments().get(0).getProperty("test"));
    }

    @Test
    public void sentence() throws Exception {
        String docId = reader.getDocuments().get(0).getId();
        List<Sentence> sentences = reader.getSentences("SENTENCE", docId);
        assertEquals("Document 1 sentence count", 2, sentences.size());
    }

    private String getResourcePath(String relativePath) {
        return getClass().getClassLoader().getResource(relativePath).getPath();
    }
}
