/*******************************************************************************
 * Copyright (c) 2006-2011
 * Software Technology Group, Dresden University of Technology
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany 
 *      - initial API and implementation
 ******************************************************************************/
package org.emftext.sdk.codegen.resource.generators;

import static org.emftext.sdk.codegen.resource.generators.IClassNameConstants.E_OBJECT;
import static org.emftext.sdk.codegen.resource.generators.IClassNameConstants.E_REFERENCE;
import static org.emftext.sdk.codegen.resource.generators.IClassNameConstants.E_STRUCTURAL_FEATURE;
import static org.emftext.sdk.codegen.resource.generators.IClassNameConstants.MAP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.emftext.sdk.codegen.annotations.SyntaxDependent;
import org.emftext.sdk.codegen.composites.JavaComposite;
import org.emftext.sdk.codegen.composites.StringComposite;
import org.emftext.sdk.codegen.parameters.ArtifactParameter;
import org.emftext.sdk.codegen.resource.GenerationContext;
import org.emftext.sdk.codegen.resource.GeneratorUtil;
import org.emftext.sdk.codegen.resource.TextResourceArtifacts;
import org.emftext.sdk.codegen.util.NameUtil;
import org.emftext.sdk.concretesyntax.ConcreteSyntax;
import org.emftext.sdk.concretesyntax.GenClassCache;
import org.emftext.sdk.util.ConcreteSyntaxUtil;
import org.emftext.sdk.util.StringUtil;

/**
 * A generator that creates a multiplexing reference resolver.
 * Depending on the type of the reference that must be resolved,
 * the generated class delegates the resolve call to the appropriate
 * reference resolver.
 * The generated resolver switch is used during code completion.
 */
@SyntaxDependent
public class ReferenceResolverSwitchGenerator extends JavaBaseGenerator<ArtifactParameter<GenerationContext>> {

	private final GeneratorUtil generatorUtil = new GeneratorUtil();
	private final ConcreteSyntaxUtil csUtil = new ConcreteSyntaxUtil();
	private final NameUtil nameUtil = new NameUtil();
	
	private GenClassCache genClassCache;
	private Collection<GenFeature> nonContainmentReferencesNeedingResolvers;

	@Override
	public void generateJavaContents(JavaComposite sc) {
		ConcreteSyntax syntax = getContext().getConcreteSyntax();
		this.genClassCache = syntax.getGenClassCache();
		this.nonContainmentReferencesNeedingResolvers = csUtil.getNonContainmentFeaturesNeedingResolver(syntax);

		sc.add("package " + getResourcePackageName() + ";");
        sc.addLineBreak();
		sc.add("public class " + getResourceClassName() + " implements " + iReferenceResolverSwitchClassName + " {");
        sc.addLineBreak();
		addFields(sc);
		addMethods(sc);
		sc.add("}");
	}
	
	private void addMethods(StringComposite sc) {
		generateGetMethods(sc);
        generateSetOptionsMethod(sc);
		generateResolveFuzzyMethod(sc);
		addGetResolverMethod(sc);
	}

