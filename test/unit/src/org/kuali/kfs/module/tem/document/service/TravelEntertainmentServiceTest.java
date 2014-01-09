/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TravelPayment;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley)
public class TravelEntertainmentServiceTest extends KualiTestBase {
    private static final int EXPENSE_AMOUNT = 100;
    private TravelEntertainmentDocument ent = null;
    private TravelerDetail traveler = null;

    private TravelEntertainmentDocumentService entservice;
    private TravelDocumentService service;
    private DocumentService documentService;

    private static final Logger LOG = Logger.getLogger(TravelEntertainmentServiceTest.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        // setup services
        entservice = SpringContext.getBean(TravelEntertainmentDocumentService.class);
        documentService = SpringContext.getBean(DocumentService.class);

        ent = DocumentTestUtils.createDocument(documentService, TravelEntertainmentDocument.class);
        documentService.prepareWorkflowDocument(ent);

        // setup traveler
        traveler = new TravelerDetail() {
            @Override
            public void refreshReferenceObject(String referenceObjectName) {
                // do nothing
            }
        };
        traveler.setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        traveler.setCustomer(new Customer());
        ent.setTraveler(traveler);
        ent.setHostAsPayee(true);
        ent.setTravelPayment(new TravelPayment());
        ent.getTravelPayment().setDocumentationLocationCode("N");
    }

    @Override
    @After
    public void tearDown() throws Exception {
        entservice = null;
        super.tearDown();
    }

    /**
     * This method test {@link TravelEntertainmentDocumentService#findByTravelId(Integer)} using travelDocumentIdentifier
     */
    @Test
    public void testFindByTravelDocumentIdentifier() throws WorkflowException {
        documentService.saveDocument(ent, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);

        // test find for non existent travelDocumentIdentifier
        List<TravelEntertainmentDocument> result = (List<TravelEntertainmentDocument>) entservice.findByTravelId("-1");
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // test find for existing travelDocument
        result = (List<TravelEntertainmentDocument>) entservice.findByTravelId(ent.getTravelDocumentIdentifier());
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * This method test {@link TravelEntertainmentDocumentService#find(String)} using documentNumber
     */
    @Test
    public void testFindByDocumentNumber() throws WorkflowException {
        // test find for non existent documentNumber
        assertNull(entservice.find(ent.getDocumentHeader().getDocumentNumber()));

        // test find for existent documentNumber
        documentService.saveDocument(ent, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        assertNotNull(entservice.find(ent.getDocumentHeader().getDocumentNumber()));
    }

    /**
     * This method tests {@link TravelEntertainmentDocumentService#addListenersTo(TravelEntertainmentDocument)}
     */
    @Test
    public void testAddListenersTo() {
        boolean success = false;

        try {
            entservice.addListenersTo(ent);
            success = true;
        }
        catch (NullPointerException e) {
            success = false;
            LOG.warn("NPE.", e);
        }

        assertTrue(success);
    }



    /**
     * This method tests {@link TravelEntertainmentDocumentService#calculateTotalsFor(TravelEntertainmentDocument)}
     *
     * @throws WorkflowException

    @Test
    public void testCalculateTotalsFor() throws WorkflowException {
        ent = new TravelEntertainmentDocument();

        traveler.setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        ent.setTraveler(traveler);

        List<ActualExpense> oteList = new ArrayList<ActualExpense>();
        ent.setActualExpenses(oteList);
        ent.setPerDiemExpenses(new ArrayList<PerDiemExpense>());

        // test with empty actualExpenseList
        Map<String, Object> resultMap = service.calculateTotalsFor(ent);
        assertTrue(((KualiDecimal) resultMap.get(TemConstants.NON_REIMBURSABLE_ATTRIBUTE)).equals(KualiDecimal.ZERO));

        // Override refreshReferenceObject - setting travelExpenseTypeCode manually
        ActualExpense ote = new ActualExpense() {
            public void refreshReferenceObject(String referenceObjectName) {
                // do nothing;
            }
        };
        ote.setExpenseAmount(new KualiDecimal(EXPENSE_AMOUNT));
        ote.setNonReimbursable(true);
        //ote.setTravelExpenseTypeCode(new TemTravelExpenseTypeCode());
        oteList.add(ote);

        ent.setActualExpenses(oteList);

        // test with non-reimbursable other expense
        resultMap = service.calculateTotalsFor(ent);
        assertTrue(((KualiDecimal) resultMap.get(TemConstants.NON_REIMBURSABLE_ATTRIBUTE)).equals(new KualiDecimal(EXPENSE_AMOUNT)));
    }
     */


    /**
     * This method tests {@link TravelEntertainmentDocumentService#generateCoversheetFor(TravelEntertainmentDocument)}
     *
     * @throws Exception
     */
    @Test
    public void testGenerateCoversheetFor() throws Exception {
        Coversheet cover = null;

        try {
            cover = entservice.generateCoversheetFor(new TravelEntertainmentDocument());
        }
        catch (RuntimeException e) {
            LOG.warn("Workflow doc is null.", e);
        }

        assertNull(cover);

        ent.setTripBegin(new Timestamp(new java.util.Date().getTime()));
        ent.setTripEnd(new Timestamp(new java.util.Date().getTime()));

        cover = entservice.generateCoversheetFor(ent);
        assertNotNull(cover);
    }
}
