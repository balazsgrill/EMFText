package org.emftext.sdk.concretesyntax.resource.cs.mopp;

// A CodeCompletionHelper can be used to derive completion proposals for partial
// documents. It run the parser generated by EMFText in a special mode (i.e., the
// rememberExpectedElements mode). Based on the elements that are expected by the
// parser for different regions in the document, valid proposals are computed.
public class CsCodeCompletionHelper {
	
	private final static org.emftext.sdk.concretesyntax.resource.cs.util.CsEClassUtil eClassUtil = new org.emftext.sdk.concretesyntax.resource.cs.util.CsEClassUtil();
	
	// Computes a set of proposals for the given document assuming the cursor is
	// at 'cursorOffset'. The proposals are derived using the meta information, i.e.,
	// the generated language plug-in.
	//
	// @param metaInformation
	// @param content the documents content
	// @param cursorOffset
	// @return
	public java.util.Collection<String> computeCompletionProposals(org.emftext.sdk.concretesyntax.resource.cs.mopp.CsMetaInformation metaInformation, String content, int cursorOffset) {
		java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(content.getBytes());
		org.emftext.sdk.concretesyntax.resource.cs.ICsTextParser parser = metaInformation.createParser(inputStream, null);
		final java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedElements = parseToExpectedElements(parser);
		if (expectedElements == null) {
			return java.util.Collections.emptyList();
		}
		if (expectedElements.size() == 0) {
			return java.util.Collections.emptyList();
		}
		java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedElementsAt = getExpectedElements(expectedElements, cursorOffset);
		setPrefix(expectedElementsAt, content, cursorOffset);
		// TODO this is done twice (was already calculated in getFinalExpectedElementAt())
		//IExpectedElement expectedAtCursor = getExpectedElementAt(offset, expectedElements);
		System.out.println(" PARSER returned expectation: " + expectedElementsAt + " for offset " + cursorOffset);
		java.util.Collection<String> proposals = deriveProposals(expectedElementsAt, content, metaInformation, cursorOffset);
		
		final java.util.List<String> sortedProposals = new java.util.ArrayList<String>(proposals);
		java.util.Collections.sort(sortedProposals);
		return sortedProposals;
	}
	
	public java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> parseToExpectedElements(org.emftext.sdk.concretesyntax.resource.cs.ICsTextParser parser) {
		final java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedElements = parser.parseToExpectedElements(null);
		if (expectedElements == null) {
			return java.util.Collections.emptyList();
		}
		removeDuplicateEntries(expectedElements);
		removeInvalidEntriesAtEnd(expectedElements);
		for (org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement expectedElement : expectedElements) {
			System.out.println("PARSER EXPECTS:   " + expectedElement);
		}
		return expectedElements;
	}
	
	private void removeDuplicateEntries(java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedElements) {
		for (int i = 0; i < expectedElements.size() - 1; i++) {
			org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement elementAtIndex = expectedElements.get(i);
			for (int j = i + 1; j < expectedElements.size();) {
				org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement elementAtNext = expectedElements.get(j);
				if (elementAtIndex.equals(elementAtNext) &&				elementAtIndex.getStartExcludingHiddenTokens() == elementAtNext.getStartExcludingHiddenTokens()) {
					expectedElements.remove(j);
				} else {
					j++;
				}
			}
		}
	}
	
	private void removeInvalidEntriesAtEnd(java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedElements) {
		for (int i = 0; i < expectedElements.size() - 1;) {
			org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement elementAtIndex = expectedElements.get(i);
			org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement elementAtNext = expectedElements.get(i + 1);
			if (elementAtIndex.getStartExcludingHiddenTokens() == elementAtNext.getStartExcludingHiddenTokens() &&			//elementAtIndex.discardFollowingExpectations() &&
			// TODO mseifert: this is wrong. we must compare the scopeIDs based on their parts!
			shouldRemove(elementAtIndex.getScopeID(), elementAtNext.getScopeID())) {
				expectedElements.remove(i + 1);
			} else {
				i++;
			}
		}
	}
	
	private boolean shouldRemove(String scopeID1, String scopeID2) {
		String[] parts1 = scopeID1.split("\\.");
		String[] parts2 = scopeID2.split("\\.");
		for (int p1 = 0; p1 < parts1.length; p1++) {
			String segment1 = parts1[p1];
			if (p1 >= parts2.length) {
				return true;
			}
			String segment2 = parts2[p1];
			int compareTo = segment1.compareTo(segment2);
			if (compareTo == 0) {
				continue;
			}
		}
		return false;
	}
	
