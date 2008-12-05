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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

@ConfigureContext(session = khuntley)
public class PaymentApplicationDocumentTest extends KualiTestBase {
    static private String ANNOTATION = "PaymentApplicationDocument testing";
    static private String TESTING = "PaymentApplicationDocument testing";
    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private CashControlDocumentService cashControlDocumentService;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
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
        List<CashControlDetailSpec> specs = new ArrayList<CashControlDetailSpec>();
        specs.add(new CashControlDetailSpec("ABB2","9999",new Date(System.currentTimeMillis()),new KualiDecimal(1000)));
        
        CashControlDocument cashControlDocument = createNewCashControlDocument(specs);
        documentService.saveDocument(cashControlDocument);
        
        for(CashControlDetail detail : cashControlDocument.getCashControlDetails()) {
            assertNotNull(detail.getReferenceFinancialDocument());
        }
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
    
    protected void changeCurrentUser(UserNameFixture sessionUser) throws Exception {
        GlobalVariables.setUserSession(new UserSession(sessionUser.toString()));
    }
    
    private CashControlDocument createNewCashControlDocument(List<CashControlDetailSpec> specs) throws WorkflowException {
        CashControlDocument cashControlDocument = (CashControlDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), CashControlDocument.class);// documentService.getNewDocument(CashControlDocument.class);
        cashControlDocument.getDocumentHeader().setDocumentDescription(TESTING);
        cashControlDocument.getAccountsReceivableDocumentHeader().setDocumentNumber(cashControlDocument.getDocumentNumber());
        documentService.saveDocument(cashControlDocument);
        for(CashControlDetailSpec spec : specs) {
            CashControlDetail cashControlDetail = buildCashControlDetail(cashControlDocument,spec);
            cashControlDocumentService.addNewCashControlDetail(TESTING, cashControlDocument, cashControlDetail);
            PaymentApplicationDocument paymentApplicationDocument = cashControlDocumentService.createAndSavePaymentApplicationDocument(TESTING, cashControlDocument, cashControlDetail);
            cashControlDetail.setReferenceFinancialDocumentNumber(paymentApplicationDocument.getDocumentNumber());
        }
        return cashControlDocument;
    }
    
    private CashControlDetail buildCashControlDetail(CashControlDocument cashControlDocument, CashControlDetailSpec spec) {
        CashControlDetail cashControlDetail = new CashControlDetail();
        cashControlDetail.setCashControlDocument(cashControlDocument);
        cashControlDetail.setDocumentNumber(cashControlDocument.getDocumentNumber());
        cashControlDetail.setCustomerNumber(spec.customerNumber);
        cashControlDetail.setCustomerPaymentMediumIdentifier(spec.customerPaymentMediumIdentifier);
        cashControlDetail.setCustomerPaymentDate(spec.customerPaymentDate);
        cashControlDetail.setFinancialDocumentLineAmount(spec.financialDocumentLineAmount);
        return cashControlDetail;
    }
    
}

