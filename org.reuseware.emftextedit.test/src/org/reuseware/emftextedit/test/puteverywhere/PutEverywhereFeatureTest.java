package org.reuseware.emftextedit.test.puteverywhere;

import static org.junit.Assert.assertNotNull;
import static org.reuseware.emftextedit.test.ConcreteSyntaxTestHelper.generateANTLRGrammarToTempFile;
import static org.reuseware.emftextedit.test.ConcreteSyntaxTestHelper.registerResourceFactories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;
import org.reuseware.emftextedit.runtime.resource.TextResource;
import org.reuseware.emftextedit.sdk.GenPackageByNameFinder;
import org.reuseware.emftextedit.sdk.MetamodelHelper;

public class PutEverywhereFeatureTest {

	@Before
	public void setUp() {
		registerResourceFactories();
	}

	@Test
	public void testPutEverywhereSyntaxExtension() throws FileNotFoundException, IOException {
		String path = "src\\org\\reuseware\\emftextedit\\test\\puteverywhere\\glue.cs";
		String absolutePath = new File(path).getAbsolutePath();
		URI fileURI = URI.createFileURI(absolutePath);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put(MetamodelHelper.GEN_PACKAGE_FINDER_KEY, new GenPackageByNameFinder());
		options.put(TextResource.OPTION_NO_VALIDATE, Boolean.TRUE);

		File result = generateANTLRGrammarToTempFile(fileURI, options);
		assertNotNull(result);
	}
}
