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

package org.emftext.sdk.concretesyntax.resource.cs.util;

/**
 * A utility class that bundles all dependencies to the Eclipse platform. Clients
 * of this class must check whether the Eclipse bundles are available in the
 * classpath. If they are not available, this class is not used, which allows to
 * run resource plug-in that are generated by EMFText in stand-alone mode. In this
 * case the EMF JARs are sufficient to parse and print resources.
 */
public class CsEclipseProxy {
	
	/**
	 * Adds all registered load option provider extension to the given map. Load
	 * option providers can be used to set default options for loading resources (e.g.
	 * input stream pre-processors).
	 */
	public void getDefaultLoadOptionProviderExtensions(java.util.Map<Object, Object> optionsMap) {
		if (org.eclipse.core.runtime.Platform.isRunning()) {
			// find default load option providers
			org.eclipse.core.runtime.IExtensionRegistry extensionRegistry = org.eclipse.core.runtime.Platform.getExtensionRegistry();
			org.eclipse.core.runtime.IConfigurationElement configurationElements[] = extensionRegistry.getConfigurationElementsFor(org.emftext.sdk.concretesyntax.resource.cs.mopp.CsPlugin.EP_DEFAULT_LOAD_OPTIONS_ID);
			for (org.eclipse.core.runtime.IConfigurationElement element : configurationElements) {
				try {
					org.emftext.sdk.concretesyntax.resource.cs.ICsOptionProvider provider = (org.emftext.sdk.concretesyntax.resource.cs.ICsOptionProvider) element.createExecutableExtension("class");
					final java.util.Map<?, ?> options = provider.getOptions();
					final java.util.Collection<?> keys = options.keySet();
					for (Object key : keys) {
						org.emftext.sdk.concretesyntax.resource.cs.util.CsMapUtil.putAndMergeKeys(optionsMap, key, options.get(key));
					}
				} catch (org.eclipse.core.runtime.CoreException ce) {
					new org.emftext.sdk.concretesyntax.resource.cs.util.CsRuntimeUtil().logError("Exception while getting default options.", ce);
				}
			}
		}
	}
	
	/**
	 * Adds all registered resource factory extensions to the given map. Such
	 * extensions can be used to register multiple resource factories for the same
	 * file extension.
	 */
	public void getResourceFactoryExtensions(java.util.Map<String, org.eclipse.emf.ecore.resource.Resource.Factory> factories) {
		if (org.eclipse.core.runtime.Platform.isRunning()) {
			org.eclipse.core.runtime.IExtensionRegistry extensionRegistry = org.eclipse.core.runtime.Platform.getExtensionRegistry();
			org.eclipse.core.runtime.IConfigurationElement configurationElements[] = extensionRegistry.getConfigurationElementsFor(org.emftext.sdk.concretesyntax.resource.cs.mopp.CsPlugin.EP_ADDITIONAL_EXTENSION_PARSER_ID);
			for (org.eclipse.core.runtime.IConfigurationElement element : configurationElements) {
				try {
					String type = element.getAttribute("type");
					org.eclipse.emf.ecore.resource.Resource.Factory factory = (org.eclipse.emf.ecore.resource.Resource.Factory) element.createExecutableExtension("class");
					if (type == null) {
						type = "";
					}
					org.eclipse.emf.ecore.resource.Resource.Factory otherFactory = factories.get(type);
					if (otherFactory != null) {
						Class<?> superClass = factory.getClass().getSuperclass();
						while(superClass != Object.class) {
							if (superClass.equals(otherFactory.getClass())) {
								factories.put(type, factory);
								break;
							}
							superClass = superClass.getClass();
						}
					}
					else {
						factories.put(type, factory);
					}
				} catch (org.eclipse.core.runtime.CoreException ce) {
					new org.emftext.sdk.concretesyntax.resource.cs.util.CsRuntimeUtil().logError("Exception while getting default options.", ce);
				}
			}
		}
	}
	