	private void generateResolveFuzzyMethod(StringComposite sc) {
		String qualifiedFuzzyResolveResultClassName = getContext().getClassName(TextResourceArtifacts.FUZZY_RESOLVE_RESULT);
		
		sc.add("public void resolveFuzzy(String identifier, " + E_OBJECT + " container, " + E_REFERENCE + " reference, int position, " + iReferenceResolveResultClassName + "<" + E_OBJECT + "> result) {");
		// this was a temporary workaround to avoid NPEs when this switch is called
		// and no container was available during code completion. New code completion
		// helpers do create containers on demand, but still checking for null doesn't
		// hurt here.
		sc.add("if (container == null) {");
		sc.add("return;");
		sc.add("}");
		for (GenFeature proxyReference : nonContainmentReferencesNeedingResolvers) {
			GenClass genClass = proxyReference.getGenClass();
			String accessorName = genClass.getGenPackage().getQualifiedPackageInterfaceName() + ".eINSTANCE.get"  + genClass.getName() + "()";
			String generatedClassName = nameUtil.getReferenceResolverClassName(proxyReference);
			GenFeature genFeature = generatorUtil.findGenFeature(genClass, proxyReference.getName());
			
			sc.add("if (" + accessorName + ".isInstance(container)) {");
			sc.add(qualifiedFuzzyResolveResultClassName + "<" + genClassCache.getQualifiedInterfaceName(genFeature.getTypeGenClass()) + "> frr = new " + qualifiedFuzzyResolveResultClassName + "<" + genClassCache.getQualifiedInterfaceName(genFeature.getTypeGenClass()) + ">(result);");
			sc.add("String referenceName = reference.getName();");
			sc.add(E_STRUCTURAL_FEATURE + " feature = container.eClass().getEStructuralFeature(referenceName);");
			sc.add("if (feature != null && feature instanceof " + E_REFERENCE + " && referenceName != null && referenceName.equals(\"" + StringUtil.escapeToJavaString(proxyReference.getName()) + "\")) {");
			sc.add(StringUtil.low(generatedClassName) + ".resolve(identifier, (" + genClassCache.getQualifiedInterfaceName(genClass) + ") container, (" + E_REFERENCE + ") feature, position, true, frr);");
			sc.add("}");
			sc.add("}");
		}
		sc.add("}");
		sc.addLineBreak();
	}

	private void generateSetOptionsMethod(StringComposite sc) {
		sc.add("public void setOptions(" + MAP + "<?, ?> options) {");
		for (GenFeature proxyReference : nonContainmentReferencesNeedingResolvers) {
			String generatedClassName = nameUtil.getReferenceResolverClassName(proxyReference);
			sc.add(StringUtil.low(generatedClassName) + ".setOptions(options);");			
		}
		sc.add("}");
		sc.addLineBreak();
	}

	private void addGetResolverMethod(StringComposite sc) {
		sc.add("public " + iReferenceResolverClassName + "<? extends " + E_OBJECT + ", ? extends " + E_OBJECT + "> getResolver(" + E_STRUCTURAL_FEATURE + " reference) {");
		for (GenFeature proxyReference : nonContainmentReferencesNeedingResolvers) {
			String generatedClassName = nameUtil.getReferenceResolverClassName(proxyReference);
			sc.add("if (reference == " + proxyReference.getGenPackage().getQualifiedPackageInterfaceName() + ".eINSTANCE.get" + proxyReference.getFeatureAccessorName() + "()) {");
			sc.add("return " + StringUtil.low(generatedClassName) + ";");
			sc.add("}");
		}
		sc.add("return null;");
		sc.add("}");
		sc.addLineBreak();
	}

	private void addFields(StringComposite sc) {
    	List<String> generatedResolvers = new ArrayList<String>();

		for (GenFeature proxyReference : nonContainmentReferencesNeedingResolvers) {
			String generatedClassName = nameUtil.getReferenceResolverClassName(proxyReference);
			if (!generatedResolvers.contains(generatedClassName)) {
				generatedResolvers.add(generatedClassName);
				String fullClassName = getContext().getQualifiedReferenceResolverClassName(proxyReference, false);
				sc.add("protected " + fullClassName + " " + StringUtil.low(generatedClassName) + " = new " + fullClassName + "();");			
			}
		}
	    sc.addLineBreak();
	}

	private void generateGetMethods(StringComposite sc) {
    	List<String> generatedResolvers = new ArrayList<String>();

		for (GenFeature proxyReference : nonContainmentReferencesNeedingResolvers) {
			String generatedClassName = nameUtil.getReferenceResolverClassName(proxyReference);
			if (!generatedResolvers.contains(generatedClassName)) {
				generatedResolvers.add(generatedClassName);
				String fullClassName = getContext().getQualifiedReferenceResolverClassName(proxyReference, false);
				sc.add("public " + fullClassName + " get" + generatedClassName + "() {");
				sc.add("return " + StringUtil.low(generatedClassName) + ";");			
				sc.add("}");
			    sc.addLineBreak();
			}
		}
	}
}
