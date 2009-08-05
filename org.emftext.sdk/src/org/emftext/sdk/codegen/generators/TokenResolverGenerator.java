/*******************************************************************************
 * Copyright (c) 2006-2009 
 * Software Technology Group, Dresden University of Technology
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA  02111-1307 USA
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany 
 *   - initial API and implementation
 ******************************************************************************/
package org.emftext.sdk.codegen.generators;

import static org.emftext.sdk.codegen.generators.IClassNameConstants.ABSTRACT_TOKEN_RESOLVER;
import static org.emftext.sdk.codegen.generators.IClassNameConstants.E_OBJECT;
import static org.emftext.sdk.codegen.generators.IClassNameConstants.E_STRUCTURAL_FEATURE;
import static org.emftext.sdk.codegen.generators.IClassNameConstants.I_TOKEN_RESOLVE_RESULT;
import static org.emftext.sdk.codegen.generators.IClassNameConstants.OBJECT;
import static org.emftext.sdk.codegen.generators.IClassNameConstants.STRING;

import java.io.PrintWriter;

import org.emftext.sdk.codegen.EArtifact;
import org.emftext.sdk.codegen.GenerationContext;
import org.emftext.sdk.codegen.composites.JavaComposite;
import org.emftext.sdk.codegen.composites.StringComposite;
import org.emftext.sdk.codegen.util.GeneratorUtil;
import org.emftext.sdk.codegen.util.NameUtil;
import org.emftext.sdk.concretesyntax.QuotedToken;
import org.emftext.sdk.concretesyntax.TokenDefinition;

/**
 * A TokenResolverGenerator generates a single TokenResolver for a given TokenDefinition.
 * 
 * For a definition with prefix and suffix it generates code which removes these 
 * strings from the beginning and the end of a lexem and then passes the manipulated 
 * lexem to the default token resolver. In the deresolvement the conversion from 
 * object to string is also delegated to the default resolver. 
 * Finally the deresolved String will be decorated by pre- and suffixes again. 
 * 
 * @author Sven Karol (Sven.Karol@tu-dresden.de)
 */
public class TokenResolverGenerator extends BaseGenerator {
	
	private static NameUtil nameUtil = new NameUtil();
	private final GeneratorUtil generatorUtil = new GeneratorUtil();
	
	private TokenDefinition definition;
	private String qualifiedDefaultTokenResolverClassName;
	
	public TokenResolverGenerator(GenerationContext context, TokenDefinition definition) {
		super(context.getResolverPackageName(), nameUtil.getTokenResolverClassName(context.getConcreteSyntax(), definition));
		this.definition = definition;
		this.qualifiedDefaultTokenResolverClassName = context.getQualifiedClassName(EArtifact.DEFAULT_TOKEN_RESOLVER);
	}

	@Override
	public boolean generate(PrintWriter out) {
		StringComposite sc = new JavaComposite();
		
		sc.add("package " + super.getResourcePackageName()+ ";");
		sc.addLineBreak();

		sc.add("public class " + super.getResourceClassName() + " extends " + ABSTRACT_TOKEN_RESOLVER + " {");
		sc.addLineBreak();
		sc.add("private " + qualifiedDefaultTokenResolverClassName + " defaultTokenResolver = new " + qualifiedDefaultTokenResolverClassName + "();");
		generateDeResolveMethod(sc);
		generateResolveMethod(sc);
	    generatorUtil.addSetOptionsMethod(sc);
		sc.add("}");
		
		out.print(sc.toString());
		return true;
	}

	private void generateDeResolveMethod(StringComposite sc) {
		sc.add("public " + STRING + " deResolve(" + OBJECT + " value, " + E_STRUCTURAL_FEATURE + " feature, " + E_OBJECT + " container) {");
		sc.add(STRING + " result = defaultTokenResolver.deResolve(value, feature, container);");
		String suffix = getSuffix();
		String prefix = getPrefix();
		
		if (suffix != null) {
			String escapedSuffix = escapeChars(suffix);
			sc.add("result = result.replaceAll(" + java.util.regex.Pattern.class.getName() + ".quote(\""+escapedSuffix+"\"),\"\\\\\\\\"+escapeDollar(escapedSuffix)+"\");");
			sc.add("result += \"" + escapedSuffix + "\";");
		}	
		
		if (prefix != null) {
			sc.add("result = \"" + escapeChars(prefix) + "\" + result;");
		}
		sc.add("return result;");
		sc.add("}");
		sc.addLineBreak();
	}

	private void generateResolveMethod(StringComposite sc) {
		sc.add("public void resolve(" + STRING + " lexem, " + E_STRUCTURAL_FEATURE + " feature, " + I_TOKEN_RESOLVE_RESULT + " result) {");

		String suffix = getSuffix();
		String prefix = getPrefix();
		
		if (prefix != null) {
			int count = prefix.length();
			sc.add("lexem = lexem.substring(" + count + ");");			
		}
		if (suffix != null) {
			int count = suffix.length();
			sc.add("lexem = lexem.substring(0, lexem.length() - " + count + ");");
			String replacement = escapeChars(suffix);
			sc.add("lexem = lexem.replaceAll(\"\\\\\\\\\"+" + java.util.regex.Pattern.class.getName() + ".quote(\""+replacement+"\"),\""+escapeDollar(replacement)+"\");");
		}
		sc.add("defaultTokenResolver.resolve(lexem, feature, result);");
		sc.add("}");
	}
	
	private String escapeChars(String candidate) {
		//for javac: replace one backslash by two and escape double quotes
		return candidate.replaceAll("\\\\","\\\\\\\\").replaceAll("\"","\\\\\"");
	}
	
	private String escapeDollar(String candidate) {
		//for java regex: $ is meta char in java regex patterns and has semantics in replacement (back references)
		return candidate.replaceAll(java.util.regex.Pattern.quote("$"),"\\\\\\\\\\$");
	}

	private String getPrefix() {
		String prefix = null;
		if (definition instanceof QuotedToken) {
			prefix = ((QuotedToken) definition).getPrefix();
		}
		return prefix;
	}

	private String getSuffix() {
		String suffix = null;
		if (definition instanceof QuotedToken) {
			suffix = ((QuotedToken) definition).getSuffix();
		}
		return suffix;
	}
}
