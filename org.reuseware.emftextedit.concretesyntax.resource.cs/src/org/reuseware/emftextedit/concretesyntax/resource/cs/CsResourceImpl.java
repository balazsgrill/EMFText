package org.reuseware.emftextedit.concretesyntax.resource.cs;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.reuseware.emftextedit.resource.*;
import org.reuseware.emftextedit.resource.impl.*;

public class CsResourceImpl extends TextResourceImpl {
	public CsResourceImpl(){
		super();
	}

	public CsResourceImpl(URI uri){
		super(uri);
	}

	protected void doLoad(InputStream inputStream, Map<?,?> options) throws IOException {
		EMFTextParser p = new CsParser(new CommonTokenStream(new CsLexer(new ANTLRInputStream(inputStream))));
		p.setResource(this);
		EObject root = p.parse();
		while (root != null) {
			getContents().add(root);
			root = null; //p.parse();
		}

		EMFTextTreeAnalyser a = new CsTreeAnalyser();

		a.analyse(this);
	}

	protected void doSave(OutputStream outputStream, Map<?,?> options) throws IOException {
		EMFTextPrinter p = new CsPrinter(outputStream, this);
		for(EObject root : getContents()) {
			p.print(root);
		}
	}

	public String[] getTokenNames() {
		return new CsParser(null).getTokenNames();
	}

	public Object getScanner() {
		return new CsLexer();
	}

}
