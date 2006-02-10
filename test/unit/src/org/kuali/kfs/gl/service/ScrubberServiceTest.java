package org.kuali.module.gl.service;

import java.util.Vector;

import org.kuali.core.batch.CommandLineStepRunner;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.FullStackSuite;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.test.KualiTestBase;

public class ScrubberServiceTest extends KualiTestBase {
	private OriginEntryGroupService groupService;
	private OriginEntryService originEntryService;

	protected void setUp() throws Exception {
		super.setUp();
		
		groupService = 
			(OriginEntryGroupService)
				SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
		originEntryService =
			(OriginEntryService)
				SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
		
		// Be sure the suite is initialized. It retrieves the set of
		// origin entry groups that will be processed during the 
		// scrubber. We need to know this so that we can properly
		// validate the actions completed by the scrubber.
		Class.forName("org.kuali.module.gl.FullStackSuite");
		
		// Invoke the Scrubber.
		CommandLineStepRunner.main(new String[] {"glScrubberStep"});
	}

	public void testValidation() {
		Vector inputGroupIds = FullStackSuite.getInputGroupIds();
		
		// for each origin entry group processed by the most recent scrubber run ...
		for(int i = 0; i < inputGroupIds.size(); i++) {

			// ... load the group ...
			OriginEntryGroup group = 
				groupService.getOriginEntryGroup(inputGroupIds.get(i).toString());
			
			// ... load all entries in the group ...
			originEntryService.getEntriesByGroup(group);
		}
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
