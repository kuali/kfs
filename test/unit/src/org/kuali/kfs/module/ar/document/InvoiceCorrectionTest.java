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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.InvoiceBillFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceDetailAccountObjectCodeFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceMilestoneFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * This class tests the invoice correction process.
 */

@ConfigureContext(session = wklykins)
public class InvoiceCorrectionTest extends CGInvoiceDocumentTestBase {

    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.module.ar.document.CGInvoiceDocumentSetupTest#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    public void testGeneralCorrection() throws WorkflowException {
        for (InvoiceAddressDetail invoiceAddressDetail: document.getInvoiceAddressDetails()) {
            invoiceAddressDetail.setInitialTransmissionDate(dateTimeService.getCurrentSqlDate());
        }
        document.getInvoiceGeneralDetail().setFinalBillIndicator(true);

        contractsGrantsInvoiceDocumentService.correctContractsGrantsInvoiceDocument(document);
        for (InvoiceAddressDetail invoiceAddressDetail: document.getInvoiceAddressDetails()) {
            assertNull(invoiceAddressDetail.getInitialTransmissionDate());
        }

        contractsGrantsInvoiceDocumentService.updateUnfinalizationToAwardAccount(document.getAccountDetails(),document.getInvoiceGeneralDetail().getProposalNumber());
        Iterator iterator = document.getAccountDetails().iterator();

        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getInvoiceGeneralDetail().getProposalNumber());
            ContractsAndGrantsBillingAwardAccount awardAccount = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);
            assertFalse(awardAccount.isFinalBilledIndicator());
        }
    }

    public void testCorrectedMilestones() throws WorkflowException {
        document.getInvoiceGeneralDetail().setBillingFrequencyCode(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE);

        documentService.saveDocument(document);

        String documentNumber = document.getDocumentNumber();
        Long proposalNumber = document.getInvoiceGeneralDetail().getProposalNumber();

        setupMilestones(documentNumber, proposalNumber, true);

        List<InvoiceMilestone> milestones = new ArrayList<InvoiceMilestone>();
        InvoiceMilestone invMilestone_2 = InvoiceMilestoneFixture.INV_MLSTN_1.createInvoiceMilestone();
        milestones.add(invMilestone_2);
        Iterator<InvoiceMilestone> iterator = milestones.iterator();
        contractsGrantsInvoiceDocumentService.correctContractsGrantsInvoiceDocument(document);
        List<InvoiceMilestone> correctedMilestones = document.getInvoiceMilestones();
        Iterator<InvoiceMilestone> correctedIterator = correctedMilestones.iterator();
        while (iterator.hasNext() || correctedIterator.hasNext()) {
            InvoiceMilestone id = iterator.next();
            InvoiceMilestone cid = correctedIterator.next();
            assertTrue(id.getMilestoneAmount().equals(cid.getMilestoneAmount().negated()));
        }

        contractsGrantsInvoiceDocumentService.updateMilestonesBilledIndicator(false, document.getInvoiceMilestones());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        List<Milestone> updatedMilestones = (List<Milestone>) boService.findMatching(Milestone.class, map);
        assertTrue(CollectionUtils.isNotEmpty(updatedMilestones));

        if (CollectionUtils.isNotEmpty(updatedMilestones)) {
            Iterator<Milestone> iterator2 = updatedMilestones.iterator();
            while (iterator2.hasNext()) {
                assertFalse(iterator2.next().isBilled());
            }
        }

    }

    public void testCorrectedBills() throws WorkflowException {
        document.getInvoiceGeneralDetail().setBillingFrequencyCode(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);
        documentService.saveDocument(document);

        String documentNumber = document.getDocumentNumber();
        Long proposalNumber = document.getInvoiceGeneralDetail().getProposalNumber();

        setupBills(documentNumber, proposalNumber, true);

        List<InvoiceBill> bills = new ArrayList<InvoiceBill>();
        InvoiceBill invBill_2 = InvoiceBillFixture.INV_BILL_1.createInvoiceBill();
        bills.add(invBill_2);
        Iterator iterator = bills.iterator();
        contractsGrantsInvoiceDocumentService.correctContractsGrantsInvoiceDocument(document);
        List<InvoiceBill> correctedBills = document.getInvoiceBills();
        Iterator correctedIterator = correctedBills.iterator();
        while (iterator.hasNext() || correctedIterator.hasNext()) {
            InvoiceBill id = (InvoiceBill) iterator.next();
            InvoiceBill cid = (InvoiceBill) correctedIterator.next();
            assertTrue(id.getEstimatedAmount().equals(cid.getEstimatedAmount().negated()));
        }

        contractsGrantsInvoiceDocumentService.updateBillsBilledIndicator(false,document.getInvoiceBills());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        List<Bill> updatedBills = (List<Bill>) boService.findMatching(Bill.class, map);
        assertTrue(CollectionUtils.isNotEmpty(updatedBills));

        if (CollectionUtils.isNotEmpty(updatedBills)) {
            Iterator<Bill> iterator2 = updatedBills.iterator();
            while (iterator2.hasNext()) {
                assertFalse(iterator2.next().isBilled());
            }
        }

    }

    public void testCorrectedInvoiceDetails() throws WorkflowException {
        List<ContractsGrantsInvoiceDetail> invoiceDetail = document.getDirectCostInvoiceDetails();
        contractsGrantsInvoiceDocumentService.correctContractsGrantsInvoiceDocument(document);
        List<ContractsGrantsInvoiceDetail> correctedInvoiceDetail = document.getDirectCostInvoiceDetails();
        Iterator iterator = invoiceDetail.iterator();
        Iterator correctedIterator = correctedInvoiceDetail.iterator();
        while (iterator.hasNext() || correctedIterator.hasNext()) {
            ContractsGrantsInvoiceDetail id = (ContractsGrantsInvoiceDetail) iterator.next();
            ContractsGrantsInvoiceDetail cid = (ContractsGrantsInvoiceDetail) correctedIterator.next();
            assertTrue(id.getExpenditures().equals(cid.getExpenditures().negated()));
        }
    }

    public void testCorrectedInvoiceDetailAccounts() throws WorkflowException {
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_1 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD1.createInvoiceDetailAccountObjectCode();
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_2 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD2.createInvoiceDetailAccountObjectCode();

        KualiDecimal value1 = (new KualiDecimal(3.01));
        KualiDecimal value2 = (new KualiDecimal(2.02));

        invoiceDetailAccountObjectCode_1.setCurrentExpenditures(value1);
        invoiceDetailAccountObjectCode_2.setCurrentExpenditures(value2);

        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = new ArrayList<InvoiceDetailAccountObjectCode>();
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_1);
        invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode_2);
        document.setInvoiceDetailAccountObjectCodes(invoiceDetailAccountObjectCodes);

        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_3 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD1.createInvoiceDetailAccountObjectCode();
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode_4 = InvoiceDetailAccountObjectCodeFixture.DETAIL_ACC_OBJ_CD2.createInvoiceDetailAccountObjectCode();
        invoiceDetailAccountObjectCode_3.setCurrentExpenditures(value1);
        invoiceDetailAccountObjectCode_4.setCurrentExpenditures(value2);
        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodesOriginal = new ArrayList<InvoiceDetailAccountObjectCode>();
        invoiceDetailAccountObjectCodesOriginal.add(invoiceDetailAccountObjectCode_3);
        invoiceDetailAccountObjectCodesOriginal.add(invoiceDetailAccountObjectCode_4);

        contractsGrantsInvoiceDocumentService.correctContractsGrantsInvoiceDocument(document);
        List<InvoiceDetailAccountObjectCode> correctedInvoiceDetailAccountObjectCode = document.getInvoiceDetailAccountObjectCodes();
        Iterator iterator = invoiceDetailAccountObjectCodesOriginal.iterator();
        Iterator correctedIterator = correctedInvoiceDetailAccountObjectCode.iterator();
        while (iterator.hasNext() || correctedIterator.hasNext()) {
            InvoiceDetailAccountObjectCode id = (InvoiceDetailAccountObjectCode) iterator.next();
            InvoiceDetailAccountObjectCode cid = (InvoiceDetailAccountObjectCode) correctedIterator.next();
            assertTrue(id.getCurrentExpenditures().equals(cid.getCurrentExpenditures().negated()));
        }
    }

    public void testCorrectedAccountDetails() throws WorkflowException {
        List<InvoiceAccountDetail> accountDetail = document.getAccountDetails();
        contractsGrantsInvoiceDocumentService.correctContractsGrantsInvoiceDocument(document);
        List<InvoiceAccountDetail> correctedAccountDetail = document.getAccountDetails();
        Iterator iterator = accountDetail.iterator();
        Iterator correctedIterator = correctedAccountDetail.iterator();
        while (iterator.hasNext() || correctedIterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            InvoiceAccountDetail cid = (InvoiceAccountDetail) correctedIterator.next();
            assertTrue(id.getExpenditureAmount().equals(cid.getExpenditureAmount().negated()));
        }
    }

}