	/**
	 * Gets the resource that is contained in the give file.
	 */
	public org.emftext.sdk.concretesyntax.resource.cs.mopp.CsResource getResource(org.eclipse.core.resources.IFile file) {
		org.eclipse.emf.ecore.resource.ResourceSet rs = new org.eclipse.emf.ecore.resource.impl.ResourceSetImpl();
		org.eclipse.emf.ecore.resource.Resource resource = rs.getResource(org.eclipse.emf.common.util.URI.createPlatformResourceURI(file.getFullPath().toString(),true), true);
		return (org.emftext.sdk.concretesyntax.resource.cs.mopp.CsResource) resource;
	}
	
	/**
	 * Returns the file that contains the given resource.
	 */
	public org.eclipse.core.resources.IFile getFileForResource(org.eclipse.emf.ecore.resource.Resource resource) {
		return getFileForURI(resource.getURI());
	}
	
	/**
	 * Returns the file that corresponds to the given URI.
	 */
	public org.eclipse.core.resources.IFile getFileForURI(org.eclipse.emf.common.util.URI uri) {
		org.eclipse.core.resources.IWorkspace workspace = org.eclipse.core.resources.ResourcesPlugin.getWorkspace();
		org.eclipse.core.resources.IWorkspaceRoot workspaceRoot = workspace.getRoot();
		String platformString = uri.toPlatformString(true);
		// If the URI is not a platform URI, we cannot determine the file.
		if (platformString == null) {
			return null;
		}
		org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path(platformString);
		return workspaceRoot.getFile(path);
	}
	
	/**
	 * Checks all registered EMF validation constraints. Note: EMF validation does not
	 * work if OSGi is not running.
	 */
	@SuppressWarnings("restriction")	
	public void checkEMFValidationConstraints(org.emftext.sdk.concretesyntax.resource.cs.ICsTextResource resource, org.eclipse.emf.ecore.EObject root, boolean includeBatchConstraints) {
		// The EMF validation framework code throws a NPE if the validation plug-in is not
		// loaded. This is a bug, which is fixed in the Helios release. Nonetheless, we
		// need to catch the exception here.
		org.emftext.sdk.concretesyntax.resource.cs.util.CsRuntimeUtil runtimeUtil = new org.emftext.sdk.concretesyntax.resource.cs.util.CsRuntimeUtil();
		if (runtimeUtil.isEclipsePlatformRunning() && runtimeUtil.isEMFValidationAvailable()) {
			// The EMF validation framework code throws a NPE if the validation plug-in is not
			// loaded. This is a workaround for bug 322079.
			if (org.eclipse.emf.validation.internal.EMFModelValidationPlugin.getPlugin() != null) {
				try {
					org.eclipse.emf.validation.service.ModelValidationService service = org.eclipse.emf.validation.service.ModelValidationService.getInstance();
					org.eclipse.core.runtime.IStatus status;
					// Batch constraints are only evaluated if requested (e.g., when a resource is
					// loaded for the first time).
					if (includeBatchConstraints) {
						org.eclipse.emf.validation.service.IBatchValidator validator = service.<org.eclipse.emf.ecore.EObject, org.eclipse.emf.validation.service.IBatchValidator>newValidator(org.eclipse.emf.validation.model.EvaluationMode.BATCH);
						validator.setIncludeLiveConstraints(false);
						status = validator.validate(root);
						addStatus(status, resource, root, org.emftext.sdk.concretesyntax.resource.cs.CsEProblemType.BATCH_CONSTRAINT_PROBLEM);
					}
					// Live constraints are always evaluated
					org.eclipse.emf.validation.service.ILiveValidator validator = service.<org.eclipse.emf.common.notify.Notification, org.eclipse.emf.validation.service.ILiveValidator>newValidator(org.eclipse.emf.validation.model.EvaluationMode.LIVE);
					java.util.Collection<org.eclipse.emf.common.notify.Notification> notifications = createNotifications(root);
					status = validator.validate(notifications);
					addStatus(status, resource, root, org.emftext.sdk.concretesyntax.resource.cs.CsEProblemType.LIVE_CONSTRAINT_PROBLEM);
				} catch (Throwable t) {
					new org.emftext.sdk.concretesyntax.resource.cs.util.CsRuntimeUtil().logError("Exception while checking contraints provided by EMF validator classes.", t);
				}
			}
		}
	}
	
