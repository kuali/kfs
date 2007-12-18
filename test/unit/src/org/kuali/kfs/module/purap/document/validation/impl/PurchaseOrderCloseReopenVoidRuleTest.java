/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import static org.kuali.test.fixtures.UserNameFixture.*;

import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.fixtures.PaymentRequestDocumentFixture;
import org.kuali.module.purap.fixtures.PurchaseOrderChangeDocumentFixture;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = PARKE)
public class PurchaseOrderCloseReopenVoidRuleTest extends PurapRuleTestBase {

    PurchaseOrderCloseDocumentRule closeRule;
    PurchaseOrderReopenDocumentRule reopenRule;
    PurchaseOrderVoidDocumentRule voidRule;
    PurchaseOrderDocument po;

    protected void setUp() throws Exception {
        super.setUp();
        po = new PurchaseOrderDocument();
        closeRule = new PurchaseOrderCloseDocumentRule();
        reopenRule = new PurchaseOrderReopenDocumentRule();
        voidRule = new PurchaseOrderVoidDocumentRule();
    }

    protected void tearDown() throws Exception {
        closeRule = null;
        reopenRule = null;
        voidRule = null;
        po = null;
        super.tearDown();
    }
    
    private void savePO(PurchaseOrderDocument po) {
        po.prepareForSave(); 
        try {
            AccountingDocumentTestUtils.saveDocument(po, SpringContext.getBean(DocumentService.class));
        }
        catch (Exception e) {
            throw new RuntimeException("Problems saving PO: " + e);
        }
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public void testCloseValidate_Open() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_OPEN.generatePO();
        savePO(po);
        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_FOR_PO_CLOSE_DOC.createPaymentRequestDocument();
        preq.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        try {
            AccountingDocumentTestUtils.saveDocument(preq, SpringContext.getBean(DocumentService.class));
        }
        catch (Exception e) {
            throw new RuntimeException("Problems saving PREQ: " + e);
        }        
        assertTrue(closeRule.processValidation(po));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public void testCloseValidate_Closed() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_CLOSED.generatePO();
        savePO(po);         
        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_FOR_PO_CLOSE_DOC.createPaymentRequestDocument();
        preq.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        try {
            AccountingDocumentTestUtils.saveDocument(preq, SpringContext.getBean(DocumentService.class));
        }
        catch (Exception e) {
            throw new RuntimeException("Problems saving PREQ: " + e);
        }              
        assertFalse(closeRule.processValidation(po));
    }    

    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public void testCloseValidate_NoPreq() {     
        po = PurchaseOrderChangeDocumentFixture.STATUS_OPEN.generatePO();
        savePO(po);
        assertFalse(closeRule.processValidation(po));
    }    

    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public void testReopenValidate_Closed() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_CLOSED.generatePO();
        savePO(po);
        assertTrue(reopenRule.processValidation(po));
    }

    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public void testReopenValidate_Open() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_OPEN.generatePO();
        savePO(po);
        assertFalse(reopenRule.processValidation(po));
    }
    
    @ConfigureContext(session = RORENFRO, shouldCommitTransactions=true)
    public void testReopenValidate_InvalidUser() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_CLOSED.generatePO();
        savePO(po);
        assertFalse(reopenRule.processValidation(po));
    }    
    
    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public void testVoidValidate_Open() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_OPEN.generatePO();
        savePO(po);
        assertTrue(voidRule.processValidation(po));
    }

    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public void testVoidValidate_PendingPrint() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_PENDING_PRINT.generatePO();
        savePO(po);
        assertTrue(voidRule.processValidation(po));
    }

    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public void testVoidValidate_InProcess() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_IN_PROCESS.generatePO();
        savePO(po);      
        assertFalse(voidRule.processValidation(po));
    }    

    @ConfigureContext(session = PARKE, shouldCommitTransactions=true)
    public void testVoidValidate_Closed() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_CLOSED.generatePO();
        savePO(po);      
        assertFalse(voidRule.processValidation(po));
    }    
    
    @ConfigureContext(session = RORENFRO, shouldCommitTransactions=true)
    public void testVoidValidate_InvalidUser() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_OPEN.generatePO();
        savePO(po);
        assertFalse(voidRule.processValidation(po));
    }    
}
