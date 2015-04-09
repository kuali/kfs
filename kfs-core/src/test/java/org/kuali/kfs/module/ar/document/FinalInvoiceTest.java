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
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * This class tests the Final Invoicing Process
 */
@ConfigureContext(session = wklykins)
public class FinalInvoiceTest extends CGInvoiceDocumentTestBase {

    WorkflowDocumentService workflowDocumentService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
    }

    public void testInvoiceOnFinal() throws WorkflowException {
        assertTrue(document.getDocumentHeader().getWorkflowDocument().isInitiated());
        documentService.saveDocument(document);

        String documentNumber = document.getDocumentNumber();
        Long proposalNumber = document.getInvoiceGeneralDetail().getProposalNumber();

        setupMilestones(documentNumber, proposalNumber, false);
        setupBills(documentNumber, proposalNumber, false);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).updateBillsAndMilestones(true, document.getInvoiceMilestones(), document.getInvoiceBills());

        List<Milestone> updatedMilestones = (List<Milestone>) boService.findMatching(Milestone.class, map);
        assertTrue(CollectionUtils.isNotEmpty(updatedMilestones));

        if (CollectionUtils.isNotEmpty(updatedMilestones)) {
            Iterator<Milestone> iterator = updatedMilestones.iterator();
            while (iterator.hasNext()) {
                assertTrue(iterator.next().isBilled());
            }
        }

        List<Bill> updatedBills = (List<Bill>) boService.findMatching(Bill.class, map);
        assertTrue(CollectionUtils.isNotEmpty(updatedBills));

        if (CollectionUtils.isNotEmpty(updatedBills)) {
            Iterator<Bill> iterator = updatedBills.iterator();

            while (iterator.hasNext()) {
                assertTrue(iterator.next().isBilled());
            }
        }
    }

    public void testMultipleInvoices() {
        document.getInvoiceGeneralDetail().setFinalBillIndicator(true);
        assertTrue(document.getDocumentHeader().getWorkflowDocument().isInitiated());
        SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).updateLastBilledDate(document);
        Iterator iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getInvoiceGeneralDetail().getProposalNumber());
            ContractsAndGrantsBillingAwardAccount awardAccount = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);
            assertTrue(awardAccount.isFinalBilledIndicator());
        }
    }

    private void route() throws Exception {
        AccountingDocumentTestUtils.testRouteDocument(document, documentService);
        documentService.prepareWorkflowDocument(document);
        documentService.superUserApproveDocument(document, "");
        documentService.routeDocument(document, "routing test doc", new Vector());
        workflowDocumentService.route(document.getDocumentHeader().getWorkflowDocument(), "", null);
    }
}
