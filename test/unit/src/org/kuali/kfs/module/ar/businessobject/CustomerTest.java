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
package org.kuali.kfs.module.ar.businessobject;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.LinkedList;
import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.document.MaintenanceDocumentTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests Customer Note and Customer Collector Maintenance Document
 */
@ConfigureContext(session = UserNameFixture.khuntley)
public class CustomerTest extends KualiTestBase {
    public MaintenanceDocument document;
    public DocumentService documentService;
    public static final Class<MaintenanceDocument> DOCUMENT_CLASS = MaintenanceDocument.class;
    private Customer customer;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument("CUS");
        document.getDocumentHeader().setDocumentDescription("Test Document");
        documentService = SpringContext.getBean(DocumentService.class);
        customer = new Customer();

        customer.setCustomerName("TEST");
        customer.setCustomerTypeCode("01");

        CustomerAddress customerAddress = new CustomerAddress();
        customerAddress.setCustomerAddressName("Test");
        customerAddress.setCustomerLine1StreetAddress("Address 1");
        customerAddress.setCustomerCityName("Abc");
        customerAddress.setCustomerCountryCode("IN");

        List list = new LinkedList();
        list.add(customerAddress);
        customer.setCustomerAddresses(list);

        // set Customer Collector
        CustomerCollector collector = new CustomerCollector();
        collector.setPrincipalId("1204808831");
        // collector.setActive(true);

        customer.setCustomerCollector(collector);

        // set Customer note
        CustomerNote customerNote = new CustomerNote();
        customerNote.setNotePostedDateToCurrent();
        customerNote.setNoteText("Note Text for testing");

        list = new LinkedList();
        list.add(customerNote);

        customer.setCustomerNotes(list);

        document.getNewMaintainableObject().setBusinessObject(customer);
        document.getNewMaintainableObject().setBoClass(customer.getClass());
    }

    /**
     * This method calls testSaveDocument method.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public void testSaveDocument() throws Exception {
        MaintenanceDocumentTestUtils.testSaveDocument(document, documentService);
    }

    /**
     * This method tests getNewDocument() method on Customer maintenance document.
     * 
     * @throws Exception
     */
    public void testGetNewDocument() throws Exception {
        Document document = (Document) documentService.getNewDocument("CUS");
        // verify document was created
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());
    }
}
