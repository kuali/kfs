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
package org.kuali.kfs.module.ar.document.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ObjectLevelService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.integration.cg.ContractsGrantsAwardInvoiceAccountInformation;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsCategory;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailTotal;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.dataaccess.BillDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.identity.ArKimAttributes;
import org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.PdfFormFillerUtil;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.AttachmentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.DocumentException;

/**
 * This class implements the services required for Contracts and Grants Invoice Document.
 */
@Transactional
public class ContractsGrantsInvoiceDocumentServiceImpl extends CustomerInvoiceDocumentServiceImpl implements ContractsGrantsInvoiceDocumentService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceDocumentServiceImpl.class);

    protected AccountService accountService;
    protected AttachmentService attachmentService;
    protected ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService;
    protected ContractsGrantsInvoiceDocumentDao contractsGrantsInvoiceDocumentDao;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected KualiModuleService kualiModuleService;
    protected BillDao billDao;
    protected NoteService noteService;
    protected ObjectCodeService objectCodeService;
    protected ObjectLevelService objectLevelService;

    public static final String REPORT_LINE_DIVIDER = "--------------------------------------------------------------------------------------------------------------";
    protected static final SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("MM-dd-yyyy");

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#createSourceAccountingLinesAndGLPEs(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public void createSourceAccountingLines(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) throws WorkflowException {
        final List<ContractsGrantsAwardInvoiceAccountInformation> awardInvoiceAccounts = retrieveInvoiceAccountsForAward(contractsGrantsInvoiceDocument.getAward());
        boolean awardBillByInvoicingAccount = false;
        List<String> invoiceAccountDetails = new ArrayList<String>();

        // To check if the Source accounting lines are existing. If they are do nothing
        if (CollectionUtils.isEmpty(contractsGrantsInvoiceDocument.getSourceAccountingLines())) {
            // To check if the invoice account section in award has a income account set.

            String receivableOffsetOption = parameterService.getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
            boolean isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
            if (isUsingReceivableFAU) {
                if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getAward()) && CollectionUtils.isNotEmpty(awardInvoiceAccounts)) {
                    for (ContractsGrantsAwardInvoiceAccountInformation awardInvoiceAccount : awardInvoiceAccounts) {
                        if (awardInvoiceAccount.getAccountType().equals(ArPropertyConstants.INCOME_ACCOUNT)) {
                            if (awardInvoiceAccount.isActive()) {// Consider the active invoice account only.
                                awardBillByInvoicingAccount = true;
                                invoiceAccountDetails.add(awardInvoiceAccount.getChartOfAccountsCode());
                                invoiceAccountDetails.add(awardInvoiceAccount.getAccountNumber());
                                invoiceAccountDetails.add(awardInvoiceAccount.getObjectCode());
                            }
                        }
                    }
                }
            }

            // To check if award is set to bill by Contract Control Account.
            final boolean awardBillByControlAccount = (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getAward()) && contractsGrantsInvoiceDocument.getAward().getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT));

            final KualiDecimal totalMilestoneAmount = getInvoiceMilestoneTotal(contractsGrantsInvoiceDocument);
            final KualiDecimal totalBillAmount = getBillAmountTotal(contractsGrantsInvoiceDocument);

            // To retrieve the financial object code from the Organization Accounting Default.
            final OrganizationAccountingDefault organizationAccountingDefault = retrieveBillingOrganizationAccountingDefault(contractsGrantsInvoiceDocument.getBillByChartOfAccountCode(), contractsGrantsInvoiceDocument.getBilledByOrganizationCode());
            if (ObjectUtils.isNotNull(organizationAccountingDefault)) {
                if (awardBillByInvoicingAccount) {
                    // If its bill by Invoicing Account , irrespective of it is by contract control account, there would be a single source accounting line with award invoice account specified by the user.
                    if (CollectionUtils.isNotEmpty(invoiceAccountDetails) && invoiceAccountDetails.size() > 2) {
                        CustomerInvoiceDetail cide = createSourceAccountingLine(contractsGrantsInvoiceDocument.getDocumentNumber(), invoiceAccountDetails.get(0), invoiceAccountDetails.get(1), invoiceAccountDetails.get(2), contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled(), Integer.parseInt("1"));
                        contractsGrantsInvoiceDocument.getSourceAccountingLines().add(cide);
                    }
                }
                else {
                    // If its bill by Contract Control Account there would be a single source accounting line.
                    if (awardBillByControlAccount) {

                        // To get the account number and coa code for contract control account.
                        String accountNumber = null;
                        // Use the first account to get the contract control account number, as every account would have the same contract control account number.
                        List<InvoiceAccountDetail> accountDetails = contractsGrantsInvoiceDocument.getAccountDetails();
                        if (CollectionUtils.isNotEmpty(accountDetails) && StringUtils.isNotEmpty(accountDetails.get(0).getContractControlAccountNumber())) {
                            accountNumber = accountDetails.get(0).getContractControlAccountNumber();
                        }

                        String coaCode = contractsGrantsInvoiceDocument.getBillByChartOfAccountCode();
                        String objectCode = organizationAccountingDefault.getDefaultInvoiceFinancialObjectCode();

                        CustomerInvoiceDetail cide = createSourceAccountingLine(contractsGrantsInvoiceDocument.getDocumentNumber(), coaCode, accountNumber, objectCode, contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled(), Integer.parseInt("1"));
                        contractsGrantsInvoiceDocument.getSourceAccountingLines().add(cide);
                    }
                    else {
                        for (InvoiceAccountDetail invAcctD : contractsGrantsInvoiceDocument.getAccountDetails()) {
                            String accountNumber = invAcctD.getAccountNumber();
                            String coaCode = invAcctD.getChartOfAccountsCode();
                            String objectCode = organizationAccountingDefault.getDefaultInvoiceFinancialObjectCode();
                            Integer sequenceNumber = contractsGrantsInvoiceDocument.getAccountDetails().indexOf(invAcctD) + 1;// To set a sequence number for the Accounting Lines
                            // To calculate totalAmount based on the billing Frequency. Assuming that there would be only one
                            // account if its Milestone/Predetermined Schedule.
                            KualiDecimal totalAmount = KualiDecimal.ZERO;
                            if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) && totalMilestoneAmount != KualiDecimal.ZERO) {
                                totalAmount = totalMilestoneAmount;
                            }
                            else if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && totalBillAmount != KualiDecimal.ZERO) {
                                totalAmount = totalBillAmount;
                            }
                            else {
                                totalAmount = invAcctD.getExpenditureAmount();
                            }

                            CustomerInvoiceDetail cide = createSourceAccountingLine(contractsGrantsInvoiceDocument.getDocumentNumber(), coaCode, accountNumber, objectCode, totalAmount, sequenceNumber);

                            contractsGrantsInvoiceDocument.getSourceAccountingLines().add(cide);

                        }
                    }
                }
            }
        }

    }

    /**
     * Calculates the total of bill amounts if the given CINV doc is a pre-determined billing kind of CINV doc
     * @param contractsGrantsInvoiceDocument the document to total bills on
     * @return the total from the bills, or 0 if no bills exist or the CINV is not a pre-determined billing kind of CINV
     */
    protected KualiDecimal getBillAmountTotal(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        KualiDecimal totalBillAmount = KualiDecimal.ZERO;
        // To calculate the total bill amount.
        if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && !CollectionUtils.isEmpty(contractsGrantsInvoiceDocument.getInvoiceBills())) {
            for (InvoiceBill bill : contractsGrantsInvoiceDocument.getInvoiceBills()) {
                if (bill.getEstimatedAmount() != null) {
                    totalBillAmount = totalBillAmount.add(bill.getEstimatedAmount());
                }
            }
        }
        return totalBillAmount;
    }

    /**
     * Calculates the total of milestones on the given CINV, assuming the CINV is a milestone kind of CINV
     * @param contractsGrantsInvoiceDocument the document to find total milestones on
     * @return the total of the milestones, or 0 if no milestones exist or if the CINV is not a milestone schedule CINV
     */
    protected KualiDecimal getInvoiceMilestoneTotal(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        KualiDecimal totalMilestoneAmount = KualiDecimal.ZERO;
        // To calculate the total milestone amount.
        if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !CollectionUtils.isEmpty(contractsGrantsInvoiceDocument.getInvoiceMilestones())) {
            for (InvoiceMilestone milestone : contractsGrantsInvoiceDocument.getInvoiceMilestones()) {
                if (milestone.getMilestoneAmount() != null) {
                    totalMilestoneAmount = totalMilestoneAmount.add(milestone.getMilestoneAmount());
                }
            }
        }
        return totalMilestoneAmount;
    }

    /**
     * Retrieves the OrganizationAccountingDefault record for the given billing organization
     * @param billByChartOfAccountsCode the chart of the billing organization
     * @param billByOrganizationCode the organization code of the billing organization
     * @return the OrganizationAccountingDefault for the given chart and organization code
     */
    protected OrganizationAccountingDefault retrieveBillingOrganizationAccountingDefault(final String billByChartOfAccountsCode, final String billByOrganizationCode) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        Integer currentYear = universityDateService.getCurrentFiscalYear();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, billByChartOfAccountsCode);
        criteria.put(ArPropertyConstants.CustomerInvoiceItemCodes.ORGANIZATION_CODE, billByOrganizationCode);
        // Need to avoid hitting database in the loop. option would be to set the financial object code when the form loads and save it somewhere.
        OrganizationAccountingDefault organizationAccountingDefault = businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
        return organizationAccountingDefault;
    }

    /**
     * Retrieves the CG Award Invoice Account Information associated with the given award
     * @param award the award to find invoice account information for
     * @return a List of invoice account information associated with the given award
     */
    protected List<ContractsGrantsAwardInvoiceAccountInformation> retrieveInvoiceAccountsForAward(final ContractsAndGrantsBillingAward award) {
        if (ObjectUtils.isNotNull(award)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
            map.put(KFSPropertyConstants.ACTIVE, true);
            return kualiModuleService.getResponsibleModuleService(ContractsGrantsAwardInvoiceAccountInformation.class).getExternalizableBusinessObjectsList(ContractsGrantsAwardInvoiceAccountInformation.class, map);
        }
        return new ArrayList<ContractsGrantsAwardInvoiceAccountInformation>();
    }

    /**
     * @param docNum
     * @param coaCode
     * @param acctNum
     * @param obCode
     * @param totalAmount
     * @param seqNum
     * @return
     */
    protected CustomerInvoiceDetail createSourceAccountingLine(String docNum, String coaCode, String acctNum, String obCode, KualiDecimal totalAmount, Integer seqNum) {
        CustomerInvoiceDetail cid = new CustomerInvoiceDetail();
        cid.setDocumentNumber(docNum);

        cid.setAccountNumber(acctNum);
        cid.setChartOfAccountsCode(coaCode);
        cid.setFinancialObjectCode(obCode);

        cid.setSequenceNumber(seqNum);
        cid.setInvoiceItemQuantity(BigDecimal.ONE);
        cid.setInvoiceItemUnitOfMeasureCode("EA");


        cid.setInvoiceItemUnitPrice(totalAmount);
        cid.setAmount(totalAmount);
        if (totalAmount.isNegative()) {
            cid.setInvoiceItemDiscountLineNumber(seqNum);
        }
        // To get AR Object codes for the GLPEs .... as it is not being called implicitly..

        cid.setAccountsReceivableObjectCode(customerInvoiceDetailService.getAccountsReceivableObjectCodeBasedOnReceivableParameter(cid));
        return cid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#recalculateNewTotalBilled(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public void recalculateNewTotalBilled(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {

        ContractsGrantsInvoiceDetail totalCostInvoiceDetail = contractsGrantsInvoiceDocument.getTotalCostInvoiceDetail();

        // To verify the expenditure amounts have been changed and
        // update the invoiceDetailObjectCode
        boolean expenditureValueChanged = adjustObjectCodeAmountsIfChanged(contractsGrantsInvoiceDocument);

        if (expenditureValueChanged) {
            // update Total Direct Cost in the Invoice Detail Tab
            KualiDecimal totalDirectCostExpenditures = getInvoiceDetailExpenditureSum(contractsGrantsInvoiceDocument.getInvoiceDetailsWithoutIndirectCosts());

            // Set expenditures to Direct Cost invoice Details
            ContractsGrantsInvoiceDetail totalDirectCostInvoiceDetail = contractsGrantsInvoiceDocument.getTotalDirectCostInvoiceDetail();
            if (ObjectUtils.isNotNull(totalDirectCostInvoiceDetail)){
                totalDirectCostInvoiceDetail.setExpenditures(totalDirectCostExpenditures);
            }

            // update Total Indirect Cost in the Invoice Detail Tab
            KualiDecimal totalInDirectCostExpenditures = getInvoiceDetailExpenditureSum(contractsGrantsInvoiceDocument.getInvoiceDetailsIndirectCostOnly());

            // Set expenditures to Indirect Cost invoice Details
            ContractsGrantsInvoiceDetail totalInDirectCostInvoiceDetail = contractsGrantsInvoiceDocument.getTotalInDirectCostInvoiceDetail();
            if (ObjectUtils.isNotNull(totalInDirectCostInvoiceDetail)){
                totalInDirectCostInvoiceDetail.setExpenditures(totalInDirectCostExpenditures);
            }

            // Set the total for Total Cost Invoice Details section.
            if(ObjectUtils.isNotNull(totalCostInvoiceDetail)) {
                totalCostInvoiceDetail.setExpenditures(totalDirectCostInvoiceDetail.getExpenditures().add(totalInDirectCostExpenditures));
            }
            recalculateAccountDetails(contractsGrantsInvoiceDocument.getAccountDetails(), contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes());

            // update source accounting lines
            updateInvoiceSourceAccountingLines(contractsGrantsInvoiceDocument.getAccountDetails(), contractsGrantsInvoiceDocument.getSourceAccountingLines());


        }

        // set the General Detail Total to be billed - there would be only one value for Total Cost invoice Details.
        contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().setNewTotalBilled(totalCostInvoiceDetail.getExpenditures().add(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBilledToDateAmount()));
    }

    /**
     * @param invoiceDetails
     * @return
     */
    public KualiDecimal getInvoiceDetailExpenditureSum(List<ContractsGrantsInvoiceDetail> invoiceDetails) {
        KualiDecimal totalExpenditures = KualiDecimal.ZERO;
        for (ContractsGrantsInvoiceDetail invoiceDetail : invoiceDetails) {
            totalExpenditures = totalExpenditures.add(invoiceDetail.getExpenditures());
        }
        return totalExpenditures;
    }


    /**
     * @param invoiceAccountDetails
     * @param sourceAccountingLines
     */
    protected void updateInvoiceSourceAccountingLines(List<InvoiceAccountDetail> invoiceAccountDetails, List sourceAccountingLines) {

        if (sourceAccountingLines.size() > 1) {// Invoice By Award
            for (CustomerInvoiceDetail cide : (List<CustomerInvoiceDetail>) sourceAccountingLines) {
                for (InvoiceAccountDetail invoiceAccountDetail : invoiceAccountDetails) {
                    if (cide.getAccountNumber().equals(invoiceAccountDetail.getAccountNumber())) {
                        cide.setInvoiceItemUnitPrice(invoiceAccountDetail.getExpenditureAmount());
                        cide.setAmount(invoiceAccountDetail.getExpenditureAmount());
                    }
                }
            }
        }
        // This would be a case where the invoice is generated by Contract Control Account or Invoice By Account.
        else if (sourceAccountingLines.size() == 1) {
            KualiDecimal totalExpenditureAmount = KualiDecimal.ZERO;
            if (invoiceAccountDetails.size() == 1) {// Invoice By Account
                // update source accounting lines
                CustomerInvoiceDetail cide = (CustomerInvoiceDetail) sourceAccountingLines.get(0);
                cide.setInvoiceItemUnitPrice(invoiceAccountDetails.get(0).getExpenditureAmount());
                cide.setAmount(invoiceAccountDetails.get(0).getExpenditureAmount());
            }
            else {// Invoice By Contract Control Account
                for (InvoiceAccountDetail invoiceAccountDetail : invoiceAccountDetails) {
                    totalExpenditureAmount = totalExpenditureAmount.add(invoiceAccountDetail.getExpenditureAmount());
                }
                // update source accounting lines
                CustomerInvoiceDetail cide = (CustomerInvoiceDetail) sourceAccountingLines.get(0);
                cide.setInvoiceItemUnitPrice(totalExpenditureAmount);
                cide.setAmount(totalExpenditureAmount);
            }
        }

    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#prorateBill(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public void prorateBill(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) throws WorkflowException {
        KualiDecimal totalCost = new KualiDecimal(0); // Amount to be billed on this invoice
        // must iterate through the invoice details because the user might have manually changed the value
        for (ContractsGrantsInvoiceDetail invD : contractsGrantsInvoiceDocument.getInvoiceDetailsWithIndirectCosts()) {
            totalCost = totalCost.add(invD.getExpenditures());
        }
        KualiDecimal billedTotalCost = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBilledToDateAmount(); // Total Billed
        // so far
        KualiDecimal accountAwardTotal = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getAwardTotal(); // AwardTotal

        if (accountAwardTotal.subtract(billedTotalCost).isGreaterEqual(new KualiDecimal(0))) {
            KualiDecimal amountEligibleForBilling = accountAwardTotal.subtract(billedTotalCost);
            // only recalculate if the current invoice is over what's billable.

            if (totalCost.isGreaterThan(amountEligibleForBilling)) {
                // use BigDecimal because percentage should not have only a scale of 2, we need more for accuracy
                BigDecimal percentage = amountEligibleForBilling.bigDecimalValue().divide(totalCost.bigDecimalValue(), 10, BigDecimal.ROUND_HALF_DOWN);
                KualiDecimal amountToBill = new KualiDecimal(0); // use to check if rounding has left a few cents off

                for (ContractsGrantsInvoiceDetail invD : contractsGrantsInvoiceDocument.getInvoiceDetailsWithIndirectCosts()) {
                    BigDecimal newValue = invD.getExpenditures().bigDecimalValue().multiply(percentage);
                    KualiDecimal newKualiDecimalValue = new KualiDecimal(newValue.setScale(2, BigDecimal.ROUND_DOWN));
                    invD.setExpenditures(newKualiDecimalValue);
                    amountToBill = amountToBill.add(newKualiDecimalValue);
                }
                // There will be some amount left, since we are rounding down. Display warning for user to manually
                // correct/distribute where they want to put the remainder
                if (!amountToBill.equals(amountEligibleForBilling)) {
                    KualiDecimal remaining = amountEligibleForBilling.subtract(amountToBill);
                    LOG.info("Amount Set for Billing does not match Total Amount Eligible For Billing.  There is " + remaining.toString() + " remaining for billing.");
                    if (remaining.isPositive()) {
                        GlobalVariables.getMessageMap().putWarning(ArConstants.PRORATE_WARNING, ArKeyConstants.ContractsGrantsInvoiceConstants.WARNING_PRORATE_VALUE_IS_LESS_THAN_ELIGIBLE_FOR_BILLING, amountToBill.toString(), remaining.toString());
                    }
                    else {
                        GlobalVariables.getMessageMap().putWarning(ArConstants.PRORATE_WARNING, ArKeyConstants.ContractsGrantsInvoiceConstants.WARNING_PRORATE_VALUE_IS_MORE_THAN_ELIGIBLE_FOR_BILLING, amountToBill.toString(), remaining.abs().toString());
                    }
                }
                recalculateNewTotalBilled(contractsGrantsInvoiceDocument);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#addToAccountObjectCodeBilledTotal(java.util.List)
     */
    @Override
    public void addToAccountObjectCodeBilledTotal(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
            totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, invoiceDetailAccountObjectCode.getProposalNumber());
            totalBilledKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, invoiceDetailAccountObjectCode.getChartOfAccountsCode());
            totalBilledKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, invoiceDetailAccountObjectCode.getAccountNumber());
            totalBilledKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, invoiceDetailAccountObjectCode.getFinancialObjectCode());

            List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilledList = (List<AwardAccountObjectCodeTotalBilled>) businessObjectService.findMatching(AwardAccountObjectCodeTotalBilled.class, totalBilledKeys);
            if (awardAccountObjectCodeTotalBilledList != null && !awardAccountObjectCodeTotalBilledList.isEmpty()) {
                AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled = awardAccountObjectCodeTotalBilledList.get(0);
                awardAccountObjectCodeTotalBilled.setTotalBilled(awardAccountObjectCodeTotalBilled.getTotalBilled().add(invoiceDetailAccountObjectCode.getCurrentExpenditures()));
                getBusinessObjectService().save(awardAccountObjectCodeTotalBilled);
            }
            else {
                AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled = new AwardAccountObjectCodeTotalBilled();
                awardAccountObjectCodeTotalBilled.setProposalNumber(invoiceDetailAccountObjectCode.getProposalNumber());
                awardAccountObjectCodeTotalBilled.setChartOfAccountsCode(invoiceDetailAccountObjectCode.getChartOfAccountsCode());
                awardAccountObjectCodeTotalBilled.setAccountNumber(invoiceDetailAccountObjectCode.getAccountNumber());
                awardAccountObjectCodeTotalBilled.setFinancialObjectCode(invoiceDetailAccountObjectCode.getFinancialObjectCode());
                awardAccountObjectCodeTotalBilled.setTotalBilled(invoiceDetailAccountObjectCode.getCurrentExpenditures());
                getBusinessObjectService().save(awardAccountObjectCodeTotalBilled);
            }
        }
    }

    /**
     * If any of the current expenditures for the cost categories on the Contracts Grants Invoice Document have changed,
     * recalculate the Object Code amounts.
     *
     * @param contractsGrantsInvoiceDocument document containing cost categories to review
     * @return true if expenditure value changed, false otherwise
     */
    protected boolean adjustObjectCodeAmountsIfChanged(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        boolean isExpenditureValueChanged = false;

        // put the invoiceDetailAccountObjectCode into a map based on category
        List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes();
        Map<String, List<InvoiceDetailAccountObjectCode>> invoiceDetailAccountObjectCodeMap = new HashMap<String, List<InvoiceDetailAccountObjectCode>>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            String categoryCode = invoiceDetailAccountObjectCode.getCategoryCode();
            List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodeList = invoiceDetailAccountObjectCodeMap.get(categoryCode);
            // if new category, create new list to put into map
            if (invoiceDetailAccountObjectCodeList == null) {
                List<InvoiceDetailAccountObjectCode> newInvoiceDetailAccountObjectCodeList = new ArrayList<InvoiceDetailAccountObjectCode>();
                newInvoiceDetailAccountObjectCodeList.add(invoiceDetailAccountObjectCode);
                invoiceDetailAccountObjectCodeMap.put(categoryCode, newInvoiceDetailAccountObjectCodeList);
            }
            // else, if list is found, add it to existing list
            else {
                invoiceDetailAccountObjectCodeMap.get(categoryCode).add(invoiceDetailAccountObjectCode);
            }
        }

        // figure out if any of the current expenditures for the category has been changed. If yes, then update the invoiceDetailObjectCode and update account details
        for (ContractsGrantsInvoiceDetail invoiceDetail : contractsGrantsInvoiceDocument.getInvoiceDetailsWithIndirectCosts()) {
            KualiDecimal total = getSumOfExpendituresOfCategory(invoiceDetailAccountObjectCodeMap.get(invoiceDetail.getCategoryCode()));
            // To set expenditures to zero if its blank - to avoid exceptions.
            if (ObjectUtils.isNull(invoiceDetail.getExpenditures())) {
                invoiceDetail.setExpenditures(KualiDecimal.ZERO);
            }

            if (invoiceDetail.getExpenditures().compareTo(total) != 0) {
                recalculateObjectCodeByCategory(contractsGrantsInvoiceDocument, invoiceDetail, total, invoiceDetailAccountObjectCodeMap.get(invoiceDetail.getCategoryCode()));
                isExpenditureValueChanged = true;
            }
        }
        return isExpenditureValueChanged;
    }

    /**
     * @param invoiceDetailAccountObjectCodes
     * @return
     */
    protected KualiDecimal getSumOfExpendituresOfCategory(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        KualiDecimal total = KualiDecimal.ZERO;
        // null can occur if this category has no invoice detail objectcode amounts
        if (invoiceDetailAccountObjectCodes != null) {
            for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
                total = total.add(invoiceDetailAccountObjectCode.getCurrentExpenditures());
            }
        }
        return total;
    }

    /**
     * This method recalculates the invoiceDetailAccountObjectCode in one category that sits behind the scenes of the invoice
     * document.
     *
     * @param contractsGrantsInvoiceDocument
     * @param invoiceDetail
     * @param total is the sum of the current expenditures from all the object codes in that category
     * @param invoiceDetailAccountObjectCodes
     */
    protected void recalculateObjectCodeByCategory(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument, ContractsGrantsInvoiceDetail invoiceDetail, KualiDecimal total, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        KualiDecimal currentExpenditure = invoiceDetail.getExpenditures();
        KualiDecimal newTotalAmount = KualiDecimal.ZERO;

        // if the sum of the object codes is 0, then distribute the expenditure change evenly to all object codes in the category
        if (total.compareTo(KualiDecimal.ZERO) == 0) {
            if (invoiceDetailAccountObjectCodes != null) {
                int numberOfObjectCodes = invoiceDetailAccountObjectCodes.size();
                if (numberOfObjectCodes != 0) {
                    KualiDecimal newAmount = new KualiDecimal(currentExpenditure.bigDecimalValue().divide(new BigDecimal(numberOfObjectCodes), 10, BigDecimal.ROUND_HALF_DOWN));
                    for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
                        invoiceDetailAccountObjectCode.setCurrentExpenditures(newAmount);
                        newTotalAmount = newTotalAmount.add(newAmount);
                    }
                }
            }
            else { // if the list is null, then there are no account/object code in the gl_balance_t. So assign the amount to the first object code in the category
                assignCurrentExpenditureToNonExistingAccountObjectCode(contractsGrantsInvoiceDocument, invoiceDetail);
            }
        }
        else {

            for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
                // this may rarely happen
                // if the initial total is 0, that means none of the object codes in this category is set to bill. If this amount is changed, then just divide evenly among all object codes.
                KualiDecimal newAmount = (new KualiDecimal(invoiceDetailAccountObjectCode.getCurrentExpenditures().bigDecimalValue().divide(total.bigDecimalValue(), 10, BigDecimal.ROUND_HALF_DOWN).multiply(currentExpenditure.bigDecimalValue())));
                invoiceDetailAccountObjectCode.setCurrentExpenditures(newAmount);
                newTotalAmount = newTotalAmount.add(newAmount);
            }

            int remainderFromRounding = currentExpenditure.subtract(newTotalAmount).multiply(new KualiDecimal(100)).intValue();

            // add remainder from rounding
            KualiDecimal addAmount = new KualiDecimal(0.01);
            if (remainderFromRounding < 0) {
                addAmount = new KualiDecimal(-0.01);
                remainderFromRounding = Math.abs(remainderFromRounding);
            }

            for (int i = 0, j = 0; i < remainderFromRounding; i++, j++) {
                // reset j if its more than size of list
                if (j >= invoiceDetailAccountObjectCodes.size()) {
                    j = 0;
                }
                invoiceDetailAccountObjectCodes.get(j).setCurrentExpenditures(invoiceDetailAccountObjectCodes.get(j).getCurrentExpenditures().add(addAmount));
            }
        }
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @param invoiceDetail
     */
    protected void assignCurrentExpenditureToNonExistingAccountObjectCode(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument, ContractsGrantsInvoiceDetail invoiceDetail) {
        String categoryCode = invoiceDetail.getCategoryCode();
        if (StringUtils.isBlank(categoryCode)) {
            LOG.error("Category Code can not be null during recalculation of account object code for Contracts and Grants Invoice Document.");
        }
        // get the category that matches this category code.
        final ContractsAndGrantsCategory category = businessObjectService.findBySinglePrimaryKey(ContractsAndGrantsCategory.class, categoryCode);

        // got the category now.
        if (category != null) {
            final KualiDecimal oneCent = new KualiDecimal(0.01);

            int size = contractsGrantsInvoiceDocument.getAccountDetails().size();
            KualiDecimal amount = new KualiDecimal(invoiceDetail.getExpenditures().bigDecimalValue().divide(new BigDecimal(size), 10, BigDecimal.ROUND_DOWN));
            KualiDecimal remainder = invoiceDetail.getExpenditures().subtract(amount.multiply(new KualiDecimal(size)));

            for (InvoiceAccountDetail invoiceAccountDetail : contractsGrantsInvoiceDocument.getAccountDetails()) {
                // get the first object code from this category
                String objectCode = (String) getObjectCodeArrayFromSingleCategory(category, contractsGrantsInvoiceDocument).toArray()[0];
                InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = new InvoiceDetailAccountObjectCode();
                invoiceDetailAccountObjectCode.setDocumentNumber(contractsGrantsInvoiceDocument.getDocumentNumber());
                invoiceDetailAccountObjectCode.setProposalNumber(contractsGrantsInvoiceDocument.getProposalNumber());
                invoiceDetailAccountObjectCode.setFinancialObjectCode(objectCode);
                invoiceDetailAccountObjectCode.setCategoryCode(categoryCode);
                invoiceDetailAccountObjectCode.setAccountNumber(invoiceAccountDetail.getAccountNumber());
                invoiceDetailAccountObjectCode.setChartOfAccountsCode(invoiceAccountDetail.getChartOfAccountsCode());
                invoiceDetailAccountObjectCode.setCumulativeExpenditures(KualiDecimal.ZERO); // it's 0.00 that's why we are in this section to begin with.
                invoiceDetailAccountObjectCode.setTotalBilled(KualiDecimal.ZERO); // this is also 0.00 because it has never been billed before

                // tack on or remove one penny until the remainder is 0.
                if (remainder.isGreaterThan(KualiDecimal.ZERO)) {
                    amount = amount.add(oneCent);
                    remainder = remainder.subtract(oneCent);
                }
                else if (remainder.isLessThan(KualiDecimal.ZERO)) {
                    amount = amount.subtract(oneCent);
                    remainder = remainder.add(oneCent);
                }
                invoiceDetailAccountObjectCode.setCurrentExpenditures(amount);

                List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes();
                if (invoiceDetailAccountObjectCodes.contains(invoiceDetailAccountObjectCode)) {
                    // update existing code
                    InvoiceDetailAccountObjectCode original = invoiceDetailAccountObjectCodes.get(invoiceDetailAccountObjectCodes.indexOf(invoiceDetailAccountObjectCode));
                    original.setCurrentExpenditures(amount);
                    original.setCategoryCode(categoryCode);
                } else {
                    // add this single account object code item to the list in the Map
                    contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes().add(invoiceDetailAccountObjectCode);
                }
            }
        }
        else {
            LOG.error("Category Code cannot be found from the category list during recalculation of account object code for Contracts and Grants Invoice Document.");
        }

        getObjectCodeArrayFromContractsAndGrantsCategories(contractsGrantsInvoiceDocument);
    }

    /**
     * @param invoiceAccountDetails
     * @param invoiceDetailAccountObjectCodes
     */
    public void recalculateAccountDetails(List<InvoiceAccountDetail> invoiceAccountDetails, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        Map<String, KualiDecimal> currentExpenditureByAccountNumberMap = new HashMap<String, KualiDecimal>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            String accountNumber = invoiceDetailAccountObjectCode.getAccountNumber();
            KualiDecimal expenditureSum = currentExpenditureByAccountNumberMap.get(accountNumber);
            // if account number not found in map, then create new total, 0
            if (expenditureSum == null) {
                expenditureSum = new KualiDecimal(0);
            }
            expenditureSum = expenditureSum.add(invoiceDetailAccountObjectCode.getCurrentExpenditures());
            currentExpenditureByAccountNumberMap.put(accountNumber, expenditureSum);
        }

        for (InvoiceAccountDetail invoiceAccountDetail : invoiceAccountDetails) {
            invoiceAccountDetail.setExpenditureAmount(currentExpenditureByAccountNumberMap.get(invoiceAccountDetail.getAccountNumber()));
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getAwardBilledToDateAmountByProposalNumber(java.lang.Long)
     */
    @Override
    public KualiDecimal getAwardBilledToDateAmountByProposalNumber(Long proposalNumber) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        List<AwardAccountObjectCodeTotalBilled> accountObjectCodeTotalBilledList = (List<AwardAccountObjectCodeTotalBilled>) businessObjectService.findMatching(AwardAccountObjectCodeTotalBilled.class, keys);
        KualiDecimal billedToDateAmount = KualiDecimal.ZERO;
        for (AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled : accountObjectCodeTotalBilledList) {
            billedToDateAmount = billedToDateAmount.add(awardAccountObjectCodeTotalBilled.getTotalBilled());
        }
        return billedToDateAmount;
    }

    /**
     * This method retrieves all CG invoice document that match the given field values
     *
     * @param fieldValues
     * @return
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> retrieveAllCGInvoicesByCriteria(Map fieldValues) {
        return contractsGrantsInvoiceDocumentDao.getMatchingInvoicesByCollection(fieldValues);
    }

    /**
     * This method retrieves CG invoice documents that match the given field values and excludes
     * the given outside collection agency code
     *
     * @param fieldValues field values to use as criteria for the search
     * @param outsideColAgencyCodeToExclude Outside collector Agency code to exclude
     * @return a collection of invoices matching the given input
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> retrieveAllCGInvoicesForReferallExcludingOutsideCollectionAgency(Map fieldValues, String outsideColAgencyCodeToExclude) {
        return contractsGrantsInvoiceDocumentDao.getMatchingInvoicesForReferallExcludingOutsideCollectionAgency(fieldValues, outsideColAgencyCodeToExclude);
    }

    /**
     * This method calculates the Budget and cumulative amount for Award Account
     *
     * @param awardAccount
     * @return
     */
    @Override
    public KualiDecimal getBudgetAndActualsForAwardAccount(ContractsAndGrantsBillingAwardAccount awardAccount, String balanceTypeCode, Date awardBeginningDate) {
        List<Balance> glBalances = new ArrayList<Balance>();
        KualiDecimal balanceAmount = KualiDecimal.ZERO;
        KualiDecimal balAmt = KualiDecimal.ZERO;
        Integer currentYear = universityDateService.getCurrentFiscalYear();
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();

        if (ObjectUtils.isNotNull(awardBeginningDate)) {
            Integer fiscalYear = universityDateService.getFiscalYear(awardBeginningDate);

            if (ObjectUtils.isNotNull(fiscalYear)) {
                for (Integer i = fiscalYear; i <= currentYear; i++) {
                    fiscalYears.add(i);
                }
                for (Integer eachFiscalYr : fiscalYears) {

                    Map<String, Object> balanceKeys = new HashMap<String, Object>();
                    balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
                    balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
                    balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
                    balanceKeys.put(KFSPropertyConstants.BALANCE_TYPE_CODE, balanceTypeCode);
                    balanceKeys.put(KFSPropertyConstants.OBJECT_TYPE_CODE, ArPropertyConstants.EXPENSE_OBJECT_TYPE);
                    glBalances.addAll(businessObjectService.findMatching(Balance.class, balanceKeys));
                }
                for (Balance bal : glBalances) {
                    if (ObjectUtils.isNull(bal.getSubAccount()) || ObjectUtils.isNull(bal.getSubAccount().getA21SubAccount()) || !StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
                        balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                        balanceAmount = balanceAmount.add(balAmt);
                    }
                }
            }
        }
        return balanceAmount;
    }

    /**
     * This method serves as a create and update. When it is first called, the List<InvoiceSuspensionCategory> is empty. This list
     * then gets populated with invoiceSuspensionCategories where the test fails. Each time the document goes through validation,
     * and this method gets called, it will update the list by adding or remvoing the suspension categories
     *
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#updateSuspensionCategoriesOnDocument(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public void updateSuspensionCategoriesOnDocument(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {

        ContractsAndGrantsBillingAward award = contractsGrantsInvoiceDocument.getAward();
        String documentNumber = contractsGrantsInvoiceDocument.getDocumentNumber();

        List<String> suspensionCategoryCodes = new ArrayList<String>(); // list of existing codes

        // the first time this is checked, the list will be empty. On subsequent checks, if the list is not empty, it will add the
        // suspension codes to the
        // List<String> suspensionCategoryCodes. This list is where we keep track of the codes, and base off this list, we will
        // create actual suspension category objects.
        List<InvoiceSuspensionCategory> invoiceSuspensionCategories = contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories();
        for (InvoiceSuspensionCategory invoiceSuspensionCategory : invoiceSuspensionCategories) {
            suspensionCategoryCodes.add(invoiceSuspensionCategory.getSuspensionCategoryCode());
        }

        // validation suspension code - Check if invoice is created after award expiration date
        if (isInvoiceCreateDateAfterAwardEndingDate(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.BILL_DATE_EXCEEDS_THE_AWARD_STOP_DATE);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.BILL_DATE_EXCEEDS_THE_AWARD_STOP_DATE);
        }

        // validation suspension code - Check if amount to bill with amount already billed is greater than the award total amount
        if (isBillAmountExceedAwardTotalAmount(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.NEW_TOTAL_BILLED_AMOUNT_EXCEEDS_AWARD_TOTAL);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.NEW_TOTAL_BILLED_AMOUNT_EXCEEDS_AWARD_TOTAL);
        }

        // validation suspension code - Check if invoice amount is less than the minimum allowed specified by the award
        if (isInvoiceAmountLessThanInvoiceMinimumRequirements(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.INVOICE_AMOUNT_IS_LESS_THAN_INVOICE_MINIMUM_REQUIREMENT);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.INVOICE_AMOUNT_IS_LESS_THAN_INVOICE_MINIMUM_REQUIREMENT);
        }

        // validation suspension code - Check to see that an attachment is made if it is required by the award
        if (contractsGrantsInvoiceDocument.getAward().isAdditionalFormsRequiredIndicator()) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.REPORTS_ARE_REQUIRED_TO_BE_ATTACHED);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.REPORTS_ARE_REQUIRED_TO_BE_ATTACHED);
        }

        // validation suspension code - Make sure the Primary Address is completed
        if (!isCustomerPrimaryAddressComplete(contractsGrantsInvoiceDocument.getInvoiceAddressDetails())) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.CUSTOMER_PRIMARY_ADDRESS_NOT_COMPLETE);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.CUSTOMER_PRIMARY_ADDRESS_NOT_COMPLETE);
        }

        // validation suspension code - Check to see if the Alternate address is completed if it was entered to begin with
        if (!isCustomerAlternateAddressComplete(contractsGrantsInvoiceDocument.getInvoiceAddressDetails())) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.CUSTOMER_ALTERNATE_ADDRESS_NOT_COMPLETE);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.CUSTOMER_ALTERNATE_ADDRESS_NOT_COMPLETE);
        }

        // validation suspension code - Make sure invoice is final if the award is already expired
        if (isInvoiceNotFinalAndAwardExpired(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.INVOICE_NOT_FINAL_AND_EXPIRATION_DATE_REACHED);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.INVOICE_NOT_FINAL_AND_EXPIRATION_DATE_REACHED);
        }

        // validation suspension code - Check to see if cost category codes are setup correctly. An object code might not
        // be assigned or could be assigned to more than one Cost Category. Check by comparing total current expenditure
        // to the sum of account current expenditure
        if (!isAwardIBillingFrequencyIsPredetermined(award) && !isAwardBillingFrequencyIsMilestone(award) && !isCategoryCumulativeExpenditureMatchAccountCumulativeExpenditureSum(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.CGB_CATEGORY_CODE_SETUP_INCORRECTLY);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.CGB_CATEGORY_CODE_SETUP_INCORRECTLY);
        }

        // validation suspension code - Check to see if Loc Amount is sufficient
        if (!isLocAmountSufficent(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.LOC_REMAINING_AMOUNT_IS_NOT_SUFFICIENT);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.LOC_REMAINING_AMOUNT_IS_NOT_SUFFICIENT);
        }

        // validation suspension code - Check to see if award has any active but expired account
        if (doesAwardHaveAnyActiveExpiredAccounts(award)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AWARD_HAS_ACTIVE_BUT_EXPIRED_ACCOUNT);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AWARD_HAS_ACTIVE_BUT_EXPIRED_ACCOUNT);
        }

        // validation suspension code - Check to see if award has 'Suspend Invoicing' enabled
        if (award.isSuspendInvoicingIndicator()) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AWARD_SUSPENDED_BY_USER);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AWARD_SUSPENDED_BY_USER);
        }

        // validation suspension code - Check to see if invoice type is missing from award
        if (StringUtils.isEmpty(award.getInvoicingOptions())) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.INVOICE_TYPE_IS_MISSING);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.INVOICE_TYPE_IS_MISSING);
        }

        // validation suspension code - Check to see if award has closed account which still has current expenditure
        if (doesAwardHaveClosedAccountWithCurrentExpenditures(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AWARD_HAS_CLOSED_ACCOUNT_WITH_CURRENT_EXPENDITURES);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AWARD_HAS_CLOSED_ACCOUNT_WITH_CURRENT_EXPENDITURES);
        }

        // validation suspension code - Check to see if invoice type is missing from award
        if (isAwardMarkedStopWork(award)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AWARD_HAS_STOP_WORK_MARKED);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AWARD_HAS_STOP_WORK_MARKED);
        }
    }

    /**
     * @param suspensionCategoryCodes
     * @param invoiceSuspensionCategories
     * @param documentNumber
     * @param suspensionCategoryCode
     */
    // this method adds a new InvoiceSuspensionCategory to the List<InvoiceSuspensionCategory> if it does not already exist.
    protected void addSuspensionCategoryToDocument(List<String> suspensionCategoryCodes, List<InvoiceSuspensionCategory> invoiceSuspensionCategories, String documentNumber, String suspensionCategoryCode) {
        if (!suspensionCategoryCodes.contains(suspensionCategoryCode)) { // check prevents duplicate
            // To check if the suspension category is active.
            SuspensionCategory suspensionCategory = businessObjectService.findBySinglePrimaryKey(SuspensionCategory.class, suspensionCategoryCode);
            if (ObjectUtils.isNotNull(suspensionCategory) && suspensionCategory.isActive()) {
                invoiceSuspensionCategories.add(new InvoiceSuspensionCategory(documentNumber, suspensionCategoryCode));
            }
        }
    }

    /**
     * @param suspensionCategoryCodes
     * @param invoiceSuspensionCategories
     * @param suspensionCategoryCode
     */
    // this method removes a suspensionCategoryCode from the List<String> and removes the object InvoiceSuspensionCategory from the
    // List<InvoiceSuspensionCategory>
    protected void removeSuspensionCategoryFromDocument(List<String> suspensionCategoryCodes, List<InvoiceSuspensionCategory> invoiceSuspensionCategories, String suspensionCategoryCode) {
        if (suspensionCategoryCodes.contains(suspensionCategoryCode)) {
            suspensionCategoryCodes.remove(suspensionCategoryCode);
            for (InvoiceSuspensionCategory invoiceSuspensionCategory : invoiceSuspensionCategories) {
                if (suspensionCategoryCode.equals(invoiceSuspensionCategory.getSuspensionCategoryCode())) {
                    invoiceSuspensionCategories.remove(invoiceSuspensionCategory);
                    break;
                }
            }
        }
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isInvoiceCreateDateAfterAwardEndingDate(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        Date documentDate = new Date(contractsGrantsInvoiceDocument.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis());
        Date awardEndingDate = contractsGrantsInvoiceDocument.getAward().getAwardEndingDate();

        // remove time
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(documentDate.getTime());
        cal.set(cal.HOUR_OF_DAY, 0);
        cal.set(cal.MINUTE, 0);
        cal.set(cal.SECOND, 0);
        cal.set(cal.MILLISECOND, 0);
        documentDate.setTime(cal.getTimeInMillis());

        // remove time
        cal = Calendar.getInstance();
        cal.setTimeInMillis(awardEndingDate.getTime());
        cal.set(cal.HOUR_OF_DAY, 0);
        cal.set(cal.MINUTE, 0);
        cal.set(cal.SECOND, 0);
        cal.set(cal.MILLISECOND, 0);
        awardEndingDate.setTime(cal.getTimeInMillis());

        return documentDate.after(awardEndingDate);
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    protected boolean isBillAmountExceedAwardTotalAmount(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        return contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().isGreaterThan(contractsGrantsInvoiceDocument.getAward().getAwardTotalAmount());
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    protected boolean isInvoiceAmountLessThanInvoiceMinimumRequirements(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        KualiDecimal invoiceMinimumAmount = contractsGrantsInvoiceDocument.getAward().getMinInvoiceAmount();
        if (invoiceMinimumAmount == null) {
            return false; // if no minimum specified, then no limit
        }
        return contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().isLessThan(invoiceMinimumAmount);
    }

    /**
     * @param agency
     * @return
     */
    protected boolean isCustomerPrimaryAddressComplete(List<InvoiceAddressDetail> addressDetails) {
        for (InvoiceAddressDetail addressDetail : addressDetails) {
            if (StringUtils.equals(ArConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE, addressDetail.getCustomerAddressTypeCode())) {
                if (ObjectUtils.isNull(addressDetail.getCustomerAddress())) {
                    addressDetail.refreshReferenceObject(ArPropertyConstants.CustomerFields.CUSTOMER_ADDRESS);
                }
                return isCustomerAddressComplete(addressDetail.getCustomerAddress());
            }
        }
        return false;
    }

    /**
     * @param agency
     * @return
     */
    protected boolean isCustomerAlternateAddressComplete(List<InvoiceAddressDetail> addressDetails) {
        for (InvoiceAddressDetail addressDetail : addressDetails) {
            if (StringUtils.equals(ArConstants.AGENCY_ALTERNATE_ADDRESSES_TYPE_CODE, addressDetail.getCustomerAddressTypeCode())) {
                return isCustomerAddressComplete(addressDetail.getCustomerAddress());
            }
        }
        return true; // if no alternate address entered at all, then that is ok
    }

    /**
     * @param agencyAddress
     * @return
     */
    protected boolean isCustomerAddressComplete(CustomerAddress customerAddress) {
        return !ObjectUtils.isNull(customerAddress)
                && !StringUtils.isBlank(customerAddress.getCustomerLine1StreetAddress())
                && !StringUtils.isBlank(customerAddress.getCustomerCityName())
                && !StringUtils.isBlank(customerAddress.getCustomerStateCode())
                && !StringUtils.isBlank(customerAddress.getCustomerZipCode())
                && !StringUtils.isBlank(customerAddress.getCustomerCountryCode());
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    protected boolean isInvoiceNotFinalAndAwardExpired(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        return isAwardExpired(contractsGrantsInvoiceDocument.getAward()) && !contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().isFinalBillIndicator();
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    protected boolean isCategoryCumulativeExpenditureMatchAccountCumulativeExpenditureSum(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        ContractsGrantsInvoiceDetail totalCostInvoiceDetail = contractsGrantsInvoiceDocument.getTotalCostInvoiceDetail();
        if (ObjectUtils.isNotNull(totalCostInvoiceDetail)) {
            KualiDecimal categoryCumulativeExpenditure = totalCostInvoiceDetail.getCumulative();
            KualiDecimal accountDetailsCumulativeExpenditure = KualiDecimal.ZERO;

            for (InvoiceAccountDetail invoiceAccountDetail : contractsGrantsInvoiceDocument.getAccountDetails()) {
                accountDetailsCumulativeExpenditure = accountDetailsCumulativeExpenditure.add(invoiceAccountDetail.getCumulativeAmount());
            }

            return categoryCumulativeExpenditure.equals(accountDetailsCumulativeExpenditure);
        }
        else {
            return false;
        }

    }

    /**
     * @param award
     * @return
     */
    protected boolean isAwardMarkedStopWork(ContractsAndGrantsBillingAward award) {
        return award.isStopWorkIndicator();
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    protected boolean isLocAmountSufficent(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if (ArConstants.LOC_BILLING_SCHEDULE_CODE.equals(contractsGrantsInvoiceDocument.getAward().getBillingFrequency().getFrequency())) {
            if (contractsGrantsInvoiceDocument.getAward().getLetterOfCreditFund().getLetterOfCreditFundAmount().isLessThan(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param award
     * @return
     */
    protected boolean doesAwardHaveAnyActiveExpiredAccounts(ContractsAndGrantsBillingAward award) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = award.getActiveAwardAccounts();
        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
            if (ObjectUtils.isNotNull(awardAccount.getAccount())) {
                Date accountExpirationDate = awardAccount.getAccount().getAccountExpirationDate();
                if (accountExpirationDate != null && now.after(accountExpirationDate) && awardAccount.getAccount().isActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param award
     * @return
     */
    protected boolean isAwardExpired(ContractsAndGrantsBillingAward award) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        return now.after(award.getAwardEndingDate());
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean doesAwardHaveClosedAccountWithCurrentExpenditures(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        // for each InvoiceDetailAccountObjectCode, extract the chart code and account number, and store it in a map
        // where the key is chart code and value is a set of account numbers (no duplicates).
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes()) {
            String chartOfAccountsCode = invoiceDetailAccountObjectCode.getChartOfAccountsCode();
            String accountNumber = invoiceDetailAccountObjectCode.getAccountNumber();
            if (map.containsKey(chartOfAccountsCode)) {
                Set<String> set = map.get(chartOfAccountsCode);
                set.add(accountNumber);
            }
            else {
                Set<String> set = new HashSet<String>();
                set.add(accountNumber);
                map.put(chartOfAccountsCode, set);
            }
        }

        // Then go through the map and check to see if any of them have closed accounts
        Set<String> keys = map.keySet();
        for (String chartOfAccountsCode : keys) {
            Set<String> values = map.get(chartOfAccountsCode);
            for (String accountNumber : values) {
                if (accountService.getByPrimaryId(chartOfAccountsCode, accountNumber).isClosed()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param award
     * @return
     */
    protected boolean isAwardBillingFrequencyIsMilestone(ContractsAndGrantsBillingAward award) {
        if (ObjectUtils.isNull(award.getPreferredBillingFrequency())) {
            return false;
        }

        return ArConstants.MILESTONE_BILLING_SCHEDULE_CODE.equals(award.getPreferredBillingFrequency());
    }

    /**
     * @param award
     * @return
     */
    protected boolean isAwardIBillingFrequencyIsPredetermined(ContractsAndGrantsBillingAward award) {
        if (ObjectUtils.isNull(award.getPreferredBillingFrequency())) {
            return false;
        }
        return ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE.equals(award.getPreferredBillingFrequency());
    }

    /**
     * This method get the milestones with the criteria defined and set value to billed.
     *
     * @param invoiceMilestones
     * @param billed
     */
    protected void retrieveAndUpdateMilestones(List<InvoiceMilestone> invoiceMilestones, boolean billed) {
        if (invoiceMilestones == null) {
            throw new IllegalArgumentException("(List<InvoiceMilestone> invoiceMilestones cannot be null");
        }
        List<Long> milestoneIds = new ArrayList<Long>();
        for (InvoiceMilestone invoiceMilestone : invoiceMilestones) {
            milestoneIds.add(invoiceMilestone.getMilestoneIdentifier());
        }
        // This method get the milestones with the criteria defined and set value to isItBilled.

        if (CollectionUtils.isNotEmpty(invoiceMilestones)) {
            setMilestonesBilled(invoiceMilestones.get(0).getProposalNumber(), milestoneIds, billed);
        }
    }

    /**
     * This method updates value of billed in Milestone BO to the value of the billed parameter
     *
     * @param proposalNumber
     * @param milestoneIds
     * @param billed
     */
    protected void setMilestonesBilled(Long proposalNumber, List<Long> milestoneIds, boolean billed) {
        Collection<Milestone> milestones = null;
        milestones = getMatchingMilestoneByProposalIdAndInListOfMilestoneId(proposalNumber, milestoneIds);

        if (!ObjectUtils.isNull(milestones)) {
            for (Milestone milestone : milestones) {
                milestone.setBilled(billed);
                getBusinessObjectService().save(milestone);
            }
        }
    }

    /**
     * Returns milestones identified by one of the given milestone ids and associated with the given proposal number
     * @param proposalNumber the proposal number to check for milestones in
     * @param milestoneIds a List of milestone identifiers to search for
     * @return a Collection of Milestones
     */
    protected Collection<Milestone> getMatchingMilestoneByProposalIdAndInListOfMilestoneId(Long proposalNumber, List<Long> milestoneIds) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        fieldValues.put("milestoneIdentifier", milestoneIds);

        Collection<Milestone> milestones = getBusinessObjectService().findMatching(Milestone.class, fieldValues);
        return milestones;
    }


    /**
     * This method get the bills with the criteria defined and set value to billed.
     *
     * @param invoiceBills
     * @param billed
     */
    protected void retrieveAndUpdateBills(List<InvoiceBill> invoiceBills, boolean billed) {
        if (invoiceBills == null) {
            throw new IllegalArgumentException("(List<InvoiceBill> invoiceBills cannot be null");
        }

        List<Map<String, String>> fieldValuesList = new ArrayList<Map<String, String>>();
        Map<String, String> tempFieldValues;

        for (InvoiceBill invoiceBill : invoiceBills) {
            tempFieldValues = new HashMap<String, String>();

            if (ObjectUtils.isNotNull(invoiceBill.getBillNumber())) {
                tempFieldValues.put(ArPropertyConstants.BillFields.BILL_NUMBER, invoiceBill.getBillNumber().toString());
            }

            if (ObjectUtils.isNotNull(invoiceBill.getBillIdentifier())) {
                tempFieldValues.put(ArPropertyConstants.BillFields.BILL_IDENTIFIER, invoiceBill.getBillIdentifier().toString());
            }

            if (ObjectUtils.isNotNull(invoiceBill.getProposalNumber())) {
                tempFieldValues.put(KFSPropertyConstants.PROPOSAL_NUMBER, invoiceBill.getProposalNumber().toString());
            }

            fieldValuesList.add(tempFieldValues);
        }

        // To get the bills with the criteria defined and set value to billed.
        setBillsBilled(fieldValuesList, billed);
    }

    /**
     * This method updates value of billed in Bill BO to billed
     *
     * @param fieldValuesList
     * @param billed
     */
    protected void setBillsBilled(List<Map<String, String>> fieldValuesList, boolean billed) {
        Collection<Bill> bills = billDao.getBillsByMatchingCriteria(fieldValuesList);
        for (Bill bill : bills) {
            bill.setBilled(billed);
        }
        List<Bill> billsToSave = new ArrayList<Bill>();
        billsToSave.addAll(bills);
        getBusinessObjectService().save(billsToSave);
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#calculateTotalPaymentsToDateByAward(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public KualiDecimal calculateTotalPaymentsToDateByAward(ContractsAndGrantsBillingAward award) {
        KualiDecimal totalPayments = KualiDecimal.ZERO;

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocs = businessObjectService.findMatching(ContractsGrantsInvoiceDocument.class, criteria);

        for (ContractsGrantsInvoiceDocument cgInvoiceDoc : cgInvoiceDocs) {
            criteria.clear();
            criteria.put("financialDocumentReferenceInvoiceNumber", cgInvoiceDoc.getDocumentNumber());

            Collection<InvoicePaidApplied> invoicePaidApplieds = businessObjectService.findMatching(InvoicePaidApplied.class, criteria);
            for (InvoicePaidApplied invoicePapidApplied : invoicePaidApplieds) {

                totalPayments = totalPayments.add(invoicePapidApplied.getInvoiceItemAppliedAmount());
            }
        }
        return totalPayments;
    }

    /**
     * This method calculates the Cumulative Disbursement amount for an awardAccount
     *
     * @param awardAccount
     * @return
     */
    protected KualiDecimal getCumulativeCashDisbursement(ContractsAndGrantsBillingAwardAccount awardAccount, java.sql.Date awardBeginningDate) {
        Integer currentYear = universityDateService.getCurrentFiscalYear();
        KualiDecimal cumAmt = KualiDecimal.ZERO;
        KualiDecimal balAmt = KualiDecimal.ZERO;
        List<Balance> glBalances = new ArrayList<Balance>();

        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();


        Integer fiscalYear = universityDateService.getFiscalYear(awardBeginningDate);

        for (Integer i = fiscalYear; i <= currentYear; i++) {
            fiscalYears.add(i);
        }
        for (Integer eachFiscalYr : fiscalYears) {
            Map<String, Object> balanceKeys = new HashMap<String, Object>();
            balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
            balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
            balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
            balanceKeys.put(KFSPropertyConstants.BALANCE_TYPE_CODE, ArPropertyConstants.ACTUAL_BALANCE_TYPE);
            balanceKeys.put(KFSPropertyConstants.OBJECT_TYPE_CODE, ArPropertyConstants.EXPENSE_OBJECT_TYPE);
            glBalances.addAll(businessObjectService.findMatching(Balance.class, balanceKeys));
        }
        for (Balance bal : glBalances) {
            if (ObjectUtils.isNull(bal.getSubAccount()) || ObjectUtils.isNull(bal.getSubAccount().getA21SubAccount()) || !StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
                balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                cumAmt = cumAmt.add(balAmt);
            }
        }
        return cumAmt;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getMilestonesBilledToDateAmount(java.lang.Long)
     */
    @Override
    public KualiDecimal getMilestonesBilledToDateAmount(Long proposalNumber) {
        Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
        totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        KualiDecimal billedToDateAmount = KualiDecimal.ZERO;

        List<Milestone> milestones = (List<Milestone>) businessObjectService.findMatching(Milestone.class, totalBilledKeys);
        if (CollectionUtils.isNotEmpty(milestones)) {
            Iterator<Milestone> iterator = milestones.iterator();
            while (iterator.hasNext()) {
                Milestone milestone = iterator.next();
                if (milestone.isBilled()) {
                    billedToDateAmount = billedToDateAmount.add(milestone.getMilestoneAmount());
                }
            }
        }
        return billedToDateAmount;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getPredeterminedBillingBilledToDateAmount(java.lang.Long)
     */
    @Override
    public KualiDecimal getPredeterminedBillingBilledToDateAmount(Long proposalNumber) {
        Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
        totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        KualiDecimal billedToDateAmount = KualiDecimal.ZERO;

        List<Bill> bills = (List<Bill>) businessObjectService.findMatching(Bill.class, totalBilledKeys);
        if (CollectionUtils.isNotEmpty(bills)) {
            Iterator<Bill> iterator = bills.iterator();
            while (iterator.hasNext()) {
                Bill bill = iterator.next();
                if (bill.isBilled()) {
                    billedToDateAmount = billedToDateAmount.add(bill.getEstimatedAmount());
                }
            }
        }
        return billedToDateAmount;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getExpiredAccountsOfAward(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     *      Retrive all the expired accounts of an award
     */
    @Override
    public Collection<Account> getExpiredAccountsOfAward(ContractsAndGrantsBillingAward award) {

        Collection<ContractsAndGrantsBillingAwardAccount> awardAccounts = award.getActiveAwardAccounts();
        Collection<Account> expiredAwardAccounts = new ArrayList<Account>();

        if (awardAccounts != null && !awardAccounts.isEmpty()) {

            Date today = dateTimeService.getCurrentSqlDateMidnight();

            for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
                Account account = awardAccount.getAccount();

                if (account != null) {
                    Date expDt = account.getAccountExpirationDate();

                    if (expDt != null && expDt.before(today)) {
                        expiredAwardAccounts.add(account);
                    }
                }

            }

            return expiredAwardAccounts;
        }

        return null;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getContractControlAccounts(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public List<Account> getContractControlAccounts(ContractsAndGrantsBillingAward award) {

        if (!hasNoActiveAccountsAssigned(award)) {
            List<Account> controlAccounts = new ArrayList<Account>();
            for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                if (ObjectUtils.isNotNull(awardAccount.getAccount().getContractControlAccount())) {
                    controlAccounts.add(awardAccount.getAccount().getContractControlAccount());
                }
            }
            if (CollectionUtils.isNotEmpty(controlAccounts)) {
                return controlAccounts;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoAccountsAssigned(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean hasNoActiveAccountsAssigned(ContractsAndGrantsBillingAward award) {
        return CollectionUtils.isEmpty(award.getActiveAwardAccounts());
    }

    /**
     * To retrieve processing chart code and org code from the billing chart code and org code
     *
     * @param cgInvoiceDocument
     * @param billingChartOfAccountsCode
     * @param billingOrganizationCode
     * @return
     */
    @Override
    public List<String> getProcessingFromBillingCodes(String billingChartCode, String billingOrgCode) {

        List<String> procCodes = new ArrayList<String>();
        // To access Organization Options to find the billing values based on procesing codes
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, billingChartCode);
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, billingOrgCode);
        OrganizationOptions organizationOptions = businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);

        if (ObjectUtils.isNotNull(organizationOptions)) {
            procCodes.add(0, organizationOptions.getProcessingChartOfAccountCode());
            procCodes.add(1, organizationOptions.getProcessingOrganizationCode());
        }

        return procCodes;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#canViewInvoice(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument, java.lang.String)
     */
    @Override
    public boolean canViewInvoice(ContractsGrantsInvoiceDocument invoice, String collectorPrincipalId) {
        boolean canViewInvoice = false;

        RoleService roleService = KimApiServiceLocator.getRoleService();

        List<String> roleIds = new ArrayList<String>();
        Map<String, String> qualification = new HashMap<String, String>(3);
        qualification.put(ArKimAttributes.BILLING_CHART_OF_ACCOUNTS_CODE, invoice.getBillByChartOfAccountCode());
        qualification.put(ArKimAttributes.BILLING_ORGANIZATION_CODE, invoice.getBilledByOrganizationCode());
        qualification.put(ArKimAttributes.PROCESSING_CHART_OF_ACCOUNTS_CODE, invoice.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode());
        qualification.put(ArKimAttributes.PROCESSING_ORGANIZATION_CODE, invoice.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode());

        String customerName = invoice.getCustomerName();
        if (StringUtils.isNotEmpty(customerName)) {
            qualification.put(ArKimAttributes.CUSTOMER_NAME, customerName);
        }

        roleIds.add(roleService.getRoleIdByNamespaceCodeAndName(ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR));
        if (roleService.principalHasRole(collectorPrincipalId, roleIds, qualification)) {
            canViewInvoice = true;
        }

        return canViewInvoice;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#attachWorkflowHeadersToCGInvoices(java.util.Collection)
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> attachWorkflowHeadersToCGInvoices(Collection<ContractsGrantsInvoiceDocument> invoices) {
        List<ContractsGrantsInvoiceDocument> docs = new ArrayList<ContractsGrantsInvoiceDocument>();
        if (invoices == null || invoices.isEmpty()) {
            return docs;
        }

        // make a list of necessary workflow docs to retrieve
        List<String> documentHeaderIds = new ArrayList<String>();
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            documentHeaderIds.add(invoice.getDocumentNumber());
        }

        // get all of our docs with full workflow headers
        try {
            for (Document doc : documentService.getDocumentsByListOfDocumentHeaderIds(ContractsGrantsInvoiceDocument.class, documentHeaderIds)) {
                docs.add((ContractsGrantsInvoiceDocument) doc);
            }
        }
        catch (WorkflowException e) {
            throw new InfrastructureException("Unable to retrieve ContractsGrants Invoice Documents", e);
        }

        return docs;
    }

    /**
     * This method retrieves all invoices with open and with final status based on proposal number
     *
     * @param proposalNumber
     * @param outputFileStream
     * @return
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByProposalNumber(Long proposalNumber) {
        // Setting up proposal number and error correcting document for search
        Map<String, String> fieldValues = new HashMap<String, String>();
        if (ObjectUtils.isNotNull(proposalNumber)) {
            fieldValues.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber.toString());
        }
        fieldValues.put(ArPropertyConstants.DOCUMENT_HEADER_FINANCIAL_DOCUMENT_IN_ERROR_NUMBER, "NULL");

        // Retrieving invoice numbers to exclude
        Collection<String> invoiceNumbers = contractsGrantsInvoiceDocumentDao.getFinancialDocumentInErrorNumbers();

        // Retrieving matching invoices
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        cgInvoices = contractsGrantsInvoiceDocumentDao.getMatchingInvoicesByCollection(fieldValues, invoiceNumbers);

        return cgInvoices;
    }

    /**
     * This method generates the attached invoices for the agency addresses in the Contracts and Grants Invoice Document.
     */
    @Override
    public void generateInvoicesForInvoiceAddresses(ContractsGrantsInvoiceDocument document) {
        InvoiceTemplate invoiceTemplate = null;
        Iterator<InvoiceAddressDetail> iterator = document.getInvoiceAddressDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAddressDetail invoiceAddressDetail = iterator.next();
            byte[] reportStream;
            byte[] copyReportStream;
            // validating the invoice template
            if (ObjectUtils.isNotNull(invoiceAddressDetail.getCustomerInvoiceTemplateCode())) {
                invoiceTemplate = businessObjectService.findBySinglePrimaryKey(InvoiceTemplate.class, invoiceAddressDetail.getCustomerInvoiceTemplateCode());
            }
            else {
                addNoteForInvoiceReportFail(document);

            }

            // generate invoices from templates.
            if (ObjectUtils.isNotNull(invoiceTemplate) && invoiceTemplate.isActive() && ObjectUtils.isNotNull(invoiceTemplate.getFilename())) {
                ModuleConfiguration systemConfiguration = kualiModuleService.getModuleServiceByNamespaceCode(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE).getModuleConfiguration();
                String templateFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getTemplateFileDirectories().get(KFSConstants.TEMPLATES_DIRECTORY_KEY);
                String templateFilePath = templateFolderPath + File.separator + invoiceTemplate.getFilename();
                File templateFile = new File(templateFilePath);
                File outputDirectory = null;
                String outputFileName;
                try {
                    // generating original invoice
                    outputFileName = document.getDocumentNumber() + "_" + invoiceAddressDetail.getCustomerAddressName() + FILE_NAME_TIMESTAMP.format(new Date()) + ArConstants.TemplateUploadSystem.EXTENSION;
                    Map<String, String> replacementList = getTemplateParameterList(document);
                    replacementList.put("customer.fullAddress", contractsGrantsBillingUtilityService.buildFullAddress(invoiceAddressDetail.getCustomerAddress()));
                    reportStream = PdfFormFillerUtil.populateTemplate(templateFile, replacementList);
                    // creating and saving the original note with an attachment
                    if (ObjectUtils.isNotNull(document.getInvoiceGeneralDetail()) && document.getInvoiceGeneralDetail().isFinalBillIndicator()) {
                        reportStream = PdfFormFillerUtil.createFinalmarkOnFile(reportStream, "FINAL INVOICE");
                    }
                    Note note = new Note();
                    note.setNotePostedTimestampToCurrent();
                    note.setNoteText("Auto-generated invoice for Invoice Address-" + document.getDocumentNumber() + "-" + invoiceAddressDetail.getCustomerAddressName());
                    note.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                    Person systemUser = personService.getPersonByPrincipalName(KFSConstants.SYSTEM_USER);
                    note = noteService.createNote(note, document.getNoteTarget(), systemUser.getPrincipalId());
                    Attachment attachment = attachmentService.createAttachment(note, outputFileName, ArConstants.TemplateUploadSystem.TEMPLATE_MIME_TYPE, reportStream.length, new ByteArrayInputStream(reportStream), "");
                    // adding attachment to the note
                    note.setAttachment(attachment);
                    noteService.save(note);
                    attachment.setNoteIdentifier(note.getNoteIdentifier());
                    businessObjectService.save(attachment);
                    document.addNote(note);

                    // generating Copy invoice
                    outputFileName = document.getDocumentNumber() + "_" + invoiceAddressDetail.getCustomerAddressName() + FILE_NAME_TIMESTAMP.format(new Date()) + "_COPY" + ArConstants.TemplateUploadSystem.EXTENSION;
                    copyReportStream = PdfFormFillerUtil.createWatermarkOnFile(reportStream, "COPY");
                    // creating and saving the copy note with an attachment
                    Note copyNote = new Note();
                    copyNote.setNotePostedTimestampToCurrent();
                    copyNote.setNoteText("Auto-generated invoice (Copy) for Invoice Address-" + document.getDocumentNumber() + "-" + invoiceAddressDetail.getCustomerAddressName());
                    copyNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                    copyNote = noteService.createNote(copyNote, document.getNoteTarget(), systemUser.getPrincipalId());
                    Attachment copyAttachment = attachmentService.createAttachment(copyNote, outputFileName, ArConstants.TemplateUploadSystem.TEMPLATE_MIME_TYPE, copyReportStream.length, new ByteArrayInputStream(copyReportStream), "");
                    // adding attachment to the note
                    copyNote.setAttachment(copyAttachment);
                    noteService.save(copyNote);
                    copyAttachment.setNoteIdentifier(copyNote.getNoteIdentifier());
                    businessObjectService.save(copyAttachment);
                    document.addNote(copyNote);
                    invoiceAddressDetail.setNoteId(note.getNoteIdentifier());
                    // saving the note to the document header
                    documentService.updateDocument(document);
                }
                catch (IOException | DocumentException ex) {
                    addNoteForInvoiceReportFail(document);
                }
            }
            else {
                addNoteForInvoiceReportFail(document);
            }
        }
    }

    /**
     * This method generated the template parameter list to populate the pdf invoices that are attached to the Document.
     *
     * @return
     */
    protected Map<String, String> getTemplateParameterList(ContractsGrantsInvoiceDocument document) {
        ContractsAndGrantsBillingAward award = document.getAward();
        Map<String, String> parameterMap = new HashMap<>();
        Map<String, Object> primaryKeys = new HashMap<>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, document.getAccountingPeriod().getUniversityFiscalYear());
        primaryKeys.put(ArPropertyConstants.SystemInformationFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, document.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode());
        primaryKeys.put(ArPropertyConstants.SystemInformationFields.PROCESSING_ORGANIZATION_CODE, document.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode());
        SystemInformation sysInfo = businessObjectService.findByPrimaryKey(SystemInformation.class, primaryKeys);
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        if (ObjectUtils.isNotNull(document.getDocumentHeader().getWorkflowDocument().getDateCreated())) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "date", FILE_NAME_TIMESTAMP.format(document.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate()));
        }
        if (ObjectUtils.isNotNull(document.getDocumentHeader().getWorkflowDocument().getDateFinalized())) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "finalStatusDate", FILE_NAME_TIMESTAMP.format(new Date(document.getDocumentHeader().getWorkflowDocument().getDateFinalized().getMillis())));
        }
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "proposalNumber", org.apache.commons.lang.ObjectUtils.toString(document.getProposalNumber()));
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "payee.name", document.getBillingAddressName());
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "payee.addressLine1", document.getBillingLine1StreetAddress());
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "payee.addressLine2", document.getBillingLine2StreetAddress());
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "payee.city", document.getBillingCityName());
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "payee.state", document.getBillingStateCode());
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "payee.zipcode", document.getBillingZipCode());
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "advanceFlag", stringifyBooleanForContractsGrantsInvoiceTemplate(isAdvance(document)));
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "reimbursementFlag", stringifyBooleanForContractsGrantsInvoiceTemplate(!(isAdvance(document))));
        contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "accountDetails.contractControlAccountNumber", getRecipientAccountNumber(document.getAccountDetails()));
        if (ObjectUtils.isNotNull(sysInfo)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "systemInformation.feinNumber", sysInfo.getUniversityFederalEmployerIdentificationNumber());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "systemInformation.name", sysInfo.getOrganizationRemitToAddressName());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "systemInformation.addressLine1", sysInfo.getOrganizationRemitToLine1StreetAddress());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "systemInformation.addressLine2", sysInfo.getOrganizationRemitToLine2StreetAddress());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "systemInformation.city", sysInfo.getOrganizationRemitToCityName());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "systemInformation.state", sysInfo.getOrganizationRemitToStateCode());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "systemInformation.zipcode", sysInfo.getOrganizationRemitToZipCode());
        }
        if (CollectionUtils.isNotEmpty(document.getInvoiceDetailsWithoutIndirectCosts())) {
            ContractsGrantsInvoiceDetail firstInvoiceDetail = document.getInvoiceDetailsWithoutIndirectCosts().get(0);

            for (int i = 0; i < document.getInvoiceDetailsWithoutIndirectCosts().size(); i++) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceDetail[" + i + "].invoiceDetailIdentifier", org.apache.commons.lang.ObjectUtils.toString(document.getInvoiceDetailsWithoutIndirectCosts().get(i).getInvoiceDetailIdentifier()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceDetail[" + i + "].documentNumber", document.getInvoiceDetailsWithoutIndirectCosts().get(i).getDocumentNumber());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceDetail[" + i + "].category", document.getInvoiceDetailsWithoutIndirectCosts().get(i).getCategoryName());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceDetail[" + i + "].budget", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceDetailsWithoutIndirectCosts().get(i).getBudget()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceDetail[" + i + "].expenditure", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceDetailsWithoutIndirectCosts().get(i).getExpenditures()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceDetail[" + i + "].cumulative", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceDetailsWithoutIndirectCosts().get(i).getCumulative()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceDetail[" + i + "].balance", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceDetailsWithoutIndirectCosts().get(i).getBalance()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceDetail[" + i + "].billed", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceDetailsWithoutIndirectCosts().get(i).getBilled()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceDetail[" + i + "].adjustedCumulativeExpenditures", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceDetailsWithoutIndirectCosts().get(i).getAdjustedCumExpenditures()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceDetail[" + i + "].adjustedBalance", contractsGrantsBillingUtilityService.formatForCurrency(firstInvoiceDetail.getAdjustedBalance()));
            }
        }
        ContractsGrantsInvoiceDetail totalDirectCostInvoiceDetail = document.getTotalDirectCostInvoiceDetail();
        if (ObjectUtils.isNotNull(totalDirectCostInvoiceDetail)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "directCostInvoiceDetail.invoiceDetailIdentifier", org.apache.commons.lang.ObjectUtils.toString(totalDirectCostInvoiceDetail.getInvoiceDetailIdentifier()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "directCostInvoiceDetail.documentNumber", totalDirectCostInvoiceDetail.getDocumentNumber());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "directCostInvoiceDetail.category", totalDirectCostInvoiceDetail.getCategoryName());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "directCostInvoiceDetail.budget", contractsGrantsBillingUtilityService.formatForCurrency(totalDirectCostInvoiceDetail.getBudget()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "directCostInvoiceDetail.expenditure", contractsGrantsBillingUtilityService.formatForCurrency(totalDirectCostInvoiceDetail.getExpenditures()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "directCostInvoiceDetail.cumulative", contractsGrantsBillingUtilityService.formatForCurrency(totalDirectCostInvoiceDetail.getCumulative()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "directCostInvoiceDetail.balance", contractsGrantsBillingUtilityService.formatForCurrency(totalDirectCostInvoiceDetail.getBalance()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "directCostInvoiceDetail.billed", contractsGrantsBillingUtilityService.formatForCurrency(totalDirectCostInvoiceDetail.getBilled()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "directCostInvoiceDetail.adjustedCumulativeExpenditures", contractsGrantsBillingUtilityService.formatForCurrency(totalDirectCostInvoiceDetail.getAdjustedCumExpenditures()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "directCostInvoiceDetail.adjustedBalance", contractsGrantsBillingUtilityService.formatForCurrency(totalDirectCostInvoiceDetail.getAdjustedBalance()));
        }
        ContractsGrantsInvoiceDetail totalInDirectCostInvoiceDetail = document.getTotalInDirectCostInvoiceDetail();
        if (ObjectUtils.isNotNull(totalInDirectCostInvoiceDetail)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "inDirectCostInvoiceDetail.invoiceDetailIdentifier", org.apache.commons.lang.ObjectUtils.toString(totalInDirectCostInvoiceDetail.getInvoiceDetailIdentifier()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "inDirectCostInvoiceDetail.documentNumber", totalInDirectCostInvoiceDetail.getDocumentNumber());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "inDirectCostInvoiceDetail.categories", totalInDirectCostInvoiceDetail.getCategoryName());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "inDirectCostInvoiceDetail.budget", contractsGrantsBillingUtilityService.formatForCurrency(totalInDirectCostInvoiceDetail.getBudget()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "inDirectCostInvoiceDetail.expenditure", contractsGrantsBillingUtilityService.formatForCurrency(totalInDirectCostInvoiceDetail.getExpenditures()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "inDirectCostInvoiceDetail.cumulative", contractsGrantsBillingUtilityService.formatForCurrency(totalInDirectCostInvoiceDetail.getCumulative()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "inDirectCostInvoiceDetail.balance", contractsGrantsBillingUtilityService.formatForCurrency(totalInDirectCostInvoiceDetail.getBalance()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "inDirectCostInvoiceDetail.billed", contractsGrantsBillingUtilityService.formatForCurrency(totalInDirectCostInvoiceDetail.getBilled()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "inDirectCostInvoiceDetail.adjustedCumulativeExpenditures", contractsGrantsBillingUtilityService.formatForCurrency(totalInDirectCostInvoiceDetail.getAdjustedCumExpenditures()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "inDirectCostInvoiceDetail.adjustedBalance", contractsGrantsBillingUtilityService.formatForCurrency(totalInDirectCostInvoiceDetail.getAdjustedBalance()));
        }
        ContractsGrantsInvoiceDetail totalCostInvoiceDetail = document.getTotalCostInvoiceDetail();
        if (ObjectUtils.isNotNull(totalCostInvoiceDetail)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.invoiceDetailIdentifier", org.apache.commons.lang.ObjectUtils.toString(totalCostInvoiceDetail.getInvoiceDetailIdentifier()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.documentNumber", totalCostInvoiceDetail.getDocumentNumber());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.categories", totalCostInvoiceDetail.getCategoryName());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.budget", contractsGrantsBillingUtilityService.formatForCurrency(totalCostInvoiceDetail.getBudget()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.expenditure", contractsGrantsBillingUtilityService.formatForCurrency(totalCostInvoiceDetail.getExpenditures()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.cumulative", contractsGrantsBillingUtilityService.formatForCurrency(totalCostInvoiceDetail.getCumulative()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.balance", contractsGrantsBillingUtilityService.formatForCurrency(totalCostInvoiceDetail.getBalance()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.billed", contractsGrantsBillingUtilityService.formatForCurrency(totalCostInvoiceDetail.getBilled()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.estimatedCost", contractsGrantsBillingUtilityService.formatForCurrency(totalCostInvoiceDetail.getBilled().add(totalCostInvoiceDetail.getExpenditures())));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.adjustedCumulativeExpenditures", contractsGrantsBillingUtilityService.formatForCurrency(totalCostInvoiceDetail.getAdjustedCumExpenditures()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalInvoiceDetail.adjustedBalance", contractsGrantsBillingUtilityService.formatForCurrency(totalCostInvoiceDetail.getAdjustedBalance()));
        }
        if (CollectionUtils.isNotEmpty(document.getInvoiceAddressDetails())) {
            for (int i = 0; i < document.getInvoiceAddressDetails().size(); i++) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceAddressDetails[" + i + "].documentNumber", document.getInvoiceAddressDetails().get(i).getDocumentNumber());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceAddressDetails[" + i + "].customerNumber", document.getInvoiceAddressDetails().get(i).getCustomerNumber());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceAddressDetails[" + i + "].customerAddressIdentifier", org.apache.commons.lang.ObjectUtils.toString(document.getInvoiceAddressDetails().get(i).getCustomerAddressIdentifier()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceAddressDetails[" + i + "].customerAddressTypeCode", document.getInvoiceAddressDetails().get(i).getCustomerAddressTypeCode());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceAddressDetails[" + i + "].customerAddressName", document.getInvoiceAddressDetails().get(i).getCustomerAddressName());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceAddressDetails[" + i + "].customerInvoiceTemplateCode", document.getInvoiceAddressDetails().get(i).getCustomerInvoiceTemplateCode());
            }
        }
        if (CollectionUtils.isNotEmpty(document.getAccountDetails())) {
            for (int i = 0; i < document.getAccountDetails().size(); i++) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "accountDetails[" + i + "].documentNumber", document.getAccountDetails().get(i).getDocumentNumber());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "accountDetails[" + i + "].accountNumber", document.getAccountDetails().get(i).getAccountNumber());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "accountDetails[" + i + "].proposalNumber", org.apache.commons.lang.ObjectUtils.toString(document.getAccountDetails().get(i).getProposalNumber()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "accountDetails[" + i + "].chartOfAccountsCode", document.getAccountDetails().get(i).getChartOfAccountsCode());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "accountDetails[" + i + "].budgetAmount", contractsGrantsBillingUtilityService.formatForCurrency(document.getAccountDetails().get(i).getBudgetAmount()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "accountDetails[" + i + "].expenditureAmount", contractsGrantsBillingUtilityService.formatForCurrency(document.getAccountDetails().get(i).getExpenditureAmount()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "accountDetails[" + i + "].balanceAmount", contractsGrantsBillingUtilityService.formatForCurrency(document.getAccountDetails().get(i).getBalanceAmount()));
                Map map = new HashMap<String, Object>();
                map.put(KFSPropertyConstants.ACCOUNT_NUMBER, document.getAccountDetails().get(i).getAccountNumber());
                map.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, document.getAccountDetails().get(i).getChartOfAccountsCode());
                Account account = businessObjectService.findByPrimaryKey(Account.class, map);
                if (ObjectUtils.isNotNull(account)) {
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "accountDetails[" + i + "].account.responsibilityID", org.apache.commons.lang.ObjectUtils.toString(account.getContractsAndGrantsAccountResponsibilityId()));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(document.getInvoiceMilestones())) {
            for (int i = 0; i < document.getInvoiceMilestones().size(); i++) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceMilestones[" + i + "].proposalNumber", org.apache.commons.lang.ObjectUtils.toString(document.getInvoiceMilestones().get(i).getProposalNumber()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceMilestones[" + i + "].milestoneNumber", org.apache.commons.lang.ObjectUtils.toString(document.getInvoiceMilestones().get(i).getMilestoneNumber()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceMilestones[" + i + "].milestoneIdentifier", org.apache.commons.lang.ObjectUtils.toString(document.getInvoiceMilestones().get(i).getMilestoneIdentifier()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceMilestones[" + i + "].milestoneDescription", document.getInvoiceMilestones().get(i).getMilestoneDescription());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceMilestones[" + i + "].milestoneAmount", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceMilestones().get(i).getMilestoneAmount()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceMilestones[" + i + "].milestoneExpectedCompletionDate", stringifyDate(document.getInvoiceMilestones().get(i).getMilestoneExpectedCompletionDate()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceMilestones[" + i + "].milestoneCompletionDate", stringifyDate(document.getInvoiceMilestones().get(i).getMilestoneActualCompletionDate()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceMilestones[" + i + "].billed", org.apache.commons.lang.ObjectUtils.toString(document.getInvoiceMilestones().get(i).isBilled()));
            }
        }
        if (ObjectUtils.isNotNull(document.getInvoiceGeneralDetail())) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.documentNumber", document.getInvoiceGeneralDetail().getDocumentNumber());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.awardDateRange", document.getInvoiceGeneralDetail().getAwardDateRange());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.billingFrequency", document.getInvoiceGeneralDetail().getBillingFrequency());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.finalBill", stringifyBooleanForContractsGrantsInvoiceTemplate(document.getInvoiceGeneralDetail().isFinalBillIndicator()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.finalInvoice", stringifyBooleanForContractsGrantsInvoiceTemplate(document.getInvoiceGeneralDetail().isFinalBillIndicator()));
            if (document.getInvoiceGeneralDetail().isFinalBillIndicator()) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.finalInvoiceYesNo", "Yes");
            }
            else {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.finalInvoiceYesNo", "No");
            }
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.billingPeriod", document.getInvoiceGeneralDetail().getBillingPeriod());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.instrumentTypeCode", document.getInvoiceGeneralDetail().getInstrumentTypeCode());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.awardTotal", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceGeneralDetail().getAwardTotal()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.newTotalBilled", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceGeneralDetail().getNewTotalBilled()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.amountRemainingToBill", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceGeneralDetail().getAmountRemainingToBill()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.billedToDateAmount", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceGeneralDetail().getBilledToDateAmount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.costShareAmount", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceGeneralDetail().getCostShareAmount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.lastBilledDate", stringifyDate(document.getInvoiceGeneralDetail().getLastBilledDate()));
            String strArray[] = document.getInvoiceGeneralDetail().getBillingPeriod().split(" to ");
            if (ObjectUtils.isNotNull(strArray[0])) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.invoicingPeriodStartDate", strArray[0]);
            }
            if (ObjectUtils.isNotNull(strArray[1])) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceGeneralDetail.invoicingPeriodEndDate", strArray[1]);
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".cumulativePeriod", award.getAwardBeginningDate().toString() + " to " + strArray[1]);
            }
        }

        if (CollectionUtils.isNotEmpty(document.getInvoiceBills())) {
            for (int i = 0; i < document.getInvoiceBills().size(); i++) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceBills[" + i + "].proposalNumber", org.apache.commons.lang.ObjectUtils.toString(document.getInvoiceBills().get(i).getProposalNumber()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceBills[" + i + "].billNumber", org.apache.commons.lang.ObjectUtils.toString(document.getInvoiceBills().get(i).getBillNumber()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceBills[" + i + "].billDescription", document.getInvoiceBills().get(i).getBillDescription());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceBills[" + i + "].billIdentifier", org.apache.commons.lang.ObjectUtils.toString(document.getInvoiceBills().get(i).getBillIdentifier()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceBills[" + i + "].billDate", stringifyDate(document.getInvoiceBills().get(i).getBillDate()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceBills[" + i + "].amount", contractsGrantsBillingUtilityService.formatForCurrency(document.getInvoiceBills().get(i).getEstimatedAmount()));
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "invoiceBills[" + i + "].billed", stringifyBooleanForContractsGrantsInvoiceTemplate(document.getInvoiceBills().get(i).isBilled()));
            }
        }
        if (ObjectUtils.isNotNull(award)) {
            KualiDecimal billing = getAwardBilledToDateAmountByProposalNumber(award.getProposalNumber());
            KualiDecimal payments = calculateTotalPaymentsToDateByAward(award);
            KualiDecimal receivable = billing.subtract(payments);
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".billings", contractsGrantsBillingUtilityService.formatForCurrency(billing));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".payments", contractsGrantsBillingUtilityService.formatForCurrency(payments));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".receivables", contractsGrantsBillingUtilityService.formatForCurrency(receivable));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".proposalNumber", org.apache.commons.lang.ObjectUtils.toString(award.getProposalNumber()));
            if (ObjectUtils.isNotNull(award.getAwardBeginningDate())) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardBeginningDate", FILE_NAME_TIMESTAMP.format(award.getAwardBeginningDate()));
            }
            if (ObjectUtils.isNotNull(award.getAwardEndingDate())) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardEndingDate", FILE_NAME_TIMESTAMP.format(award.getAwardEndingDate()));
            }
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardTotalAmount", contractsGrantsBillingUtilityService.formatForCurrency(award.getAwardTotalAmount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardAddendumNumber", award.getAwardAddendumNumber());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardAllocatedUniversityComputingServicesAmount", contractsGrantsBillingUtilityService.formatForCurrency(award.getAwardAllocatedUniversityComputingServicesAmount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".federalPassThroughFundedAmount", contractsGrantsBillingUtilityService.formatForCurrency(award.getFederalPassThroughFundedAmount()));
            if (ObjectUtils.isNotNull(award.getAwardEntryDate())) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardEntryDate", FILE_NAME_TIMESTAMP.format(award.getAwardEntryDate()));
            }
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".agencyFuture1Amount", contractsGrantsBillingUtilityService.formatForCurrency(award.getAgencyFuture1Amount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".agencyFuture2Amount", contractsGrantsBillingUtilityService.formatForCurrency(award.getAgencyFuture2Amount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".agencyFuture3Amount", contractsGrantsBillingUtilityService.formatForCurrency(award.getAgencyFuture3Amount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardDocumentNumber", award.getAwardDocumentNumber());
            if (ObjectUtils.isNotNull(award.getAwardLastUpdateDate())) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardLastUpdateDate", FILE_NAME_TIMESTAMP.format(award.getAwardLastUpdateDate()));
            }
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".federalPassthroughIndicator", stringifyBooleanForContractsGrantsInvoiceTemplate(award.getFederalPassThroughIndicator()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".oldProposalNumber", org.apache.commons.lang.ObjectUtils.toString(award.getOldProposalNumber()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardDirectCostAmount", contractsGrantsBillingUtilityService.formatForCurrency(award.getAwardDirectCostAmount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardIndirectCostAmount", contractsGrantsBillingUtilityService.formatForCurrency(award.getAwardIndirectCostAmount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".federalFundedAmount", contractsGrantsBillingUtilityService.formatForCurrency(award.getFederalFundedAmount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardCreateTimestamp", stringifyDate(award.getAwardCreateTimestamp()));
            if (ObjectUtils.isNotNull(award.getAwardClosingDate())) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardClosingDate", FILE_NAME_TIMESTAMP.format(award.getAwardClosingDate()));
            }
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".proposalAwardTypeCode", award.getProposalAwardTypeCode());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardStatusCode", award.getAwardStatusCode());
            if (ObjectUtils.isNotNull(award.getLetterOfCreditFund())) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".letterOfCreditFundGroupCode", award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode());
            }
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".letterOfCreditFundCode", award.getLetterOfCreditFundCode());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".grantDescriptionCode", award.getGrantDescriptionCode());
            if (ObjectUtils.isNotNull(award.getProposal())) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".grantNumber", award.getProposal().getGrantNumber());
            }
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "agencyNumber", award.getAgencyNumber());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "agency.fullName", award.getAgency().getFullName());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".federalPassThroughAgencyNumber", award.getFederalPassThroughAgencyNumber());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".agencyAnalystName", award.getAgencyAnalystName());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".analystTelephoneNumber;", award.getAnalystTelephoneNumber());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".preferredBillingFrequency", award.getPreferredBillingFrequency());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardProjectTitle", award.getAwardProjectTitle());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardPurposeCode", award.getAwardPurposeCode());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".active", stringifyBooleanForContractsGrantsInvoiceTemplate(award.isActive()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".kimGroupNames", award.getKimGroupNames());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".routingOrg", award.getRoutingOrg());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".routingChart", award.getRoutingChart());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".suspendInvoicing", stringifyBooleanForContractsGrantsInvoiceTemplate(award.isSuspendInvoicingIndicator()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".additionalFormsRequired", stringifyBooleanForContractsGrantsInvoiceTemplate(award.isAdditionalFormsRequiredIndicator()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".additionalFormsDescription", award.getAdditionalFormsDescription());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".instrumentTypeCode", award.getInstrumentTypeCode());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".minInvoiceAmount", contractsGrantsBillingUtilityService.formatForCurrency(award.getMinInvoiceAmount()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".autoApprove", stringifyBooleanForContractsGrantsInvoiceTemplate(award.getAutoApproveIndicator()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".lookupPersonUniversalIdentifier", award.getLookupPersonUniversalIdentifier());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".lookupPerson", award.getLookupPerson().getPrincipalName());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".userLookupRoleNamespaceCode", award.getUserLookupRoleNamespaceCode());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".userLookupRoleName", award.getUserLookupRoleName());
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".fundingExpirationDate", stringifyDate(award.getFundingExpirationDate()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".stopWorkIndicator", stringifyBooleanForContractsGrantsInvoiceTemplate(award.isStopWorkIndicator()));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".stopWorkReason", award.getStopWorkReason());
            if (ObjectUtils.isNotNull(award.getAwardPrimaryProjectDirector())) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".awardProjectDirector.name", award.getAwardPrimaryProjectDirector().getProjectDirector().getName());
            }
            contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".letterOfCreditFundCode", award.getLetterOfCreditFundCode());
            if (ObjectUtils.isNotNull(award.getAwardPrimaryFundManager())) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".primaryFundManager.name", award.getAwardPrimaryFundManager().getFundManager().getName());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".primaryFundManager.email", award.getAwardPrimaryFundManager().getFundManager().getEmailAddress());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, KFSPropertyConstants.AWARD+".primaryFundManager.phone", award.getAwardPrimaryFundManager().getFundManager().getPhoneNumber());
            }
            if (ObjectUtils.isNotNull(document.getInvoiceGeneralDetail())) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(parameterMap, "totalAmountDue", contractsGrantsBillingUtilityService.formatForCurrency(receivable.add(document.getInvoiceGeneralDetail().getNewTotalBilled())));
            }
        }
        return parameterMap;
    }

    /**
     * Returns true if the billing Frequency is Predetermined Billing.
     *
     * @return
     */
    protected boolean isAdvance(ContractsGrantsInvoiceDocument document) {
        return (ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE.equals(document.getInvoiceGeneralDetail().getBillingFrequency()));
    }

    /**
     * Converts boolean to a String to display on pdf report
     * @param bool a boolean value
     * @return a String for the pdf based on the given boolean value
     */
    protected String stringifyBooleanForContractsGrantsInvoiceTemplate(boolean bool) { // the name is longer than the code : - ?
       return bool ? "Yes" : "Off";
    }

    /**
     * Turns a given date into a String for the sake of PDF parameters
     * @param d the date to format into a String
     * @return the date converted into a String or an empty String if the date was null
     */
    protected String stringifyDate(java.util.Date d) {
        if (!ObjectUtils.isNull(d)) {
            return getDateTimeService().toDateString(d);
        }
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * returns proper contract control Account Number.
     *
     * @return
     */
    protected String getRecipientAccountNumber(List<InvoiceAccountDetail> accountDetails) {
        if (CollectionUtils.isNotEmpty(accountDetails)) {
            if (ObjectUtils.isNull(accountDetails.get(0).getContractControlAccountNumber())) {
                return accountDetails.get(0).getAccountNumber();
            }
            return accountDetails.get(0).getContractControlAccountNumber();
        }
        return null;
    }

    /**
     * This method sets the last billed date to Award and Award Account objects based on the status of the invoice. Final or
     * Corrected.
     *
     * @param document
     */
    @Override
    public void updateLastBilledDate(ContractsGrantsInvoiceDocument document) {
        boolean isFinalBill = document.getInvoiceGeneralDetail().isFinalBillIndicator();
        String invoiceStatus = "FINAL";
        if (document.isInvoiceReversal()) {
            invoiceStatus = "CORRECTED";
        }
        String invoiceDocumentStatus = document.getDocumentHeader().getWorkflowDocument().getStatus().getLabel();

        // To calculate and update Last Billed Date based on the status of the invoice. Final or Corrected.
        // 1. Set last Billed Date to Award Accounts

        Iterator<InvoiceAccountDetail> iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = iterator.next();
            if (isFinalBill) {
                setAwardAccountFinalBilledValueAndLastBilledDate(id, true, document.getProposalNumber(), invoiceStatus, document.getInvoiceGeneralDetail().getLastBilledDate(), invoiceDocumentStatus);
            } else {
                calculateAwardAccountLastBilledDate(id, invoiceStatus, document.getInvoiceGeneralDetail().getLastBilledDate(), document.getProposalNumber(), invoiceDocumentStatus);
            }
        }

        // 2. Set last Billed to Award = least of last billed date of award account.
        Long proposalNumber = document.getProposalNumber();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        ContractsAndGrantsBillingAward award = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);

        if (CollectionUtils.isNotEmpty(award.getActiveAwardAccounts())) {
            // To set last billed Date to award.
            contractsAndGrantsModuleBillingService.setLastBilledDateToAward(proposalNumber, getLastBilledDate(award));
        }

    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getLastBilledDate(java.lang.Long)
     */
    @Override
    public java.sql.Date getLastBilledDate(ContractsAndGrantsBillingAward award) {
        java.sql.Date awdLastBilledDate = null;

        if (ObjectUtils.isNotNull(award)) {
            // last Billed of Award = least of last billed date of award account.
            ContractsAndGrantsBillingAwardAccount awardAccount;

            if (CollectionUtils.isNotEmpty(award.getActiveAwardAccounts())) {
                ContractsAndGrantsBillingAwardAccount firstActiveawardAccount = award.getActiveAwardAccounts().get(0);

                awardAccount = firstActiveawardAccount;
                awdLastBilledDate = firstActiveawardAccount.getCurrentLastBilledDate();

                for (int i = 0; i < award.getActiveAwardAccounts().size(); i++) {
                    if (ObjectUtils.isNull(awdLastBilledDate) || ObjectUtils.isNull(award.getActiveAwardAccounts().get(i).getCurrentLastBilledDate())) {
                        // The dates would be null if the user is correcting the first invoice created for the award.
                        // Then the award last billed date should also be null.
                        awdLastBilledDate = null;
                    }
                    else if (ObjectUtils.isNotNull(awdLastBilledDate) && ObjectUtils.isNotNull(award.getActiveAwardAccounts().get(i).getCurrentLastBilledDate())) {
                        if (awdLastBilledDate.after(award.getActiveAwardAccounts().get(i).getCurrentLastBilledDate())) {
                            awdLastBilledDate = award.getActiveAwardAccounts().get(i).getCurrentLastBilledDate();
                        }
                    }
                }
            }
        }

        return awdLastBilledDate;
    }

    /**
     * This method updates the AwardAccount object's last billed Variable with the value provided
     *
     * @param id
     * @param invoiceStatus
     * @param lastBilledDate
     * @param proposalNumber
     * @param invoiceDocumentStatus
     */
    protected void calculateAwardAccountLastBilledDate(InvoiceAccountDetail id, String invoiceStatus, java.sql.Date lastBilledDate, Long proposalNumber, String invoiceDocumentStatus) {
        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        // To set previous and current last Billed Date for award account .
        contractsAndGrantsModuleBillingService.setLastBilledDateToAwardAccount(mapKey, invoiceStatus, lastBilledDate, invoiceDocumentStatus);

    }

    /**
     * This method updates the Bills and Milestone objects billed Field.
     *
     * @param billed
     * @param invoiceMilestones
     * @param invoiceBills
     */
    @Override
    public void updateBillsAndMilestones(boolean billed, List<InvoiceMilestone> invoiceMilestones, List<InvoiceBill> invoiceBills) {
        updateMilestonesBilledIndicator(billed, invoiceMilestones);
        updateBillsBilledIndicator(billed, invoiceBills);
    }

    /**
     * Update Milestone objects billed value.
     *
     * @param billed
     * @param invoiceMilestones
     */
    protected void updateMilestonesBilledIndicator(boolean billed, List<InvoiceMilestone> invoiceMilestones) {
        // Get a list of invoiceMilestones from the Contracts Grants Invoice document. Then search for the actual Milestone object in this list through dao
        // Finally, set these milestones to billed
        if (invoiceMilestones != null && !invoiceMilestones.isEmpty()) {

            List<Long> milestoneIds = new ArrayList<Long>();
            for (InvoiceMilestone invoiceMilestone : invoiceMilestones) {
                milestoneIds.add(invoiceMilestone.getMilestoneIdentifier());
            }

            retrieveAndUpdateMilestones(invoiceMilestones, billed);
        }
    }

    /**
     * Update Bill objects billed value.
     *
     * @param billed
     * @param invoiceBills
     */
    protected void updateBillsBilledIndicator(boolean billed, List<InvoiceBill> invoiceBills) {
        if (CollectionUtils.isNotEmpty(invoiceBills)) {
            retrieveAndUpdateBills(invoiceBills, billed);
        }
    }

    /**
     * This method updates the ContractsAndGrantsBillingAwardAccount object's FinalBilled Variable with the value provided
     *
     * @param id
     * @param value
     * @param proposalNumber
     */
    protected void setAwardAccountFinalBilledValue(InvoiceAccountDetail id, boolean value, Long proposalNumber) {
        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        // To set final Billed to award Account
        contractsAndGrantsModuleBillingService.setFinalBilledToAwardAccount(mapKey, value);
    }

    /**
     * This method updates the ContractsAndGrantsBillingAwardAccount object's FinalBilled Variable with the value provided
     * and also sets the last billed date and invoice status.
     *
     * @param id
     * @param finalBilled
     * @param proposalNumber
     * @param invoiceStatus
     * @param lastBilledDate
     * @param invoiceDocumentStatus
     */
    protected void setAwardAccountFinalBilledValueAndLastBilledDate(InvoiceAccountDetail id, boolean finalBilled, Long proposalNumber, String invoiceStatus, java.sql.Date lastBilledDate, String invoiceDocumentStatus) {
        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, id.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, id.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        // To set previous and current last Billed Date for award account .
        contractsAndGrantsModuleBillingService.setFinalBilledAndLastBilledDateToAwardAccount(mapKey, finalBilled, invoiceStatus, lastBilledDate, invoiceDocumentStatus);
    }

    /**
     * This method updates AwardAccounts
     */
    @Override
    public void updateUnfinalizationToAwardAccount(List<InvoiceAccountDetail> accountDetails, Long proposalNumber) {
        Iterator iterator = accountDetails.iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            setAwardAccountFinalBilledValue(id, false, proposalNumber);
        }
    }

    /**
     * Corrects the Contracts and Grants Invoice Document.
     *
     * @throws WorkflowException
     */
    @Override
    public void correctContractsGrantsInvoiceDocument(ContractsGrantsInvoiceDocument document) throws WorkflowException {
        InvoiceDetailTotal directCostTotal = new InvoiceDetailTotal();
        Iterator iterator = document.getInvoiceDetailsWithoutIndirectCosts().iterator();
        // correct Invoice Details.
        while (iterator.hasNext()) {
            ContractsGrantsInvoiceDetail id = (ContractsGrantsInvoiceDetail) iterator.next();
            correctInvoiceDetail(id);
            directCostTotal.sumInvoiceDetail(id);
        }
        correctInvoiceDetailTotals(document, directCostTotal);

        // update correction to the InvoiceAccountDetail objects
        iterator = document.getAccountDetails().iterator();
        while (iterator.hasNext()) {
            InvoiceAccountDetail id = (InvoiceAccountDetail) iterator.next();
            correctInvoiceAccountDetail(id);
        }

        // correct invoiceDetailAccountObjectCode.
        iterator = document.getInvoiceDetailAccountObjectCodes().iterator();
        while (iterator.hasNext()) {
            InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = (InvoiceDetailAccountObjectCode) iterator.next();
            invoiceDetailAccountObjectCode.correctInvoiceDetailAccountObjectCodeExpenditureAmount();
        }

        // correct Bills
        KualiDecimal totalBillingAmount = KualiDecimal.ZERO;
        iterator = document.getInvoiceBills().iterator();
        while (iterator.hasNext()) {
            InvoiceBill bill = (InvoiceBill) iterator.next();
            bill.setEstimatedAmount(bill.getEstimatedAmount().negated());
            totalBillingAmount = totalBillingAmount.add(bill.getEstimatedAmount());
        }

        // correct Milestones
        KualiDecimal totalMilestonesAmount = KualiDecimal.ZERO;
        iterator = document.getInvoiceMilestones().iterator();
        while (iterator.hasNext()) {
            InvoiceMilestone milestone = (InvoiceMilestone) iterator.next();
            milestone.setMilestoneAmount(milestone.getMilestoneAmount().negated());
            totalMilestonesAmount = totalMilestonesAmount.add(milestone.getMilestoneAmount());
        }

        // set the billed to Date Field
        if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) && CollectionUtils.isNotEmpty(document.getInvoiceMilestones())) {
            // check if award has milestones
            document.getInvoiceGeneralDetail().setBilledToDateAmount(getMilestonesBilledToDateAmount(document.getProposalNumber()));
            // update the new total billed for the invoice.
            document.getInvoiceGeneralDetail().setNewTotalBilled(document.getInvoiceGeneralDetail().getNewTotalBilled().add(totalMilestonesAmount));
        }
        else if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && CollectionUtils.isNotEmpty(document.getInvoiceBills())) {
            // check if award has bills
            document.getInvoiceGeneralDetail().setBilledToDateAmount(getPredeterminedBillingBilledToDateAmount(document.getProposalNumber()));
            // update the new total billed for the invoice.
            document.getInvoiceGeneralDetail().setNewTotalBilled(document.getInvoiceGeneralDetail().getNewTotalBilled().add(totalBillingAmount));
        }
        else {
            document.getInvoiceGeneralDetail().setBilledToDateAmount(getAwardBilledToDateAmountByProposalNumber(document.getProposalNumber()));
            document.getInvoiceGeneralDetail().setNewTotalBilled(KualiDecimal.ZERO);
        }

        // to set Date email processed and Date report processed to null.
        document.setDateEmailProcessed(null);
        document.setDateReportProcessed(null);
    }

    /**
     * Error corrects an invoice detail
     * @param invoiceDetail the invoice detail to error correct
     */
    protected void correctInvoiceDetail(ContractsGrantsInvoiceDetail invoiceDetail) {
        invoiceDetail.setBilled(invoiceDetail.getExpenditures());
        invoiceDetail.setExpenditures(invoiceDetail.getExpenditures().negated());
        invoiceDetail.setCumulative(KualiDecimal.ZERO);
        invoiceDetail.setInvoiceDocument(null);
    }

    /**
     * Corrects the sums on the direct cost sub-total invoice detail and the total invoice detail on the given contracts and grants invoice document
     * @param document contracts and grants invoice performing error correction
     * @param directCostTotal the category totals from the corrected direct cost invoice details
     */
    protected void correctInvoiceDetailTotals(ContractsGrantsInvoiceDocument document, InvoiceDetailTotal directCostTotal) {
        ContractsGrantsInvoiceDetail directCostSubTotal = document.getTotalDirectCostInvoiceDetail();
        ContractsGrantsInvoiceDetail indirectCostSubTotal = document.getTotalInDirectCostInvoiceDetail();
        ContractsGrantsInvoiceDetail costTotal = document.getTotalCostInvoiceDetail();

        if (directCostSubTotal != null && indirectCostSubTotal != null && costTotal != null) {
            directCostSubTotal.setBudget(directCostTotal.getBudget());
            directCostSubTotal.setBilled(directCostTotal.getBilled());
            directCostSubTotal.setCumulative(directCostTotal.getCumulative());
            directCostSubTotal.setExpenditures(directCostTotal.getExpenditures());

            // now let's fix the total
            final KualiDecimal indirectBudget = (null != indirectCostSubTotal.getBudget()) ? indirectCostSubTotal.getBudget() : KualiDecimal.ZERO;
            costTotal.setBudget(directCostTotal.getBudget().add(indirectBudget));
            final KualiDecimal indirectBilled = (null != indirectCostSubTotal.getBilled()) ? indirectCostSubTotal.getBilled() : KualiDecimal.ZERO;
            costTotal.setBilled(directCostTotal.getBilled().add(indirectBilled));
            final KualiDecimal indirectCumulative = (null != indirectCostSubTotal.getCumulative()) ? indirectCostSubTotal.getCumulative() : KualiDecimal.ZERO;
            costTotal.setCumulative(directCostTotal.getCumulative().add(indirectCumulative));
            final KualiDecimal indirectExpenditures = (null != indirectCostSubTotal.getExpenditures()) ? indirectCostSubTotal.getExpenditures() : KualiDecimal.ZERO;
            costTotal.setExpenditures(directCostTotal.getExpenditures().add(indirectExpenditures));
        }
    }

    /**
     * Error corrects an invoice account detail
     * @param invoiceAccountDetail the invoice account detail to error correct
     */
    protected void correctInvoiceAccountDetail(InvoiceAccountDetail invoiceAccountDetail) {
        invoiceAccountDetail.setBilledAmount(invoiceAccountDetail.getExpenditureAmount());
        invoiceAccountDetail.setExpenditureAmount(invoiceAccountDetail.getExpenditureAmount().negated());
        invoiceAccountDetail.setCumulativeAmount(KualiDecimal.ZERO);
        invoiceAccountDetail.setInvoiceDocument(null);
    }

    /**
     * This method corrects the Maintenance Document for Predetermined Billing
     *
     * @throws WorkflowException
     */
    @Override
    public void correctBills(List<InvoiceBill> invoiceBills) throws WorkflowException {
        updateBillsBilledIndicator(false, invoiceBills);
    }

    /**
     * This method corrects the Maintenance Document for milestones
     *
     * @throws WorkflowException
     */
    @Override
    public void correctMilestones(List<InvoiceMilestone> invoiceMilestones) throws WorkflowException {
        updateMilestonesBilledIndicator(false, invoiceMilestones);
    }

    /**
     * This method takes a ContractsAndGrantsCategory, retrieves the specified object code or object code range. It then parses this
     * string, and returns all the possible object codes specified by this range.
     *
     * @param category
     * @return Set<String> objectCodes
     */
    @Override
    public Set<String> getObjectCodeArrayFromSingleCategory(ContractsAndGrantsCategory category, ContractsGrantsInvoiceDocument document) throws IllegalArgumentException {
        Set<String> objectCodeArray = new HashSet<String>();
        Set<String> levels = new HashSet<String>();
        if (ObjectUtils.isNotNull(category.getCategoryObjectCodes()) && StringUtils.isNotEmpty(category.getCategoryObjectCodes())) {
            List<String> objectCodes = Arrays.asList(category.getCategoryObjectCodes().split(","));

            // get a list of qualifying object codes listed in the categories
            for (int j = 0; j < objectCodes.size(); j++) {

                // This is to check if the object codes are in a range of values like 1001-1009 or 100* or 10* or 1*. The wildcard
                // should be included in the suffix only.
                if (objectCodes.get(j).contains("-")) {// To check ranges like A000 - ZZZZ (includes A001, A002 .. A009 , A00A to A00Z and so on to ZZZZ)
                    String obCodeFirst = StringUtils.substringBefore(objectCodes.get(j), "-").trim();
                    String obCodeLast = StringUtils.substringAfter(objectCodes.get(j), "-").trim();
                    // To validate if the object Code formed is in proper format of [0-9a-zA-Z]{4}

                    if (obCodeFirst.matches("[0-9a-zA-Z]{4}") && obCodeLast.matches("[0-9a-zA-Z]{4}")) {
                        List<String> objectCodeValues = incrementAlphaNumericString(obCodeFirst, obCodeLast);
                        // To Check for the first value as it is not being included in the array
                        objectCodeArray.add(obCodeFirst);

                        for (int i = 0; i < objectCodeValues.size(); i++) {
                            objectCodeArray.add(objectCodeValues.get(i));
                        }
                    }
                    else {
                        throw new IllegalArgumentException("Invalid Object Code range specification for the category:" + category.getCategoryName());
                    }
                }
                else if (objectCodes.get(j).contains("*")) {// To check for wildcard suffix
                    String obCodeFirst = StringUtils.substringBefore(objectCodes.get(j), "*").trim();
                    String obCodeLast = StringUtils.substringBefore(objectCodes.get(j), "*").trim(); // substringBefore is correct
                                                                                                     // here
                    // To make the code work for wildcards like 1* 10* 100* etc
                    // 10* will give you from 100 - 10Z.

                    for (int x = obCodeFirst.length(); x < 4; x++) {
                        obCodeFirst = obCodeFirst.concat("0");
                    }

                    for (int x = obCodeLast.length(); x < 4; x++) {
                        obCodeLast = obCodeLast.concat("Z");
                    }
                    if (obCodeFirst.matches("[0-9a-zA-Z]{4}") && obCodeLast.matches("[0-9a-zA-Z]{4}")) {
                        List<String> obCodeValues = incrementAlphaNumericString(obCodeFirst, obCodeLast);

                        // To Check for the first value as it is not being included in the array
                        objectCodeArray.add(obCodeFirst);
                        for (int i = 0; i < obCodeValues.size(); i++) {
                            objectCodeArray.add(obCodeValues.get(i));
                        }
                    }
                    else {
                        throw new IllegalArgumentException("Invalid Object Code range specification for the category:" + category.getCategoryName());
                    }
                }
                else {// If the object code is directly provided.
                    if (objectCodes.get(j).trim().matches("[0-9a-zA-Z]{4}")) {

                        objectCodeArray.add(objectCodes.get(j).trim());
                    }
                    else {
                        throw new IllegalArgumentException("Invalid Object Code range specification for the category:" + category.getCategoryName());
                    }
                }

            }
        }
        if (ObjectUtils.isNotNull(category.getCategoryConsolidations()) && StringUtils.isNotEmpty(category.getCategoryConsolidations())) {
            List<String> consolidationCodes = Arrays.asList(category.getCategoryConsolidations().split(","));
            List<ObjectLevel> objectLevels = objectLevelService.getObjectLevelsByConsolidationsIds(consolidationCodes);
            if (ObjectUtils.isNotNull(objectLevels) && !objectLevels.isEmpty()) {
                for (ObjectLevel level : objectLevels) {
                    levels.add(level.getFinancialObjectLevelCode());
                }
            }
        }
        if (ObjectUtils.isNotNull(category.getCategoryLevels()) && StringUtils.isNotEmpty(category.getCategoryLevels())) {
            List<String> levelCodes = Arrays.asList(category.getCategoryLevels().split(","));
            List<ObjectLevel> objectLevels = objectLevelService.getObjectLevelsByLevelIds(levelCodes);
            if (ObjectUtils.isNotNull(objectLevels) && !objectLevels.isEmpty()) {
                for (ObjectLevel level : objectLevels) {
                    levels.add(level.getFinancialObjectLevelCode());
                }
            }
        }
        if (ObjectUtils.isNotNull(levels) && !levels.isEmpty()) {
            List<ObjectCode> objectCodes = objectCodeService.getObjectCodesByLevelIds(new ArrayList<String>(levels));
            if (ObjectUtils.isNotNull(objectCodes) && !objectCodes.isEmpty()) {
                for (ObjectCode objectCode : objectCodes) {
                    objectCodeArray.add(objectCode.getFinancialObjectCode());
                }
            }
        }
        return objectCodeArray;
    }

    /**
     * This method returns the complete set of object codes for ALL ContractsAndGrantsCategories.
     *
     * @return Set<String> objectCodes
     */
    @Override
    public Set<String> getObjectCodeArrayFromContractsAndGrantsCategories(ContractsGrantsInvoiceDocument document) {
        Set<String> objectCodeArray = new HashSet<String>();
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        Collection<ContractsAndGrantsCategory> contractsAndGrantsCategories = businessObjectService.findMatching(ContractsAndGrantsCategory.class, criteria);
        Iterator<ContractsAndGrantsCategory> contractsAndGrantsCategoriesIterator = contractsAndGrantsCategories.iterator();

        while (contractsAndGrantsCategoriesIterator.hasNext()) {
            ContractsAndGrantsCategory category = contractsAndGrantsCategoriesIterator.next();
            objectCodeArray.addAll(getObjectCodeArrayFromSingleCategory(category, document));
        }

        return objectCodeArray;
    }

    /**
     * This method returns a list of character strings that represent base 36 integers from start(non-inclusive) to limit
     * (inclusive).
     *
     * @param start the starting point of the list. This value is not included in the list.
     * @param limit the ending point of the list. This value is included in the list
     * @return the list of strings
     * @throws IllegalArgumentException if start is not less than limit
     */
    protected List<String> incrementAlphaNumericString(String stringToIncrement, String stringLimit) throws IllegalArgumentException {
        int startInt = Integer.parseInt(stringToIncrement, 36);
        int limitInt = Integer.parseInt(stringLimit, 36);
        if (startInt >= limitInt) {
            throw new IllegalArgumentException("Starting code must be less than limit code.");
        }
        List<String> retval = new ArrayList<String>();
        for (int i = startInt + 1; i <= limitInt; i++) {
            // format below forces the string back to 4 characters and replace makes the extra
            // characters '0'
            retval.add(String.format("%4s", Integer.toString(i, 36)).replace('\u0020', '0'));
        }
        return retval;
    }

    protected void addNoteForInvoiceReportFail(ContractsGrantsInvoiceDocument document) {
        Note note = new Note();
        note.setNotePostedTimestampToCurrent();
        note.setNoteText(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_FILE_UPLOAD_NO_PDF_FILE_SELECTED_FOR_SAVE));
        note.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
        Person systemUser = personService.getPersonByPrincipalName(KFSConstants.SYSTEM_USER);
        note = noteService.createNote(note, document.getNoteTarget(), systemUser.getPrincipalId());
        noteService.save(note);
        document.addNote(note);
    }

    /**
     * Defers to the DAO to lookup C&G invoices, limited by the given Map of criteria
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> getMatchingInvoicesByCollection(Map fieldValues) {
        return getContractsGrantsInvoiceDocumentDao().getMatchingInvoicesByCollection(fieldValues);
    }

    @Override
    public List<String> checkAwardContractControlAccounts(ContractsAndGrantsBillingAward award) {
        List<String> errorString = new ArrayList<String>();
        boolean isValid = true;
        int accountNum = award.getActiveAwardAccounts().size();
        // To check if invoicing options exist on the award
        if (ObjectUtils.isNotNull(award.getInvoicingOptions())) {

            // To check if the award account is associated with a contract control account.
            for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                if (ObjectUtils.isNull(awardAccount.getAccount().getContractControlAccount())) {
                    isValid = false;
                    break;
                }
            }

            // if the Invoicing option is "By Contract Control Account" and there are no contract control accounts for one / all
            // award accounts, then throw error.
            if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT)) {
                if (!isValid) {
                    errorString.add(ArKeyConstants.AwardConstants.ERROR_NO_CTRL_ACCT);
                    errorString.add(award.getInvoicingOptionDescription());
                }
            }

            // if the Invoicing option is "By Award" and there are no contract control accounts for one / all award accounts, then
            // throw error.
            else if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_AWARD)) {
                if (!isValid) {
                    errorString.add(ArKeyConstants.AwardConstants.ERROR_NO_CTRL_ACCT);
                    errorString.add(award.getInvoicingOptionDescription());
                }
                else {
                    if (accountNum != 1) {
                        Account tmpAcct1, tmpAcct2;

                        Object[] awardAccounts = award.getActiveAwardAccounts().toArray();
                        for (int i = 0; i < awardAccounts.length - 1; i++) {
                            tmpAcct1 = ((ContractsAndGrantsBillingAwardAccount) awardAccounts[i]).getAccount().getContractControlAccount();
                            tmpAcct2 = ((ContractsAndGrantsBillingAwardAccount) awardAccounts[i + 1]).getAccount().getContractControlAccount();
                            // if the Invoicing option is "By Award" and there are more than one contract control account assigned
                            // for the award, then throw error.
                            if (ObjectUtils.isNull(tmpAcct1) || !tmpAcct1.equals(tmpAcct2)) {
                                errorString.add(ArKeyConstants.AwardConstants.ERROR_MULTIPLE_CTRL_ACCT);
                                errorString.add(award.getInvoicingOptionDescription());
                            }
                        }
                    }
                }
            }
        }
        return errorString;
    }

    /**
     * Determines whether the given ContractsGrantsInvoiceDocument is "effective" or not: if it is disapproved, cancelled, or error corrected then it is NOT effective,
     * and in all other cases, it is effective
     * @param invoiceDocument the invoice document to check
     * @return true if the document is "effective" given the rules above, false otherwise
     */
    @Override
    public boolean isInvoiceDocumentEffective(String documentNumber) {
        final FinancialSystemDocumentHeader invoiceDocHeader = getBusinessObjectService().findBySinglePrimaryKey(FinancialSystemDocumentHeader.class, documentNumber);
        final String documentStatus = invoiceDocHeader.getWorkflowDocumentStatusCode();
        if (StringUtils.isBlank(invoiceDocHeader.getFinancialDocumentInErrorNumber()) && !StringUtils.equals(documentStatus, DocumentStatus.CANCELED.getCode()) && !StringUtils.equals(documentStatus, DocumentStatus.DISAPPROVED.getCode())) { // skip error correcting CINVs, as they should be taken care of by the error correcting code
            final DocumentHeader correctingDocumentHeader = getFinancialSystemDocumentService().getCorrectingDocumentHeader(documentNumber);
            if (ObjectUtils.isNull(correctingDocumentHeader) || isCorrectedInvoiceDocumentEffective(correctingDocumentHeader.getDocumentNumber())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if an error correction is "effective" - ie, currently locking resources like milestones and pre-determined billing from the original CINV
     * @param errorCorrectionDocumentNumber the document number to check for the effectiveness of
     * @return true if the document is effectively locking resources, false otherwise
     */
    protected boolean isCorrectedInvoiceDocumentEffective(String errorCorrectionDocumentNumber) {
        final FinancialSystemDocumentHeader invoiceDocHeader = getBusinessObjectService().findBySinglePrimaryKey(FinancialSystemDocumentHeader.class, errorCorrectionDocumentNumber);
        final String documentStatus = invoiceDocHeader.getWorkflowDocumentStatusCode();
        if (getFinancialSystemDocumentService().getPendingDocumentStatuses().contains(documentStatus)) {
            return true; // the error correction document is currently pending, then it has not yet freed the milestones and pre-billings on the original CINV, so it's effective
        }
        final DocumentHeader correctingDocumentHeader = getFinancialSystemDocumentService().getCorrectingDocumentHeader(errorCorrectionDocumentNumber);
        if (!ObjectUtils.isNull(correctingDocumentHeader) && isCorrectedInvoiceDocumentEffective(correctingDocumentHeader.getDocumentNumber())) {
            return true; // is the error correction currently undergoing error correction itself?  Then recheck the rules on the newer error corrector to see if this document is effective or not
        }
        return false; // the error correction document is not effective and has freed resources
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isTemplateValidForContractsGrantsInvoiceDocument(org.kuali.kfs.module.ar.businessobject.InvoiceTemplate, org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public boolean isTemplateValidForContractsGrantsInvoiceDocument(InvoiceTemplate invoiceTemplate, ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument)) {
            return StringUtils.equals(invoiceTemplate.getBillByChartOfAccountCode(), contractsGrantsInvoiceDocument.getBillByChartOfAccountCode()) && StringUtils.equals(invoiceTemplate.getBilledByOrganizationCode(),contractsGrantsInvoiceDocument.getBilledByOrganizationCode());
        }
        return true;
    }

    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }

    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    /**
     * Sets the accountService attribute value.
     *
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Gets the objectCodeService attribute.
     * @return Returns the objectCodeService.
     */
    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    /**
     * Sets the objectCodeService attribute value.
     * @param objectCodeService The objectCodeService to set.
     */
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public ObjectLevelService getObjectLevelService() {
        return objectLevelService;
    }

    public void setObjectLevelService(ObjectLevelService objectLevelService) {
        this.objectLevelService = objectLevelService;
    }

    /**
     * Gets the attachmentService attribute.
     *
     * @return Returns the attachmentService.
     */
    public AttachmentService getAttachmentService() {
        return attachmentService;
    }

    /**
     * Sets the attachmentService attribute value.
     *
     * @param attachmentService The attachmentService to set.
     */
    public void setAttachmentService(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    @Override
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * @return
     */
    public ContractsGrantsInvoiceDocumentDao getContractsGrantsInvoiceDocumentDao() {
        return contractsGrantsInvoiceDocumentDao;
    }

    /**
     * @param contractsGrantsInvoiceDocumentDao
     */
    public void setContractsGrantsInvoiceDocumentDao(ContractsGrantsInvoiceDocumentDao contractsGrantsInvoiceDocumentDao) {
        this.contractsGrantsInvoiceDocumentDao = contractsGrantsInvoiceDocumentDao;
    }

    public BillDao getBillDao() {
        return billDao;
    }

    public void setBillDao(BillDao billDao) {
        this.billDao = billDao;
    }

    public ContractsGrantsBillingUtilityService getContractsGrantsBillingUtilityService() {
        return contractsGrantsBillingUtilityService;
    }

    public void setContractsGrantsBillingUtilityService(ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService) {
        this.contractsGrantsBillingUtilityService = contractsGrantsBillingUtilityService;
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

}