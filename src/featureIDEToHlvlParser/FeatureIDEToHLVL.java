package featureIDEToHlvlParser;


import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.coffee.modelParsers.basicHLVLPackage.DecompositionType;
import com.coffee.modelParsers.basicHLVLPackage.GroupType;
import com.coffee.modelParsers.basicHLVLPackage.HlvlBasicFactory;
import com.coffee.modelParsers.basicHLVLPackage.IHlvlParser;
import com.coffee.modelParsers.basicHLVLPackage.IhlvlBasicFactory;
import com.coffee.modelParsers.utils.FileUtils;
import com.coffee.modelParsers.utils.ParsingParameters;

public class FeatureIDEToHLVL implements IHlvlParser {

	private StringBuilder HlvlCode;
	private ParsingParameters params;
	private IhlvlBasicFactory converter;
	private XmlReader xmlReader;
	private ArrayList<Node> xmlTree;
	
	public FeatureIDEToHLVL(ParsingParameters params) {
		this.params = params;
	}

	public FeatureIDEToHLVL() {
		
	}

	public void Initializate() {
		xmlReader = new XmlReader();
		xmlReader.loadXmlFiel(params.getInputPath());
		xmlTree = xmlReader.getXmlTree();
	}

	public void Initializate(String xml) {
		converter = new HlvlBasicFactory();
		xmlReader = new XmlReader();
		converter = new HlvlBasicFactory();
		HlvlCode = new StringBuilder();
		xmlReader.loadXmlString(xml);
		xmlTree = xmlReader.getXmlTree();

	}

	public void writeFile() {
		FileUtils.writeHLVLProgram(params.getOutputPath(), HlvlCode.toString());
		System.out.println("Conversion complete");
	}

	public void readTree(Node n) {
		
		if (((n.getNodeName().equals("feature")) || (n.getNodeName().equals("or")))
				|| (n.getNodeName().equals("and"))|| (n.getNodeName().equals("alt"))) {
			addElement(n);
			addRelations(n);
		}else if (n.getNodeName().equals("rule")) {
			addConstrains(n);
		}
		NodeList childrens = n.getChildNodes();
		for (int i = 0; i < childrens.getLength(); i++) {
			Node grandchildren = childrens.item(i);
			readTree(grandchildren);
		}
	}
	
	public void addConstrains(Node n) {
		NodeList childsAux= n.getChildNodes();
		for (int i = 0; i < childsAux.getLength(); i++) {	
			if (childsAux.item(i).getNodeName().equals("imp")) {
				String name1="Null";
				String name2="Null";
				NodeList granChildrens =childsAux.item(i).getChildNodes();
				for (int j = 0; j < granChildrens.getLength(); j++) {		
					if (granChildrens.item(j).getNodeName().equals("var")&&name1.equals("Null")) {
						name1=granChildrens.item(j).getFirstChild().getNodeValue();
					}else if(granChildrens.item(j).getNodeName().equals("var")&&!name1.equals("Null")) {
						name2=granChildrens.item(j).getFirstChild().getNodeValue();
						HlvlCode.append("	"+converter.getImplies(name1,name2));
					}else if (granChildrens.item(j).getNodeName().equals("not")) {
						name2=granChildrens.item(j).getChildNodes().item(1).getFirstChild().getNodeValue();
						HlvlCode.append("	"+converter.getMutex(name1,name2));
					}
				}
				
			}
		}
	}

	public ArrayList<String> agruparNombresHijos(Node n) {
		ArrayList<String> nombres = new ArrayList<String>();
		NodeList childrens = n.getChildNodes();
		for (int i = 0; i < childrens.getLength(); i++) {
			Node grandchildren = childrens.item(i);
			if (!findNameInNode(grandchildren).equals("")) {
				nombres.add(findNameInNode(grandchildren));

			}
		}
		return nombres;
	}

