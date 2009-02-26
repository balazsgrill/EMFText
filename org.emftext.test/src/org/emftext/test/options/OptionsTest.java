package org.emftext.test.options;

import static org.emftext.test.ConcreteSyntaxTestHelper.generateANTLRGrammarToTempFile;
import static org.emftext.test.ConcreteSyntaxTestHelper.registerResourceFactories;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.emftext.sdk.MetamodelHelper;
import org.emftext.sdk.finders.GenPackageByNameFinder;
import org.junit.Before;
import org.junit.Test;

/**
 * A test for using the various code generation options provided
 * by EMFText.
 */
public class OptionsTest {
	
	@Before
	public void setUp() {
		registerResourceFactories();
	}

	@Test
	public void testOptions() throws FileNotFoundException, IOException {
		String path = "src\\org\\emftext\\test\\options\\options.cs";
		String absolutePath = new File(path).getAbsolutePath();
		URI fileURI = URI.createFileURI(absolutePath);
		
		Map<String, Object> options = new HashMap<String, Object>();
		options.put(MetamodelHelper.GEN_PACKAGE_FINDER_KEY, new GenPackageByNameFinder());

		File result = generateANTLRGrammarToTempFile(fileURI, options);
		assertNotNull(result);
	}
}
