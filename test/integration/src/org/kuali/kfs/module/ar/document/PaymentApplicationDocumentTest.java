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
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.Set;

import org.kuali.kfs.module.ar.fixture.CashControlDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class PaymentApplicationDocumentTest extends KualiTestBase {
    static private String ANNOTATION = "PaymentApplicationDocument testing";
    private DocumentService documentService;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    public void testCreatePaymentApplicationDocument() throws Exception {
        PaymentApplicationDocument document = (PaymentApplicationDocument) documentService.getNewDocument(PaymentApplicationDocument.class);
        document.getDocumentHeader().setDocumentDescription("Testing");
        documentService.saveDocument(document);
    }
    
    // Create a cash control document
    // Approve it
    // Check that a payapp doc was created with the same document number as the ref doc number on the cash control doc
    public void testCreatedUponApprovingCashControlDocument() throws Exception {
        CashControlDocument cashControlDocument = CashControlDocumentFixture.CASH_CONTROL_DOCUMENT.getNewCashControlDocumentWithDetails();
        try {
            documentService.blanketApproveDocument(cashControlDocument, ANNOTATION, new ArrayList());
            assertNotNull(cashControlDocument.getReferenceFinancialDocument());
        } catch(Throwable t) {
            ErrorMap e = GlobalVariables.getErrorMap();
            Set s = e.keySet();
            if(0 != s.size()) {
                boolean trap = true;
            }
        }
    }
    
    public void testCreatedWithoutCashControlDocument() throws Exception {
    }
    
    public void testPendingEntriesGeneratedCorrectly() throws Exception {
    }
    
    public void testOverApplyingFails() throws Exception {
        // TODO implement
    }
    
    public void testUnderApplyingFails() throws Exception {
        // TODO implement
    }
    
    public void testExactlyApplyingSucceeds() throws Exception {
        // TODO implement
    }
    
}

