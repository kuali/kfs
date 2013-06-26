/*
 * Copyright 2007-2008 The Kuali Foundation
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

import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderAmendmentDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.kfs.sys.document.validation.impl.CompositeValidation;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = parke)
public class PurchaseOrderAmendmentRuleTest extends PurapRuleTestBase {

    private Map<String, CompositeValidation> validations;
    PurchaseOrderAmendmentDocument poAmendment;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        poAmendment = new PurchaseOrderAmendmentDocument();
        validations = SpringContext.getBeansOfType(CompositeValidation.class);
    }

    @Override
    protected void tearDown() throws Exception {
        validations = null;
        poAmendment = null;
        super.tearDown();
    }

    private void savePO(PurchaseOrderDocument poAmend) {
        poAmend.prepareForSave();
        try {
            AccountingDocumentTestUtils.saveDocument(poAmend, SpringContext.getBean(DocumentService.class));
        }
        catch (Exception e) {
            throw new RuntimeException("Problems saving PO: " + e);
        }
    }

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testAmendmentValidate_Open() throws WorkflowException {

        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        RequisitionDocument requisition =RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        requisition.prepareForSave();
        SpringContext.getBean(DocumentService.class).completeDocument(requisition, "complete action ", null);
        PurchaseOrderDocument po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        po.setRequisitionIdentifier(requisition.getPurapDocumentIdentifier());
        po.prepareForSave();
        AccountingDocumentTestUtils.routeDocument(po, "test annotation", null, documentService);
        poAmendment = PurchaseOrderAmendmentDocumentFixture.PO_AMEND_STATUS_OPEN.createPurchaseOrderAmendmentDocument();
        poAmendment.setPurapDocumentIdentifier(po.getPurapDocumentIdentifier());
        savePO(poAmendment);

        CompositeValidation validation = validations.get("PurchaseOrderAmendment-routeDocumentValidation");
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", poAmendment)) );
    }

    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testAmendmentValidate_NoItem() throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        RequisitionDocument requisition =RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        requisition.prepareForSave();
        SpringContext.getBean(DocumentService.class).completeDocument(requisition, "complete action ", null);
        PurchaseOrderDocument po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        po.setRequisitionIdentifier(requisition.getPurapDocumentIdentifier());
        po.prepareForSave();
        AccountingDocumentTestUtils.routeDocument(po, "test annotation", null, documentService);
        poAmendment = PurchaseOrderAmendmentDocumentFixture.PO_AMEND_STATUS_OPEN.createPurchaseOrderAmendmentDocument();
        poAmendment.setPurapDocumentIdentifier(po.getPurapDocumentIdentifier());
        poAmendment.deleteItem(0);
        savePO(poAmendment);
        CompositeValidation validation = validations.get("PurchaseOrderAmendment-routeDocumentValidation");
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", poAmendment)) );
    }
}

