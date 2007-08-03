/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service;

import static org.kuali.kfs.util.SpringServiceLocator.getProcurementCardCreateDocumentService;
import static org.kuali.test.fixtures.UserNameFixture.KULUSER;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.test.RequiresSpringContext;
/**
 * This class tests the services used to create ProcurementCard documents.
 * 
 * 
 */
@RequiresSpringContext(session = KULUSER)
public class ProcurementCardDocumentServiceTest extends KualiTestBase {

    public void testCreatePCardDocuments() throws Exception {
        boolean documentsCreated = getProcurementCardCreateDocumentService().createProcurementCardDocuments();
        assertTrue("problem creating documents",documentsCreated);
    }

     public void testRoutePCardDocuments() throws Exception {
        boolean routeSuccessful = getProcurementCardCreateDocumentService().routeProcurementCardDocuments();
    }

}