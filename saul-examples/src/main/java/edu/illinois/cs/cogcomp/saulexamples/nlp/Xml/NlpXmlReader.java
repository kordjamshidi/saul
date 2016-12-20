package edu.illinois.cs.cogcomp.saulexamples.nlp.Xml;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Document;
import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.Sentence;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.*;

/**
 * Created by Taher on 2016-12-18.
 */
public class NlpXmlReader {
    private final XPath xpath;
    org.w3c.dom.Document xmlDocument;
    Map<String, Element> documentsMap = new HashMap<>();
    List<Document> documents = new ArrayList<>();
    boolean documentsLoaded = false;
    private String documentTagName;

    public NlpXmlReader(String path) throws Exception {
        this(new File(path));
    }

    public NlpXmlReader(String path, String documentTagName) throws Exception {
        this(new File(path), documentTagName);
    }

    public NlpXmlReader(File file) throws Exception {
        this(file, "document");
    }

    public NlpXmlReader(File file, String documentTagName) throws Exception {
        this.documentTagName = documentTagName;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        xmlDocument = dBuilder.parse(file);
        XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
    }

    public void loadDocuments() throws Exception {
        if (documentsLoaded)
            return;
        NodeList nodes = getNodeList(documentTagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) nodes.item(i);
                Document d = new Document(
                        getStringAttribute(e, "id"),
                        getIntAttribute(e, "start"),
                        getIntAttribute(e, "end"),
                        getStringAttribute(e, "text"));
                documentsMap.put(d.getId(), e);
                documents.add(d);
            }
        }
        documentsLoaded = true;
    }


    public List<Document> getDocuments() throws Exception {
        checkLoaded();
        return documents;
    }

    public List<Sentence> getAllSentences() throws Exception {
        return getSentences("sentence", null);
    }

    public List<Sentence> getAllSentences(String tagName) throws Exception {
        return getSentences(tagName, null);
    }

    public List<Sentence> getSentences(String documentId) throws Exception {
        return getSentences("sentence", documentId);
    }

    public List<Sentence> getSentences(String tagName, String documentId) throws Exception {
        checkLoaded();
        NodeList nodes = documentId == null ?
                getNodeList(tagName) :
                getNodeList(documentId, tagName);
        List<Sentence> sentences = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) nodes.item(i);
                Sentence s = new Sentence(
                        getStringAttribute(e, "id"),
                        getIntAttribute(e, "start"),
                        getIntAttribute(e, "end"),
                        getStringAttribute(e, "text"));
                documentsMap.put(s.getId(), e);
                sentences.add(s);
            }
        }
        return sentences;
    }

    private void checkLoaded() throws Exception {
        if (!documentsLoaded)
            throw new Exception("Please load documents first.");
    }

    private NodeList getNodeList(String parentId, String tagName) throws XPathExpressionException {
        String query = String.format("//*[@id='%s']//%s", parentId, tagName);
        return (NodeList) xpath.evaluate(query, xmlDocument, XPathConstants.NODESET);
    }
    private NodeList getNodeList(String tagName) throws XPathExpressionException {
        return xmlDocument.getElementsByTagName(tagName);
    }

    private String getStringAttribute(Element e, String name) {
        if (e.hasAttribute(name))
            return e.getAttribute(name);
        NodeList innerElement = e.getElementsByTagName(name);
        if (innerElement.getLength() > 0)
            return innerElement.item(0).getTextContent();
        return null;
    }

    private Integer getIntAttribute(Element e, String name) {
        if (e.hasAttribute(name))
            return Integer.parseInt(e.getAttribute(name));
        return -1;
    }

}
