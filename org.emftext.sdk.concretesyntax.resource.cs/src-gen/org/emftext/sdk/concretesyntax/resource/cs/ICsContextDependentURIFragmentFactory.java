package org.emftext.sdk.concretesyntax.resource.cs;

// An interface for factories to create instances of IContextDependentURIFragment.
//
// @param <ContainerType> the type of the class containing the reference to be resolved
// @param <ReferenceType> the type of the reference to be resolved
//
// TODO jjohannes: figure out whether this interface must really be exposed
public interface ICsContextDependentURIFragmentFactory<ContainerType extends org.eclipse.emf.ecore.EObject, ReferenceType extends org.eclipse.emf.ecore.EObject> {
	
	// Create a new instance of the IContextDependentURIFragment interface.
	//
	// @param identifier the identifier that references an org.eclipse.emf.ecore.EObject
	// @param container the object that contains the reference
	// @param reference the reference itself
	// @param positionInReference the position of the identifier (if the reference is multiple)
	// @param proxy the proxy that will be resolved later to the actual org.eclipse.emf.ecore.EObject
	// @return
	public org.emftext.sdk.concretesyntax.resource.cs.ICsContextDependentURIFragment<?> create(String identifier, ContainerType container, org.eclipse.emf.ecore.EReference reference, int positionInReference, org.eclipse.emf.ecore.EObject proxy);
}