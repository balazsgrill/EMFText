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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.ecore.EObject;
import org.emftext.runtime.resource.IReferenceResolveResult;
import org.emftext.runtime.resource.IReferenceResolverSwitch;
import org.emftext.runtime.resource.impl.FuzzyResolveResult;
import org.emftext.sdk.codegen.GenerationContext;
import org.emftext.sdk.codegen.composites.JavaComposite;
import org.emftext.sdk.codegen.composites.StringComposite;
import org.emftext.sdk.codegen.util.GeneratorUtil;
import org.emftext.sdk.codegen.util.NameUtil;

/**
 * A generator that creates a multiplexing reference resolver.
 * Depending on the type of the reference that must be resolved,
 * the generated class delegates the resolve call to the appropriate
 * reference resolver.
 */
public class ReferenceResolverSwitchGenerator extends BaseGenerator {
	
	private final NameUtil nameUtil = new NameUtil();
	private final GeneratorUtil generatorUtil = new GeneratorUtil();

	private final GenerationContext context;

	public ReferenceResolverSwitchGenerator(GenerationContext context) {
		super(context.getPackageName(), context.getReferenceResolverSwitchClassName());
		this.context = context;
	}
	
	@Override
	public boolean generate(PrintWriter out) {
		out.print(generateReferenceResolverSwitch());
		return true;
	}
	
	 /**
     * Generates the reference resolver switch that calls the correct
     * reference resolvers generated by <code>generateReferenceResolver()</code>.
     */
    private String generateReferenceResolverSwitch()  {  
    	StringComposite sc = new JavaComposite();
        sc.add("package " + getResourcePackageName() + ";");
        sc.addLineBreak();
        
		sc.add("public class " + getResourceClassName() + " implements " + IReferenceResolverSwitch.class.getName() + " {");
        sc.addLineBreak();
		
		generateFields(sc);
		generateGetMethods(sc);
        generateSetOptionsMethod(sc);
		generateResolveFuzzyMethod(sc);
		
		sc.add("}");
		
    	return sc.toString();	
    }

	private void generateResolveFuzzyMethod(StringComposite sc) {
		sc.add("public void resolveFuzzy(" + String.class.getName() + " identifier, " + EObject.class.getName() + " container, int position, " + IReferenceResolveResult.class.getName() + "<" + EObject.class.getName() + "> result) {");
		for (GenFeature proxyReference : context.getNonContainmentReferences()) {
			GenClass genClass = proxyReference.getGenClass();
			String accessorName = genClass.getGenPackage().getQualifiedPackageInterfaceName() + ".eINSTANCE.get"  + genClass.getName() + "()";
			String generatedClassName = nameUtil.getReferenceResolverClassName(proxyReference);
			GenFeature genFeature = generatorUtil.findGenFeature(genClass, proxyReference.getName());
			sc.add("if (" + accessorName+ ".isInstance(container)) {");
			sc.add(FuzzyResolveResult.class.getName() + "<" + genFeature.getTypeGenClass().getQualifiedInterfaceName() + "> frr = new " + FuzzyResolveResult.class.getName() + "<" + genFeature.getTypeGenClass().getQualifiedInterfaceName() + ">(result);");

			// TODO use the feature constant instead of the feature name, but NOT the way it is done
			// in the next line, because this does not work when genClass is a super typer of  
			// container.eClass(). Sub types do have different feature IDs for inherited features
			// than the super types.
			//sc.add(org.eclipse.emf.ecore.EStructuralFeature.class.getName() + " feature = container.eClass()." + generatorUtil.createGetFeatureCall(genClass, genFeature) + ";");
			sc.add(org.eclipse.emf.ecore.EStructuralFeature.class.getName() + " feature = container.eClass().getEStructuralFeature(\"" + genFeature.getName() + "\");");
			
			sc.add("if (feature instanceof " + org.eclipse.emf.ecore.EReference.class.getName() + ") {");
			sc.add(low(generatedClassName) + ".resolve(identifier, (" + genClass.getQualifiedInterfaceName() + ") container, (" + org.eclipse.emf.ecore.EReference.class.getName() + ") feature, position, true, frr);");
			sc.add("}");
			sc.add("}");
		}
		sc.add("}");
	}

	private void generateSetOptionsMethod(StringComposite sc) {
		sc.add("public void setOptions(" + java.util.Map.class.getName() + "<?, ?> options) {");
		for (GenFeature proxyReference : context.getNonContainmentReferences()) {
			String generatedClassName = nameUtil.getReferenceResolverClassName(proxyReference);
			sc.add(low(generatedClassName) + ".setOptions(options);");			
		}
		sc.add("}");
		sc.addLineBreak();
	}

	private void generateFields(StringComposite sc) {
    	List<String> generatedResolvers = new ArrayList<String>();

		for (GenFeature proxyReference : context.getNonContainmentReferences()) {
			String generatedClassName = nameUtil.getReferenceResolverClassName(proxyReference);
			if (!generatedResolvers.contains(generatedClassName)) {
				generatedResolvers.add(generatedClassName);
				String fullClassName = context.getQualifiedReferenceResolverClassName(proxyReference);
				sc.add("protected " + fullClassName + " " + low(generatedClassName) + " = new " + fullClassName + "();");			
			}
		}
	    sc.addLineBreak();
	}

	private void generateGetMethods(StringComposite sc) {
    	List<String> generatedResolvers = new ArrayList<String>();

		for (GenFeature proxyReference : context.getNonContainmentReferences()) {
			String generatedClassName = nameUtil.getReferenceResolverClassName(proxyReference);
			if (!generatedResolvers.contains(generatedClassName)) {
				generatedResolvers.add(generatedClassName);
				String fullClassName = context.getQualifiedReferenceResolverClassName(proxyReference);
				sc.add("public " + fullClassName + " get" + generatedClassName + "() {");
				sc.add("return " + low(generatedClassName) + ";");			
				sc.add("}");
			    sc.addLineBreak();
			}
		}
	}
}
