/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingFrequency;
import org.kuali.kfs.integration.cg.ContractsGrantsAwardInvoiceAccountInformation;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsBillingAwardVerificationService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;

public class ContractsGrantsBillingAwardVerificationServiceImpl implements ContractsGrantsBillingAwardVerificationService {
    protected AccountingPeriodService accountingPeriodService;
    protected BusinessObjectService businessObjectService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected CustomerService customerService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected KualiModuleService kualiModuleService;
    protected ParameterService parameterService;
    protected VerifyBillingFrequencyService verifyBillingFrequencyService;
    protected UniversityDateService universityDateService;

    /**
     * Check if Billing Frequency is set correctly.
     *
     * @param award
     * @return False if billing frequency code is blank, or set as predetermined billing schedule or milestone billing schedule
     *         and award has no award account or more than 1 award accounts assigned.
     */
    @Override
    public boolean isBillingFrequencySetCorrectly(ContractsAndGrantsBillingAward award) {

        if (StringUtils.isBlank(award.getBillingFrequencyCode()) || ((award.getBillingFrequencyCode().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) || award.getBillingFrequencyCode().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE)) && award.getActiveAwardAccounts().size() != 1)) {
            return false;
        }
        return true;
    }


    /**
     * Check if the value of the billing frequency code is in the BillingFrequency value set.
     *
     * @param award
     * @return
     */
    @Override
    public boolean isValueOfBillingFrequencyValid(ContractsAndGrantsBillingAward award) {
        if (!StringUtils.isBlank(award.getBillingFrequencyCode())) {
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(KFSPropertyConstants.FREQUENCY, award.getBillingFrequencyCode());
            criteria.put(KFSPropertyConstants.ACTIVE, true);
            Collection<ContractsAndGrantsBillingFrequency> matchingBillingFrequencies = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingFrequency.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingFrequency.class, criteria);

            if (matchingBillingFrequencies != null && matchingBillingFrequencies.size() > 0) {
                return true;
            }
        }

        return false;
    }



    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardFinalInvoiceAlreadyBuilt(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean isAwardFinalInvoiceAlreadyBuilt(ContractsAndGrantsBillingAward award) {
        for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
            if (awardAccount.isFinalBilledIndicator()) {
                return true;
            }
        }

        return false;
    }

    /**
     * this method checks If all accounts of award has invoices in progress.
     * @param award
     * @return
     */
    @Override
    public boolean isInvoiceInProgress(ContractsAndGrantsBillingAward award) {
        Map<String, Object> fieldValues = new HashMap<>();
        fieldValues.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, getFinancialSystemDocumentService().getPendingDocumentStatuses());

        return getBusinessObjectService().countMatching(ContractsGrantsInvoiceDocument.class, fieldValues) > 0;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoMilestonesToInvoice(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean hasMilestonesToInvoice(ContractsAndGrantsBillingAward award) {
        boolean hasMilestonesToInvoice = true;
        if (award.getBillingFrequencyCode().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
            List<Milestone> milestones = new ArrayList<Milestone>();
            List<Milestone> validMilestones = new ArrayList<Milestone>();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
            map.put(KFSPropertyConstants.ACTIVE, true);
            milestones = (List<Milestone>) businessObjectService.findMatching(Milestone.class, map);

            // To retrieve the previous period end Date to check for milestones and billing schedule.

            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            java.sql.Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            java.sql.Date invoiceDate = pair[1];


            for (Milestone awdMilestone : milestones) {
                if (awdMilestone.getMilestoneActualCompletionDate() != null && !invoiceDate.before(awdMilestone.getMilestoneActualCompletionDate()) && !awdMilestone.isBilled() && awdMilestone.getMilestoneAmount().isGreaterThan(KualiDecimal.ZERO)) {
                    validMilestones.add(awdMilestone);
                }
            }
            if (CollectionUtils.isEmpty(validMilestones)) {
                hasMilestonesToInvoice = false;
            }
        }
        return hasMilestonesToInvoice;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoBillsToInvoice(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean hasBillsToInvoice(ContractsAndGrantsBillingAward award) {
        boolean hasBillsToInvoice = true;
        if (award.getBillingFrequencyCode().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {

            List<Bill> bills = new ArrayList<Bill>();
            List<Bill> validBills = new ArrayList<Bill>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
            map.put(KFSPropertyConstants.ACTIVE, true);

            bills = (List<Bill>) businessObjectService.findMatching(Bill.class, map);
            // To retrieve the previous period end Date to check for milestones and billing schedule.

            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            java.sql.Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            java.sql.Date invoiceDate = pair[1];

            for (Bill awdBill : bills) {
                if (awdBill.getBillDate() != null && !invoiceDate.before(awdBill.getBillDate()) && !awdBill.isBilled() && awdBill.getEstimatedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                    validBills.add(awdBill);
                }
            }
            if (CollectionUtils.isEmpty(validBills)) {
                hasBillsToInvoice = false;
            }
        }
        return hasBillsToInvoice;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#owningAgencyHasNoCustomerRecord(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean owningAgencyHasCustomerRecord(ContractsAndGrantsBillingAward award) {
        boolean isValid = true;
        if (ObjectUtils.isNotNull(award.getAgency().getCustomerNumber())) {
            Customer customer = customerService.getByPrimaryKey(award.getAgency().getCustomerNumber());
            return !ObjectUtils.isNull(customer);
        }

        return false;
    }

    /**
     * This method checks if the System Information and ORganization Accounting Default are setup for the Chart Code and Org Code
     * from the award accounts.
     *
     * @param award
     * @return
     */
    @Override
    public boolean isChartAndOrgSetupForInvoicing(ContractsAndGrantsBillingAward award) {
        String coaCode = award.getPrimaryAwardOrganization().getChartOfAccountsCode();
        String orgCode = award.getPrimaryAwardOrganization().getOrganizationCode();
        String procCoaCode = null, procOrgCode = null;
        Integer currentYear = universityDateService.getCurrentFiscalYear();

        Map<String, Object> criteria = new HashMap<String, Object>();
        Map<String, Object> sysCriteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        sysCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);


        // To retrieve processing codes based on billing codes using organization options
        List<String> procCodes = getContractsGrantsInvoiceDocumentService().getProcessingFromBillingCodes(coaCode, orgCode);
        if (!CollectionUtils.isEmpty(procCodes) && procCodes.size() > 1) {

            sysCriteria.put(ArPropertyConstants.OrganizationAccountingDefaultFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, procCodes.get(0));
            sysCriteria.put(ArPropertyConstants.OrganizationAccountingDefaultFields.PROCESSING_ORGANIZATION_CODE, procCodes.get(1));
            OrganizationAccountingDefault organizationAccountingDefault = businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);

            SystemInformation systemInformation = businessObjectService.findByPrimaryKey(SystemInformation.class, sysCriteria);
            if (ObjectUtils.isNotNull(organizationAccountingDefault) && ObjectUtils.isNotNull(systemInformation)) {
                return true;
            }
        }
        return false;

    }

    /**
     * This method checks if the Offset Definition is setup for the Chart Code from the award accounts.
     *
     * @param award
     * @return
     */
    @Override
    public boolean isOffsetDefinitionSetupForInvoicing(ContractsAndGrantsBillingAward award) {
        String coaCode = null, orgCode = null;
        Integer currentYear = universityDateService.getCurrentFiscalYear();
        String receivableOffsetOption = parameterService.getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        final SystemOptions systemOption = getBusinessObjectService().findBySinglePrimaryKey(SystemOptions.class, currentYear);
        boolean isUsingReceivableFAU = receivableOffsetOption.equals(ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU);
        // This condition is validated only if GLPE is 3 and CG enhancements is ON
        if (isUsingReceivableFAU) {
            Map<String, Object> criteria = new HashMap<>();
            Map<String, Object> sysCriteria = new HashMap<>();
            criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
            criteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, systemOption.getActualFinancialBalanceTypeCd());
            criteria.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);
            // 1. To get the chart code and org code for invoicing depending on the invoicing options.
            if (ObjectUtils.isNotNull(award.getInvoicingOptions())) {
                if (award.getInvoicingOptions().equalsIgnoreCase(ArConstants.INV_ACCOUNT)) {
                    for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                        coaCode = awardAccount.getAccount().getChartOfAccountsCode();
                        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                        OffsetDefinition offset = businessObjectService.findByPrimaryKey(OffsetDefinition.class, criteria);
                        if (ObjectUtils.isNull(offset)) {
                            return false;
                        }
                    }
                }
                if (award.getInvoicingOptions().equalsIgnoreCase(ArConstants.INV_CONTRACT_CONTROL_ACCOUNT)) {
                    List<Account> controlAccounts = getContractsGrantsInvoiceDocumentService().getContractControlAccounts(award);

                    for (Account controlAccount : controlAccounts) {
                        coaCode = controlAccount.getChartOfAccountsCode();
                        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                        OffsetDefinition offset = businessObjectService.findByPrimaryKey(OffsetDefinition.class, criteria);
                        if (ObjectUtils.isNull(offset)) {
                            return false;
                        }
                    }
                }
                if (award.getInvoicingOptions().equalsIgnoreCase(ArConstants.INV_AWARD)) {
                    List<Account> controlAccounts = getContractsGrantsInvoiceDocumentService().getContractControlAccounts(award);

                    for (Account controlAccount : controlAccounts) {
                        coaCode = controlAccount.getChartOfAccountsCode();
                        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                        OffsetDefinition offset = businessObjectService.findByPrimaryKey(OffsetDefinition.class, criteria);
                        if (ObjectUtils.isNull(offset)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * This method checks if there is atleast one AR Invoice Account present when the GLPE is 3.
     *
     * @param award
     * @return
     */
    @Override
    public boolean hasARInvoiceAccountAssigned(ContractsAndGrantsBillingAward award) {
        boolean isValid = true;
        String receivableOffsetOption = parameterService.getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = StringUtils.equals(receivableOffsetOption, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU);
        if (isUsingReceivableFAU) {
            if (ObjectUtils.isNull(award.getActiveAwardInvoiceAccounts()) || CollectionUtils.isEmpty(award.getActiveAwardInvoiceAccounts())) {
                return false;
            }
            else {
                for (ContractsGrantsAwardInvoiceAccountInformation awardInvoiceAccount : award.getActiveAwardInvoiceAccounts()) {
                    if (awardInvoiceAccount.getAccountType().equals(ArConstants.AR_ACCOUNT)) {
                        return true;
                    }
                }
                return false; // we made it through the loop without finding an award invoice account with type AR account...so this test fails
            }
        }
        return true;
    }

    public AccountingPeriodService getAccountingPeriodService() {
        return accountingPeriodService;
    }

    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public VerifyBillingFrequencyService getVerifyBillingFrequencyService() {
        return verifyBillingFrequencyService;
    }

    public void setVerifyBillingFrequencyService(VerifyBillingFrequencyService verifyBillingFrequencyService) {
        this.verifyBillingFrequencyService = verifyBillingFrequencyService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
}
