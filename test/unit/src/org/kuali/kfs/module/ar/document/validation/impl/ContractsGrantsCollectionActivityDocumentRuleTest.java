/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRuleTest.java
import org.kuali.kfs.module.ar.businessobject.CollectionActivityInvoiceDetail;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
=======
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRuleTest.java
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the rules in ContractsGrantsCollectionActivityDocumentRule
 */
@ConfigureContext(session = khuntley)
public class ContractsGrantsCollectionActivityDocumentRuleTest extends KualiTestBase {

    private DocumentService documentService;
    private ContractsGrantsCollectionActivityDocumentRule contractsGrantsCollectionActivityDocumentRule;
    private ContractsGrantsCollectionActivityDocument contractsGrantsCollectionActivityDocument;

    private final static String ACTIVITY_CODE = "LSTS";

    /**
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
        contractsGrantsCollectionActivityDocumentRule = new ContractsGrantsCollectionActivityDocumentRule();
        contractsGrantsCollectionActivityDocument = createCollectionActivityDocument();
    }

    /**
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        documentService = null;
        contractsGrantsCollectionActivityDocumentRule = null;
        contractsGrantsCollectionActivityDocument = null;
    }

    /**
     * Creates the collection activity document.
     *
     * @return Returns new document of collection activity.
     * @throws WorkflowException
     */
    private ContractsGrantsCollectionActivityDocument createCollectionActivityDocument() throws WorkflowException {
        ContractsGrantsCollectionActivityDocument contractsGrantsCollectionActivityDocument = (ContractsGrantsCollectionActivityDocument) documentService.getNewDocument(ContractsGrantsCollectionActivityDocument.class);
        contractsGrantsCollectionActivityDocument.getDocumentHeader().setDocumentDescription("Testing document");

        return contractsGrantsCollectionActivityDocument;
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns true when all rules passed.
     */
<<<<<<< HEAD:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRuleTest.java
    public void testValidateCollectionActivityDocument_True() {
        collectionActivityDocument.setProposalNumber(11L);
        collectionActivityDocument.setActivityCode(ACTIVITY_CODE);
        collectionActivityDocument.setActivityDate(new Date(System.currentTimeMillis()));
        collectionActivityDocument.setActivityText("testing activity comment");
        List<CollectionActivityInvoiceDetail> invoiceDetails = new ArrayList<>();
        CollectionActivityInvoiceDetail invoiceDetail = new CollectionActivityInvoiceDetail();
        invoiceDetails.add(invoiceDetail);
        collectionActivityDocument.setInvoiceDetails(invoiceDetails);

        boolean result = collectionActivityDocumentRule.validateCollectionActivityDocument(collectionActivityDocument);
=======
    public void testValidateEvent_True() {
        CollectionEvent event = new CollectionEvent();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));

        boolean result = contractsGrantsCollectionActivityDocumentRule.validateCollectionEvent(event);
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRuleTest.java
        assertTrue(result);
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns false when the rules fail.
     */
<<<<<<< HEAD:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRuleTest.java
    public void testValidateCollectionActivityDocument_False() {
        boolean result = collectionActivityDocumentRule.validateCollectionActivityDocument(collectionActivityDocument);
=======
    public void testValidateEvent_False() {
        CollectionEvent event = new CollectionEvent();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));
        event.setFollowup(Boolean.TRUE);

        boolean result = contractsGrantsCollectionActivityDocumentRule.validateCollectionEvent(event);
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRuleTest.java
        assertFalse(result);
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns false when the rule fails.
     */
<<<<<<< HEAD:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRuleTest.java
    public void testValidateCollectionActivityDocument_False_missing_ProposalNumber() {
        collectionActivityDocument.setActivityCode(ACTIVITY_CODE);
        collectionActivityDocument.setActivityDate(new Date(System.currentTimeMillis()));
        collectionActivityDocument.setActivityText("testing activity comment");
        List<CollectionActivityInvoiceDetail> invoiceDetails = new ArrayList<>();
        CollectionActivityInvoiceDetail invoiceDetail = new CollectionActivityInvoiceDetail();
        invoiceDetails.add(invoiceDetail);
        collectionActivityDocument.setInvoiceDetails(invoiceDetails);
        boolean result = collectionActivityDocumentRule.validateCollectionActivityDocument(collectionActivityDocument);
        assertFalse(result);
=======
    public void testValidateEvents_True() {
        CollectionEvent event = new CollectionEvent();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));

        contractsGrantsCollectionActivityDocument.getCollectionEvents().add(event);
        boolean result = contractsGrantsCollectionActivityDocumentRule.validateCollectionEvents(contractsGrantsCollectionActivityDocument);
        assertTrue(result);
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRuleTest.java
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns false when the rule fails.
     */
<<<<<<< HEAD:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRuleTest.java
    public void testValidateCollectionActivityDocument_False_missing_ActivityCode() {
        collectionActivityDocument.setProposalNumber(11L);
        collectionActivityDocument.setActivityDate(new Date(System.currentTimeMillis()));
        collectionActivityDocument.setActivityText("testing activity comment");
        List<CollectionActivityInvoiceDetail> invoiceDetails = new ArrayList<>();
        CollectionActivityInvoiceDetail invoiceDetail = new CollectionActivityInvoiceDetail();
        invoiceDetails.add(invoiceDetail);
        collectionActivityDocument.setInvoiceDetails(invoiceDetails);
        boolean result = collectionActivityDocumentRule.validateCollectionActivityDocument(collectionActivityDocument);
=======
    public void testValidateEvents_False() {
        CollectionEvent event = new CollectionEvent();
        contractsGrantsCollectionActivityDocument.getCollectionEvents().add(event);
        boolean result = contractsGrantsCollectionActivityDocumentRule.validateCollectionEvents(contractsGrantsCollectionActivityDocument);
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRuleTest.java
        assertFalse(result);
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns false when the rule fails.
     */
<<<<<<< HEAD:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRuleTest.java
    public void testValidateCollectionActivityDocument_False_missing_ActivityDate() {
        collectionActivityDocument.setProposalNumber(11L);
        collectionActivityDocument.setActivityCode(ACTIVITY_CODE);
        collectionActivityDocument.setActivityText("testing activity comment");
        List<CollectionActivityInvoiceDetail> invoiceDetails = new ArrayList<>();
        CollectionActivityInvoiceDetail invoiceDetail = new CollectionActivityInvoiceDetail();
        invoiceDetails.add(invoiceDetail);
        collectionActivityDocument.setInvoiceDetails(invoiceDetails);
        boolean result = collectionActivityDocumentRule.validateCollectionActivityDocument(collectionActivityDocument);
        assertFalse(result);
=======
    public void testProcessAddEventBusinessRules_True() {
        CollectionEvent event = new CollectionEvent();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));

        contractsGrantsCollectionActivityDocument.getCollectionEvents().add(event);
        boolean result = contractsGrantsCollectionActivityDocumentRule.processAddCollectionEventBusinessRules(contractsGrantsCollectionActivityDocument, event);
        assertTrue(result);
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRuleTest.java
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns false when the rule fails.
     */
<<<<<<< HEAD:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/CollectionActivityDocumentRuleTest.java
    public void testValidateCollectionActivityDocument_False_missing_ActivityText() {
        collectionActivityDocument.setProposalNumber(11L);
        collectionActivityDocument.setActivityCode(ACTIVITY_CODE);
        collectionActivityDocument.setActivityDate(new Date(System.currentTimeMillis()));
        List<CollectionActivityInvoiceDetail> invoiceDetails = new ArrayList<>();
        CollectionActivityInvoiceDetail invoiceDetail = new CollectionActivityInvoiceDetail();
        invoiceDetails.add(invoiceDetail);
        collectionActivityDocument.setInvoiceDetails(invoiceDetails);
        boolean result = collectionActivityDocumentRule.validateCollectionActivityDocument(collectionActivityDocument);
=======
    public void testProcessAddEventBusinessRules_False() {
        CollectionEvent event = new CollectionEvent();
        event.setActivityCode(ACTIVITY_CODE);
        event.setActivityText("Testing text");
        event.setActivityDate(new Date(12333232323L));
        event.setCompleted(Boolean.TRUE);

        contractsGrantsCollectionActivityDocument.getCollectionEvents().add(event);
        boolean result = contractsGrantsCollectionActivityDocumentRule.processAddCollectionEventBusinessRules(contractsGrantsCollectionActivityDocument, event);
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:test/unit/src/org/kuali/kfs/module/ar/document/validation/impl/ContractsGrantsCollectionActivityDocumentRuleTest.java
        assertFalse(result);
    }

    /**
     * Tests the validateCollectionActivityDocument() method of service and returns false when the rule fails.
     */
    public void testValidateCollectionActivityDocument_False_missing_InvoiceDetails() {
        collectionActivityDocument.setProposalNumber(11L);
        collectionActivityDocument.setActivityCode(ACTIVITY_CODE);
        collectionActivityDocument.setActivityDate(new Date(System.currentTimeMillis()));
        collectionActivityDocument.setActivityText("testing activity comment");
        boolean result = collectionActivityDocumentRule.validateCollectionActivityDocument(collectionActivityDocument);
        assertFalse(result);
    }

}