	private String findPrefix(java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedElements, org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement expectedAtCursor, String content, int cursorOffset) {
		if (cursorOffset < 0) {
			return "";
		}
		int end = 0;
		for (org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement expectedElement : expectedElements) {
			if (expectedElement == expectedAtCursor) {
				final int start = expectedElement.getStartExcludingHiddenTokens();
				if (start >= 0  && start < Integer.MAX_VALUE) {
					end = start;
				}
				break;
			}
		}
		end = Math.min(end, cursorOffset);
		//System.out.println("substring("+end+","+offset+")");
		final String prefix = content.substring(end, Math.min(content.length(), cursorOffset + 1));
		System.out.println("Found prefix '" + prefix + "'");
		return prefix;
	}
	
	private java.util.Collection<String> deriveProposals(	java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedElements, String content, org.emftext.sdk.concretesyntax.resource.cs.mopp.CsMetaInformation metaInformation, int cursorOffset) {
		java.util.Collection<String> resultSet = new java.util.HashSet<String>();
		for (org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement expectedElement : expectedElements) {
			resultSet.addAll(deriveProposals(expectedElement, content, metaInformation, cursorOffset));
		}
		return resultSet;
	}
	
	private java.util.Collection<String> deriveProposals(	org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement expectedElement, String content, org.emftext.sdk.concretesyntax.resource.cs.mopp.CsMetaInformation metaInformation, int cursorOffset) {
		if (expectedElement instanceof org.emftext.sdk.concretesyntax.resource.cs.mopp.CsExpectedCsString) {
			org.emftext.sdk.concretesyntax.resource.cs.mopp.CsExpectedCsString csString = (org.emftext.sdk.concretesyntax.resource.cs.mopp.CsExpectedCsString) expectedElement;
			return deriveProposal(csString, content, cursorOffset);
		} else if (expectedElement instanceof org.emftext.sdk.concretesyntax.resource.cs.mopp.CsExpectedStructuralFeature) {
			org.emftext.sdk.concretesyntax.resource.cs.mopp.CsExpectedStructuralFeature expectedFeature = (org.emftext.sdk.concretesyntax.resource.cs.mopp.CsExpectedStructuralFeature) expectedElement;
			org.eclipse.emf.ecore.EStructuralFeature feature = expectedFeature.getFeature();
			org.eclipse.emf.ecore.EClassifier featureType = feature.getEType();
			org.eclipse.emf.ecore.EObject container = expectedFeature.getContainer();
			if (feature instanceof org.eclipse.emf.ecore.EReference) {
				org.eclipse.emf.ecore.EReference reference = (org.eclipse.emf.ecore.EReference) feature;
				if (featureType instanceof org.eclipse.emf.ecore.EClass) {
					if (reference.isContainment()) {
						org.eclipse.emf.ecore.EClass classType = (org.eclipse.emf.ecore.EClass) featureType;
						return deriveProposals(classType, metaInformation, content, cursorOffset);
					} else {
						return handleNCReference(content, metaInformation, cursorOffset, container);
					}
				}
			} else if (feature instanceof org.eclipse.emf.ecore.EAttribute) {
				org.eclipse.emf.ecore.EAttribute attribute = (org.eclipse.emf.ecore.EAttribute) feature;
				if (featureType instanceof org.eclipse.emf.ecore.EEnum) {
					org.eclipse.emf.ecore.EEnum enumType = (org.eclipse.emf.ecore.EEnum) featureType;
					return deriveProposals(expectedElement, enumType, content, cursorOffset);
				} else {
					// handle EAttributes (derive default value depending on
					// the type of the attribute, figure out token resolver, and
					// call deResolve())
					return handleAttribute(metaInformation, expectedFeature, container, attribute);
				}
			} else {
				// there should be no other subclass of EStructuralFeature
				assert false;
			}
		} else {
			// there should be no other class implementing IExpectedElement
			assert false;
		}
		return java.util.Collections.emptyList();
	}
	
