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
package org.kuali.kfs.module.purap.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;
import static org.kuali.kfs.sys.fixture.UserNameFixture.sterner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

/**
 * Includes tests of the methods in RequisitionService.  For tests of isAutomaticPurchaseOrderAllowed,
 * and by implication, checkAutomaticPurchaseOrderRules and checkAPORulesPerItemForCommodityCodes that
 * are negative, (that is, that test what these methods do under conditions which should prevent the
 * requisition from becoming an APO and generate one of a large number of potential error messages),
 * see the class NegativeAPOTest.
 *
 * @see org.kuali.kfs.module.purap.document.NegativeAPOTest.
 */
@ConfigureContext(session = parke)
public class RequisitionServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionServiceTest.class);

    private static final String ACCOUNT_REVIEW = "Account";

    private DocumentService docService;
    private RequisitionService reqService;
    private DocumentService documentService;
    private BusinessObjectService businessObjectService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (null == reqService) {
            reqService = SpringContext.getBean(RequisitionService.class);
        }
        if (null == documentService) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        if (null == businessObjectService) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
    }

    private RequisitionDocument routeReqToFinal( RequisitionDocument requistionDocument) throws Exception {
        String docid = requistionDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(requistionDocument, SpringContext.getBean(DocumentService.class));
        WorkflowTestUtils.waitForNodeChange(requistionDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);

        changeCurrentUser(sterner);
        requistionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docid);
        SpringContext.getBean(DocumentService.class).approveDocument(requistionDocument, "Test approving as sterner", null);

        return requistionDocument;
    }

    // Tests of isAutomaticPurchaseOrderAllowed
    public void testIsAutomaticPurchaseOrderAllowed_Valid() {
        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        assertTrue(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));
    }

    public void testIsAutomaticPurchaseOrderAllowed_Alternative() {
        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ALTERNATE_APO.createRequisitionDocument();
        assertTrue(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));
    }

    public void testGetDocumentsNumbersAwaitingContractManagerAssignment() throws Exception {
        Set<String> docIds = new HashSet<String>();

        //PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CONTRACT_MANAGER_ASSGN
        RequisitionDocument req1 = routeReqToFinal(RequisitionDocumentFixture.REQ_NO_APO_VALID.createRequisitionDocument());

        //PurapConstants.RequisitionStatuses.APPDOC_AWAIT_FISCAL_REVIEW
        RequisitionDocument req2 = routeReqToFinal(RequisitionDocumentFixture.REQ_TWO_ITEMS.createRequisitionDocument());

        RequisitionDocument req3 = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        req3.setApplicationDocumentStatus(PurapConstants.RequisitionStatuses.APPDOC_AWAIT_CONTRACT_MANAGER_ASSGN);
        documentService.saveDocument(req3);

        List<RequisitionDocument> reqs = reqService.getRequisitionsAwaitingContractManagerAssignment();

        for(RequisitionDocument req : reqs) {
            docIds.add(req.getDocumentNumber());
        }

        assertTrue(docIds.contains(req1.getDocumentNumber()));
        assertTrue(!docIds.contains(req2.getDocumentNumber()));
        assertTrue(!docIds.contains(req3.getDocumentNumber()));

    }
}
