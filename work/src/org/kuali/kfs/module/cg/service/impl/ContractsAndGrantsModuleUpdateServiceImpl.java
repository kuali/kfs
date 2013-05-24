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
package org.kuali.kfs.module.cg.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.Bill;
import org.kuali.kfs.module.cg.businessobject.Milestone;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.dataaccess.BillDao;
import org.kuali.kfs.module.cg.dataaccess.MilestoneDao;
import org.kuali.kfs.module.cg.service.AwardService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This Class provides implementation to the services required for inter module communication.
 */
@NonTransactional
public class ContractsAndGrantsModuleUpdateServiceImpl implements ContractsAndGrantsModuleUpdateService {
    private AwardService awardService;
    private BillDao billDao;
    private BusinessObjectService businessObjectService;
    private MilestoneDao milestoneDao;

    /**
     * This method sets last Billed Date to award Account.
     *
     * @param criteria
     * @param invoiceStatus
     * @param lastBilledDate
     */
    @Override
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, String invoiceStatus, Date lastBilledDate) {
        AwardAccount awardAccount = getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        // If the invoice is final, transpose current last billed date to previous and set invoice last billed date to current.

        if (invoiceStatus.equalsIgnoreCase("FINAL")) {
            awardAccount.setPreviousLastBilledDate(awardAccount.getCurrentLastBilledDate());
            awardAccount.setCurrentLastBilledDate(lastBilledDate);
        }

        // If the invoice is corrected, transpose previous billed date to current and set previous last billed date to null.
        else if (invoiceStatus.equalsIgnoreCase("CORRECTED")) {
            awardAccount.setCurrentLastBilledDate(awardAccount.getPreviousLastBilledDate());
            awardAccount.setPreviousLastBilledDate(null);
        }

        getBusinessObjectService().save(awardAccount);

    }

    /**
     * This method sets last billed Date to Award
     *
     * @param proposalNumber
     * @param lastBilledDate
     */
    @Override
    public void setLastBilledDateToAward(Long proposalNumber, Date lastBilledDate) {
        Award award = getBusinessObjectService().findBySinglePrimaryKey(Award.class, proposalNumber);

        award.setLastBilledDate(lastBilledDate);
        getBusinessObjectService().save(award);

    }

    /**
     * This method updates value of isItBilled in Bill BO to Yes
     *
     * @param criteria
     */
    @Override
    public void setBillsisItBilled(Criteria criteria, String value) {
        Collection<Bill> bills = getBillDao().getBillsByMatchingCriteria(criteria);
        for (Bill bill : bills) {
            bill.setIsItBilled(value);
            getBusinessObjectService().save(bill);
        }
    }

    /**
     * This method updates value of isItBilled in Milestone BO to Yes
     *
     * @param criteria
     */
    @Override
    @SuppressWarnings("null")
    public void setMilestonesisItBilled(Long proposalNumber, List<Long> milestoneIds, String value) {
        Collection<Milestone> milestones = null;
        try {
            milestones = getMilestoneDao().getMatchingMilestoneByProposalIdAndInListOfMilestoneId(proposalNumber, milestoneIds);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        for (Milestone milestone : milestones) {
            milestone.setIsItBilled(value);
            getBusinessObjectService().save(milestone);
        }
    }


    /**
     * This method sets value of LOC Creation Type to Award
     *
     * @param proposalNumber
     * @param locCreationType
     */
    @Override
    public void setLOCCreationTypeToAward(Long proposalNumber, String locCreationType) {
        Award award = getBusinessObjectService().findBySinglePrimaryKey(Award.class, proposalNumber);

        award.setLocCreationType(locCreationType);
        getBusinessObjectService().save(award);
    }

    /**
     * This method sets amount to draw to award Account.
     *
     * @param criteria
     * @param amountToDraw
     */
    @Override
    public void setAmountToDrawToAwardAccount(Map<String, Object> criteria, KualiDecimal amountToDraw) {
        AwardAccount awardAccount = getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        awardAccount.setAmountToDraw(amountToDraw);
        getBusinessObjectService().save(awardAccount);
    }

    /**
     * This method sets loc review indicator to award Account.
     *
     * @param criteria
     * @param locReviewIndicator
     */
    @Override
    public void setLOCReviewIndicatorToAwardAccount(Map<String, Object> criteria, boolean locReviewIndicator) {
        AwardAccount awardAccount = getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        awardAccount.setLocReviewIndicator(locReviewIndicator);
        getBusinessObjectService().save(awardAccount);
    }

    /**
     * This method sets final billed to award Account.
     *
     * @param criteria
     * @param finalBilled
     */
    @Override
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled) {
        AwardAccount awardAccount = getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        awardAccount.setFinalBilled(finalBilled);
        getBusinessObjectService().save(awardAccount);
    }

    /**
     * This method sets invoice Document Status to award Account.
     *
     * @param criteria
     * @param invoiceDocumentStatus
     */
    @Override
    public void setAwardAccountInvoiceDocumentStatus(Map<String, Object> criteria, String invoiceDocumentStatus) {
        AwardAccount awardAccount = getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        if(ObjectUtils.isNotNull(awardAccount)){
        awardAccount.setInvoiceDocumentStatus(invoiceDocumentStatus);
        getBusinessObjectService().save(awardAccount);
        }
    }

    /**
     * This method sets values to Award respective to junit testing
     *
     * @param proposalNumber
     * @param fieldValues
     */
    @Override
    @SuppressWarnings("deprecation")
    public void setAwardAccountsToAward(Long proposalNumber, List<ContractsAndGrantsCGBAwardAccount> awardAccounts) {

        // Award and proposal is being saved
        Proposal proposal = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(Proposal.class, proposalNumber);
        if (ObjectUtils.isNull(proposal)) {
            proposal = new Proposal();
            proposal.setProposalNumber(proposalNumber);
        }
        getBusinessObjectService().save(proposal);

        Award award = getBusinessObjectService().findBySinglePrimaryKey(Award.class, proposalNumber);
        if (ObjectUtils.isNull(award)) {
            award = new Award();
            award.setProposalNumber(proposalNumber);

        }
        getBusinessObjectService().save(award);

        List<AwardAccount> awdAccts = new ArrayList<AwardAccount>();


        for (ContractsAndGrantsCGBAwardAccount awardAccount : awardAccounts) {
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
            mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
            mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount.getProposalNumber());
            AwardAccount awdAcct = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AwardAccount.class, mapKey);
            if (ObjectUtils.isNull(awdAcct)) {
                awdAcct = new AwardAccount();
                awdAcct.setAccountNumber(awardAccount.getAccountNumber());
                awdAcct.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
                awdAcct.setProposalNumber(awardAccount.getProposalNumber());

            }


            getBusinessObjectService().save(awdAcct);

            awdAccts.add(awdAcct);
        }
        award.setAwardAccounts(awdAccts);


    }


    /**
     * Gets the awardService attribute.
     *
     * @return Returns the awardService.
     */
    public AwardService getAwardService() {
        return awardService;
    }

    /**
     * Sets the awardService attribute value.
     *
     * @param awardService The awardService to set.
     */
    public void setAwardService(AwardService awardService) {
        this.awardService = awardService;
    }

    /**
     * Gets the billDao attribute.
     *
     * @return Returns the billDao.
     */
    public BillDao getBillDao() {
        return billDao;
    }

    /**
     * Sets the billDao attribute value.
     *
     * @param billDao The billDao to set.
     */
    public void setBillDao(BillDao billDao) {
        this.billDao = billDao;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the milestoneDao attribute.
     *
     * @return Returns the milestoneDao.
     */
    public MilestoneDao getMilestoneDao() {
        return milestoneDao;
    }

    /**
     * Sets the milestoneDao attribute value.
     *
     * @param milestoneDao The milestoneDao to set.
     */
    public void setMilestoneDao(MilestoneDao milestoneDao) {
        this.milestoneDao = milestoneDao;
    }


}
