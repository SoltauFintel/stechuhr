package de.mwvb.stechuhr.base;

import java.io.File;
import java.io.InputStream;

import de.mwvb.base.xml.XMLDocument;

/**
 * XMLDocument mit UTF-8 Encoding
 */
public class XMLDocumentUTF8 extends XMLDocument {

	public XMLDocumentUTF8() {
		super();
	}

	public XMLDocumentUTF8(boolean isResource, String fileName) {
		super(isResource, fileName);
	}

	public XMLDocumentUTF8(Class<?> clazz, String resourceName) {
		super(clazz, resourceName);
	}

	public XMLDocumentUTF8(File file) {
		super(file);
	}

	public XMLDocumentUTF8(InputStream stream) {
		super(stream);
	}

	public XMLDocumentUTF8(String xml) {
		super(xml);
	}
	
	public static XMLDocumentUTF8 load(String dateiname) {
		XMLDocumentUTF8 dok = new XMLDocumentUTF8();
		dok.loadFile(dateiname);
		return dok;
	}

	@Override
	protected String getEncoding() {
		return "UTF-8";
	}
}
