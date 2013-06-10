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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.lookup.ReferralToCollectionsDocumentUtil;
import org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.module.ar.document.service.impl.ContractsGrantsInvoiceDocumentServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the methods of ReferralToCollectionsDocumentUtil
 */
@ConfigureContext(session = khuntley)
public class ReferralToCollectionsDocumentUtilTest extends KualiTestBase {

    private ContractsGrantsInvoiceDocumentServiceImpl contractsGrantsInvoiceDocumentServiceImpl;
    private ReferralToCollectionsDocument referralToCollectionsDocument;

    private static final String CUSTOMER_NUMBER = "ABB2";
    private static final String LOOKUP_SEQUENCE_NUMBER = "3095";
    private static final String PERSON_ID = "6162502038";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contractsGrantsInvoiceDocumentServiceImpl = new ContractsGrantsInvoiceDocumentServiceImpl();
        contractsGrantsInvoiceDocumentServiceImpl.setContractsGrantsInvoiceDocumentDao(SpringContext.getBean(ContractsGrantsInvoiceDocumentDao.class));
        referralToCollectionsDocument = new ReferralToCollectionsDocument();
    }

    /**
     * This method tests getCGInvoiceDocumentsFromLookupResultsSequenceNumber() method of ReferralToCollectionsDocumentUtil.
     */
    public void testGetCGInvoiceDocumentsFromLookupResultsSequenceNumber() {
        List<ContractsGrantsInvoiceDocument> invoices = new ArrayList<ContractsGrantsInvoiceDocument>(ReferralToCollectionsDocumentUtil.getCGInvoiceDocumentsFromLookupResultsSequenceNumber(LOOKUP_SEQUENCE_NUMBER, PERSON_ID));
        assertTrue(invoices.size() > 0);
    }

    /**
     * This method tests getInvoicesByAward() method of ReferralToCollectionsDocumentUtil.
     */
    public void testGetInvoicesByAward() {
        List<ContractsGrantsInvoiceDocument> invoices = new ArrayList<ContractsGrantsInvoiceDocument>(contractsGrantsInvoiceDocumentServiceImpl.retrieveOpenAndFinalCGInvoicesByCustomerNumber(CUSTOMER_NUMBER, "ROCTestError.txt"));
        assertNotNull(invoices);

        Map<Long, List<ContractsGrantsInvoiceDocument>> resultMap = ReferralToCollectionsDocumentUtil.getInvoicesByAward(invoices);
        int subListSize = 0;
        for (Long key : resultMap.keySet()) {
            subListSize += resultMap.get(key).size();
        }

        assertEquals(invoices.size(), subListSize);
    }

    /**
     * This method tests setReferralToCollectionsDocumentDetailsFromLookupResultsSequenceNumber() method of
     * ReferralToCollectionsDocumentUtil.
     */
    public void testSetReferralToCollectionsDocumentDetailsFromLookupResultsSequenceNumber() {
        ReferralToCollectionsDocumentUtil.setReferralToCollectionsDetailsFromLookupResultsSequenceNumber(referralToCollectionsDocument, LOOKUP_SEQUENCE_NUMBER, PERSON_ID);
        assertTrue(referralToCollectionsDocument.getReferralToCollectionsDetails().size() > 0);
    }

    /**
     * This method tests getPopulatedReferralToCollectionsLookupResults() method of ReferralToCollectionsDocumentUtil.
     */
    public void testGetPopulatedReferralToCollectionsLookupResults() {
        List<ContractsGrantsInvoiceDocument> invoices = new ArrayList<ContractsGrantsInvoiceDocument>(ReferralToCollectionsDocumentUtil.getCGInvoiceDocumentsFromLookupResultsSequenceNumber(LOOKUP_SEQUENCE_NUMBER, PERSON_ID));
        assertNotNull(ReferralToCollectionsDocumentUtil.getPopulatedReferralToCollectionsLookupResults(invoices));
    }

    /**
     * This method tests getPopulatedReferralToCollectionsDocumentDetails() method of ReferralToCollectionsDocumentUtil.
     */
    public void testGetPopulatedReferralToCollectionsDocumentDetails() {
        List<ContractsGrantsInvoiceDocument> invoices = new ArrayList<ContractsGrantsInvoiceDocument>(ReferralToCollectionsDocumentUtil.getCGInvoiceDocumentsFromLookupResultsSequenceNumber(LOOKUP_SEQUENCE_NUMBER, PERSON_ID));
        assertNotNull(ReferralToCollectionsDocumentUtil.getPopulatedReferralToCollectionsDetails(referralToCollectionsDocument, invoices));
    }
}