	private java.util.Collection<org.eclipse.emf.common.notify.Notification> createNotifications(org.eclipse.emf.ecore.EObject eObject) {
		java.util.List<org.eclipse.emf.common.notify.Notification> notifications = new java.util.ArrayList<org.eclipse.emf.common.notify.Notification>();
		createNotification(eObject, notifications);
		java.util.Iterator<org.eclipse.emf.ecore.EObject> allContents = eObject.eAllContents();
		while (allContents.hasNext()) {
			org.eclipse.emf.ecore.EObject next = (org.eclipse.emf.ecore.EObject) allContents.next();
			createNotification(next, notifications);
		}
		return notifications;
	}
	
	private void createNotification(org.eclipse.emf.ecore.EObject eObject, java.util.List<org.eclipse.emf.common.notify.Notification> notifications) {
		if (eObject instanceof org.eclipse.emf.ecore.InternalEObject) {
			org.eclipse.emf.ecore.InternalEObject internalEObject = (org.eclipse.emf.ecore.InternalEObject) eObject;
			org.eclipse.emf.common.notify.Notification notification = new org.eclipse.emf.ecore.impl.ENotificationImpl(internalEObject, 0, org.eclipse.emf.ecore.impl.ENotificationImpl.NO_FEATURE_ID, null, null);
			notifications.add(notification);
		}
	}
	
	public void addStatus(org.eclipse.core.runtime.IStatus status, org.emftext.sdk.concretesyntax.resource.cs.ICsTextResource resource, org.eclipse.emf.ecore.EObject root, org.emftext.sdk.concretesyntax.resource.cs.CsEProblemType problemType) {
		java.util.List<org.eclipse.emf.ecore.EObject> causes = new java.util.ArrayList<org.eclipse.emf.ecore.EObject>();
		causes.add(root);
		if (status instanceof org.eclipse.emf.validation.model.ConstraintStatus) {
			org.eclipse.emf.validation.model.ConstraintStatus constraintStatus = (org.eclipse.emf.validation.model.ConstraintStatus) status;
			java.util.Set<org.eclipse.emf.ecore.EObject> resultLocus = constraintStatus.getResultLocus();
			causes.clear();
			causes.addAll(resultLocus);
		}
		org.eclipse.core.runtime.IStatus[] children = status.getChildren();
		boolean hasChildren = children != null && children.length > 0;
		// Ignore composite status objects that have children. The actual status
		// information is then contained in the child objects.
		if (!status.isMultiStatus() || !hasChildren) {
			int severity = status.getSeverity();
			if (severity == org.eclipse.core.runtime.IStatus.ERROR) {
				for (org.eclipse.emf.ecore.EObject cause : causes) {
					resource.addError(status.getMessage(), problemType, cause);
				}
			}
			if (severity == org.eclipse.core.runtime.IStatus.WARNING) {
				for (org.eclipse.emf.ecore.EObject cause : causes) {
					resource.addWarning(status.getMessage(), problemType, cause);
				}
			}
		}
		if (children != null) {
			for (org.eclipse.core.runtime.IStatus child : children) {
				addStatus(child, resource, root, problemType);
			}
		}
	}
	
	/**
	 * Returns the encoding for this resource that is specified in the workspace file
	 * properties or determined by the default workspace encoding in Eclipse.
	 */
	public String getPlatformResourceEncoding(org.eclipse.emf.common.util.URI uri) {
		// We can't determine the encoding if the platform is not running.
		if (!new org.emftext.sdk.concretesyntax.resource.cs.util.CsRuntimeUtil().isEclipsePlatformRunning()) {
			return null;
		}
		if (uri != null && uri.isPlatform()) {
			String platformString = uri.toPlatformString(true);
			org.eclipse.core.resources.IResource platformResource = org.eclipse.core.resources.ResourcesPlugin.getWorkspace().getRoot().findMember(platformString);
			if (platformResource instanceof org.eclipse.core.resources.IFile) {
				org.eclipse.core.resources.IFile file = (org.eclipse.core.resources.IFile) platformResource;
				try {
					return file.getCharset();
				} catch (org.eclipse.core.runtime.CoreException ce) {
					new org.emftext.sdk.concretesyntax.resource.cs.util.CsRuntimeUtil().logWarning("Could not determine encoding of platform resource: " + uri.toString(), ce);
				}
			}
		}
		return null;
	}
	
}
