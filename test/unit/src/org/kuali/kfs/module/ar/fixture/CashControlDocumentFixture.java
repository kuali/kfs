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
package org.kuali.kfs.module.ar.fixture;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;

public enum CashControlDocumentFixture {
    CASH_CONTROL_DOCUMENT();
    
    static private String TESTING = "PaymentApplicationDocument testing";
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private CashControlDocumentService cashControlDocumentService;
    
    private CashControlDocumentFixture() {
        documentService = SpringContext.getBean(DocumentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
    }
    
    public CashControlDocument getNewCashControlDocumentWithDetails() throws WorkflowException {
        CashControlDocument cashControlDocument = (CashControlDocument) documentService.getNewDocument(CashControlDocument.class);
        CashControlDetail cashControlDetail = buildCashControlDetail(cashControlDocument,"ABB2","9999",new Date(System.currentTimeMillis()),new KualiDecimal(1000));
        cashControlDocument.addCashControlDetail(cashControlDetail);
        cashControlDocument.getDocumentHeader().setDocumentDescription(TESTING);
        cashControlDocumentService.createAndSavePaymentApplicationDocument(TESTING, cashControlDocument, cashControlDetail);
        return cashControlDocument;
    }
    
    private CashControlDetail buildCashControlDetail(CashControlDocument cashControlDocument, String customerNumber, String customerPaymentMediumIdentifier, Date customerPaymentDate, KualiDecimal financialDocumentLineAmount) {
        CashControlDetail cashControlDetail = new CashControlDetail();
        cashControlDetail.setCashControlDocument(cashControlDocument);
        cashControlDetail.setCustomerPaymentMediumIdentifier(customerPaymentMediumIdentifier);
        cashControlDetail.setCustomerPaymentDate(customerPaymentDate);
        cashControlDetail.setFinancialDocumentLineAmount(financialDocumentLineAmount);
        
        return cashControlDetail;
    }
}
