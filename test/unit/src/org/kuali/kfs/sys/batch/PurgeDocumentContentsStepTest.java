/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.batch;

import static org.kuali.test.fixtures.UserNameFixture.KULUSER;

import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.test.ConfigureContext;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class tests the class {@link PurgeDocumentContentsStep}
 */
@ConfigureContext(session = KULUSER)
public class PurgeDocumentContentsStepTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeDocumentContentsStepTest.class);

    private static final String INTERNAL_BILLING_DOCUMENT_TYPE_NAME = "InternalBillingDocument";
    private static final String MAINTENANCE_DOCUMENT_TYPENAME = "AccountMaintenanceDocument";

    /**
     * Constructs a PurgeDocumentContentStepTest.java.
     */
    public PurgeDocumentContentsStepTest() {
        super();
    }

    public final void testSetDocumentContent() throws Exception {
        PurgeDocumentContentsStep purgeDocumentContentsStep = SpringContext.getBean(PurgeDocumentContentsStep.class);
        Document document = SpringContext.getBean(DocumentService.class).getNewDocument(INTERNAL_BILLING_DOCUMENT_TYPE_NAME);
        try {
            purgeDocumentContentsStep.setFinalDocumentDocumentContent(document.getDocumentHeader());
        }
        catch (WorkflowException e) {
            LOG.error("Caught WorkflowException", e);
            fail();
        }
    }

}
