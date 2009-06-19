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
package org.emftext.sdk.ant;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.emftext.runtime.resource.ITextResource;
import org.emftext.runtime.util.TextResourceUtil;
import org.emftext.sdk.SDKOptionProvider;
import org.emftext.sdk.codegen.generators.ResourcePluginGenerator.Result;
import org.emftext.sdk.concretesyntax.ConcreteSyntax;

/**
 * A custom task for the ANT build tool that generates
 * a resource plug-in for a given syntax specification.
 * 
 * TODO there should be a build exception when the syntax
 * contains errors (e.g., issued by ANTLR)
 */
public class GenerateTextResourceTask extends Task {

	private File rootFolder;
	private File syntaxFile;
	private String syntaxProjectName;

	private List<GenModelElement> genModels = new ArrayList<GenModelElement>();
	private List<GenPackageElement> genPackages = new ArrayList<GenPackageElement>();
	
	@Override
	public void execute() throws BuildException {
		checkParameters();
		
		// Get the task class loader we used to load this tag.
		AntClassLoader taskloader = 
			(AntClassLoader)this.getClass().getClassLoader();
		// Shove it into the Thread, replacing the thread's ClassLoader:
		taskloader.setThreadContextLoader();

		registerResourceFactories();
		try {
			log("loading syntax file...");
			ITextResource csResource = TextResourceUtil.getResource(syntaxFile, new SDKOptionProvider().getOptions());
			ConcreteSyntax syntax = (ConcreteSyntax) csResource.getContents().get(0);
			
			Result result = new AntResourcePluginGenerator().run(
					syntax, 
					new AntGenerationContext(syntax, rootFolder, syntaxProjectName, new AntProblemCollector(this)), 
					new AntLogMarker(this), 
					new AntDelegateProgressMonitor(this)
			);
			if (result != Result.SUCCESS) {
				if (result == Result.ERROR_FOUND_UNRESOLVED_PROXIES) {
					for (EObject unresolvedProxy : result.getUnresolvedProxies()) {
						log("Found unresolved proxy \"" + ((InternalEObject) unresolvedProxy).eProxyURI() + "\" in " + unresolvedProxy.eResource());
					}
					 // Reset the Thread's original ClassLoader.
					taskloader.resetThreadContextLoader(); 
					throw new BuildException("Generation failed " + result);
				} else {
					 // Reset the Thread's original ClassLoader.
					taskloader.resetThreadContextLoader(); 
					throw new BuildException("Generation failed " + result);
				}
			}
		} catch (Exception e) {
			 // Reset the Thread's original ClassLoader.
			taskloader.resetThreadContextLoader(); 
			e.printStackTrace();
			throw new BuildException(e);
		}
		 // Reset the Thread's original ClassLoader.
		taskloader.resetThreadContextLoader(); 
	}
	
	private void checkParameters() {
		if (syntaxFile == null) {
			throw new BuildException("Parameter \"syntax\" is missing.");
		}
		if (rootFolder == null) {
			throw new BuildException("Parameter \"rootFolder\" is missing.");
		}
		if (syntaxProjectName == null) {
			throw new BuildException("Parameter \"syntaxProjectName\" is missing.");
		}
	}
	


	public void setSyntax(File syntaxFile) {
		this.syntaxFile = syntaxFile;
	}

	public void setRootFolder(File rootFolder) {
		this.rootFolder = rootFolder;
	}
	
	public void setSyntaxProjectName(String syntaxProjectName) {
		this.syntaxProjectName = syntaxProjectName;
	}
	
	public void addGenModel(GenModelElement factory) {
		genModels.add(factory);
	}

	public void addGenPackage(GenPackageElement factory) {
		genPackages.add(factory);
	}

	public void registerResourceFactories() {
		//in case a old version from last run is registered here
		EPackage.Registry.INSTANCE.remove("http://www.emftext.org/sdk/concretesyntax");
		registerFactory("cs", new org.emftext.sdk.concretesyntax.resource.cs.CsResourceFactory());
		
		//TODO: the rest of this method is never used in our build scripts at the moment
		for (GenModelElement genModelElement : genModels) {
			String namespaceURI = genModelElement.getNamespaceURI();
			String genModelURI = genModelElement.getGenModelURI();
			try {
				log("registering genmodel " + namespaceURI + " at " + genModelURI);
				EcorePlugin.getEPackageNsURIToGenModelLocationMap().put(
						namespaceURI,
						URI.createURI(genModelURI)
				);
				
				//register a mapping for "resource:/plugin/..." URIs in case 
				//one genmodel imports another genmodel using this URI
				String filesSystemURI = genModelURI;
				int startIdx = filesSystemURI.indexOf("/plugins");
				int versionStartIdx = filesSystemURI.indexOf("_",startIdx);
				int filePathStartIdx = filesSystemURI.lastIndexOf("!/") + 1;
				if(startIdx > -1 && versionStartIdx > startIdx && filePathStartIdx > versionStartIdx) {
					String platformPluginURI = "platform:"  + filesSystemURI.substring(startIdx, versionStartIdx);
					platformPluginURI = platformPluginURI + filesSystemURI.substring(filePathStartIdx);
					platformPluginURI = platformPluginURI.replace("/plugins/", "/plugin/");
					
					//gen model
					log("adding mapping from " + platformPluginURI + " to " + filesSystemURI);
					URIConverter.URI_MAP.put(
							URI.createURI(platformPluginURI),
							URI.createURI(filesSystemURI));
					
					//ecore model (this code assumes that both files are named equally)
					filesSystemURI = filesSystemURI.replace(".genmodel", ".ecore");
					platformPluginURI = platformPluginURI.replace(".genmodel", ".ecore");
					log("adding mapping from " + platformPluginURI + " to " + filesSystemURI);
					URIConverter.URI_MAP.put(
							URI.createURI(platformPluginURI),
							URI.createURI(filesSystemURI));
				}
			} catch (Exception e) {
				throw new BuildException("Error while registering genmodel " + namespaceURI, e);
			}
		}

		for (GenPackageElement genPackage : genPackages) {
			String namespaceURI = genPackage.getNamespaceURI();
			String ePackageClassName = genPackage.getEPackageClassName();
			try {
				log("registering ePackage " + namespaceURI + " at " + ePackageClassName);
				Field factoryInstance = Class.forName(ePackageClassName).getField("eINSTANCE");
				Object ePackageObject = factoryInstance.get(null);
				EPackage.Registry.INSTANCE.put(namespaceURI, ePackageObject);
			} catch (Exception e) {
				e.printStackTrace();
				throw new BuildException("Error while registering EPackage " + namespaceURI, e);
			}
		}
	}

	private void registerFactory(String extension, Object factory) {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().remove(extension);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				extension,
				factory);
		

	}
}
