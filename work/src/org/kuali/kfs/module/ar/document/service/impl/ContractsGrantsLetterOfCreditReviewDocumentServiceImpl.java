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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsLetterOfCreditReviewDocumentService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ContractsGrantsLetterOfCreditReviewDocumentServiceImpl implements ContractsGrantsLetterOfCreditReviewDocumentService {
    protected AwardAccountObjectCodeTotalBilledDao awardAccountObjectCodeTotalBilledDao;
    protected ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected KualiModuleService kualiModuleService;
    protected OptionsService optionsService;

    /**
     * Calculates the amount to draw for each award account and puts it in a map, keyed by chart-account
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsLetterOfCreditReviewDocumentService#calculateAwardAccountAmountsToDraw(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward, java.util.List)
     */
    @Override
    public Map<String, KualiDecimal> calculateAwardAccountAmountsToDraw(ContractsAndGrantsBillingAward award, List<ContractsAndGrantsBillingAwardAccount> awardAccounts) {
        Map<String, KualiDecimal> amounts = new HashMap<>();

        List<AwardAccountObjectCodeTotalBilled> awardAccountTotalBilledAmounts = getAwardAccountObjectCodeTotalBilledDao().getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);
        final SystemOptions systemOptions = optionsService.getCurrentYearOptions();

        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
            // 2. Get the Cumulative amount from GL Balances.
            final KualiDecimal cumAmt = getContractsGrantsInvoiceDocumentService().getBudgetAndActualsForAwardAccount(awardAccount, systemOptions.getActualFinancialBalanceTypeCd(), award.getAwardBeginningDate());
            KualiDecimal billedAmount = KualiDecimal.ZERO;

            // 3. Amount to Draw = Cumulative amount - Billed to Date.(This would be ultimately the current expenditures in the invoice document.
            for (AwardAccountObjectCodeTotalBilled awardAccountTotalBilledAmount : awardAccountTotalBilledAmounts) {
                if (StringUtils.equals(awardAccountTotalBilledAmount.getAccountNumber(), awardAccount.getAccountNumber()) && StringUtils.equals(awardAccountTotalBilledAmount.getChartOfAccountsCode(), awardAccount.getChartOfAccountsCode()) && awardAccountTotalBilledAmount.getProposalNumber().equals(awardAccount.getProposalNumber())) {
                    billedAmount = billedAmount.add(awardAccountTotalBilledAmount.getTotalBilled());
                }
            }
            final KualiDecimal amountToDraw = cumAmt.subtract(billedAmount);
            amounts.put(getAwardAccountKey(awardAccount), amountToDraw);
        }

        return amounts;
    }

    /**
     * Generates the key as chart of accounts code - account number
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsLetterOfCreditReviewDocumentService#getAwardAccountKey(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount)
     */
    @Override
    public String getAwardAccountKey(ContractsAndGrantsBillingAwardAccount awardAccount) {
        StringBuilder s = new StringBuilder();
        s.append(awardAccount.getChartOfAccountsCode());
        s.append('-');
        s.append(awardAccount.getAccountNumber());
        return s.toString();
    }

    /**
     * This method retrieves the amount available to draw for the award accounts
     *
     * @param awardTotalAmount
     * @param awardAccount
     */
    @Override
    public KualiDecimal getAmountAvailableToDraw(KualiDecimal awardTotalAmount, List<ContractsAndGrantsBillingAwardAccount> awardAccounts) {

        // Get the billed to date amount for every award account based on the criteria passed.
        List<AwardAccountObjectCodeTotalBilled> awardAccountTotalBilledAmounts = getAwardAccountObjectCodeTotalBilledDao().getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);
        KualiDecimal billedAmount = KualiDecimal.ZERO;
        KualiDecimal amountAvailableToDraw = KualiDecimal.ZERO;
        for (AwardAccountObjectCodeTotalBilled awardAccountTotalBilledAmount : awardAccountTotalBilledAmounts) {
            billedAmount = billedAmount.add(awardAccountTotalBilledAmount.getTotalBilled());
        }
        amountAvailableToDraw = awardTotalAmount.subtract(billedAmount);

        return amountAvailableToDraw;
    }


    @Override
    public void generateContractsGrantsInvoiceDocuments(ContractsGrantsLetterOfCreditReviewDocument locReviewDoc) {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();
        // 1. compare the hiddenamountodraw and amount to draw field.
        Set<Long> proposalNumberSet = new HashSet<>();
        for (ContractsGrantsLetterOfCreditReviewDetail detail : locReviewDoc.getHeaderReviewDetails()) {
            if (!proposalNumberSet.contains(detail.getProposalNumber())) {
                awards.add(retrieveAward(detail.getProposalNumber()));
                proposalNumberSet.add(detail.getProposalNumber());
            }
        }

        // To set the loc Creation Type to award based on the LOC Review document being retrieved.
        final String locCreationType = !ObjectUtils.isNull(locReviewDoc.getLetterOfCreditFundCode()) ? ArConstants.LOC_BY_LOC_FUND : ArConstants.LOC_BY_LOC_FUND_GRP;
        getContractsGrantsInvoiceCreateDocumentService().createCGInvoiceDocumentsByAwards(awards, locReviewDoc.getAccountReviewDetails(), locCreationType);

        //To route the invoices automatically as the initator would be system user after a wait time.
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        getContractsGrantsInvoiceCreateDocumentService().routeContractsGrantsInvoiceDocuments();

        // The next important step is to set the locReviewIndicator to false and amount to Draw fields in award Account to zero.
        // This should not affect any further invoicing.
        for (ContractsAndGrantsBillingAward award : awards) {
            for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                Map<String, Object> criteria = new HashMap<String, Object>();
                criteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
                criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
                criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount.getProposalNumber());
                SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).setAmountToDrawToAwardAccount(criteria, KualiDecimal.ZERO);
                SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).setLOCReviewIndicatorToAwardAccount(criteria, false);
            }
            SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).setLOCCreationTypeToAward(award.getProposalNumber(), null);
        }
    }

    /**
     * Retrieves an award based on proposal number
     * @param proposalNumber the proposal number to look up the award for
     * @return the award
     */
    protected ContractsAndGrantsBillingAward retrieveAward(Long proposalNumber) {
        Map<String, Object> map = new HashMap<>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        ContractsAndGrantsBillingAward awd = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);
        return awd;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsLetterOfCreditReviewDocumentService#getActiveAwardsByCriteria(java.util.Map)
     */
    @Override
    public List<ContractsAndGrantsBillingAward> getActiveAwardsByCriteria(Map<String, Object> criteria) {
        return getKualiModuleService().getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingAward.class, criteria);
    }

    public AwardAccountObjectCodeTotalBilledDao getAwardAccountObjectCodeTotalBilledDao() {
        return awardAccountObjectCodeTotalBilledDao;
    }

    public void setAwardAccountObjectCodeTotalBilledDao(AwardAccountObjectCodeTotalBilledDao awardAccountObjectCodeTotalBilledDao) {
        this.awardAccountObjectCodeTotalBilledDao = awardAccountObjectCodeTotalBilledDao;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }

    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public OptionsService getOptionsService() {
        return optionsService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public ContractsGrantsInvoiceCreateDocumentService getContractsGrantsInvoiceCreateDocumentService() {
        return contractsGrantsInvoiceCreateDocumentService;
    }

    public void setContractsGrantsInvoiceCreateDocumentService(ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService) {
        this.contractsGrantsInvoiceCreateDocumentService = contractsGrantsInvoiceCreateDocumentService;
    }

}
