/*
 * Copyright 2011 The Kuali Foundation.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * This class tests the Final Invoicing Process
 */
@ConfigureContext(session = khuntley)
public class FinalInvoiceTest extends CGInvoiceDocumentSetupTest {

    WorkflowDocumentService workflowDocumentService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
    }

    public void testInvoiceOnFinal() {
        assertTrue(document.getDocumentHeader().getWorkflowDocument().getStatus().equals("S"));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getProposalNumber());
        SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).updateBillsAndMilestones(KFSConstants.ParameterValues.STRING_YES,document.getInvoiceMilestones(),document.getInvoiceBills());

        List<Milestone> milestones = (List<Milestone>) SpringContext.getBean(BusinessObjectService.class).findMatching(Milestone.class, map);

        if (CollectionUtils.isEmpty(milestones)) {
            Iterator<Milestone> iterator = milestones.iterator();
            while (iterator.hasNext()) {
                assertTrue(iterator.next().isBilledIndicator());
            }
        }

        List<Bill> bills = (List<Bill>) SpringContext.getBean(BusinessObjectService.class).findMatching(Bill.class, map);

        if (CollectionUtils.isEmpty(bills)) {
            Iterator iterator = bills.iterator();

            while (iterator.hasNext())
                assertTrue(((Bill) iterator.next()).isBilledIndicator());
        }
    }

    public void testMultipleInvoices() {
        document.getInvoiceGeneralDetail().setFinalBillIndicator(true);
        assertTrue(document.getDocumentHeader().getWorkflowDocument().getStatus().equals("S"));
        SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).doWhenFinalInvoice(document);
        Iterator iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getProposalNumber());
            ContractsAndGrantsCGBAwardAccount awardAccount = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAwardAccount.class, mapKey);
            assertTrue(awardAccount.isFinalBilledIndicator());
        }
    }

    @ConfigureContext(session = khuntley)
    private void route() {
        try {
            AccountingDocumentTestUtils.testRouteDocument(document, documentService);
            documentService.prepareWorkflowDocument(document);
            documentService.superUserApproveDocument(document, "");
            documentService.routeDocument(document, "routing test doc", new Vector());
            workflowDocumentService.route(document.getDocumentHeader().getWorkflowDocument(), "", null);
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}
