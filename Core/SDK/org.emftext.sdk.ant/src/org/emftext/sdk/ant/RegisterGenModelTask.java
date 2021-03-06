/*******************************************************************************
 * Copyright (c) 2006-2012
 * Software Technology Group, Dresden University of Technology
 * DevBoost GmbH, Berlin, Amtsgericht Charlottenburg, HRB 140026
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Software Technology Group - TU Dresden, Germany;
 *   DevBoost GmbH - Berlin, Germany
 *      - initial API and implementation
 ******************************************************************************/
package org.emftext.sdk.ant;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class RegisterGenModelTask extends Task {
       
    private String namespaceURI;
    private String genModelURI;

    @Override
    public void execute() throws BuildException {
    
		EPackage.Registry.INSTANCE.put(
				GenModelPackage.eNS_URI,
				GenModelPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(
				EcorePackage.eNS_URI,
				EcorePackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(
				"http://www.eclipse.org/uml2/2.2.0/GenModel",
				getUML2GenPackage());
		EPackage.Registry.INSTANCE.put(
				"http://www.eclipse.org/uml2/4.0.0/Types",
				getUML2TypesPackage());
		
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
			"ecore",
			new org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
			"genmodel",
			new org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl());
		
        final Map<String, URI> packageNsURIToGenModelLocationMap = EcorePlugin.getEPackageNsURIToGenModelLocationMap();
        URI genModelURIObject = URI.createURI(genModelURI);
        ResourceSet rs = new ResourceSetImpl();
        Resource r = null;
        try {
        	r = rs.getResource(genModelURIObject, true);
        	
            for (GenPackage genPackage : ((GenModel)r.getContents().get(0)).getGenPackages()) {
            	EPackage.Registry.INSTANCE.put(genPackage.getEcorePackage().getNsURI(),
            			genPackage.getEcorePackage());
            }
        } catch (Exception e) {
        	e.printStackTrace();
          	throw new BuildException("Can't load generator model from " + genModelURIObject);
        }
    	
        packageNsURIToGenModelLocationMap.put(namespaceURI, genModelURIObject);
    }
    
	private EPackage getUML2GenPackage() {
		try {
			Class<?> factoryClass = Class.forName(
					"org.eclipse.uml2.codegen.ecore.genmodel.GenModelPackage");
			Field eINSTANCE = factoryClass.getField("eINSTANCE");
			EPackage p = (EPackage) eINSTANCE.get(null);
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Error while creating UML2 gen model adapter.", e);
		}
	}
	
	private EPackage getUML2TypesPackage() {
		try {
			Class<?> factoryClass = Class.forName(
					"org.eclipse.uml2.types.TypesPackage");
			Field eINSTANCE = factoryClass.getField("eINSTANCE");
			EPackage p = (EPackage) eINSTANCE.get(null);
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("Error while accessing UML2 types package.", e);
		}
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	public String getGenModelURI() {
		return genModelURI;
	}

	public void setGenModelURI(String genModelURI) {
		this.genModelURI = genModelURI;
	}
}
 
