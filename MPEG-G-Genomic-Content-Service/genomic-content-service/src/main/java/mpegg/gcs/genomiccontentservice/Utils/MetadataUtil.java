package mpegg.gcs.genomiccontentservice.Utils;

import mpegg.gcs.genomiccontentservice.Models.Dataset;
import mpegg.gcs.genomiccontentservice.Models.DatasetGroup;
import mpegg.gcs.genomiccontentservice.Models.Sample;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MetadataUtil {
    public DatasetGroup parseDatasetGroup(String xml, DatasetGroup dg) throws ParserConfigurationException, IOException, SAXException {
        dg.clearMetadata();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        Document d = builder.parse(is);
        Element root = d.getDocumentElement();
        NodeList childList = d.getChildNodes();
        if (root.getElementsByTagName("Title").getLength() != 0) {
            Node n = root.getElementsByTagName("Title").item(0);
            if (n.getParentNode().getNodeName().equals("DatasetGroupMetadata")) dg.setTitle(n.getTextContent());
        }
        if (root.getElementsByTagName("Description").getLength() != 0) {
            Node n = root.getElementsByTagName("Description").item(0);
            if (n.getParentNode().getNodeName().equals("DatasetGroupMetadata")) dg.setDescription(n.getTextContent());
        }
        if (root.getElementsByTagName("Type").getLength() != 0) {
            Node n = root.getElementsByTagName("Type").item(0);
            if (n.getParentNode().getNodeName().equals("DatasetGroupMetadata")) {
                n = n.getAttributes().getNamedItem("existing_study_type");
                if (n != null) dg.setType(n.getNodeValue());
            }
        }
        if (root.getElementsByTagName("ProjectCentreName").getLength() != 0) {
            Node n = root.getElementsByTagName("ProjectCentreName").item(0);
            if (n.getParentNode().getNodeName().equals("ProjectCentre")) dg.setCenter(n.getTextContent());
        }
        return dg;
    }

    public Dataset parseDataset(String xml, Dataset dt) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        Document d = builder.parse(is);
        Element root = d.getDocumentElement();
        NodeList list = root.getElementsByTagName("Sample");
        ArrayList<Sample> sampleList = new ArrayList<Sample>();
        for (int i = 0; i < list.getLength(); ++i) {
            Element sample = (Element)list.item(0);
            Sample s = new Sample(dt);
            if (sample.getElementsByTagName("Title").getLength() != 0) s.setTitle(sample.getElementsByTagName("Title").item(0).getTextContent());
            if (sample.getElementsByTagName("TaxonId").getLength() != 0) s.setTaxon_id(sample.getElementsByTagName("TaxonId").item(0).getTextContent());
            sampleList.add(s);
        }
        dt.setSamples(sampleList);
        return dt;
    }
}