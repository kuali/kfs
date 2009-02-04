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
package org.kuali.kfs.module.purap.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderChangeDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.rice.kns.service.DocumentService;

@ConfigureContext(session = parke)
public class PurchaseOrderAmendmentRuleTest extends PurapRuleTestBase {

    PurchaseOrderAmendmentDocumentRule amendRule;
    PurchaseOrderDocument po;

    protected void setUp() throws Exception {
        super.setUp();
        po = new PurchaseOrderDocument();
        amendRule = new PurchaseOrderAmendmentDocumentRule();
    }

    protected void tearDown() throws Exception {
        amendRule = null;
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

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testAmendmentValidate_Open() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_OPEN.generatePO();
        savePO(po);      
        assertTrue(amendRule.processValidation(po));
    }

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testAmendmentValidate_NoItem() {
        po = PurchaseOrderChangeDocumentFixture.STATUS_OPEN.generatePO();
        po.deleteItem(0);
        savePO(po);       
        assertFalse(amendRule.processValidation(po));
    }
}

