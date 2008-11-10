package org.reuseware.emftextedit.runtime.resource;

import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EObject;

/**
 * Basic Interface to directly manipulate parsed tokens and convert them into the attribute type in the metamodel.
 * All generated TokenResolvers inherit from JavaBasedTokenResolver which does the standard convertation to Java Types.
 * 
 * @see org.reuseware.emftextedit.runtime.resource.impl.JavaBasedTokenResolver
 * @see org.reuseware.emftextedit.codegen.TokenResolverGenerator
 * 
 * 
 * @author skarol
 *
 */
public interface TokenResolver {
	
	public void setOptions(Map<?,?> options);
	
	/**
	 * Converts a parsed String into an Object (might be a String again). 
	 * 
	 * @param lexem the parsed String
	 * @param feature the corresponding feature in the metamodel
	 * @param container the attribute/reference holder
	 * @param resource the text resource, which can be used to add processing errors
	 * @return the converted lexem or null if a problem occured
	 */
	public Object resolve(String lexem, EStructuralFeature feature,EObject container, TextResource resource);
	
	
	/**
	 * Does the inverse mapping from Object to a lexem which can be printed.
	 * 
	 * @param value the Object to be printed as String
	 * @param feature the corresponding feature
	 * @param container the container of the object
	 * @return the String representation or null if a problem occured
	 */
	public String deResolve(Object value, EStructuralFeature feature,EObject container);
	
	/** 
	 * @return an error message for previously occured problems (e.g. if resolve or deResolve returned null)
	 */
	public String getErrorMessage();

}
