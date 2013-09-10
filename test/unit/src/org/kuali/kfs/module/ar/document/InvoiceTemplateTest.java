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
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.coa.businessobject.defaultvalue.CurrentUserChartValueFinder;
import org.kuali.kfs.coa.businessobject.defaultvalue.CurrentUserOrgValueFinder;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.MaintenanceDocumentTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class is used to test Invoice template class
 */
@ConfigureContext(session = UserNameFixture.khuntley)
public class InvoiceTemplateTest extends KualiTestBase {

    public MaintenanceDocument document;
    public DocumentService documentService;
    public static final String TYPE_CODE = "1115";
    public static final String TYPE_DESCRIPTION = "Federal SF-1115";
    public static final boolean ACTIVE = true;
    public static final Class<MaintenanceDocument> DOCUMENT_CLASS = MaintenanceDocument.class;
    private InvoiceTemplate invoiceTemplate;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument("CGAI");
        document.getDocumentHeader().setDocumentDescription("Test Document");
        documentService = SpringContext.getBean(DocumentService.class);
        invoiceTemplate = new InvoiceTemplate();
        invoiceTemplate.setInvoiceTemplateCode(TYPE_CODE);
        invoiceTemplate.setInvoiceTemplateDescription(TYPE_DESCRIPTION);
        invoiceTemplate.setActive(ACTIVE);
        document.getNewMaintainableObject().setBusinessObject(invoiceTemplate);
        document.getNewMaintainableObject().setBoClass(invoiceTemplate.getClass());
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public void testSaveDocument() throws Exception {
        MaintenanceDocumentTestUtils.testSaveDocument(document, documentService);
    }


    public void testGetNewDocument() throws Exception {
        Document document = (Document) documentService.getNewDocument("CGAI");
        // verify document was created
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());
    }

    public void testValidOrganization() {
        assertFalse(invoiceTemplate.isValidOrganization());
        invoiceTemplate.setBillByChartOfAccountCode((new CurrentUserChartValueFinder()).getValue());
        invoiceTemplate.setBilledByOrganizationCode((new CurrentUserOrgValueFinder()).getValue());
        assertTrue(invoiceTemplate.isValidOrganization());
    }

}
