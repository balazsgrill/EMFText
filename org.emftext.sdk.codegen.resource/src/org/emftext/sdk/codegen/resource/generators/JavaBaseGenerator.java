package org.emftext.sdk.codegen.resource.generators;

import java.io.PrintWriter;

import org.emftext.sdk.codegen.ArtifactDescriptor;
import org.emftext.sdk.codegen.ICodeGenerationComponent;
import org.emftext.sdk.codegen.composites.JavaComposite;
import org.emftext.sdk.codegen.composites.StringComposite;
import org.emftext.sdk.codegen.resource.GenerationContext;
import org.emftext.sdk.concretesyntax.ConcreteSyntax;
import org.emftext.sdk.util.LicenceHeaderUtil;

public abstract class JavaBaseGenerator<ParameterType> extends ResourceBaseGenerator<ParameterType> {

	public static final String DEFAULT_LICENCE_HEADER_TEXT = "/**\n" + " * <copyright>\n" + " * </copyright>\n"
	+ " *\n" + " * \n" + " */";

	private LicenceHeaderUtil licenceHeaderUtil = new LicenceHeaderUtil();

	protected JavaBaseGenerator() {
		super();
	}

	public JavaBaseGenerator(ICodeGenerationComponent parent, GenerationContext context, ArtifactDescriptor<GenerationContext, ParameterType> artifact) {
		super(parent, context, null, artifact);
	}

	final public boolean generate(PrintWriter out) {
		JavaComposite sc = new JavaComposite();
		addLicenceHeader(sc);
		boolean success = generateJavaContents(sc);
		out.write(sc.toString());
		return success;
	}

	private void addLicenceHeader(StringComposite sc) {
		String licenceText = getLicenceText();
		if (licenceText != null) {
			sc.add(licenceText);
		}
	}

	private String getLicenceText() {
		// check whether the licence header was loaded before and 
		// stored in the context
		if (getContext().getLicenceText() == null) {
			// the licence header was not loaded before
			ConcreteSyntax concreteSyntax = getContext().getConcreteSyntax();
			String licenceHeaderText = licenceHeaderUtil.loadLicenceHeaderText(concreteSyntax);
			if (licenceHeaderText == null) {
				// file was not found - use default text
				licenceHeaderText = DEFAULT_LICENCE_HEADER_TEXT;
			}
			// store text in context to avoid unnecessary loading
			getContext().setLicenceText(licenceHeaderText);
		}
		return getContext().getLicenceText();
	}

	public abstract boolean generateJavaContents(JavaComposite sc);
}
