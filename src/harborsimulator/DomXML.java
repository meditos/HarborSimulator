package harborsimulator;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/**
 *
 */
public class DomXML {
    
    private Document doc;
    private Element root;
    
    public DomXML(){
        try {
            //Creating an empty XML Document
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            this.doc = docBuilder.newDocument();
        }catch(ParserConfigurationException pce){
            pce.printStackTrace();
        }
    }
    
    public void createRoot(){
        Element rootElement = doc.createElement("simulation");
        doc.appendChild(rootElement);
        this.root = rootElement;
    }
    
    public Element appendElement(Element rootElement, String newElement){
        Element staff = doc.createElement(newElement);
        rootElement.appendChild(staff);
        return staff;
    }
    
    public void appendAttribute(Element staff, String id, String value){    
        Attr attr = doc.createAttribute(id);
        attr.setValue(value);
        staff.setAttributeNode(attr);
    }
    
    public void appendText(Element staff, String text){    
        staff.appendChild(doc.createTextNode(text));
    }
    
    //Write the content into xml file
    public void saveXMLFile(File file){
        try{
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result =  new StreamResult(file);
            transformer.transform(source, result);
        }catch(TransformerException tfe){
            tfe.printStackTrace();
        }
    }
    
    public Element getRoot(){
        return this.root;
    }
}