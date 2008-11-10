package org.reuseware.emftextedit.sdk.codegen;

import org.reuseware.emftextedit.sdk.concretesyntax.ConcreteSyntax;
import org.eclipse.core.resources.IFolder;
import java.util.Map;

/**
 * A resource package provides all information that is needed by the 
 * ResourcePackageGenerator. This includes a resolved concrete syntax, 
 * a package name for parser and printer, a package name for resolvers 
 * (proxy and token resolvers) and a resource target folder.
 * 
 * @see org.reuseware.emftextedit.sdk.codegen.ResourcePackageGenerator
 * 
 * @author skarol
 */
public class ResourcePackage {
	
	private ConcreteSyntax csSource;
	private String csPackageName;
	private String resolverPackageName;
	private IFolder targetFolder;
	private Map<String,Boolean> preferences;
	
	public ResourcePackage(ConcreteSyntax csSource,String csPackageName,IFolder targetFolder, Map<String,Boolean> preferences){
		if(csSource==null||targetFolder==null)
			throw new NullPointerException("A ConcreteSyntax and an IFolder have to be specified!");
		this.csSource = csSource;
		this.targetFolder = targetFolder;
		this.csPackageName = csPackageName;
		resolverPackageName = (csPackageName==null||csPackageName.equals("")?"":csPackageName+  ".") + "analysis";
		this.preferences = preferences;
	}
	
	/**
	 * @return The concrete syntax to be processed and which is 
	 * assumed to contain all resolved information.
	 */
	public ConcreteSyntax getConcreteSyntax(){
		return csSource;
	}
	
	/**
	 * @return The base package where parser and lexer will go to.
	 */
	public String getCsPackageName(){
		return csPackageName;
	}
	
	/**	 
	 * @return The base package where token and proxy resolvers go to.
	 */
	public String getResolverPackageName(){
		return resolverPackageName;
	}
	
	/**
	 * @return The base folder to which generated packages are printed.
	 */
	public IFolder getTargetFolder(){
		return targetFolder;
	}
	
	public boolean getPreference(String name){
		return preferences==null?false:(preferences.get(name)==null?false:preferences.get(name));
	}
	
}
