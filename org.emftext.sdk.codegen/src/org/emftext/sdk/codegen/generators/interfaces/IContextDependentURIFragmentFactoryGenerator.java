/*******************************************************************************
 * Copyright (c) 2006-2010 
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
package org.emftext.sdk.codegen.generators.interfaces;

import static org.emftext.sdk.codegen.generators.IClassNameConstants.E_OBJECT;
import static org.emftext.sdk.codegen.generators.IClassNameConstants.E_REFERENCE;

import org.emftext.sdk.codegen.EArtifact;
import org.emftext.sdk.codegen.GenerationContext;
import org.emftext.sdk.codegen.IGenerator;
import org.emftext.sdk.codegen.composites.JavaComposite;
import org.emftext.sdk.codegen.generators.JavaBaseGenerator;

public class IContextDependentURIFragmentFactoryGenerator extends JavaBaseGenerator {

	private String iContextDependentURIFragmentClassName;

	public IContextDependentURIFragmentFactoryGenerator() {
		super();
	}

	private IContextDependentURIFragmentFactoryGenerator(GenerationContext context) {
		super(context, EArtifact.I_CONTEXT_DEPENDENT_URI_FRAGMENT_FACTORY);
		iContextDependentURIFragmentClassName = getContext().getQualifiedClassName(EArtifact.I_CONTEXT_DEPENDENT_URI_FRAGMENT);
	}

	public IGenerator newInstance(GenerationContext context) {
		return new IContextDependentURIFragmentFactoryGenerator(context);
	}

	public boolean generateJavaContents(JavaComposite sc) {
		
		sc.add("package " + getResourcePackageName() + ";");
		sc.addLineBreak();
		
		sc.addJavadoc(
			"An interface for factories to create instances of " + iContextDependentURIFragmentClassName + ".\n\n" +
			"@param <ContainerType> the type of the class containing the reference to be resolved\n" +
			"@param <ReferenceType> the type of the reference to be resolved"
		);
		sc.add("public interface " + getResourceClassName() + "<ContainerType extends " + E_OBJECT + ", ReferenceType extends " + E_OBJECT + "> {");
		sc.addLineBreak();
		
		sc.addJavadoc(
			"Create a new instance of the " + iContextDependentURIFragmentClassName + " interface.\n\n" +
			"@param identifier the identifier that references an " + E_OBJECT + "\n" +
			"@param container the object that contains the reference\n" +
			"@param reference the reference itself\n" +
			"@param positionInReference the position of the identifier (if the reference is multiple)\n" +
			"@param proxy the proxy that will be resolved later to the actual " + E_OBJECT + "\n" +
			"@return the new instance of " + iContextDependentURIFragmentClassName);
		sc.add("public " + iContextDependentURIFragmentClassName + "<?> create(String identifier, ContainerType container, " + E_REFERENCE + " reference, int positionInReference, " + E_OBJECT + " proxy);");
		sc.add("}");
		return true;
	}
}