	public void addCore() {
		if (HlvlCode.indexOf(converter.getRelationsLab()) < 0) {
			HlvlCode.append(converter.getRelationsLab());
		}
	}
	public void addGroup(Node n) {
		if (n.getNodeName().equals("or")) {
			ArrayList<String> nombres = agruparNombresHijos(n);
			HlvlCode.append("	" + converter.getGroup(findNameInNode(n), nombres, GroupType.Or));
		} else if (n.getNodeName().equals("alt")) {
			ArrayList<String> nombres = agruparNombresHijos(n);
			HlvlCode.append("	" + converter.getGroup(findNameInNode(n), nombres, GroupType.And));
		}	
	}
	public void addDescomposition(Node n) {
		for (int i = 0; i < n.getAttributes().getLength(); i++) {
			if (n.getAttributes().item(i).getNodeName().equals("mandatory")
					&& (findNameInNode(n.getParentNode()).equals("")) && (!n.getParentNode().getNodeName().equals("or")
							&& !n.getParentNode().getNodeName().equals("alt"))) {
				HlvlCode.append("	" + converter.getCore(findNameInNode(n)));
			} else if (n.getAttributes().item(i).getNodeName().equals("mandatory")
					&& (!findNameInNode(n.getParentNode()).equals("")) && (!n.getParentNode().getNodeName().equals("or")
							&& !n.getParentNode().getNodeName().equals("alt"))) {
				HlvlCode.append("	" + converter.getDecomposition(findNameInNode(n.getParentNode()), findNameInNode(n),
						DecompositionType.Mandatory));
			} else if ((!findNameInNode(n.getParentNode()).equals("")) && n.getAttributes().getLength() == 1
					&& (!n.getParentNode().getNodeName().equals("or")
							&& !n.getParentNode().getNodeName().equals("alt"))) {
				HlvlCode.append("	" + converter.getDecomposition(findNameInNode(n.getParentNode()), findNameInNode(n),
						DecompositionType.Optional));
			}
		}
	}
	public void addRelations(Node n) {
		// SE agrega la etiqueta para las relaciones
		addCore();
		// SE agregan los grupos
		addGroup(n);
		// SE agregan las descomposiciones
		addDescomposition(n);
	}

	public String findNameInNode(Node n) {
		String nodeName = "";
		if (n.getAttributes() != null) {
			for (int i = 0; i < n.getAttributes().getLength(); i++) {
				if (n.getAttributes().item(i).getNodeName().equals("name")) {
					nodeName = n.getAttributes().item(i).getNodeValue();
				}
			}
		}
		return nodeName;
	}

	public void addElement(Node n) {
		for (int i = 0; i < n.getAttributes().getLength(); i++) {
			if (n.getAttributes().item(i).getNodeName().equals("name")) {
				if (params!=null) {			
					HlvlCode.insert(converter.getHeader(params.getTargetName() + "_generated").length(),
							"	" + converter.getElement(n.getAttributes().item(i).getNodeValue()));
				}else {
					
					HlvlCode.insert((converter.getHeader("Auto_generated").length()),
							"	" + converter.getElement(n.getAttributes().item(i).getNodeValue()));
				}
			}
		}
	}


	@Override
	public String getValidName(String name) {
		return name.replaceAll(" ", "_").replaceAll("\\-", "Minus").replaceAll("\\+", "Plus").replaceAll("\\.", "dot")
				.replaceAll("/", "");
	}

	@Override
	public void parse() throws Exception {
		Initializate();
		for (int i = 0; i < xmlTree.size(); i++) {
			converter = new HlvlBasicFactory();
			HlvlCode = new StringBuilder();
			HlvlCode.append(converter.getHeader(params.getTargetName() + "_generated"));
			readTree(xmlTree.get(i));
			writeFile();
			params.setTargetName(params.getTargetName()+i);
			HlvlCode.delete(0,HlvlCode.length());
		}
		
	}

	@Override
	public String parse(String data) throws Exception {
		Initializate(data);
		HlvlCode.append(converter.getHeader("Auto_generated"));
		readTree(xmlTree.get(0));
		return HlvlCode.toString();
	}

}
