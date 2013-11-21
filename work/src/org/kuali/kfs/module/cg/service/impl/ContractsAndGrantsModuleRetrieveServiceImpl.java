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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.module.cg.businessobject.lookup.AwardLookupableHelperServiceImpl;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.service.AgencyService;
import org.kuali.kfs.module.cg.service.AwardService;
import org.kuali.kfs.module.cg.service.CfdaService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import java.util.ArrayList;

/**
 * This Class provides the implementation to the services required for inter module communication.
 */
@NonTransactional
public class ContractsAndGrantsModuleRetrieveServiceImpl implements ContractsAndGrantsModuleRetrieveService {

    private AwardService awardService;
    private ParameterService parameterService;
    private AgencyService agencyService;
    private CfdaService cfdaService;
    private BusinessObjectService businessObjectService;

    /**
     * This method would return list of business object - in this case Awards for CG Invoice On Demand functionality in AR.
     * @param fieldValues
     * @param unbounded
     * @return
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
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

        return service.callGetSearchResultsHelper(LookupUtils.forceUppercase(Award.class, fieldValues), unbounded);
    }

    /**
     * This method retrieves awards for Invoice On Demand in AR.
     * @param lookupResultsSequenceNumber
     * @param personId
     * @return
     */
    @Override
    public Collection<ContractsAndGrantsBillingAward> getAwardsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId){
        Collection<ContractsAndGrantsBillingAward> intgAwards = new ArrayList<ContractsAndGrantsBillingAward>();
        Collection<Award> awards = new ArrayList<Award>();
        try {
            for (PersistableBusinessObject obj : SpringContext.getBean(LookupResultsService.class).retrieveSelectedResultBOs(lookupResultsSequenceNumber, Award.class, personId)) {
                awards.add((Award) obj);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        intgAwards = new ArrayList<ContractsAndGrantsBillingAward>(awards);
        return intgAwards;
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

    /**
     * Retrieve the boolean value for whether CG and Billing Enhancements are on from system parameter
     * @return true if parameter ENABLE_CG_BILLING_ENHANCEMENTS_IND is set to "Y"; otherwise false.
     */
    @Override
    public boolean isContractsGrantsBillingEnhancementsActive() {

        return getParameterService().getParameterValueAsBoolean(KfsParameterConstants.CONTRACTS_AND_GRANTS_ALL.class, CGConstants.ENABLE_CG_BILLING_ENHANCEMENTS_IND);
    }


    /**
     * This method checks the Contract Control account set for Award Account based on award's invoicing option.
     * @return errorString
     */
    @Override
    public List<String> hasValidContractControlAccounts(Long proposalNumber){
        Award award = getAwardService().getByPrimaryId(proposalNumber);
        return getAwardService().hasValidContractControlAccounts(award);
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