	private java.util.Collection<String> handleNCReference(String content, org.emftext.sdk.concretesyntax.resource.cs.mopp.CsMetaInformation metaInformation, int cursorOffset, org.eclipse.emf.ecore.EObject container) {
		// handle non-containment references
		org.emftext.sdk.concretesyntax.resource.cs.ICsReferenceResolverSwitch resolverSwitch = metaInformation.getReferenceResolverSwitch();
		org.emftext.sdk.concretesyntax.resource.cs.ICsReferenceResolveResult<org.eclipse.emf.ecore.EObject> result = new org.emftext.sdk.concretesyntax.resource.cs.mopp.CsReferenceResolveResult<org.eclipse.emf.ecore.EObject>(true);
		resolverSwitch.resolveFuzzy("", container, 0, result);
		java.util.Collection<org.emftext.sdk.concretesyntax.resource.cs.ICsReferenceMapping<org.eclipse.emf.ecore.EObject>> mappings = result.getMappings();
		if (mappings != null) {
			java.util.Collection<String> resultSet = new java.util.HashSet<String>();
			for (org.emftext.sdk.concretesyntax.resource.cs.ICsReferenceMapping<org.eclipse.emf.ecore.EObject> mapping : mappings) {
				final String identifier = mapping.getIdentifier();
				System.out.println("deriveProposals() " + identifier);
				resultSet.add(identifier);
			}
			return resultSet;
		}
		return java.util.Collections.emptyList();
	}
	
	private java.util.Collection<String> handleAttribute(org.emftext.sdk.concretesyntax.resource.cs.mopp.CsMetaInformation metaInformation, org.emftext.sdk.concretesyntax.resource.cs.mopp.CsExpectedStructuralFeature expectedFeature, org.eclipse.emf.ecore.EObject container,
	org.eclipse.emf.ecore.EAttribute attribute) {
		java.lang.Object defaultValue = getDefaultValue(attribute);
		if (defaultValue != null) {
			org.emftext.sdk.concretesyntax.resource.cs.ICsTokenResolverFactory tokenResolverFactory = metaInformation.getTokenResolverFactory();
			String tokenName = expectedFeature.getTokenName();
			if (tokenName != null) {
				org.emftext.sdk.concretesyntax.resource.cs.ICsTokenResolver tokenResolver = tokenResolverFactory.createTokenResolver(tokenName);
				if (tokenResolver != null) {
					String defaultValueAsString = tokenResolver.deResolve(defaultValue, attribute, container);
					java.util.Collection<String> resultSet = new java.util.HashSet<String>();
					resultSet.add(defaultValueAsString);
					return resultSet;
				}
			}
		}
		return java.util.Collections.emptyList();
	}
	
	private java.lang.Object getDefaultValue(org.eclipse.emf.ecore.EAttribute attribute) {
		String typeName = attribute.getEType().getName();
		if ("EString".equals(typeName)) {
			return "some" + org.emftext.sdk.concretesyntax.resource.cs.util.CsStringUtil.capitalize(attribute.getName());
		}
		// TODO mseifert: add more default values for other types
		System.out.println("CodeCompletionHelper.getDefaultValue() unknown type " + typeName);
		return attribute.getDefaultValue();
	}
	
	private java.util.Collection<String> deriveProposals(	org.eclipse.emf.ecore.EClass type,
	org.emftext.sdk.concretesyntax.resource.cs.mopp.CsMetaInformation metaInformation,
	String content, int cursorOffset) {
		java.util.Collection<String> allProposals = new java.util.HashSet<String>();
		// find all sub classes and call parseToExpectedElements() for each
		// of them
		org.eclipse.emf.ecore.EClass[] availableClasses = metaInformation.getClassesWithSyntax();
		java.util.Collection<org.eclipse.emf.ecore.EClass> allSubClasses = eClassUtil.getSubClasses(type, availableClasses);
		for (org.eclipse.emf.ecore.EClass subClass : allSubClasses) {
			org.emftext.sdk.concretesyntax.resource.cs.ICsTextParser parser = metaInformation.createParser(new java.io.ByteArrayInputStream(new byte[0]), null);
			final java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedElements = parser.parseToExpectedElements(subClass);
			if (expectedElements == null) {
				continue;
			}
			if (expectedElements.size() == 0) {
				continue;
			}
			java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedElementsAt = getExpectedElements(expectedElements, 0);
			setPrefix(expectedElementsAt, content, 0);
			System.out.println("computeCompletionProposals() " + expectedElementsAt + " for offset " + cursorOffset);
			java.util.Collection<String> proposals = deriveProposals(expectedElementsAt, content, metaInformation, cursorOffset);
			allProposals.addAll(proposals);
		}
		return allProposals;
	}
	
