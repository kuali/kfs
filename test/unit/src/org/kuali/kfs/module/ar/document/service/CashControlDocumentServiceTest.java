/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.service;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.test.ConfigureContext;

import edu.iu.uis.eden.exception.WorkflowException;

@ConfigureContext(session = KHUNTLEY)
public class CashControlDocumentServiceTest extends KualiTestBase {
    CashControlDocumentService service;
    DocumentService documentService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        service = SpringContext.getBean(CashControlDocumentService.class);
        documentService = SpringContext.getBean(DocumentService.class);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        service = null;
        documentService = null;
        super.tearDown();
    }

    /**
     * This method test if createAndSavePaymentApplicationDocument creates and saves a payment application document
     * 
     * @throws WorkflowException
     */
    public void testCreateAndSavePaymentApplicationDocument() throws WorkflowException {

        CashControlDocument cashControlDocument = new CashControlDocument();
        CashControlDetail cashControlDetail = new CashControlDetail();
        PaymentApplicationDocument applicationDocument = service.createAndSavePaymentApplicationDocument(ArConstants.CREATED_BY_CASH_CTRL_DOC, cashControlDocument, cashControlDetail);

        assertNotNull(applicationDocument);

        PaymentApplicationDocument applicationDocument2 = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(applicationDocument.getDocumentNumber());

        assertNotNull(applicationDocument2);
        assertTrue(applicationDocument2.getDocumentHeader().getWorkflowDocument().stateIsSaved());
    }

}
