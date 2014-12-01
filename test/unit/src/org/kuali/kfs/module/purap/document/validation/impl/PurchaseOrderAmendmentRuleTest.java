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
package org.kuali.kfs.module.purap.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.util.Map;

import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderAmendmentDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.kfs.sys.document.validation.impl.CompositeValidation;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = parke)
public class PurchaseOrderAmendmentRuleTest extends PurapRuleTestBase {

    private Map<String, CompositeValidation> validations;
    PurchaseOrderAmendmentDocument poAmendment;

    protected void setUp() throws Exception {
        super.setUp();
        poAmendment = new PurchaseOrderAmendmentDocument();
        validations = SpringContext.getBeansOfType(CompositeValidation.class);
    }

    protected void tearDown() throws Exception {
        validations = null;
        poAmendment = null;
        super.tearDown();
    }
    
    private void savePO(PurchaseOrderAmendmentDocument poAmend) {
        poAmend.prepareForSave(); 
        try {
            AccountingDocumentTestUtils.saveDocument(poAmend, SpringContext.getBean(DocumentService.class));
        }
        catch (Exception e) {
            throw new RuntimeException("Problems saving PO: " + e);
        }
    }

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testAmendmentValidate_Open() {
        poAmendment = (PurchaseOrderAmendmentDocument) PurchaseOrderAmendmentDocumentFixture.PO_AMEND_STATUS_OPEN.createPurchaseOrderAmendmentDocument();
        savePO(poAmendment);      
        
        CompositeValidation validation = (CompositeValidation)validations.get("PurchaseOrderAmendment-routeDocumentValidation");
        
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", poAmendment)) );        
    }

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testAmendmentValidate_NoItem() {
        poAmendment = (PurchaseOrderAmendmentDocument) PurchaseOrderAmendmentDocumentFixture.PO_AMEND_STATUS_OPEN.createPurchaseOrderAmendmentDocument();
        poAmendment.deleteItem(0);
        savePO(poAmendment);       
        CompositeValidation validation = (CompositeValidation)validations.get("PurchaseOrderAmendment-routeDocumentValidation");        
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", poAmendment)) );        
    }
}