	private java.util.Collection<String> deriveProposals(org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement expectedElement, org.eclipse.emf.ecore.EEnum enumType, String content, int cursorOffset) {
		java.util.Collection<org.eclipse.emf.ecore.EEnumLiteral> enumLiterals = enumType.getELiterals();
		java.util.Collection<String> result = new java.util.HashSet<String>();
		for (org.eclipse.emf.ecore.EEnumLiteral literal : enumLiterals) {
			String proposal = literal.getLiteral();
			if (proposal.startsWith(expectedElement.getPrefix())) {
				result.add(proposal);
			}
		}
		return result;
	}
	
	private java.util.Collection<String> deriveProposal(org.emftext.sdk.concretesyntax.resource.cs.mopp.CsExpectedCsString csString, String content, int cursorOffset) {
		String proposal = csString.getValue();
		
		java.util.Collection<String> result = new java.util.HashSet<String>(1);
		result.add(proposal);
		return result;
	}
	
	// Returns the element(s) that is most suitable at the given cursor
	// index based on the list of expected elements.
	//
	// @param cursorOffset
	// @param allExpectedElements
	// @return
	// TODO mseifert: figure out what other combinations of elements before
	// and after the cursor position exist and which action should be taken.
	// For example, when a StructuralFeature is expected right before the
	// cursor and a CsString right after, we should return both elements.
	public java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> getExpectedElements(final java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> allExpectedElements,
	int cursorOffset) {
		
		java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedAfterCursor = getElementsExpectedAt(allExpectedElements, cursorOffset);
		java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedBeforeCursor = getElementsExpectedAt(allExpectedElements, cursorOffset - 1);
		System.out.println("parseToCursor(" + cursorOffset + ") BEFORE CURSOR " + expectedBeforeCursor);
		System.out.println("parseToCursor(" + cursorOffset + ") AFTER CURSOR  " + expectedAfterCursor);
		java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> allExpectedAtCursor = new java.util.ArrayList<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement>();
		allExpectedAtCursor.addAll(expectedAfterCursor);
		if (expectedBeforeCursor != null) {
			for (org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement expectedBefore : expectedBeforeCursor) {
				// if the thing right before the cursor is something that could
				// be long we add it to the list of proposals
				if (expectedBefore instanceof org.emftext.sdk.concretesyntax.resource.cs.mopp.CsExpectedStructuralFeature) {
					//allExpectedAtCursor.clear();
					allExpectedAtCursor.add(expectedBefore);
				}
			}
		}
		return allExpectedAtCursor;
	}
	
	private void setPrefix(java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> allExpectedElements, String content, int cursorOffset) {
		if (cursorOffset < 0) {
			return;
		}
		for (org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement expectedElementAtCursor : allExpectedElements) {
			expectedElementAtCursor.setPrefix(findPrefix(allExpectedElements, expectedElementAtCursor, content, cursorOffset));
		}
	}
	
	public java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> getElementsExpectedAt(java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> allExpectedElements, int cursorOffset) {
		java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> expectedAtCursor = new java.util.ArrayList<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement>();
		for (int i = 0; i < allExpectedElements.size(); i++) {
			org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement expectedElement = allExpectedElements.get(i);
			
			int startIncludingHidden = expectedElement.getStartIncludingHiddenTokens();
			//int startExcludingHidden = expectedElement.getStartExcludingHiddenTokens();
			int end = getEnd(allExpectedElements, i);
			//System.out.println("END = " + end + " for " + expectedElement);
			if (cursorOffset >= startIncludingHidden &&			cursorOffset <= end) {
				expectedAtCursor.add(expectedElement);
			}
		}
		return expectedAtCursor;
	}
	
	private int getEnd(java.util.List<org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement> allExpectedElements, int indexInList) {
		org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement elementAtIndex = allExpectedElements.get(indexInList);
		int startIncludingHidden = elementAtIndex.getStartIncludingHiddenTokens();
		int startExcludingHidden = elementAtIndex.getStartExcludingHiddenTokens();
		
		for (int i = indexInList + 1; i < allExpectedElements.size(); i++) {
			org.emftext.sdk.concretesyntax.resource.cs.ICsExpectedElement elementAtI = allExpectedElements.get(i);
			int startIncludingHiddenForI = elementAtI.getStartIncludingHiddenTokens();
			int startExcludingHiddenForI = elementAtI.getStartExcludingHiddenTokens();
			if (startIncludingHidden != startIncludingHiddenForI || startExcludingHidden != startExcludingHiddenForI) {
				return startIncludingHiddenForI - 1;
			}
		}
		return Integer.MAX_VALUE;
	}
}