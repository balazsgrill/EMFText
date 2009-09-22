package org.emftext.sdk.codegen.generators.interfaces;

import static org.emftext.sdk.codegen.generators.IClassNameConstants.INPUT_STREAM;

import java.io.PrintWriter;

import org.emftext.sdk.codegen.EArtifact;
import org.emftext.sdk.codegen.GenerationContext;
import org.emftext.sdk.codegen.IGenerator;
import org.emftext.sdk.codegen.generators.BaseGenerator;

public class IInputStreamProcessorProviderGenerator extends BaseGenerator {

	private String inputStreamProcessorClassName;

	public IInputStreamProcessorProviderGenerator() {
		super();
	}

	private IInputStreamProcessorProviderGenerator(GenerationContext context) {
		super(context, EArtifact.I_INPUT_STREAM_PROCESSOR_PROVIDER);
		inputStreamProcessorClassName = getContext().getQualifiedClassName(EArtifact.INPUT_STREAM_PROCESSOR);
	}

	public IGenerator newInstance(GenerationContext context) {
		return new IInputStreamProcessorProviderGenerator(context);
	}

	public boolean generate(PrintWriter out) {
		org.emftext.sdk.codegen.composites.StringComposite sc = new org.emftext.sdk.codegen.composites.JavaComposite();
		sc.add("package " + getResourcePackageName() + ";");
		sc.addLineBreak();
		
		sc.add("// Implementors of this interface can provide InputStreamProcessors. These");
		sc.add("// processors can be used to pre-process input stream before a text resource");
		sc.add("// is actually lexed and parsed. This can be for example useful to do an");
		sc.add("// encoding conversion.");
		sc.add("//");
		sc.add("// TODO use EMF's load option Resource.OPTION_CIPHER instead");
		sc.add("public interface " + getResourceClassName() + " {");
		sc.addLineBreak();
		
		sc.add("// Return a processor for the given input stream.");
		sc.add("//");
		sc.add("// @param inputStream the actual stream that provides the content of a resource");
		sc.add("// @return a processor that pre-processes the input stream");
		
		sc.add("public " + inputStreamProcessorClassName + " getInputStreamProcessor(" + INPUT_STREAM + " inputStream);");
		sc.add("}");
		out.print(sc.toString());
		return true;
	}
}