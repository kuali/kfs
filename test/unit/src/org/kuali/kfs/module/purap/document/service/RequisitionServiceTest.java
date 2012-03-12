/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.DocumentService;

/**
 * Includes tests of the methods in RequisitionService.  For tests of isAutomaticPurchaseOrderAllowed,
 * and by implication, checkAutomaticPurchaseOrderRules and checkAPORulesPerItemForCommodityCodes that
 * are negative, (that is, that test what these methods do under conditions which should prevent the
 * requisition from becoming an APO and generate one of a large number of potential error messages),
 * see the class NegativeAPOTest.
 * 
 * @see org.kuali.kfs.module.purap.document.NegativeAPOTest.
 */
@ConfigureContext(session = parke)
public class RequisitionServiceTest extends KualiTestBase {    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionServiceTest.class);

    private DocumentService docService;
    private RequisitionService reqService;

    @Override
    protected void setUp() throws Exception {      
        super.setUp();
        if (null == reqService) {
            reqService = SpringContext.getBean(RequisitionService.class);
        }
    }

    // Tests of isAutomaticPurchaseOrderAllowed
    public void testIsAutomaticPurchaseOrderAllowed_Valid() {
        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        assertTrue(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));
    }
    
    public void testIsAutomaticPurchaseOrderAllowed_Alternative() {
        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ALTERNATE_APO.createRequisitionDocument();
        assertTrue(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));
    }
}
