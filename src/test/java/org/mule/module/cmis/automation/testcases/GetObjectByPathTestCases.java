package org.mule.module.cmis.automation.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.cmis.VersioningState;

public class GetObjectByPathTestCases extends CMISTestParent {
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("getObjectByPath");
			String rootFolderId = rootFolderId();
			
			ObjectId createFolderResult = createFolder((String) testObjects.get("folderName"), rootFolderId);
			testObjects.put("folderId", createFolderResult.getId());
			
			createDocumentById(createFolderResult.getId(), (String) testObjects.get("filename"), (String) testObjects.get("content"), (String) testObjects.get("mimeType"), 
					(VersioningState) testObjects.get("versioningState"), (String) testObjects.get("objectType"), (Map<String, Object>) testObjects.get("propertiesRef"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Category({RegressionTests.class})
	@Test
	public void testGetObjectByPath() {
		try {
			testObjects.put("path", "/" + (String) testObjects.get("folderName") + "/" + (String) testObjects.get("filename"));
			
			MessageProcessor flow = lookupFlowConstruct("get-object-by-path");
			MuleEvent response = flow.process(getTestEvent(testObjects));

			CmisObject cmisObj = (CmisObject) response.getMessage().getPayload();
			
			assertNotNull(cmisObj);
			assertEquals((String) testObjects.get("filename"), cmisObj.getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			String folderId = (String) testObjects.get("folderId");
			deleteTree(getObjectById(folderId), folderId, true, true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
