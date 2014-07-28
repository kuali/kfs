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
package org.kuali.kfs.module.cg.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.lookup.AwardLookupableHelperServiceImpl;
import org.kuali.kfs.module.cg.service.AgencyService;
import org.kuali.kfs.module.cg.service.AwardService;
import org.kuali.kfs.module.cg.service.CfdaService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This Class provides implementation to the services required for inter-module communication, allowing AR to utilize
 * the CG module to do Contracts & Grants Billing operations
 */
public class ContractsAndGrantsModuleBillingServiceImpl implements ContractsAndGrantsModuleBillingService {
    protected AwardService awardService;
    protected ParameterService parameterService;
    protected AgencyService agencyService;
    protected CfdaService cfdaService;
    protected BusinessObjectService businessObjectService;

    /**
     * This method would return list of business object - in this case Awards for CG Invoice functionality in AR.
     * @param fieldValues
     * @param unbounded
     * @return
     */
    @Override
    public List<? extends ContractsAndGrantsAward> lookupAwards(Map<String, String> fieldValues, boolean unbounded) {
     // call awardLookupableHelperService to find the awards according to the search criteria
        AwardLookupableHelperServiceImpl service = new AwardLookupableHelperServiceImpl();
        service.setBusinessObjectClass(Award.class);
        // Alter the map, convert the key's as per Award's lookup screen, might need to add more in the future

        String value = fieldValues.remove("accountNumber");
        fieldValues.put("awardAccounts.account.accountNumber", value);
        value = fieldValues.remove("awardBillingFrequency");
        fieldValues.put("preferredBillingFrequency", value);
        value = fieldValues.remove("awardTotal");
        fieldValues.put("awardTotalAmount", value);

        if(StringUtils.isNotEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate")) && StringUtils.isNotEmpty(fieldValues.get("awardBeginningDate"))){
            String date = fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate") + ".." + fieldValues.remove("awardBeginningDate");
            fieldValues.put("awardBeginningDate", date);
        }
        else if(StringUtils.isEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate")) && StringUtils.isNotEmpty(fieldValues.get("awardBeginningDate"))){
            String date = "<=" + fieldValues.remove("awardBeginningDate");
            fieldValues.put("awardBeginningDate", date);
        }
        else if(StringUtils.isEmpty(fieldValues.get("awardBeginningDate")) && StringUtils.isNotEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate"))){
            String date = ">=" + fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate");
            fieldValues.put("awardBeginningDate", date);
        }

        if(StringUtils.isNotEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate")) && StringUtils.isNotEmpty(fieldValues.get("awardEndingDate"))){
            String date = fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate") + ".." + fieldValues.remove("awardEndingDate");
            fieldValues.put("awardEndingDate", date);
        }
        else if(StringUtils.isEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate")) && StringUtils.isNotEmpty(fieldValues.get("awardEndingDate"))){
            String date = "<=" + fieldValues.remove("awardEndingDate");
            fieldValues.put("awardEndingDate", date);
        }
        else if(StringUtils.isEmpty(fieldValues.get("awardEndingDate")) && StringUtils.isNotEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate"))){
            String date = ">=" + fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate");
            fieldValues.put("awardEndingDate", date);
        }

        return (List<Award>)service.callGetSearchResultsHelper(LookupUtils.forceUppercase(Award.class, fieldValues), unbounded);
    }

    /**
     * This method sets last Billed Date to award Account.
     *
     * @param criteria
     * @param invoiceStatus
     * @param lastBilledDate
     */
    @Override
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, String invoiceStatus, Date lastBilledDate, String invoiceDocumentStatus) {
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
        // This is a No-Op since getLastBilledDate on the CG Award is deriving the value from the AwardAccounts
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

        award.setLetterOfCreditCreationType(locCreationType);
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
        awardAccount.setLetterOfCreditReviewIndicator(locReviewIndicator);
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
        awardAccount.setFinalBilledIndicator(finalBilled);
        getBusinessObjectService().save(awardAccount);
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setFinalBilledAndLastBilledDateToAwardAccount(java.util.Map, java.lang.String, java.sql.Date)
     */
    @Override
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> criteria, boolean finalBilled, String invoiceStatus, Date lastBilledDate, String invoiceDocumentStatus) {

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

        awardAccount.setFinalBilledIndicator(finalBilled);
        getBusinessObjectService().save(awardAccount);
    }

    /**
     * Returns an implementation of the parameterService
     *
     * @return an implementation of the parameterService
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Returns the default implementation of the C&G AgencyService
     *
     * @return an implementation of AgencyService
     */
    public AgencyService getAgencyService() {
        return agencyService;
    }

    /**
     * Returns an implementation of the CfdaService
     *
     * @return an implementation of the CfdaService
     */
    public CfdaService getCfdaService() {
        return cfdaService;
    }

    /**
     * Returns an implementation of the BusinessObjectService
     *
     * @return an implementation of the BusinessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Returns an implementation of the BusinessObjectService
     *
     * @return an implementation of the BusinessObjectService
     */
    public AwardService getAwardService() {
        return awardService;
    }

    public void setAwardService(AwardService awardService) {
        this.awardService = awardService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setAgencyService(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    public void setCfdaService(CfdaService cfdaService) {
        this.cfdaService = cfdaService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
