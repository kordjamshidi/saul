package edu.illinois.cs.cogcomp.saulexamples.nlp.Xml;

import edu.illinois.cs.cogcomp.saulexamples.nlp.BaseTypes.*;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
    private String documentTagName;
    private String startTagName = "start";
    private String endTagName = "end";
    private String textTagName = "text";
    private String idTagName = "id";

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

    public List<Document> getDocuments() throws Exception {
        return getElementList(documentTagName, null, NlpBaseElementTypes.Document);
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
        return getElementList(tagName, documentId, NlpBaseElementTypes.Sentence);
    }

    private <T extends NlpBaseElement> List<T> getElementList(String tagName, String parentId, NlpBaseElementTypes type) {
        NodeList nodes = parentId == null ?
                getNodeList(tagName) :
                getNodeList(parentId, tagName);
        List<T> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) nodes.item(i);
                NlpBaseElement s = getNlpBaseElement(e, type);
                list.add((T) s);
            }
        }
        return list;
    }

    private NlpBaseElement getNlpBaseElement(Element e, NlpBaseElementTypes type) {
        NlpBaseElement element = null;
        switch (type) {
            case Document:
                element = new Document();
                break;
            case Sentence:
                element = new Sentence();
                break;
            case Phrase:
                element = new Phrase();
                break;
            case Token:
                element = new Token();
                break;
        }
        element.setId(getStringAttribute(e, idTagName));
        element.setStart(getIntAttribute(e, startTagName));
        element.setEnd(getIntAttribute(e, endTagName));
        element.setText(getStringAttribute(e, textTagName));
        NamedNodeMap attributes = e.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            element.setProperty(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
        }

        return element;
    }

    private NodeList getNodeList(String parentId, String tagName) {
        String query = String.format("//*[@id='%s']//%s", parentId, tagName);
        try {
            return (NodeList) xpath.evaluate(query, xmlDocument, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private NodeList getNodeList(String tagName) {
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
