/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.web.struts.ReferralToCollectionsLookupForm;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the Referral To Collections lookup.
 */
@ConfigureContext(session = khuntley)
public class ReferralToCollectionsLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReferralToCollectionsLookupableHelperServiceImplTest.class);

    private ReferralToCollectionsLookupableHelperServiceImpl referralToCollectionsLookupableHelperServiceImpl;
    private ReferralToCollectionsLookupForm referralToCollectionsLookupForm;
    private Map fieldValues;

    private static final String AGENCY_NUMBER = "12631";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        referralToCollectionsLookupableHelperServiceImpl = new ReferralToCollectionsLookupableHelperServiceImpl();
        referralToCollectionsLookupableHelperServiceImpl.setContractsGrantsInvoiceDocumentService(SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class));
        referralToCollectionsLookupableHelperServiceImpl.setBusinessObjectClass(ReferralToCollectionsLookupResult.class);

        referralToCollectionsLookupForm = new ReferralToCollectionsLookupForm();
        fieldValues = new LinkedHashMap();
        fieldValues.put("agencyNumber", AGENCY_NUMBER);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        referralToCollectionsLookupableHelperServiceImpl = null;
        referralToCollectionsLookupForm = null;
        fieldValues = null;
    }

    /**
     * This method tests the performLookup method of ReferralToCollectionsLookupableHelperServiceImpl.
     */
    public void testPerformLookup() {
        Collection resultTable = new ArrayList<String>();
        referralToCollectionsLookupForm.setFieldsForLookup(fieldValues);

        assertTrue(referralToCollectionsLookupableHelperServiceImpl.performLookup(referralToCollectionsLookupForm, resultTable, true).size() > 0);
    }
}
