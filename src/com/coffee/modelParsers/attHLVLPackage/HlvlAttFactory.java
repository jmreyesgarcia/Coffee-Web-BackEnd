package com.coffee.modelParsers.attHLVLPackage;

import com.coffee.modelParsers.basicHLVLPackage.HlvlBasicFactory;

public class HlvlAttFactory extends HlvlBasicFactory implements HlvlAttKeys, IHlvlAttFactory{

	@Override
	public String getElement(String identifier, AttType type) {
		String out ="";
		switch (type) {
		case INTEGER:
			out= ATT +SPACE + INTEGER + SPACE + identifier;
			break;
		case LONG:
			out= ATT +SPACE + LONG + SPACE + identifier;
			break;
		case STRING:
			out= ATT +SPACE + STRING + SPACE + identifier;
			break;
		case DOUBLE:
			out= ATT +SPACE + DOUBLE + SPACE + identifier;
			break;
		}
		return  out;
	}
}
