package featureIDEToHlvlParser;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.coffee.modelParsers.utils.FileUtils;

public class XmlReader {

	private ArrayList<Node> xmlTree;


	public XmlReader() {
		xmlTree= new ArrayList<Node>();
	}
	public void loadXmlFiel(String path) {

		List<File> xmlFiel = FileUtils.readFileFromDirectory(path);
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			for (int i = 0; i < xmlFiel.size(); i++) {
				xmlTree.add(builder.parse(xmlFiel.get(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Node> getXmlTree() {
		return xmlTree;
	}

	public void setXmlTree(ArrayList<Node> xmlTree) {
		this.xmlTree = xmlTree;
	}

	public void loadXmlString(String xml) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xmlTree.add(builder.parse(new InputSource(new StringReader(xml))));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

	}
}
