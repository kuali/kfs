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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBill;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingFrequency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsMilestone;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService;
import org.kuali.kfs.integration.cg.ContractsGrantsAwardInvoiceAccountInformation;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequency;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.businessobject.CollectorHierarchy;
import org.kuali.kfs.module.ar.businessobject.CollectorInformation;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsCategories;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.businessobject.ReferralType;
import org.kuali.kfs.module.ar.businessobject.SuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.businessobject.lookup.DunningLetterDistributionOnDemandLookupUtil;
import org.kuali.kfs.module.ar.businessobject.lookup.ReferralToCollectionsDocumentUtil;
import org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao;
import org.kuali.kfs.module.ar.dataaccess.CollectorHierarchyDao;
import org.kuali.kfs.module.ar.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class implements the services required for Contracts and Grants Invoice Document.
 */
public class ContractsGrantsInvoiceDocumentServiceImpl extends CustomerInvoiceDocumentServiceImpl implements ContractsGrantsInvoiceDocumentService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceDocumentServiceImpl.class);
    private CustomerInvoiceDetailService customerInvoiceDetailService;
    private ContractsGrantsInvoiceDocumentDao contractsGrantsInvoiceDocumentDao;
    private AccountingPeriodService accountingPeriodService;
    private AwardAccountObjectCodeTotalBilledDao awardAccountObjectCodeTotalBilledDao;
    private VerifyBillingFrequency verifyBillingFrequency;
    private DateTimeService dateTimeService;
    private InvoicePaidAppliedService invoicePaidAppliedService;
    private CollectorHierarchyDao collectorHierarchyDao;
    public static final String REPORT_LINE_DIVIDER = "--------------------------------------------------------------------------------------------------------------";

    public InvoicePaidAppliedService getInvoicePaidAppliedService() {
        return invoicePaidAppliedService;
    }

    public void setInvoicePaidAppliedService(InvoicePaidAppliedService invoicePaidAppliedService) {
        this.invoicePaidAppliedService = invoicePaidAppliedService;
    }

    /**
     * Gets the customerInvoiceDetailService attribute.
     * 
     * @return Returns the customerInvoiceDetailService.
     */
    public CustomerInvoiceDetailService getCustomerInvoiceDetailService() {
        return customerInvoiceDetailService;
    }

    /**
     * Sets the customerInvoiceDetailService attribute value.
     * 
     * @param customerInvoiceDetailService The customerInvoiceDetailService to set.
     */
    public void setCustomerInvoiceDetailService(CustomerInvoiceDetailService customerInvoiceDetailService) {
        this.customerInvoiceDetailService = customerInvoiceDetailService;
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

    /**
     * @return
     */
    public AwardAccountObjectCodeTotalBilledDao getAwardAccountObjectCodeTotalBilledDao() {
        return awardAccountObjectCodeTotalBilledDao;
    }

    /**
     * @param awardAccountObjectCodeTotalBilledDao
     */
    public void setAwardAccountObjectCodeTotalBilledDao(AwardAccountObjectCodeTotalBilledDao awardAccountObjectCodeTotalBilledDao) {
        this.awardAccountObjectCodeTotalBilledDao = awardAccountObjectCodeTotalBilledDao;
    }
    
    
    /**
     * Gets the collectorHierarchyDao attribute.
     * @return Returns the collectorHierarchyDao.
     */
    public CollectorHierarchyDao getCollectorHierarchyDao() {
        return collectorHierarchyDao;
    }

    /**
     * Sets the collectorHierarchyDao attribute value.
     * @param collectorHierarchyDao The collectorHierarchyDao to set.
     */
    public void setCollectorHierarchyDao(CollectorHierarchyDao collectorHierarchyDao) {
        this.collectorHierarchyDao = collectorHierarchyDao;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#createSourceAccountingLinesAndGLPEs(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    public void createSourceAccountingLinesAndGLPEs(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) throws WorkflowException {
        List<ContractsGrantsAwardInvoiceAccountInformation> awardInvoiceAccounts = new ArrayList<ContractsGrantsAwardInvoiceAccountInformation>();
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getAward())){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, contractsGrantsInvoiceDocument.getAward().getProposalNumber());
        map.put(KFSPropertyConstants.ACTIVE, true);
        awardInvoiceAccounts = (List) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsGrantsAwardInvoiceAccountInformation.class).getExternalizableBusinessObjectsList(ContractsGrantsAwardInvoiceAccountInformation.class, map);
        }
        boolean awardBillByControlAccount = false;
        boolean awardBillByInvoicingAccount = false;
        List<String> invoiceAccountDetails = new ArrayList<String>();
        boolean invoiceWithControlAccount = false;

        // To check if the Source accounting lines are existing. If they are do nothing
        if (CollectionUtils.isEmpty(contractsGrantsInvoiceDocument.getSourceAccountingLines())) {
            // To check if the invoice account section in award has a income account set.

            String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
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

            if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocument.getAward()) && contractsGrantsInvoiceDocument.getAward().getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT)) {
                awardBillByControlAccount = true;
            }
            else {
                awardBillByControlAccount = false;
            }


            KualiDecimal totalMilestoneAmount = KualiDecimal.ZERO;
            // To calculate the total milestone amount.
            if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && contractsGrantsInvoiceDocument.getInvoiceMilestones().size() > 0) {
                for (InvoiceMilestone milestone : contractsGrantsInvoiceDocument.getInvoiceMilestones()) {
                    if (milestone.getMilestoneAmount() != null) {
                        totalMilestoneAmount = totalMilestoneAmount.add(milestone.getMilestoneAmount());
                    }
                }
            }
            KualiDecimal totalBillAmount = KualiDecimal.ZERO;
            // To calculate the total bill amount.
            if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && contractsGrantsInvoiceDocument.getInvoiceBills().size() > 0) {
                for (InvoiceBill bill : contractsGrantsInvoiceDocument.getInvoiceBills()) {
                    if (bill.getEstimatedAmount() != null) {
                        totalBillAmount = totalBillAmount.add(bill.getEstimatedAmount());
                    }
                }
            }

            // To retrieve the financial object code from the Organization Accounting Default.
            Map<String, Object> criteria = new HashMap<String, Object>();
            Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
            criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
            criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, contractsGrantsInvoiceDocument.getBillByChartOfAccountCode());
            criteria.put(ArPropertyConstants.CustomerInvoiceItemCodes.ORGANIZATION_CODE, contractsGrantsInvoiceDocument.getBilledByOrganizationCode());
            // Need to avoid hitting database in the loop. option would be to set the financial object code when the form loads and
            // save
            // it somewhere.
            OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
            if (ObjectUtils.isNotNull(organizationAccountingDefault)) {
                if (awardBillByInvoicingAccount) {
                    // If its bill by Invoicing Account , irrespective of it is by contract control account, there would be a single
                    // source accounting line with award invoice account specified by the user.
                    try {
                        CustomerInvoiceDetail cide = createSourceAccountingLine(contractsGrantsInvoiceDocument.getDocumentNumber(), invoiceAccountDetails.get(0), invoiceAccountDetails.get(1), invoiceAccountDetails.get(2), contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled(), Integer.parseInt("1"));
                        contractsGrantsInvoiceDocument.getSourceAccountingLines().add(cide);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
                else {
                    // If its bill by Contract Control Account there would be a single source accounting line.
                    if (awardBillByControlAccount) {

                        // To get the account number and coa code for contract control account.
                        String accountNumber = null;
                        // Use the first account to get the contract control account number, as every account would have the same
                        // contract control account number.
                        if (StringUtils.isNotEmpty(contractsGrantsInvoiceDocument.getAccountDetails().get(0).getContractControlAccountNumber())) {
                            accountNumber = contractsGrantsInvoiceDocument.getAccountDetails().get(0).getContractControlAccountNumber();
                        }

                        String coaCode = contractsGrantsInvoiceDocument.getBillByChartOfAccountCode();
                        String objectCode = organizationAccountingDefault.getDefaultInvoiceFinancialObjectCode();

                        try {
                            CustomerInvoiceDetail cide = createSourceAccountingLine(contractsGrantsInvoiceDocument.getDocumentNumber(), coaCode, accountNumber, objectCode, contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled(), Integer.parseInt("1"));
                            contractsGrantsInvoiceDocument.getSourceAccountingLines().add(cide);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        for (InvoiceAccountDetail invAcctD : contractsGrantsInvoiceDocument.getAccountDetails()) {
                            String accountNumber = invAcctD.getAccountNumber();
                            String coaCode = invAcctD.getChartOfAccountsCode();
                            String objectCode = organizationAccountingDefault.getDefaultInvoiceFinancialObjectCode();
                            Integer sequenceNumber = contractsGrantsInvoiceDocument.getAccountDetails().indexOf(invAcctD) + 1;// To
                                                                                                                              // set
                                                                                                                              // a
                                                                                                                              // sequence
                                                                                                                              // number
                                                                                                                              // for
                                                                                                                              // the
                                                                                                                              // Accounting
                                                                                                                              // Lines
                            try {
                                // To calculate totalAmount based on the billing Frequency. Assuming that there would be only one
                                // account if its Milestone/Predetermined Schedule.
                                KualiDecimal totalAmount = KualiDecimal.ZERO;
                                if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && totalMilestoneAmount != KualiDecimal.ZERO) {
                                    totalAmount = totalMilestoneAmount;
                                }
                                else if (contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && totalBillAmount != KualiDecimal.ZERO) {
                                    totalAmount = totalBillAmount;
                                }
                                else {
                                    totalAmount = invAcctD.getExpenditureAmount();
                                }


                                CustomerInvoiceDetail cide = createSourceAccountingLine(contractsGrantsInvoiceDocument.getDocumentNumber(), coaCode, accountNumber, objectCode, totalAmount, sequenceNumber);
                                
                                contractsGrantsInvoiceDocument.getSourceAccountingLines().add(cide);

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }

                        }
                    }
                }
            }
        }

    }

    /**
     * @param docNum
     * @param coaCode
     * @param acctNum
     * @param obCode
     * @param totalAmount
     * @param seqNum
     * @return
     * @throws Exception
     */
    public CustomerInvoiceDetail createSourceAccountingLine(String docNum, String coaCode, String acctNum, String obCode, KualiDecimal totalAmount, Integer seqNum) throws Exception {
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
        if(totalAmount.isNegative()){
           cid.setInvoiceItemDiscountLineNumber(seqNum);
        }
        // To get AR Object codes for the GLPEs .... as it is not being called implicitly..

        cid.setAccountsReceivableObjectCode(customerInvoiceDetailService.getAccountsReceivableObjectCodeBasedOnReceivableParameter(cid));
        return cid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#recalculateNewTotalBilled(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    public void recalculateNewTotalBilled(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {

        // To verify the expenditure amounts have been changed and
        // update the invoiceDetailObjectCode
        boolean expenditureValueChanged = adjustObjectCodeAmountsIfChanged(contractsGrantsInvoiceDocument);

        if (expenditureValueChanged) {
            // update Total Direct Cost in the Invoice Detail Tab
            KualiDecimal totalDirectCostExpenditures = getInvoiceDetailExpenditureSum(contractsGrantsInvoiceDocument.getInvoiceDetails());
            // Set expenditures to Direct Cost invoice Details
            contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(0).setExpenditures(totalDirectCostExpenditures);
            
            // update Total Indirect Cost in the Invoice Detail Tab
            KualiDecimal totalInDirectCostExpenditures = getInvoiceDetailExpenditureSum(contractsGrantsInvoiceDocument.getInvoiceDetailsIndirectCostOnly());
            // Set expenditures to Indirect Cost invoice Details
            contractsGrantsInvoiceDocument.getInDirectCostInvoiceDetails().get(0).setExpenditures(totalInDirectCostExpenditures);
            
            // Set the total for Total Cost Invoice Details section.
            contractsGrantsInvoiceDocument.getTotalInvoiceDetails().get(0).setExpenditures(contractsGrantsInvoiceDocument.getDirectCostInvoiceDetails().get(0).getExpenditures().add(totalInDirectCostExpenditures));

            recalculateAccountDetails(contractsGrantsInvoiceDocument.getAccountDetails(), contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes());

            // update source accounting lines
            updateInvoiceSourceAccountingLines(contractsGrantsInvoiceDocument.getAccountDetails(), contractsGrantsInvoiceDocument.getSourceAccountingLines());


        }

        // set the General Detail Total to be billed - there would be only one value for Total Cost invoice Details.
        contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().setNewTotalBilled(contractsGrantsInvoiceDocument.getTotalInvoiceDetails().get(0).getExpenditures().add(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBilledToDate()));
    }

    /**
     * @param invoiceDetails
     * @return
     */
    public KualiDecimal getInvoiceDetailExpenditureSum(List<InvoiceDetail> invoiceDetails) {
        KualiDecimal totalExpenditures = KualiDecimal.ZERO;
        for (InvoiceDetail invoiceDetail : invoiceDetails) {
            totalExpenditures = totalExpenditures.add(invoiceDetail.getExpenditures());
        }
        return totalExpenditures;
    }
    

    /**
     * @param invoiceAccountDetails
     * @param sourceAccountingLines
     */
    public void updateInvoiceSourceAccountingLines(List<InvoiceAccountDetail> invoiceAccountDetails, List sourceAccountingLines) {

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
    public void prorateBill(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) throws WorkflowException {


        KualiDecimal totalCost = new KualiDecimal(0); // Amount to be billed on this invoice
        // must iterate through the invoice details because the user might have manually changed the value
        for (InvoiceDetail invD : contractsGrantsInvoiceDocument.getInvoiceDetailsWithIndirectCosts()) {
            totalCost = totalCost.add(invD.getExpenditures());
        }
        KualiDecimal billedTotalCost = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getBilledToDate(); // Total Billed
                                                                                                                   // so far
        KualiDecimal accountAwardTotal = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getAwardTotal(); // AwardTotal

        if (accountAwardTotal.subtract(billedTotalCost).isGreaterEqual(new KualiDecimal(0))) {
            KualiDecimal amountEligibleForBilling = accountAwardTotal.subtract(billedTotalCost);
            // only recalculate if the current invoice is over what's billable.

            if (totalCost.isGreaterThan(amountEligibleForBilling)) {
                // use BigDecimal because percentage should not have only a scale of 2, we need more for accuracy
                BigDecimal percentage = amountEligibleForBilling.bigDecimalValue().divide(totalCost.bigDecimalValue(), 10, BigDecimal.ROUND_HALF_DOWN);
                KualiDecimal amountToBill = new KualiDecimal(0); // use to check if rounding has left a few cents off

                for (InvoiceDetail invD : contractsGrantsInvoiceDocument.getInvoiceDetailsWithIndirectCosts()) {
                    BigDecimal newValue = invD.getExpenditures().bigDecimalValue().multiply(percentage);
                    KualiDecimal newKualiDecimalValue = new KualiDecimal(newValue.setScale(2, BigDecimal.ROUND_DOWN));
                    invD.setExpenditures(newKualiDecimalValue);
                    amountToBill = amountToBill.add(newKualiDecimalValue);
                }
                // There will be some amount left, since we are rounding down.  Display warning for user to manually correct/distribute where they want to put the remainder
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
    public void addToAccountObjectCodeBilledTotal(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
            totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, invoiceDetailAccountObjectCode.getProposalNumber());
            totalBilledKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, invoiceDetailAccountObjectCode.getChartOfAccountsCode());
            totalBilledKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, invoiceDetailAccountObjectCode.getAccountNumber());
            totalBilledKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, invoiceDetailAccountObjectCode.getFinancialObjectCode());

            List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilledList = (List<AwardAccountObjectCodeTotalBilled>) SpringContext.getBean(BusinessObjectService.class).findMatching(AwardAccountObjectCodeTotalBilled.class, totalBilledKeys);
            if (awardAccountObjectCodeTotalBilledList != null && !awardAccountObjectCodeTotalBilledList.isEmpty()) {
                AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled = awardAccountObjectCodeTotalBilledList.get(0);
                awardAccountObjectCodeTotalBilled.setTotalBilled(awardAccountObjectCodeTotalBilled.getTotalBilled().add(invoiceDetailAccountObjectCode.getCurrentExpenditures()));
                awardAccountObjectCodeTotalBilledDao.save(awardAccountObjectCodeTotalBilled);
            }
            else {
                AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled = new AwardAccountObjectCodeTotalBilled();
                awardAccountObjectCodeTotalBilled.setProposalNumber(invoiceDetailAccountObjectCode.getProposalNumber());
                awardAccountObjectCodeTotalBilled.setChartOfAccountsCode(invoiceDetailAccountObjectCode.getChartOfAccountsCode());
                awardAccountObjectCodeTotalBilled.setAccountNumber(invoiceDetailAccountObjectCode.getAccountNumber());
                awardAccountObjectCodeTotalBilled.setFinancialObjectCode(invoiceDetailAccountObjectCode.getFinancialObjectCode());
                awardAccountObjectCodeTotalBilled.setTotalBilled(invoiceDetailAccountObjectCode.getCurrentExpenditures());
                awardAccountObjectCodeTotalBilledDao.save(awardAccountObjectCodeTotalBilled);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(java.util.List)
     */
    public List<AwardAccountObjectCodeTotalBilled> getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(List<ContractsAndGrantsCGBAwardAccount> awardAccounts) {
        return awardAccountObjectCodeTotalBilledDao.getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#adjustObjectCodeAmountsIfChanged(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    public boolean adjustObjectCodeAmountsIfChanged(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {

        boolean expenditureValueChanged = false;

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
            else
                invoiceDetailAccountObjectCodeMap.get(categoryCode).add(invoiceDetailAccountObjectCode);
        }

        // figure out if any of the current expenditures for the category has been changed. If yes, then update the
        // invoiceDetailObjectCode
        // and update account details
        for (InvoiceDetail invoiceDetail : contractsGrantsInvoiceDocument.getInvoiceDetailsWithIndirectCosts()){
            KualiDecimal total = getSumOfExpendituresOfCategory(invoiceDetailAccountObjectCodeMap.get(invoiceDetail.getCategoryCode()));
            // To set expenditures to zero if its blank - to avoid exceptions.
            if (ObjectUtils.isNull(invoiceDetail.getExpenditures())) {
                invoiceDetail.setExpenditures(KualiDecimal.ZERO);
            }

            if (invoiceDetail.getExpenditures().compareTo(total) != 0) {
                recalculateObjectCodeByCategory(contractsGrantsInvoiceDocument, invoiceDetail, total, invoiceDetailAccountObjectCodeMap.get(invoiceDetail.getCategoryCode()));
                expenditureValueChanged = true;
            }
        }
        return expenditureValueChanged;
    }

    /**
     * @param invoiceDetailAccountObjectCodes
     * @return
     */
    private KualiDecimal getSumOfExpendituresOfCategory(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
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
    private void recalculateObjectCodeByCategory(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument, InvoiceDetail invoiceDetail, KualiDecimal total, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
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
            else { // if the list is null, then there are no account/object code in the gl_balance_t. So assign the amount to the
                   // first object code in the category
                assignCurrentExpenditureToNonExistingAccountObjectCode(contractsGrantsInvoiceDocument, invoiceDetail);
            }
        }
        else {

            for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
                // this may rarely happen
                // if the initial total is 0, that means none of the object codes in this category is set to bill. If this amount is
                // change, then just divide evenly among all object codes.
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
    private void assignCurrentExpenditureToNonExistingAccountObjectCode(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument, InvoiceDetail invoiceDetail) {
        String categoryCode = invoiceDetail.getCategoryCode();
        if (categoryCode == null) {
            LOG.error("Category Code can not be null during recalculation of account object code for Contracts and Grants Invoice Document.");
        }
        // get the category that matches this category code.
        Collection<ContractsAndGrantsCategories> contractsAndGrantsCategories = SpringContext.getBean(BusinessObjectService.class).findAll(ContractsAndGrantsCategories.class);
        Iterator<ContractsAndGrantsCategories> contractsAndGrantsCategoriesIterator = contractsAndGrantsCategories.iterator();
        ContractsAndGrantsCategories category = null;
        while (contractsAndGrantsCategoriesIterator.hasNext()) {
            category = contractsAndGrantsCategoriesIterator.next();
            if (category.getCategoryCode().equals(categoryCode)) {
                break;
            }
        }

        // got the category now.
        if (category != null) {
            final KualiDecimal oneCent = new KualiDecimal(0.01);

            int size = contractsGrantsInvoiceDocument.getAccountDetails().size();
            KualiDecimal amount = new KualiDecimal(invoiceDetail.getExpenditures().bigDecimalValue().divide(new BigDecimal(size), 10, BigDecimal.ROUND_DOWN));
            KualiDecimal remainder = invoiceDetail.getExpenditures().subtract(amount.multiply(new KualiDecimal(size)));

            for (InvoiceAccountDetail invoiceAccountDetail : contractsGrantsInvoiceDocument.getAccountDetails()) {
                // get the first object code from this category
                String objectCode = (String) contractsGrantsInvoiceDocument.getObjectCodeArrayFromSingleCategory(category).toArray()[0];
                InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = new InvoiceDetailAccountObjectCode();
                invoiceDetailAccountObjectCode.setDocumentNumber(contractsGrantsInvoiceDocument.getDocumentNumber());
                invoiceDetailAccountObjectCode.setProposalNumber(contractsGrantsInvoiceDocument.getProposalNumber());
                invoiceDetailAccountObjectCode.setFinancialObjectCode(objectCode);
                invoiceDetailAccountObjectCode.setCategoryCode(categoryCode);
                invoiceDetailAccountObjectCode.setAccountNumber(invoiceAccountDetail.getAccountNumber());
                invoiceDetailAccountObjectCode.setChartOfAccountsCode(invoiceAccountDetail.getChartOfAccountsCode());
                invoiceDetailAccountObjectCode.setCumulativeExpenditures(KualiDecimal.ZERO); // it's 0.00 that's why we are in this
                                                                                             // section to begin with.
                invoiceDetailAccountObjectCode.setTotalBilled(KualiDecimal.ZERO); // this is also 0.00 because it has never been
                                                                                  // billed before

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

                // add this single account object code item to the list in the Map
                contractsGrantsInvoiceDocument.getInvoiceDetailAccountObjectCodes().add(invoiceDetailAccountObjectCode);
            }
        }
        else {
            LOG.error("Category Code cannot be found from the category list during recalculation of account object code for Contracts and Grants Invoice Document.");
        }

        contractsGrantsInvoiceDocument.getObjectCodeArrayFromContractsAndGrantsCategories();
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
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#performInvoiceAccountObjectCodeCleanup(java.util.List)
     */
    public void performInvoiceAccountObjectCodeCleanup(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            if (invoiceDetailAccountObjectCode.getCurrentExpenditures().compareTo(KualiDecimal.ZERO) == 0) {
                invoiceDetailAccountObjectCode = null;
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getAllOpenContractsGrantsInvoiceDocuments(boolean)
     */
    public Collection<ContractsGrantsInvoiceDocument> getAllOpenContractsGrantsInvoiceDocuments(boolean includeWorkflowHeaders) {
        Collection<ContractsGrantsInvoiceDocument> invoices = new ArrayList<ContractsGrantsInvoiceDocument>();

        // retrieve the set of documents without workflow headers
        invoices = contractsGrantsInvoiceDocumentDao.getAllOpen();

        // if we dont need workflow headers, then we're done
        if (!includeWorkflowHeaders || invoices.isEmpty()) {
            return invoices;
        }
        else {
            return populateWorkflowHeaders(invoices);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getAllCGInvoiceDocuments(boolean)
     */
    public Collection<ContractsGrantsInvoiceDocument> getAllCGInvoiceDocuments(boolean includeWorkflowHeaders) {
        Collection<ContractsGrantsInvoiceDocument> invoices = new ArrayList<ContractsGrantsInvoiceDocument>();

        // retrieve the set of documents without workflow headers
        invoices = contractsGrantsInvoiceDocumentDao.getAllCGInvoiceDocuments();

        // if we dont need workflow headers, then we're done
        if (!includeWorkflowHeaders || invoices.isEmpty()) {
            return invoices;
        }
        else {
            return populateWorkflowHeaders(invoices);
        }
    }

    /**
     * @param invoices
     * @return
     */
    private Collection<ContractsGrantsInvoiceDocument> populateWorkflowHeaders(Collection<ContractsGrantsInvoiceDocument> invoices) {
        // make a list of necessary workflow docs to retrieve
        List<String> documentHeaderIds = new ArrayList<String>();
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            documentHeaderIds.add(invoice.getDocumentNumber());
        }
        // get all of our docs with full workflow headers
        Collection<ContractsGrantsInvoiceDocument> docs = new ArrayList<ContractsGrantsInvoiceDocument>();
        try {
            docs = documentService.getDocumentsByListOfDocumentHeaderIds(ContractsGrantsInvoiceDocument.class, documentHeaderIds);
        }
        catch (WorkflowException e) {
            throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
        }
        return docs;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getAwardBilledToDateByProposalNumber(java.lang.Long)
     */
    public KualiDecimal getAwardBilledToDateByProposalNumber(Long proposalNumber) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        List<AwardAccountObjectCodeTotalBilled> accountObjectCodeTotalBilledList = (List<AwardAccountObjectCodeTotalBilled>) SpringContext.getBean(BusinessObjectService.class).findMatching(AwardAccountObjectCodeTotalBilled.class, keys);
        KualiDecimal billedToDate = KualiDecimal.ZERO;
        for (AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled : accountObjectCodeTotalBilledList) {
            billedToDate = billedToDate.add(awardAccountObjectCodeTotalBilled.getTotalBilled());
        }
        return billedToDate;
    }

    /**
     * This method retrieves all CG invoice document that match the given criteria
     * 
     * @param criteria
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveAllCGInvoicesByCriteria(Criteria criteria) {
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentDao.getInvoicesByCriteria(criteria);
        if (CollectionUtils.isEmpty(cgInvoices)) {
            return null;
        }
        return cgInvoices;
    }


    /**
     * This method retrieves all invoices with open and with final status based on loc creation type = LOC fund
     * 
     * @param locFund
     * @param errorFileName
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByLOCFund(String locFund, String errorFileName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("locCreationType", ArConstants.LOC_BY_LOC_FUND);
        criteria.addEqualTo("letterOfCreditFundCode", locFund);
        criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, true);
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        String detail = "LOC Creation Type:" + ArConstants.LOC_BY_LOC_FUND + " of value " + locFund;
        cgInvoices = contractsGrantsInvoiceDocumentDao.getInvoicesByCriteria(criteria);
        List<String> invalidInvoices = validateInvoices(cgInvoices, detail, errorFileName);
        if (!CollectionUtils.isEmpty(invalidInvoices)) {
            return null;

        }
        return cgInvoices;
    }

    /**
     * This method retrieves all invoices with open and with final status based on loc creation type = LOC fund group
     * 
     * @param locFundGroup
     * @param errorFileName
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByLOCFundGroup(String locFundGroup, String errorFileName) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("locCreationType", ArConstants.LOC_BY_LOC_FUND_GRP);
        criteria.addEqualTo("letterOfCreditFundGroupCode", locFundGroup);
        criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, true);
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        String detail = "LOC Creation Type:" + ArConstants.LOC_BY_LOC_FUND_GRP + " of value " + locFundGroup;
        cgInvoices = contractsGrantsInvoiceDocumentDao.getInvoicesByCriteria(criteria);
        List<String> invalidInvoices = validateInvoices(cgInvoices, detail, errorFileName);
        if (!CollectionUtils.isEmpty(invalidInvoices)) {
            return null;

        }
        return cgInvoices;
    }

    /**
     * This method retrieves all invoices with open and with final status based on customer number
     * 
     * @param customerNumber
     * @param errorFileName
     * @return
     */
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByCustomerNumber(String customerNumber, String errorFileName) {

        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        cgInvoices = contractsGrantsInvoiceDocumentDao.getOpenInvoicesByCustomerNumber(customerNumber);
        String detail = "Customer Number#" + customerNumber;
        List<String> invalidInvoices = validateInvoices(cgInvoices, detail, errorFileName);
        if (!CollectionUtils.isEmpty(invalidInvoices)) {
            return null;

        }
        return cgInvoices;
    }

    /**
     * This method validates invoices and output an error file including unqualified invoices with reason stated.
     * 
     * @param cgInvoices
     * @param outputFileStream
     * @return
     */
    public List<String> validateInvoices(Collection<ContractsGrantsInvoiceDocument> cgInvoices, String detail, String errorFileName) {
        boolean result = false;
        boolean invalid = false;
        String line = null;
        List<String> invalidGroup = new ArrayList<String>();
        if (CollectionUtils.isEmpty(cgInvoices)) {
            line = "There were no invoices retrieved to process for " + detail;
            invalidGroup.add(line);
            try {
                File errOutPutFile = new File(errorFileName);
                PrintStream outputFileStream = null;

                try {
                    outputFileStream = new PrintStream(errOutPutFile);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                writeErrorEntry(line, outputFileStream);
            }
            catch (IOException ioe) {
                LOG.error("LetterOfCreditCreateServiceImpl.validateInvoices Stopped: " + ioe.getMessage());
                throw new RuntimeException("LetterOfCreditCreateServiceImpl.validateInvoices Stopped: " + ioe.getMessage(), ioe);
            }
            return invalidGroup;
        }
        for (ContractsGrantsInvoiceDocument cgInvoice : cgInvoices) {
            invalid = false;
            // if the invoices are not final yet - then the LOC cannot be created
            if (!cgInvoice.getDocumentHeader().getFinancialDocumentStatusCode().equalsIgnoreCase(KFSConstants.DocumentStatusCodes.APPROVED)) {
                line = "Contracts Grants Invoice# " + cgInvoice.getDocumentNumber() + " : " + ArConstants.BatchFileSystem.LOC_CREATION_ERROR_INVOICE_NOT_FINAL;
                invalidGroup.add(line);
                invalid = true;
            }

            // if invalid is true, the award is unqualified.
            // records the unqualified award with failed reasons.
            if (invalid) {
                File errOutPutFile = new File(errorFileName);
                PrintStream outputFileStream = null;

                try {
                    outputFileStream = new PrintStream(errOutPutFile);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    writeErrorEntry(line, outputFileStream);
                }
                catch (IOException ioe) {
                    LOG.error("LetterOfCreditCreateServiceImpl.validateInvoices Stopped: " + ioe.getMessage());
                    throw new RuntimeException("LetterOfCreditCreateServiceImpl.validateInvoices Stopped: " + ioe.getMessage(), ioe);
                }

                try {
                    writeNewLines("", outputFileStream);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return invalidGroup;
    }

    /**
     * This method would write errors to the error file
     * 
     * @param line
     * @param printStream
     * @throws IOException
     */
    protected void writeErrorEntry(String line, PrintStream printStream) throws IOException {
        try {
            printStream.printf("%s\n", line);
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * This method would write new line argument to the error file.
     * 
     * @param newline
     * @param printStream
     * @throws IOException
     */
    protected void writeNewLines(String newline, PrintStream printStream) throws IOException {
        try {
            printStream.printf("%s\n", newline);
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * This method calculates the Budget and cumulative amount for Award Account
     * 
     * @param awardAccount
     * @return
     */
    public KualiDecimal getBudgetAndActualsForAwardAccount(ContractsAndGrantsCGBAwardAccount awardAccount, String balanceTypeCode, Date awardBeginningDate) {
        List<Balance> glBalances = new ArrayList<Balance>();
        KualiDecimal balanceAmount = KualiDecimal.ZERO;
        KualiDecimal balAmt = KualiDecimal.ZERO;
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();
        
        
        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getFiscalYear(awardBeginningDate);
        
        for(Integer i = fiscalYear; i<= currentYear; i++ ){                                
         fiscalYears.add(i);
        }
        
        for(Integer eachFiscalYr: fiscalYears){

        Map<String, Object> balanceKeys = new HashMap<String, Object>();
        balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
        balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
        balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
        balanceKeys.put("balanceTypeCode", balanceTypeCode);
        balanceKeys.put("objectTypeCode", ArPropertyConstants.EXPENSE_OBJECT_TYPE);
        glBalances.addAll((List<Balance>) SpringContext.getBean(BusinessObjectService.class).findMatching(Balance.class, balanceKeys));
        }
        for (Balance bal : glBalances) {
            balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
            balanceAmount = balanceAmount.add(balAmt);
        }
        return balanceAmount;
    }

    /**
     * This method retrieves the amount to draw for the award account based on teh criteria passed
     * 
     * @param awardaccounts
     * @return
     */
    public void setAwardAccountToDraw(List<ContractsAndGrantsCGBAwardAccount> awardAccounts, ContractsAndGrantsCGBAward award) {

        boolean valid = true;
        // 1. To get the billed to date amount for every award account based on the criteria passed.
        List<AwardAccountObjectCodeTotalBilled> awardAccountTotalBilledAmounts = awardAccountObjectCodeTotalBilledDao.getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);


        for (ContractsAndGrantsCGBAwardAccount awardAccount : awardAccounts) {

            // 2. Get the Cumulative amount from GL Balances.

            KualiDecimal cumAmt = getBudgetAndActualsForAwardAccount(awardAccount, ArPropertyConstants.ACTUAL_BALANCE_TYPE, award.getAwardBeginningDate());
            KualiDecimal billedAmount = KualiDecimal.ZERO;
            KualiDecimal amountToDraw = KualiDecimal.ZERO;


            // 3. Amount to Draw = Cumulative amount - Billed to Date.(This would be ultimately the current expenditures in the
            // invoice document.
            for (AwardAccountObjectCodeTotalBilled awardAccountTotalBilledAmount : awardAccountTotalBilledAmounts) {
                if (awardAccountTotalBilledAmount.getAccountNumber().equals(awardAccount.getAccountNumber()) && awardAccountTotalBilledAmount.getChartOfAccountsCode().equals(awardAccount.getChartOfAccountsCode()) && awardAccountTotalBilledAmount.getProposalNumber().equals(awardAccount.getProposalNumber())) {
                    billedAmount = billedAmount.add(awardAccountTotalBilledAmount.getTotalBilled());
                }
            }
            amountToDraw = cumAmt.subtract(billedAmount);
            // set the amount to Draw in the award Account
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
            criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
            criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount.getProposalNumber());
            SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setAmountToDrawToAwardAccount(criteria, amountToDraw);
        }

    }

    /**
     * This method calculates the claim on cash balance for every award account.
     * 
     * @param awardaccount
     * @return
     */
    public KualiDecimal getClaimOnCashforAwardAccount(ContractsAndGrantsCGBAwardAccount awardAccount, java.sql.Date awardBeginningDate) {

        // 2. Get the Cumulative amount from GL Balances.
        KualiDecimal balAmt = KualiDecimal.ZERO;
        KualiDecimal expAmt = KualiDecimal.ZERO;
        KualiDecimal incAmt = KualiDecimal.ZERO;
        KualiDecimal claimOnCash = KualiDecimal.ZERO;
        List<Balance> glBalances = new ArrayList<Balance>();
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();
        
        
        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getFiscalYear(awardBeginningDate);
        
        for(Integer i = fiscalYear; i<= currentYear; i++ ){                                
         fiscalYears.add(i);
        }
        
        for(Integer eachFiscalYr: fiscalYears){
        Map<String, Object> balanceKeys = new HashMap<String, Object>();
        balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
        balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
        balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
        balanceKeys.put("balanceTypeCode", ArPropertyConstants.ACTUAL_BALANCE_TYPE);
        glBalances.addAll((List<Balance>) SpringContext.getBean(BusinessObjectService.class).findMatching(Balance.class, balanceKeys));
        }
        for (Balance bal : glBalances) {
            if (bal.getObjectTypeCode().equalsIgnoreCase(ArPropertyConstants.EXPENSE_OBJECT_TYPE)) {
                balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                
                expAmt = expAmt.add(balAmt);
            }
            else if (bal.getObjectTypeCode().equalsIgnoreCase(ArPropertyConstants.INCOME_OBJECT_TYPE)) {
                balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                
                incAmt = incAmt.add(balAmt);
            }
        }

        return claimOnCash = incAmt.subtract(expAmt);


    }

    /**
     * This method retrieves the amount available to draw for the award accounts
     * 
     * @param awardTotalAmount
     * @param awardAccount
     */
    public KualiDecimal getAmountAvailableToDraw(KualiDecimal awardTotalAmount, List<ContractsAndGrantsCGBAwardAccount> awardAccounts) {

        // 1. To get the billed to date amount for every award account based on the criteria passed.
        List<AwardAccountObjectCodeTotalBilled> awardAccountTotalBilledAmounts = awardAccountObjectCodeTotalBilledDao.getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);
        KualiDecimal billedAmount = KualiDecimal.ZERO;
        KualiDecimal amountAvailableToDraw = KualiDecimal.ZERO;
        for (AwardAccountObjectCodeTotalBilled awardAccountTotalBilledAmount : awardAccountTotalBilledAmounts) {
            billedAmount = billedAmount.add(awardAccountTotalBilledAmount.getTotalBilled());
        }
        amountAvailableToDraw = awardTotalAmount.subtract(billedAmount);

        return amountAvailableToDraw;
    }

    /**
     * This method serves as a create and update. When it is first called, the List<InvoiceSuspensionCategory> is empty. This list
     * then gets populated with invoiceSuspensionCategories where the test fails. Each time the document goes through validation,
     * and this method gets called, it will update the list by adding or remvoing the suspension categories
     * 
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#updateSuspensionCategoriesOnDocument(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    public void updateSuspensionCategoriesOnDocument(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {

        ContractsAndGrantsCGBAward award = contractsGrantsInvoiceDocument.getAward();
        String documentNumber = contractsGrantsInvoiceDocument.getDocumentNumber();
        // ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService =
        // SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

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
        if (isReportNotAttachedButRequiredByAward(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.REPORTS_ARE_REQUIRED_TO_BE_ATTACHED);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.REPORTS_ARE_REQUIRED_TO_BE_ATTACHED);
        }

        // validation suspension code - Make sure the Primary Address is completed
        if (!isAgencyPrimaryAddressComplete(award.getAgency())) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AGENCY_PRIMARY_ADDRESS_NOT_COMPLETE);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AGENCY_PRIMARY_ADDRESS_NOT_COMPLETE);
        }

        // validation suspension code - Check to see if the Alternate address is completed if it was entered to begin with
        if (!isAgencyAlternateAddressComplete(award.getAgency())) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AGENCY_ALTERNATE_ADDRESS_NOT_COMPLETE);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AGENCY_ALTERNATE_ADDRESS_NOT_COMPLETE);
        }

        // validation suspension code - Make sure invoice is final if the award is already expired
        if (isInvoiceNotFinalAndAwardExpired(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.INVOICE_NOT_FINAL_AND_EXPIRATION_DATE_REACHED);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.INVOICE_NOT_FINAL_AND_EXPIRATION_DATE_REACHED);
        }

        // validation suspension code - Check to see if object codes are included in a category. one way to check is to compare
        // total current expenditure to the sum of account current expenditure
        if (!isAwardIBillingFrequencyIsPredetermined(award) && !isAwardBillingFrequencyIsMilestone(award) && !isCategoryCumulativeExpenditureMatchAccountCumulativeExpenditureSum(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.OBJECT_CODE_FOR_AN_ACCOUNT_MAY_BE_MISSING_FROM_CGB_CATEGORY_CODE);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.OBJECT_CODE_FOR_AN_ACCOUNT_MAY_BE_MISSING_FROM_CGB_CATEGORY_CODE);
        }

        // validation suspension code - Check to see if Loc Amount is sufficient
        if (isLocAmountNotSufficent(contractsGrantsInvoiceDocument)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.LOC_REMAINING_AMOUNT_IS_NOT_SUFFICIENT);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.LOC_REMAINING_AMOUNT_IS_NOT_SUFFICIENT);
        }

        // validation suspension code - Check to see if award has any active but expired account
        if (isAwardHasActiveExpiredAccount(award)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AWARD_HAS_ACTIVE_BUT_EXPIRED_ACCOUNT);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AWARD_HAS_ACTIVE_BUT_EXPIRED_ACCOUNT);
        }

        // validation suspension code - Check to see if award has 'Suspend Invoicing' enabled
        if (award.isSuspendInvoicing()) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.AWARD_SUSPENDED_BY_USER);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.AWARD_SUSPENDED_BY_USER);
        }

        // validation suspension code - Check to see if invoice type is missing from award
        if (isAwardInvoiceTypeMissing(award)) {
            addSuspensionCategoryToDocument(suspensionCategoryCodes, invoiceSuspensionCategories, documentNumber, ArConstants.SuspensionCategories.INVOICE_TYPE_IS_MISSING);
        }
        else {
            removeSuspensionCategoryFromDocument(suspensionCategoryCodes, invoiceSuspensionCategories, ArConstants.SuspensionCategories.INVOICE_TYPE_IS_MISSING);
        }
        
        // validation suspension code - Check to see if award has closed account of which the still have current expenditure
        if (isAwardHasClosedAccountWithCurrentExpenditures(contractsGrantsInvoiceDocument)) {
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
    private void addSuspensionCategoryToDocument(List<String> suspensionCategoryCodes, List<InvoiceSuspensionCategory> invoiceSuspensionCategories, String documentNumber, String suspensionCategoryCode) {
        if (!suspensionCategoryCodes.contains(suspensionCategoryCode)) { // check prevents duplicate
            // To check if the suspension category is active.
            SuspensionCategory suspensionCategory = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(SuspensionCategory.class, suspensionCategoryCode);
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
    private void removeSuspensionCategoryFromDocument(List<String> suspensionCategoryCodes, List<InvoiceSuspensionCategory> invoiceSuspensionCategories, String suspensionCategoryCode) {
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
        Date documentDate = contractsGrantsInvoiceDocument.getDocumentHeader().getWorkflowDocument().getDateCreated();
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
    public boolean isBillAmountExceedAwardTotalAmount(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        return contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().isGreaterThan(contractsGrantsInvoiceDocument.getAward().getAwardTotalAmount());
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isInvoiceAmountLessThanInvoiceMinimumRequirements(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        KualiDecimal invoiceMinimumAmount = contractsGrantsInvoiceDocument.getAward().getMinInvoiceAmount();
        if (invoiceMinimumAmount == null) {
            return false; // if no minimum specified, then no limit
        }
        return contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled().isLessThan(invoiceMinimumAmount);
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isReportNotAttachedButRequiredByAward(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if( contractsGrantsInvoiceDocument.getAward().isAdditionalFormsRequired()){
            return true;
        }
        return false;
    }

    /**
     * @param agency
     * @return
     */
    public boolean isAgencyPrimaryAddressComplete(ContractsAndGrantsCGBAgency agency) {

        List<ContractsAndGrantsAgencyAddress> agencyAddresses = new ArrayList<ContractsAndGrantsAgencyAddress>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.AGENCY_NUMBER, agency.getAgencyNumber());
        agencyAddresses = (List) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObjectsList(ContractsAndGrantsAgencyAddress.class, map);
        for (ContractsAndGrantsAgencyAddress agencyAddress : agencyAddresses) {
            if (ArConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE.equals(agencyAddress.getAgencyAddressTypeCode())) {
                return isAgencyAddressComplete(agencyAddress);
            }
        }
        return false;
    }

    /**
     * @param agency
     * @return
     */
    public boolean isAgencyAlternateAddressComplete(ContractsAndGrantsCGBAgency agency) {

        List<ContractsAndGrantsAgencyAddress> agencyAddresses = new ArrayList<ContractsAndGrantsAgencyAddress>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.AGENCY_NUMBER, agency.getAgencyNumber());
        agencyAddresses = (List) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObjectsList(ContractsAndGrantsAgencyAddress.class, map);

        for (ContractsAndGrantsAgencyAddress agencyAddress : agencyAddresses) {
            if (ArConstants.AGENCY_ALTERNATE_ADDRESSES_TYPE_CODE.equals(agencyAddress.getAgencyAddressTypeCode())) {
                return isAgencyAddressComplete(agencyAddress);
            }
        }
        return true; // if no alternate address entered at all, then that is ok
    }

    /**
     * @param agencyAddress
     * @return
     */
    public boolean isAgencyAddressComplete(ContractsAndGrantsAgencyAddress agencyAddress) {
        if (!StringUtils.isEmpty(agencyAddress.getAgencyLine1StreetAddress()) && !StringUtils.isEmpty(agencyAddress.getAgencyCityName()) && !StringUtils.isEmpty(agencyAddress.getAgencyStateCode()) && !StringUtils.isEmpty(agencyAddress.getAgencyZipCode()) && !StringUtils.isEmpty(agencyAddress.getAgencyCountryCode())) {
            return true;
        }
        return false;
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isInvoiceNotFinalAndAwardExpired(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        return isAwardExpired(contractsGrantsInvoiceDocument.getAward()) && !contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().isFinalBill();
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isCategoryCumulativeExpenditureMatchAccountCumulativeExpenditureSum(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        KualiDecimal categoryCumulativeExpenditure = contractsGrantsInvoiceDocument.getTotalInvoiceDetails().get(0).getCumulative();
        KualiDecimal accountDetailsCumulativeExpenditure = KualiDecimal.ZERO;

        for (InvoiceAccountDetail invoiceAccountDetail : contractsGrantsInvoiceDocument.getAccountDetails()) {
            accountDetailsCumulativeExpenditure = accountDetailsCumulativeExpenditure.add(invoiceAccountDetail.getCumulativeAmount());
        }

        if (categoryCumulativeExpenditure.equals(accountDetailsCumulativeExpenditure)) {
            return true;
        }
        else {
            return false;
        }

    }
    /**
     * @param award
     * @return
     */
    public boolean isAwardMarkedStopWork(ContractsAndGrantsCGBAward award) {
        return award.isStopWork();
    }
    
    
    /**
     * @param award
     * @return
     */
    public boolean isAwardInvoiceTypeMissing(ContractsAndGrantsCGBAward award) {
        return StringUtils.isEmpty(award.getInvoicingOptions());
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isLocAmountNotSufficent(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument){
        if(ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE.equals(contractsGrantsInvoiceDocument.getAward().getBillingFrequency().getFrequency())){
            if(contractsGrantsInvoiceDocument.getAward().getLetterOfCreditFund().getLetterOfCreditFundAmount()
                    .isLessThan(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getNewTotalBilled())){
                return true;
            }
        }
        return false;
    }

    /**
     * @param award
     * @return
     */
    public boolean isAwardHasActiveExpiredAccount(ContractsAndGrantsCGBAward award) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        List<ContractsAndGrantsCGBAwardAccount> awardAccounts = award.getActiveAwardAccounts();
        for (ContractsAndGrantsCGBAwardAccount awardAccount : awardAccounts) {
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
    public boolean isAwardExpired(ContractsAndGrantsCGBAward award) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        return now.after(award.getAwardEndingDate());
    }

    /**
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    public boolean isAwardHasClosedAccountWithCurrentExpenditures(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
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
        AccountService accountService = SpringContext.getBean(AccountService.class);
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
    public boolean isAwardBillingFrequencyIsMilestone(ContractsAndGrantsCGBAward award) {
        if (ObjectUtils.isNull(award.getPreferredBillingFrequency())) {
            return false;
        }

        return ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE.equals(award.getPreferredBillingFrequency());
    }

    /**
     * @param award
     * @return
     */
    public boolean isAwardIBillingFrequencyIsPredetermined(ContractsAndGrantsCGBAward award) {
        if (ObjectUtils.isNull(award.getPreferredBillingFrequency())) {
            return false;
        }
        return ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE.equals(award.getPreferredBillingFrequency());
    }


    /**
     * This method would make sure the amounts of the currrent period are not included. So it calculates the cumulative and
     * subtracts the current period values. This would be done for Billing Frequencies - Monthly, Quarterly, Semi-Annual and Annual.
     * 
     * @param glBalance
     * @return balanceAmount
     */
    public KualiDecimal retrieveAccurateBalanceAmount(java.sql.Date lastBilledDate, Balance glBalance) {


        // 1. calculate invoice period
        AccountingPeriod invoicePeriod = accountingPeriodService.getByDate(lastBilledDate);
        String invoicePeriodCode = invoicePeriod.getUniversityFiscalPeriodCode();

        // 2. Get the current Period Code
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        java.sql.Date today = new java.sql.Date(ts.getTime());
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
        String currentPeriodCode = currPeriod.getUniversityFiscalPeriodCode();

        // 3. Now to iterate over the period codes and find the amounts for the differnce between invoice Period and current Period
        // - Assuming its the same fiscal year
        List<AccountingPeriod> acctPeriodList = new ArrayList<AccountingPeriod>();

        acctPeriodList = (List<AccountingPeriod>) accountingPeriodService.getAllAccountingPeriods();
        KualiDecimal currentBalanceAmount = KualiDecimal.ZERO;
        java.lang.reflect.Method method;

        if (CollectionUtils.isNotEmpty(acctPeriodList)) {

            for (int i = acctPeriodList.indexOf(invoicePeriod) + 1; i <= acctPeriodList.indexOf(currPeriod); i++) {
                if (acctPeriodList.get(i).getUniversityFiscalYear().equals(currPeriod.getUniversityFiscalYear()) && acctPeriodList.get(i).isActive()) {
                    // Now to get the month for the period.
                    String periodCode = acctPeriodList.get(i).getUniversityFiscalPeriodCode().replaceFirst("^0*", "");
                    String methodName = "getMonth" + periodCode + "Amount";
                    try {
                        method = glBalance.getClass().getMethod(methodName);
                        currentBalanceAmount = currentBalanceAmount.add((KualiDecimal) method.invoke(glBalance));

                    }
                    catch (SecurityException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    catch (NoSuchMethodException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    catch (IllegalArgumentException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    catch (IllegalAccessException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    catch (InvocationTargetException e) {
                        throw new RuntimeException(e.getMessage());
                    }

                }
            }
        }
        KualiDecimal balAmt = glBalance.getContractsGrantsBeginningBalanceAmount().add(glBalance.getAccountLineAnnualBalanceAmount());
        KualiDecimal accurateBalanceAmount = balAmt.subtract(currentBalanceAmount);

        return accurateBalanceAmount;
    }

    /**
     * This method get the milestones with the criteria defined and set value to isItBilled.
     */
    public void retrieveAndUpdateMilestones(List<InvoiceMilestone> invoiceMilestones, String string) throws Exception {
        if (invoiceMilestones == null) {
            throw new Exception("(List<InvoiceMilestone> invoiceMilestones cannot be null");
        }
        List<Long> milestoneIds = new ArrayList<Long>();
        for (InvoiceMilestone invoiceMilestone : invoiceMilestones) {
            milestoneIds.add(invoiceMilestone.getMilestoneIdentifier());
        }
        // This method get the milestones with the criteria defined and set value to isItBilled.

        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setMilestonesisItBilled(invoiceMilestones.get(0).getProposalNumber(), milestoneIds, string);
    }


    /**
     * This method get the bills with the criteria defined and set value to isItBilled.
     */
    public void retrieveAndUpdateBills(List<InvoiceBill> invoiceBills, String value) throws Exception {
        if (invoiceBills == null) {
            throw new Exception("(List<InvoiceBill> invoiceBills cannot be null");
        }

        Criteria mainCriteria = new Criteria();

        for (InvoiceBill invoiceBill : invoiceBills) {
            Criteria billNumberCriteria = new Criteria();
            Criteria billIdCriteria = new Criteria();
            Criteria proposalNumberCriteria = new Criteria();
            Criteria subCriteria = new Criteria();

            billNumberCriteria.addEqualTo("billNumber", invoiceBill.getBillNumber());
            billIdCriteria.addEqualTo("billIdentifier", invoiceBill.getBillIdentifier());
            proposalNumberCriteria.addEqualTo("proposalNumber", invoiceBill.getProposalNumber());

            subCriteria.addAndCriteria(billNumberCriteria);
            subCriteria.addAndCriteria(billIdCriteria);
            subCriteria.addAndCriteria(proposalNumberCriteria);

            mainCriteria.addOrCriteria(subCriteria);
        }

        // To get the bills with the criteria defined and set value to isItBilled.
        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setBillsisItBilled(mainCriteria, value);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#calculateTotalPaymentsToDateByAward(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public KualiDecimal calculateTotalPaymentsToDateByAward(ContractsAndGrantsCGBAward award) {
        KualiDecimal totalPayments = KualiDecimal.ZERO;

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocs = SpringContext.getBean(BusinessObjectService.class).findMatching(ContractsGrantsInvoiceDocument.class, criteria);

        for (ContractsGrantsInvoiceDocument cgInvoiceDoc : cgInvoiceDocs) {
            criteria.clear();
            criteria.put("financialDocumentReferenceInvoiceNumber", cgInvoiceDoc.getDocumentNumber());

            Collection<InvoicePaidApplied> invoicePaidApplieds = SpringContext.getBean(BusinessObjectService.class).findMatching(InvoicePaidApplied.class, criteria);
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
    public KualiDecimal getCumulativeCashDisbursement(ContractsAndGrantsCGBAwardAccount awardAccount, java.sql.Date awardBeginningDate) {
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        KualiDecimal cumAmt = KualiDecimal.ZERO;
        KualiDecimal balAmt = KualiDecimal.ZERO;
        List<Balance> glBalances = new ArrayList<Balance>();
        
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();
        
        
        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getFiscalYear(awardBeginningDate);
        
        for(Integer i = fiscalYear; i<= currentYear; i++ ){                                
         fiscalYears.add(i);
        }
        
        for(Integer eachFiscalYr: fiscalYears){
        Map<String, Object> balanceKeys = new HashMap<String, Object>();
        balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
        balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
        balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
        balanceKeys.put("balanceTypeCode", ArPropertyConstants.ACTUAL_BALANCE_TYPE);
        balanceKeys.put("objectTypeCode", ArPropertyConstants.EXPENSE_OBJECT_TYPE);
        glBalances.addAll((List<Balance>) SpringContext.getBean(BusinessObjectService.class).findMatching(Balance.class, balanceKeys));
        }
        for (Balance bal : glBalances) {
            balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
            cumAmt = cumAmt.add(balAmt);
        }
        return cumAmt;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardInvoicingOptionMissing(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean isAwardInvoicingOptionMissing(ContractsAndGrantsCGBAward award) {
        String invOption = award.getInvoicingOptions();
        if (invOption == null || invOption.isEmpty()) {
            return true;
        }
        return false;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardClosed(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean isAwardClosed(ContractsAndGrantsCGBAward award) {
        Date today, clsDt;
        today = dateTimeService.getCurrentSqlDateMidnight();
        clsDt = award.getAwardClosingDate();
        if (ObjectUtils.isNotNull(clsDt) && clsDt.before(today)) {
            return true;
        }
        return false;

    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasExpiredAccounts(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean hasExpiredAccounts(ContractsAndGrantsCGBAward award) {
        Collection<Account> accounts = getExpiredAccountsOfAward(award);
        if (ObjectUtils.isNotNull(accounts) && !accounts.isEmpty())
            return true;
        return false;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoAccountsAssigned(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean hasNoActiveAccountsAssigned(ContractsAndGrantsCGBAward award) {

        Collection<ContractsAndGrantsCGBAwardAccount> awardAccounts = award.getActiveAwardAccounts();
        if (awardAccounts.isEmpty())
            return true;
        return false;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardInvoicingSuspendedByUser(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean isAwardInvoicingSuspendedByUser(ContractsAndGrantsCGBAward award) {

        return award.isSuspendInvoicing();
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardOrganizationIncomplete(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean isAwardOrganizationIncomplete(ContractsAndGrantsCGBAward award) {

        return false;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardPassedStopDate(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean isAwardPassedStopDate(ContractsAndGrantsCGBAward award) {

        Date today = dateTimeService.getCurrentSqlDateMidnight();
        Date stopDt = award.getAwardEndingDate();
        if (ObjectUtils.isNotNull(stopDt) && stopDt.before(today)) {
            return true;
        }
        return false;
    }

    /**
     * Check if Preferred Billing Frequency is set correctly.
     * 
     * @param award
     * @return False if preferred billing schedule is null, or set as perdetermined billing schedule or milestone billing schedule
     *         and award has no award account or more than 1 award accounts assigned.
     */
    public boolean isPreferredBillingFrequencySetCorrectly(ContractsAndGrantsCGBAward award) {

        if (award.getPreferredBillingFrequency() == null || ((award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) || award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE)) && award.getActiveAwardAccounts().size() != 1)) {
            return false;
        }
        return true;
    }


    /**
     * Check if the value of PreferredBillingFrequency is in the BillingFrequency value set.
     * 
     * @param award
     * @return
     */
    public boolean isValueOfPreferredBillingFrequencyValid(ContractsAndGrantsCGBAward award) {
        Boolean valid = false;
        if (award.getPreferredBillingFrequency() != null) {
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(KFSPropertyConstants.ACTIVE, true);
            Collection<ContractsAndGrantsBillingFrequency> set = (List) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingFrequency.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingFrequency.class, criteria);
            for (ContractsAndGrantsBillingFrequency billingFrequency : set) {
                if (award.getPreferredBillingFrequency().equalsIgnoreCase(billingFrequency.getFrequency())) {
                    valid = true;
                    break;
                }
            }
        }

        return valid;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getExpiredAccountsOfAward(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     *      Retrive all the expired accounts of an award
     */
    public Collection<Account> getExpiredAccountsOfAward(ContractsAndGrantsCGBAward award) {

        Collection<ContractsAndGrantsCGBAwardAccount> awardAccounts = award.getActiveAwardAccounts();
        Collection<Account> expiredAwardAccounts = new ArrayList<Account>();

        if (awardAccounts != null && !awardAccounts.isEmpty()) {

            Date today = dateTimeService.getCurrentSqlDateMidnight();

            for (ContractsAndGrantsCGBAwardAccount awardAccount : awardAccounts) {
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
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getContractControlAccounts(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public Collection<Account> getContractControlAccounts(ContractsAndGrantsCGBAward award) {

        if (!this.hasNoActiveAccountsAssigned(award)) {
            Collection<Account> controlAccounts = new ArrayList<Account>();
            for (ContractsAndGrantsCGBAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                if (ObjectUtils.isNotNull(awardAccount.getAccount().getContractControlAccount())) {
                    controlAccounts.add(awardAccount.getAccount().getContractControlAccount());
                }
            }
            if (CollectionUtils.isEmpty(controlAccounts)) {
                return null;
            }
            else {
                return controlAccounts;
            }
        }
        return null;
    }


    /**
     * Sets the BusinessObjectService. Provides Spring compatibility.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardFinalInvoiceAlreadyBuilt(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean isAwardFinalInvoiceAlreadyBuilt(ContractsAndGrantsCGBAward award) {
        List<ContractsAndGrantsCGBAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsCGBAwardAccount>();
        ContractsAndGrantsCGBAwardAccount awardAccount;
        Iterator<ContractsAndGrantsCGBAwardAccount> iterator = award.getActiveAwardAccounts().iterator();
        while (iterator.hasNext()) {
            awardAccount = iterator.next();
            if (!awardAccount.isFinalBilled()) {
                awardAccounts.add(awardAccount);
            }
            if (CollectionUtils.isEmpty(awardAccounts)) {
                return true;
            }
        }

        return false;
    }


    /**
     * this method checks If all accounts of award has invoices in progress.
     * 
     * @param award
     * @return
     */
    public boolean isInvoiceInProgress(ContractsAndGrantsCGBAward award) {

        List<ContractsAndGrantsCGBAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsCGBAwardAccount>();
        ContractsAndGrantsCGBAwardAccount awardAccount;
        Iterator<ContractsAndGrantsCGBAwardAccount> iterator = award.getActiveAwardAccounts().iterator();
        while (iterator.hasNext()) {
            awardAccount = iterator.next();
            if (StringUtils.isBlank(awardAccount.getInvoiceDocumentStatus()) || awardAccount.getInvoiceDocumentStatus().equalsIgnoreCase(KewApiConstants.ROUTE_HEADER_FINAL_LABEL) || awardAccount.getInvoiceDocumentStatus().equalsIgnoreCase(KewApiConstants.ROUTE_HEADER_CANCEL_LABEL) || awardAccount.getInvoiceDocumentStatus().equalsIgnoreCase(KewApiConstants.ROUTE_HEADER_DISAPPROVED_LABEL)) {
                awardAccounts.add(awardAccount);
            }
            if (CollectionUtils.isEmpty(awardAccounts)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getActiveAwardsByCriteria(java.util.Map)
     */
    public List<ContractsAndGrantsCGBAward> getActiveAwardsByCriteria(Map<String, Object> criteria) {

        return (List) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAward.class).getExternalizableBusinessObjectsList(ContractsAndGrantsCGBAward.class, criteria);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoMilestonesToInvoice(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean hasNoMilestonesToInvoice(ContractsAndGrantsCGBAward award) {
        boolean valid = false;
        if (award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
            List<ContractsAndGrantsMilestone> milestones = new ArrayList<ContractsAndGrantsMilestone>();
            List<ContractsAndGrantsMilestone> validMilestones = new ArrayList<ContractsAndGrantsMilestone>();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
            milestones = (List) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsMilestone.class).getExternalizableBusinessObjectsList(ContractsAndGrantsMilestone.class, map);

            // To retrieve the previous period end Date to check for milestones and billing schedule.

            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            java.sql.Date[] pair = verifyBillingFrequency.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            java.sql.Date invoiceDate = pair[1];


            for (ContractsAndGrantsMilestone awdMilestone : milestones) {
                if (awdMilestone.getMilestoneActualCompletionDate() != null && !invoiceDate.before(awdMilestone.getMilestoneActualCompletionDate()) && awdMilestone.getIsItBilled().equals(KFSConstants.ParameterValues.STRING_NO) && awdMilestone.getMilestoneAmount().isGreaterThan(KualiDecimal.ZERO)) {
                    validMilestones.add(awdMilestone);
                }
            }
            if (CollectionUtils.isEmpty(validMilestones)) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoBillsToInvoice(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean hasNoBillsToInvoice(ContractsAndGrantsCGBAward award) {
        boolean valid = false;
        if (award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {

            List<ContractsAndGrantsBill> bills = new ArrayList<ContractsAndGrantsBill>();
            List<ContractsAndGrantsBill> validBills = new ArrayList<ContractsAndGrantsBill>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());

            bills = (List) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBill.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBill.class, map);
            // To retrieve the previous period end Date to check for milestones and billing schedule.

            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            java.sql.Date[] pair = verifyBillingFrequency.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            java.sql.Date invoiceDate = pair[1];

            for (ContractsAndGrantsBill awdBill : bills) {
                if (awdBill.getBillDate() != null && !invoiceDate.before(awdBill.getBillDate()) && awdBill.getIsItBilled().equals(KFSConstants.ParameterValues.STRING_NO) && awdBill.getEstimatedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                    validBills.add(awdBill);
                }
            }
            if (CollectionUtils.isEmpty(validBills)) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#owningAgencyHasNoCustomerRecord(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward)
     */
    public boolean owningAgencyHasNoCustomerRecord(ContractsAndGrantsCGBAward award) {
        boolean valid = true;
        CustomerService customerService = SpringContext.getBean(CustomerService.class);
        if (ObjectUtils.isNotNull(award.getAgency().getCustomerNumber())) {
            Customer customer = customerService.getByPrimaryKey(award.getAgency().getCustomerNumber());
            if (ObjectUtils.isNotNull(customer)) {
                valid = false;
            }
        }

        return valid;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getContractsGrantsInvoiceDocumentAppliedByPaymentApplicationNumber(java.lang.String)
     */
    public Collection<ContractsGrantsInvoiceDocument> getContractsGrantsInvoiceDocumentAppliedByPaymentApplicationNumber(String paymentApplicationNumberCorrecting) {
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        try {
            PaymentApplicationDocument paymentApplicationDocument = (PaymentApplicationDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(paymentApplicationNumberCorrecting);
            for (InvoicePaidApplied invoicePaidApplied : paymentApplicationDocument.getInvoicePaidApplieds()) {
                cgInvoices.add((ContractsGrantsInvoiceDocument) invoicePaidApplied.getCustomerInvoiceDocument());
            }
            return cgInvoices;
        }
        catch (WorkflowException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * This method checks if the System Information and ORganization Accounting Default are setup for the Chart Code and Org Code
     * from the award accounts.
     * 
     * @param award
     * @return
     */
    public boolean isChartAndOrgNotSetupForInvoicing(ContractsAndGrantsCGBAward award) {
        String coaCode = null, orgCode = null;
        String procCoaCode = null, procOrgCode = null;
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();

        Map<String, Object> criteria = new HashMap<String, Object>();
        Map<String, Object> sysCriteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        sysCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        
        
        // To get the chart code and org code for invoicing depending on the invoicing options.
        if (ObjectUtils.isNotNull(award.getInvoicingOptions())) {
            if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_ACCOUNT)) {
                for (ContractsAndGrantsCGBAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    coaCode = awardAccount.getAccount().getChartOfAccountsCode();
                    orgCode = awardAccount.getAccount().getOrganizationCode();
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
                    //To retrieve processing codes based on billing codes using organization options
                    List<String> procCodes =  getProcessingFromBillingCodes(coaCode, orgCode);
                    if(CollectionUtils.isEmpty(procCodes)){
                        return true;
                    }
                    sysCriteria.put("processingChartOfAccountCode", procCodes.get(0));
                    sysCriteria.put("processingOrganizationCode", procCodes.get(1));
                    OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
                   
                    SystemInformation systemInformation = (SystemInformation) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, sysCriteria);
                    if (ObjectUtils.isNull(organizationAccountingDefault) || ObjectUtils.isNull(systemInformation)) {
                        return true;
                    }
                    
                }
            }
            if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT)) {
                List<Account> controlAccounts = (List<Account>) getContractControlAccounts(award);

                for (Account controlAccount : controlAccounts) {
                    coaCode = controlAccount.getChartOfAccountsCode();
                    orgCode = controlAccount.getOrganizationCode();
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
                    //To retrieve processing codes based on billing codes using organization options
                    List<String> procCodes =  getProcessingFromBillingCodes(coaCode, orgCode);
                    if(CollectionUtils.isEmpty(procCodes)){
                        return true;
                    }
                    sysCriteria.put("processingChartOfAccountCode", procCodes.get(0));
                    sysCriteria.put("processingOrganizationCode", procCodes.get(1));
                    OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
                   
                    SystemInformation systemInformation = (SystemInformation) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, sysCriteria);
                    if (ObjectUtils.isNull(organizationAccountingDefault) || ObjectUtils.isNull(systemInformation)) {
                        return true;
                    }
                }
            }
            if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_AWARD)) {
                List<Account> controlAccounts = (List<Account>) getContractControlAccounts(award);

                for (Account controlAccount : controlAccounts) {
                    coaCode = controlAccount.getChartOfAccountsCode();
                    orgCode = controlAccount.getOrganizationCode();
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
                    //To retrieve processing codes based on billing codes using organization options
                    List<String> procCodes =  getProcessingFromBillingCodes(coaCode, orgCode);
                    if(CollectionUtils.isEmpty(procCodes)){
                        return true;
                    }
                    sysCriteria.put("processingChartOfAccountCode", procCodes.get(0));
                    sysCriteria.put("processingOrganizationCode", procCodes.get(1));
                    OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
                   
                    SystemInformation systemInformation = (SystemInformation) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, sysCriteria);
                    if (ObjectUtils.isNull(organizationAccountingDefault) || ObjectUtils.isNull(systemInformation)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * To retrieve processing chart code and org code from the billing chart code and org code
     * @param cgInvoiceDocument
     * @param billingChartOfAccountsCode
     * @param billingOrganizationCode
     * @return
     */
    public List<String> getProcessingFromBillingCodes(String billingChartCode, String billingOrgCode ) {
        
        List<String> procCodes = new ArrayList<String>();
        //To access Organization Options to find the billing values based on procesing codes
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, billingChartCode);
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, billingOrgCode);
        OrganizationOptions organizationOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        if (ObjectUtils.isNotNull(organizationOptions)) {
            procCodes.add(0, organizationOptions.getProcessingChartOfAccountCode());
            procCodes.add(1, organizationOptions.getProcessingOrganizationCode());
        }        
        
        return procCodes;
    }
    
    /**
     * This method checks if the Offset Definition is setup for the Chart Code
     * from the award accounts.
     * 
     * @param award
     * @return
     */
    public boolean isOffsetDefNotSetupForInvoicing(ContractsAndGrantsCGBAward award) {
        String coaCode = null, orgCode = null;
        Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = receivableOffsetOption.equals("3");
        //This condition is validated only if GLPE is 3 and CG enhancements is ON
        if (isUsingReceivableFAU ) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        Map<String, Object> sysCriteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        criteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, ArPropertyConstants.ACTUAL_BALANCE_TYPE);
        criteria.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, ArConstants.CGIN_DOCUMENT_TYPE);        
        // 1. To get the chart code and org code for invoicing depending on the invoicing options.
        if (ObjectUtils.isNotNull(award.getInvoicingOptions())) {
            if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_ACCOUNT)) {
                for (ContractsAndGrantsCGBAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    coaCode = awardAccount.getAccount().getChartOfAccountsCode();
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    OffsetDefinition offset = (OffsetDefinition) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OffsetDefinition.class, criteria); 
                    if (ObjectUtils.isNull(offset)) {
                        return true;
                    }
                }
            }
            if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT)) {
                List<Account> controlAccounts = (List<Account>) getContractControlAccounts(award);

                for (Account controlAccount : controlAccounts) {
                    coaCode = controlAccount.getChartOfAccountsCode();
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    OffsetDefinition offset = (OffsetDefinition) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OffsetDefinition.class, criteria); 
                    if (ObjectUtils.isNull(offset)) {
                        return true;
                    }
                }
            }
            if (award.getInvoicingOptions().equalsIgnoreCase(ArPropertyConstants.INV_AWARD)) {
                List<Account> controlAccounts = (List<Account>) getContractControlAccounts(award);

                for (Account controlAccount : controlAccounts) {
                    coaCode = controlAccount.getChartOfAccountsCode();
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);                   
                    OffsetDefinition offset = (OffsetDefinition) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OffsetDefinition.class, criteria); 
                    if (ObjectUtils.isNull(offset)) {
                        return true;
                    }
                }
            }
        }
        }
        return false;
    }
    
    
    /**
     * Gets the accountingPeriodService attribute.
     * 
     * @return Returns the accountingPeriodService.
     */
    public AccountingPeriodService getAccountingPeriodService() {
        return accountingPeriodService;
    }

    /**
     * Sets the accountingPeriodService attribute value.
     * 
     * @param accountingPeriodService The accountingPeriodService to set.
     */
    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    /**
     * Gets the verifyBillingFrequency attribute.
     * 
     * @return Returns the verifyBillingFrequency.
     */
    public VerifyBillingFrequency getVerifyBillingFrequency() {
        return verifyBillingFrequency;
    }

    /**
     * Sets the verifyBillingFrequency attribute value.
     * 
     * @param verifyBillingFrequency The verifyBillingFrequency to set.
     */
    public void setVerifyBillingFrequency(VerifyBillingFrequency verifyBillingFrequency) {
        this.verifyBillingFrequency = verifyBillingFrequency;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getMilestonesBilledToDate(java.lang.Long)
     */
    public KualiDecimal getMilestonesBilledToDate(Long proposalNumber) {
        Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
        totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        KualiDecimal billedToDate = KualiDecimal.ZERO;

        List<ContractsAndGrantsMilestone> milestones = (List<ContractsAndGrantsMilestone>) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObjectsList(ContractsAndGrantsMilestone.class, totalBilledKeys);
        if (CollectionUtils.isNotEmpty(milestones)) {
            Iterator<ContractsAndGrantsMilestone> iterator = milestones.iterator();
            while (iterator.hasNext()) {
                ContractsAndGrantsMilestone milestone = iterator.next();
                if (KFSConstants.ParameterValues.STRING_YES.equals(milestone.getIsItBilled())) {
                    billedToDate = billedToDate.add(milestone.getMilestoneAmount());
                }
            }
        }
        return billedToDate;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getPredeterminedBillingBilledToDate(java.lang.Long)
     */
    public KualiDecimal getPredeterminedBillingBilledToDate(Long proposalNumber) {
        Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
        totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        KualiDecimal billedToDate = KualiDecimal.ZERO;

        List<ContractsAndGrantsBill> bills = (List<ContractsAndGrantsBill>) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBill.class, totalBilledKeys);
        if (CollectionUtils.isNotEmpty(bills)) {
            Iterator<ContractsAndGrantsBill> iterator = bills.iterator();
            while (iterator.hasNext()) {
                ContractsAndGrantsBill bill = iterator.next();
                if (KFSConstants.ParameterValues.STRING_YES.equals(bill.getIsItBilled())) {
                    billedToDate = billedToDate.add(bill.getEstimatedAmount());
                }
            }
        }
        return billedToDate;
    }
    
    
    /**
     * This method checks if there is atleast one AR Invoice Account present when the GLPE is 3. 
     * @param award
     * @return
     */
    public boolean hasARInvoiceAccountAssigned(ContractsAndGrantsCGBAward award){
        boolean valid = true;
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = receivableOffsetOption.equals("3");
        //This condition is validated only if GLPE is 3 and CG enhancements is ON
        if (isUsingReceivableFAU ) {
            if (ObjectUtils.isNull(award.getActiveAwardInvoiceAccounts()) || CollectionUtils.isEmpty(award.getActiveAwardInvoiceAccounts())) {
                valid = false;
            }
            else{
                int arCount = 0;
                for (ContractsGrantsAwardInvoiceAccountInformation awardInvoiceAccount : award.getActiveAwardInvoiceAccounts()) {
                    if (awardInvoiceAccount.getAccountType().equals(ArPropertyConstants.AR_ACCOUNT)) {
                            arCount++;
                        
                    }
                }
                if (arCount == 0) {
                    valid = false;
                }
            }
        }
        return valid;
    }
    
    
     public Collection<DunningLetterDistributionOnDemandLookupResult> getInvoiceDocumentsForDunningLetterOnDemandLookup(Map<String, String> fieldValues) {
         
            //  to get the search criteria
            String proposalNumber = fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);            
            String customerNumber = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NUMBER);
            String invoiceDocumentNumber = fieldValues.get("invoiceDocumentNumber");
            String awardTotal = fieldValues.get("awardTotal");
            String accountNumber = fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);
            
              

            Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments;
            Criteria criteria = new Criteria();
            if(ObjectUtils.isNotNull(proposalNumber)&& StringUtils.isNotBlank(proposalNumber.toString()) && StringUtils.isNotEmpty(proposalNumber.toString())){
                criteria.addEqualTo(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
            }
            if(ObjectUtils.isNotNull(customerNumber) && StringUtils.isNotBlank(customerNumber) && StringUtils.isNotEmpty(customerNumber)){
                criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
            }
            if(ObjectUtils.isNotNull(invoiceDocumentNumber) && StringUtils.isNotBlank(invoiceDocumentNumber) && StringUtils.isNotEmpty(invoiceDocumentNumber)){
                criteria.addEqualTo("documentNumber", invoiceDocumentNumber);
            }
            if(ObjectUtils.isNotNull(awardTotal) && StringUtils.isNotBlank(awardTotal) && StringUtils.isNotEmpty(awardTotal)){
                criteria.addEqualTo("invoiceGeneralDetail.awardTotal", awardTotal);
            }
            if(ObjectUtils.isNotNull(accountNumber) && StringUtils.isNotBlank(accountNumber) && StringUtils.isNotEmpty(accountNumber)){
            criteria.addIn("accountDetails.accountNumber", Arrays.asList(new String[] {accountNumber}));
             }
            criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, "true");
            criteria.addEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
           
            
                cgInvoiceDocuments = retrieveAllCGInvoicesByCriteria(criteria);
            
            
            // attach headers
            cgInvoiceDocuments = attachWorkflowHeadersToCGInvoices(cgInvoiceDocuments);
            
            //To validate the invoices for any additional parameters.
            
            Collection<ContractsGrantsInvoiceDocument> eligibleInvoiceDocuments = validateInvoicesForDunningLetters(fieldValues, cgInvoiceDocuments);
            
    
            return DunningLetterDistributionOnDemandLookupUtil.getPopulatedDunningLetterDistributionOnDemandLookupResults(eligibleInvoiceDocuments);
        }
     
     
     private Collection<ContractsGrantsInvoiceDocument> validateInvoicesForDunningLetters(Map<String, String> fieldValues, Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments){
         Integer agingBucketStartValue = null;
         Integer agingBucketEndValue = null;
         Integer cutoffdate0 = 0;
         Integer cutoffdate30 = 30;
         Integer cutoffdate60 = 60;
         Integer cutoffdate90 = 90;
         Integer cutoffdate120 = 120;

         //To get value for FINAL days past due.
         String stateAgencyFinalCutOffDate = null;
         String finalCutOffDate = SpringContext.getBean(ParameterService.class).getParameterValueAsString(DunningCampaign.class, ArConstants.DunningLetters.DYS_PST_DUE_FINAL_PARM);
         if(ObjectUtils.isNull(finalCutOffDate)){
             finalCutOffDate = "0";
         }
         Integer cutoffdateFinal = new Integer(finalCutOffDate);
         String agencyNumber = fieldValues.get(KFSPropertyConstants.AGENCY_NUMBER);
         String campaignID = fieldValues.get("campaignID");
         String collector = fieldValues.get("principalId");         
         String agingBucket = fieldValues.get("agingBucket");
         String collectorPrincName = fieldValues.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);
         
         boolean checkAgingBucket = ObjectUtils.isNotNull(agingBucket) && StringUtils.isNotBlank(agingBucket) && StringUtils.isNotEmpty(agingBucket);
         
         
         if(checkAgingBucket && agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_CURRENT)){
             agingBucketStartValue = 0;
             agingBucketEndValue = 30;
         }
         //Including State agency final here just to get some default value in place. The value will be overriden later after checking with the agency.
         else if(checkAgingBucket && (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_FINAL) || agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL))){
             agingBucketStartValue = cutoffdateFinal + 1;
             agingBucketEndValue = 0;
         }
         else if(checkAgingBucket && agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)){
             agingBucketStartValue = 121;
             agingBucketEndValue = cutoffdateFinal;
         }
         else if(checkAgingBucket && StringUtils.isNotBlank(agingBucket)){
             agingBucketStartValue = new Integer(agingBucket.split("-")[0]);
             agingBucketEndValue = new Integer(agingBucket.split("-")[1]);
         }
         
         //check other categories
         boolean checkAgencyNumber = ObjectUtils.isNotNull(agencyNumber) && StringUtils.isNotBlank(agencyNumber) && StringUtils.isNotEmpty(agencyNumber);
         
         boolean checkDunningCampaign = ObjectUtils.isNotNull(campaignID) && StringUtils.isNotBlank(campaignID) && StringUtils.isNotEmpty(campaignID);
         boolean checkCollector = ObjectUtils.isNotNull(collector) && StringUtils.isNotBlank(collector) && StringUtils.isNotEmpty(collector);
         boolean isCollector = true;
         
         if(ObjectUtils.isNotNull(collectorPrincName) && StringUtils.isNotEmpty(collectorPrincName.trim())) {
             checkCollector = true;
             Person collectorObj = getPersonService().getPersonByPrincipalName(collectorPrincName);
             if(collectorObj != null) {
                 collector = collectorObj.getPrincipalId();
             } else {
                 isCollector = false;
             }
         }
         
         // Here add logic to check if selected collector exists as collector head or collector information.
         if(isCollector && checkCollector) {
            isCollector = collectorHierarchyDao.isCollector(collector);
         }
         
         // walk through what we have, and do any extra filtering based on age and dunning campaign, if necessary
         boolean eligibleInvoiceFlag;
         Collection<ContractsGrantsInvoiceDocument> eligibleInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
         for (ContractsGrantsInvoiceDocument invoice : cgInvoiceDocuments) {
             eligibleInvoiceFlag = false;
             if (ObjectUtils.isNotNull(invoice.getAge())) {
                 if(invoice.getAward() == null || invoice.getAward().getDunningCampaign() == null){
                     eligibleInvoiceFlag = false;
                     continue;
                 }      
             String dunningCampaignCode = invoice.getAward().getDunningCampaign();

             DunningCampaign dunningCampaign = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(DunningCampaign.class, dunningCampaignCode);
             if(ObjectUtils.isNull(dunningCampaign) || !dunningCampaign.isActive()){
                 eligibleInvoiceFlag = false;
                 continue;
             }
             if (checkCollector) {
                 if(isCollector) {
                     if ((invoice.getCustomer().getCustomerCollector() == null || invoice.getCustomer().getCustomerCollector().getPrincipalId() == null || !invoice.getCustomer().getCustomerCollector().getPrincipalId().equalsIgnoreCase(collector))) {
                         
                         // chk if customer collector is assigned to head
                         Criteria collectorCriteria = new Criteria();
                         collectorCriteria.addEqualTo(ArPropertyConstants.COLLECTOR_HEAD, collector);
                         collectorCriteria.addEqualTo(KFSPropertyConstants.ACTIVE, true);

                         Collection<CollectorHierarchy> collectorHierarchies = collectorHierarchyDao.getCollectorHierarchyByCriteria(collectorCriteria);
                         if (ObjectUtils.isNotNull(collectorHierarchies) && !collectorHierarchies.isEmpty()) {
                             CollectorHierarchy collectorHead = new ArrayList<CollectorHierarchy>(collectorHierarchies).get(0);
                             if (ObjectUtils.isNotNull(collectorHead)) {
                                 // check principal ids of collector
                                 if (ObjectUtils.isNotNull(collectorHead.getCollectorInformations()) && CollectionUtils.isNotEmpty(collectorHead.getCollectorInformations())) {
                                     for (CollectorInformation collectorInfo : collectorHead.getCollectorInformations()) {
                                         if ((collectorInfo.isActive() && ObjectUtils.isNotNull(collectorInfo.getPrincipalId()) && ObjectUtils.isNotNull(invoice.getCustomer().getCustomerCollector()) && collectorInfo.getPrincipalId().equalsIgnoreCase(invoice.getCustomer().getCustomerCollector().getPrincipalId()))) {
                                             eligibleInvoiceFlag = true;
                                             break;
                                         } else {
                                             eligibleInvoiceFlag = false;
                                         }
                                     }
                                 }
                             }
                         }
                         if(!eligibleInvoiceFlag) {
                             continue;
                         }
                     } 
                 } else {
                     eligibleInvoiceFlag = false;
                     continue;
                 }
             }
             if (checkAgencyNumber && ((invoice.getAward().getAgencyNumber() == null || !invoice.getAward().getAgencyNumber().equals(agencyNumber)))) {
                 eligibleInvoiceFlag = false;
                 continue;
             }
             if (checkDunningCampaign && ((invoice.getAward().getDunningCampaign() == null || !invoice.getAward().getDunningCampaign().equals(campaignID)))) {
                 eligibleInvoiceFlag = false;
                 continue;
             }
             
             //To override agingBucketStartValue and agingBucketEndValue if State Agency Final is true.
             
             ContractsAndGrantsCGBAgency agency = invoice.getAward().getAgency();
             if(agency.isStateAgency()){
                 stateAgencyFinalCutOffDate = SpringContext.getBean(ParameterService.class).getParameterValueAsString(DunningCampaign.class, ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL_PARM);  
             }
             if(ObjectUtils.isNotNull(stateAgencyFinalCutOffDate) && agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)){
                 
                 agingBucketStartValue = new Integer(stateAgencyFinalCutOffDate) +1;
                 agingBucketEndValue = 0;
             }
             else if(ObjectUtils.isNotNull(stateAgencyFinalCutOffDate) && agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)){
                 
                 agingBucketStartValue = 121;
                 agingBucketEndValue = new Integer(stateAgencyFinalCutOffDate);
             }
             //Now to validate based on agingbucket and make sure the agency = stateagency is applied.
             if(ObjectUtils.isNotNull(agingBucketStartValue) && ObjectUtils.isNotNull(agingBucketStartValue)){
                 if(agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_FINAL)){
                     if(agency.isStateAgency()){
                         eligibleInvoiceFlag = false;
                         continue;
                     }
                     else{
                        if((invoice.getAge().compareTo(agingBucketStartValue) <0)){
                            eligibleInvoiceFlag = false;
                            continue;
                        }
                     }
                 }
                 else if(agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)){
                     if(!agency.isStateAgency()){
                         eligibleInvoiceFlag = false;
                         continue;
                     }
                     else{
                        if((invoice.getAge().compareTo(agingBucketStartValue) <0)){
                            eligibleInvoiceFlag = false;
                            continue;
                        }
                     }
                 }
                 else{
                     if((invoice.getAge().compareTo(agingBucketStartValue) <0) || (invoice.getAge().compareTo(agingBucketEndValue) >0)){
                         eligibleInvoiceFlag = false;
                         continue;
                     }
                 }
             }
             
             List<DunningLetterDistribution> dunningLetterDistributions = dunningCampaign.getDunningLetterDistributions();
             if(dunningLetterDistributions.isEmpty() ){
                 eligibleInvoiceFlag = false;
                 continue;
             }                
             for(DunningLetterDistribution dunningLetterDistribution: dunningLetterDistributions){
                 
                 DunningLetterTemplate dunningLetterTemplate = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(DunningLetterTemplate.class, dunningLetterDistribution.getDunningLetterTemplate());
                 
                 if(dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_CURRENT)){
                     if((invoice.getAge().compareTo(cutoffdate0) >=0) && (invoice.getAge().compareTo(cutoffdate30) <=0)){
                         if(dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())){
                             eligibleInvoiceFlag = true;
                             invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                             break;
                         }
                     }
                 }
                 else if(dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_31_60)){
                     if((invoice.getAge().compareTo(cutoffdate30) >0) && (invoice.getAge().compareTo(cutoffdate60) <=0)){
                         if(dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())){
                             eligibleInvoiceFlag = true;
                             invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                             break;
                         }
                     }
                 }
                 else if(dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_61_90)){
                     if((invoice.getAge().compareTo(cutoffdate60) >0) && (invoice.getAge().compareTo(cutoffdate90) <=0)){
                         if(dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())){
                             eligibleInvoiceFlag = true;
                             invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                             break;
                         }
                     }
                 }
                 else if(dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_91_120)){
                     if((invoice.getAge().compareTo(cutoffdate90) >0) && (invoice.getAge().compareTo(cutoffdate120) <=0)){
                         if(dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())){
                             eligibleInvoiceFlag = true;
                             invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                             break;
                         }
                     }
                 }
                 else if(dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)){
                     if(agency.isStateAgency()){//To replace final with state agency final value
                         cutoffdateFinal = new Integer(stateAgencyFinalCutOffDate);
                     }                     
                     if((invoice.getAge().compareTo(cutoffdate120) >0) && (invoice.getAge().compareTo(cutoffdateFinal) <=0)){
                         if(dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())){
                             eligibleInvoiceFlag = true;
                             invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                             break;
                         }
                     
                     }
                 }
                 else if(dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_FINAL)){
                     if(agency.isStateAgency()){//to proceed only if agency is not state agency
                         continue;
                     } 
                     else{
                     if((invoice.getAge().compareTo(cutoffdateFinal) >0)){
                         if(dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())){
                             eligibleInvoiceFlag = true;
                             invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                             break;
                         }
                     }
                     }
                 }
                 else if(dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)){
                     if(agency.isStateAgency()){//to replace final with state agency final value
                         cutoffdateFinal = new Integer(stateAgencyFinalCutOffDate);
                     } 
                     else{//If the agency is not state agency - nothing to calculate.
                         continue;
                     }
                     if((invoice.getAge().compareTo(cutoffdateFinal) >0)){
                         if(dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilepath())){
                             eligibleInvoiceFlag = true;
                             invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                             break;
                         }
                     }
                 }
             }
             } else {
                 eligibleInvoiceFlag = false;
             }
         
             if (eligibleInvoiceFlag) {
                 SpringContext.getBean(BusinessObjectService.class).save(invoice.getInvoiceGeneralDetail());
                 eligibleInvoices.add(invoice);
             }
         }
         return eligibleInvoices;
     }
     
    /**
     * 
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
             docs = documentService.getDocumentsByListOfDocumentHeaderIds(ContractsGrantsInvoiceDocument.class, documentHeaderIds);
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
    public Collection<ContractsGrantsInvoiceDocument> retrieveOpenAndFinalCGInvoicesByProposalNumber(Long proposalNumber, String errorFileName) {
    
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        criteria.addIsNull(ArPropertyConstants.DOCUMENT_HEADER_FINANCIAL_DOCUMENT_IN_ERROR_NUMBER);
        
        Collection<String> invoiceNumbers = contractsGrantsInvoiceDocumentDao.getFinancialDocumentInErrorNumbers();
        criteria.addNotIn(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, invoiceNumbers);
        
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        cgInvoices = contractsGrantsInvoiceDocumentDao.getInvoicesByCriteria(criteria);
        String detail = "Proposal Number#" + proposalNumber;
        return cgInvoices;
    }
    
    public KualiDecimal retrievePaymentAmountByDocumentNumber(String documentNumber) {
        KualiDecimal paymentAmount = KualiDecimal.ZERO;
        Collection<InvoicePaidApplied> invoicePaidApplieds = invoicePaidAppliedService.getInvoicePaidAppliedsForInvoice(documentNumber);
        if (invoicePaidApplieds != null && !invoicePaidApplieds.isEmpty()) {
            for (InvoicePaidApplied invPaidApp : invoicePaidApplieds) {
                paymentAmount = paymentAmount.add(invPaidApp.getInvoiceItemAppliedAmount());
            }
        }
        return paymentAmount;
    }

    public java.sql.Date retrievePaymentDateByDocumentNumber(String documentNumber) {
        List<InvoicePaidApplied> invoicePaidApplieds = (List<InvoicePaidApplied>) invoicePaidAppliedService.getInvoicePaidAppliedsForInvoice(documentNumber);
        java.sql.Date paymentDate = null;
        if (invoicePaidApplieds != null && !invoicePaidApplieds.isEmpty()) {
            InvoicePaidApplied invPaidApp = invoicePaidApplieds.get(invoicePaidApplieds.size() - 1);
            PaymentApplicationDocument referenceFinancialDocument;
            try {
                referenceFinancialDocument = (PaymentApplicationDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(invPaidApp.getDocumentNumber());
                paymentDate = referenceFinancialDocument.getDocumentHeader().getDocumentFinalDate();
            }
            catch (WorkflowException ex) {
                LOG.error("Could not retrieve payment application document while calculating payment date: " + ex.getMessage());
            }
        }
        return paymentDate;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#getInvoiceDocumentsForReferralToCollectionsLookup(java.util.Map)
     */
    public Collection<ReferralToCollectionsLookupResult> getInvoiceDocumentsForReferralToCollectionsLookup(Map<String, String> fieldValues) {
        
        String agencyNumber = fieldValues.get(KFSPropertyConstants.AGENCY_NUMBER);
        String accountNumber = fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        String proposalNumber = fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String invoiceDocumentNumber = fieldValues.get(ArPropertyConstants.ReferralToCollectionsFields.INVOICE_DOCUMENT_NUMBER);
        String awardDocumentNumber = fieldValues.get(ArPropertyConstants.ReferralToCollectionsFields.AWARD_DOCUMENT_NUMBER);
        String customerNumber = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NUMBER);
        String customerName = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NAME);
        
        Collection<ContractsGrantsInvoiceDocument> invoices;
        Criteria criteria = new Criteria();
//        if(ObjectUtils.isNotNull(agencyNumber)&& StringUtils.isNotBlank(agencyNumber.toString()) && StringUtils.isNotEmpty(agencyNumber.toString())){
//            criteria.addIn(ArPropertyConstants.ReferralToCollectionsFields.AGENCY_ADDRESS_DETAILS_AGENCY_NUMBER, Arrays.asList(new String[] {agencyNumber}));
//        }
        if(ObjectUtils.isNotNull(proposalNumber)&& StringUtils.isNotBlank(proposalNumber.toString()) && StringUtils.isNotEmpty(proposalNumber.toString())){
            criteria.addEqualTo(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }
        if(ObjectUtils.isNotNull(accountNumber)&& StringUtils.isNotBlank(accountNumber.toString()) && StringUtils.isNotEmpty(accountNumber.toString())){
            criteria.addIn(ArPropertyConstants.ReferralToCollectionsFields.ACCOUNT_DETAILS_ACCOUNT_NUMBER, Arrays.asList(new String[] {accountNumber}));
        }
        if(ObjectUtils.isNotNull(invoiceDocumentNumber) && StringUtils.isNotBlank(invoiceDocumentNumber) && StringUtils.isNotEmpty(invoiceDocumentNumber)){
            criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, invoiceDocumentNumber);
        }
        if(ObjectUtils.isNotNull(customerNumber) && StringUtils.isNotBlank(customerNumber) && StringUtils.isNotEmpty(customerNumber)){
            criteria.addEqualTo(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        }
        if(ObjectUtils.isNotNull(customerName) && StringUtils.isNotBlank(customerName) && StringUtils.isNotEmpty(customerName)){
            criteria.addEqualTo(ArPropertyConstants.ReferralToCollectionsFields.ACCOUNTS_RECEIVABLE_CUSTOMER_NAME, customerName);
        }

        criteria.addEqualTo(ArPropertyConstants.OPEN_INVOICE_IND, "true");
        criteria.addEqualTo(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);

        Criteria referralNull = new Criteria();
        Criteria referralOutside = new Criteria();
        Criteria subCriteria = new Criteria();
        
        Map<String, String> refFieldValues = new HashMap<String, String>();
        refFieldValues.put(ArPropertyConstants.ReferralTypeFields.OUTSIDE_COLLECTION_AGENCY, "true");
        refFieldValues.put(ArPropertyConstants.ReferralTypeFields.ACTIVE, "true");
        List<ReferralType> refTypes = (List<ReferralType>) SpringContext.getBean(BusinessObjectService.class).findMatching(ReferralType.class, refFieldValues);
        String outsideColAgencyCode = CollectionUtils.isNotEmpty(refTypes) ? refTypes.get(0).getReferralTypeCode() : null;
        
        referralNull.addIsNull(ArPropertyConstants.ReferralToCollectionsFields.REFERRAL_TYPE);
        referralOutside.addNotEqualTo(ArPropertyConstants.ReferralToCollectionsFields.REFERRAL_TYPE, outsideColAgencyCode);
        
        subCriteria.addOrCriteria(referralNull);
        subCriteria.addOrCriteria(referralOutside);
        
        criteria.addAndCriteria(subCriteria);
        
        invoices = this.retrieveAllCGInvoicesByCriteria(criteria);
        
        if((ObjectUtils.isNotNull(awardDocumentNumber) && StringUtils.isNotBlank(awardDocumentNumber) && StringUtils.isNotEmpty(awardDocumentNumber)) ||
                ObjectUtils.isNotNull(agencyNumber)&& StringUtils.isNotBlank(agencyNumber.toString()) && StringUtils.isNotEmpty(agencyNumber.toString())) {
            this.filterInvoicesByAwardDocumentNumber(invoices, agencyNumber, awardDocumentNumber);
        }
        
        return ReferralToCollectionsDocumentUtil.getPopulatedReferralToCollectionsLookupResults(invoices);
    }

    /**
     * removes the invoices from list which does not match the given award document number.
     * 
     * @param invoices list of invoices.
     * @param awardDocumentNumber award document number to filter invoices.
     */
    private void filterInvoicesByAwardDocumentNumber(Collection<ContractsGrantsInvoiceDocument> invoices, String agencyNumber, String awardDocumentNumber) {
        boolean checkAwardNumber = ObjectUtils.isNotNull(awardDocumentNumber) && StringUtils.isNotBlank(awardDocumentNumber) && StringUtils.isNotEmpty(awardDocumentNumber);
        boolean checkAgencyNumber = ObjectUtils.isNotNull(agencyNumber)&& StringUtils.isNotBlank(agencyNumber.toString()) && StringUtils.isNotEmpty(agencyNumber.toString());
        if (invoices != null && !invoices.isEmpty()) {
            Iterator<ContractsGrantsInvoiceDocument> itr = invoices.iterator();
            while (itr.hasNext()) {
                ContractsGrantsInvoiceDocument invoice = itr.next();
                if (invoice.getAward() == null
                        || (checkAwardNumber && (invoice.getAward().getAwardDocumentNumber() == null || !invoice.getAward().getAwardDocumentNumber().equals(awardDocumentNumber)))
                        || (checkAgencyNumber && (invoice.getAward().getAgencyNumber() == null || !invoice.getAward().getAgencyNumber().equals(agencyNumber)))) {
                    itr.remove();
                }
            }
        }
    }
}
