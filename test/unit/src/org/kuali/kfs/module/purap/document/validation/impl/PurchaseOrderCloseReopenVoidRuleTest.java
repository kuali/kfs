/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.util.Map;

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.PaymentRequestDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderChangeDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.kfs.sys.document.validation.impl.CompositeValidation;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = parke, shouldCommitTransactions=true)
public class PurchaseOrderCloseReopenVoidRuleTest extends PurapRuleTestBase {

    private Map<String, Validation> validations;
    PurchaseOrderDocument po;

    protected void setUp() throws Exception {
        super.setUp();
        po = new PurchaseOrderDocument();
        validations = SpringContext.getBeansOfType(Validation.class);
    }

    protected void tearDown() throws Exception {
        validations = SpringContext.getBeansOfType(Validation.class);
        po = null;
        super.tearDown();
    }
    
    /**
     * TODO: Remove once other tests are fixed
     */
    public void testNothing() {
        
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

    public void PATCHFIX_testCloseValidate_Open() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_OPEN.generatePO();
        savePO(po);
        GlobalVariables.getUserSession().setBackdoorUser( "appleton" );
        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_FOR_PO_CLOSE_DOC.createPaymentRequestDocument();
        preq.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        try {
            AccountingDocumentTestUtils.saveDocument(preq, SpringContext.getBean(DocumentService.class));
        }
        catch (Exception e) {
            throw new RuntimeException("Problems saving PREQ: " + e);
        }
        GlobalVariables.getUserSession().clearBackdoorUser();
        
        CompositeValidation validation = (CompositeValidation)validations.get("PurchaseOrderClose-routeDocumentValidation");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", po)));                
    }

    public void PATCHFIX_testReopenValidate_Closed() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_CLOSED.generatePO();
        savePO(po);
        
        CompositeValidation validation = (CompositeValidation)validations.get("PurchaseOrderReopen-routeDocumentValidation");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", po)));                        
    }

    public void PATCHFIX_testVoidValidate_Open() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_OPEN.generatePO();
        savePO(po);
        
        CompositeValidation validation = (CompositeValidation)validations.get("PurchaseOrderVoid-routeDocumentValidation");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", po)));                        
    }

    public void PATCHFIX_testVoidValidate_PendingPrint() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_PENDING_PRINT.generatePO();
        savePO(po);

        CompositeValidation validation = (CompositeValidation)validations.get("PurchaseOrderVoid-routeDocumentValidation");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", po)));                        
    }
   
}

