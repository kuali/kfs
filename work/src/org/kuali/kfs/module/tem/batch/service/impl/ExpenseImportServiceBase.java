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
package org.kuali.kfs.module.tem.batch.service.impl;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.util.BufferedLogger.error;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ProjectCodeService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.module.tem.TemConstants.AgencyMatchProcessParameter;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;


public class ExpenseImportServiceBase {    
    private static Logger LOG = org.apache.log4j.Logger.getLogger(ExpenseImportServiceBase.class);

    /**
     * 
     * This method returns true if the Account exists. 
     * @param chartCode
     * @param accountNumber
     * @return
     */
    public boolean isAccountNumberValid(String chartCode, String accountNumber) {
        boolean valid = false;
        if (StringUtils.isNotEmpty(chartCode) && StringUtils.isNotEmpty(accountNumber)) {
            Account account = getAccountService().getByPrimaryId(chartCode, accountNumber);
            if (ObjectUtils.isNotNull(account)) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * 
     * This method returns true if the SubAccount exists.
     * @param chartCode
     * @param accountNumber
     * @param subAccountNumber
     * @return
     */
    public boolean isSubAccountNumberValid(String chartCode, String accountNumber, String subAccountNumber) {
        boolean valid = false;
        if (StringUtils.isNotEmpty(chartCode) && StringUtils.isNotEmpty(accountNumber) && StringUtils.isNotEmpty(subAccountNumber)) {
            SubAccount subAccount = getSubAccountService().getByPrimaryId(chartCode, accountNumber, subAccountNumber);
            if (ObjectUtils.isNotNull(subAccount)) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * 
     * This method returns true if the Project Code exists.
     * @param projectCode
     * @return
     */
    public boolean isProjectCodeValid(String projectCode) {
        boolean valid = false;
        if (StringUtils.isNotEmpty(projectCode)) {
            ProjectCode project = getProjectCodeService().getByPrimaryId(projectCode);
            if (ObjectUtils.isNotNull(project)) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * 
     * This method returns true if the Object Code exists.
     * @param chartCode
     * @param objectCode
     * @return
     */
    public boolean isObjectCodeValid(String chartCode, String objectCode) {
        boolean valid = false;
        if (StringUtils.isNotEmpty(chartCode) && StringUtils.isNotEmpty(objectCode)) {
            ObjectCode code = getObjectCode(chartCode, objectCode);
            if (ObjectUtils.isNotNull(code)) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * 
     * This method returns true if the SubObject Code exists.
     * @param chartCode
     * @param accountNumber
     * @param objectCode
     * @param subObjectCode
     * @return
     */
    public boolean isSubObjectCodeValid(String chartCode, String accountNumber, String objectCode, String subObjectCode) {
        boolean valid = false;
        if (StringUtils.isNotEmpty(chartCode) && StringUtils.isNotEmpty(accountNumber) && StringUtils.isNotEmpty(objectCode) && StringUtils.isNotEmpty(subObjectCode)) {
            SubObjectCode code = getSubObjectCodeService().getByPrimaryIdForCurrentYear(chartCode, accountNumber, objectCode, subObjectCode);
            if (ObjectUtils.isNotNull(code)) {
                valid = true;
            }
        }
        return valid;
    }

    /**
     * 
     * This method returns true if the amount is null or 0.
     * @param amount
     * @return
     */
    public boolean isAmountEmpty(KualiDecimal amount) {
        if (ObjectUtils.isNull(amount) || amount == KualiDecimal.ZERO) {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * This method gets the objectCode based on the trip type.
     * @param agencyData
     * @return
     */
    public String getObjectCode(AgencyStagingData agencyData) {

        String objectCodeParam = null;
        if (StringUtils.isNotEmpty(agencyData.getAirTicketNumber())) {
            objectCodeParam = AgencyMatchProcessParameter.CTS_AIR_OBJECT_CODE;
        }
        else if (StringUtils.isNotEmpty(agencyData.getLodgingItineraryNumber())) {
            objectCodeParam = AgencyMatchProcessParameter.CTS_LODGING_OBJECT_CODE;
        }
        else if (StringUtils.isNotEmpty(agencyData.getRentalCarItineraryNumber())) {
            objectCodeParam = AgencyMatchProcessParameter.CTS_RENTAL_CAR_OBJECT_CODE;
        }
        else {
            LOG.warn("No valid expenses.");
            return null;
        }
        
        return getParameter(objectCodeParam, AgencyMatchProcessParameter.AGENCY_MATCH_DTL_TYPE);
    }
    
    /**
     * 
     * This method returns the {@link ObjectCode} based on the Chart Code and Object Code
     * @param chartCode
     * @param objectCode
     * @return
     */
    public ObjectCode getObjectCode(String chartCode, String objectCode) {
        return getObjectCodeService().getByPrimaryIdForCurrentYear(chartCode, objectCode);
    }
    
    /**
     * 
     * This method returns the db value of the parameter. Throws a RuntimeException of an error occurs.
     * @param parameter
     * @return
     */
    public String getParameter(String parameter, String detailType) {
        try {
            String objectCode = getParameterService().getParameterValue(PARAM_NAMESPACE, detailType, parameter);
            return objectCode;
        }
        catch (IllegalArgumentException e) {
            error("IllegalArgumentException trying to get: " + parameter, e);
            throw new RuntimeException("Unable to get parameter " + e.getMessage());
        }  
    }
    
    /**
     * 
     * This method returns the db values of the parameter. Throws a RuntimeException of an error occurs.
     * @param parameter
     * @param detailType
     * @return
     */
    public List<String> getParameterValues(String parameter, String detailType) {
        try {
            List<String> objectCode = getParameterService().getParameterValues(PARAM_NAMESPACE, detailType, parameter);
            return objectCode;
        }
        catch (IllegalArgumentException e) {
            error("IllegalArgumentException trying to get: " + parameter, e);
            throw new RuntimeException("Unable to get parameter " + e.getMessage());
        }  
    }
    
    /**
     * A helper method that checks the intended target value for null and empty strings. If the intended target value is not null or
     * an empty string, it returns that value, otherwise, it returns a backup value.
     * 
     * @param targetValue
     * @param backupValue
     * @return String
     */
    protected final String getEntryValue(String targetValue, String backupValue) {

        if (StringUtils.isNotBlank(targetValue)) {
            return targetValue;
        }
        else {
            return backupValue;
        }
    }
    
    /**
     * 
     * This method verifies that the Credit Card Agency Code is valid.
     * @param code
     * @return
     */
    public boolean isCreditCardAgencyValid(AgencyStagingData agencyData) {
        
        if (StringUtils.isNotEmpty(agencyData.getCreditCardOrAgencyCode())) {
            
            CreditCardAgency ccAgency = getTravelExpenseService().getCreditCardAgency(agencyData.getCreditCardOrAgencyCode());
            if (ObjectUtils.isNotNull(ccAgency)) {
                return true;
            }
        }
        LOG.error("Invalid Credit Card Agency Code in Agency Data record");
        setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_CC_AGENCY);
        return false;
    }
    
    /**
     * 
     * This method sets the errorCode on the {@link AgencyStagingData} if no previous errors have been encountered.
     * @param agencyData
     * @param error
     */
    public void setErrorCode(AgencyStagingData agencyData, String error) {
        if (StringUtils.equals(agencyData.getErrorCode(), AgencyStagingDataErrorCodes.AGENCY_NO_ERROR)) {
            agencyData.setErrorCode(error);
        }
    }

    
    /**
     * Gets the accountService attribute. 
     * @return Returns the accountService.
     */
    public AccountService getAccountService() {
        return SpringContext.getBean(AccountService.class);
    }

    /**
     * Gets the subAccountService attribute. 
     * @return Returns the subAccountService.
     */
    public SubAccountService getSubAccountService() {
        return SpringContext.getBean(SubAccountService.class);
    }

    /**
     * Gets the projectCodeService attribute. 
     * @return Returns the projectCodeService.
     */
    public ProjectCodeService getProjectCodeService() {
        return SpringContext.getBean(ProjectCodeService.class);
    }

    /**
     * Gets the objectCodeService attribute. 
     * @return Returns the objectCodeService.
     */
    public ObjectCodeService getObjectCodeService() {
        return SpringContext.getBean(ObjectCodeService.class);
    }

    /**
     * Gets the subObjectCodeService attribute. 
     * @return Returns the subObjectCodeService.
     */
    public SubObjectCodeService getSubObjectCodeService() {
        return SpringContext.getBean(SubObjectCodeService.class);
    }
    
    public ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    /**
     * Gets the universityDateService attribute. 
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return SpringContext.getBean(UniversityDateService.class);
    }

    /**
     * Gets the dateTimeService attribute. 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }

    /**
     * Gets the TravelExpenseService attribute. 
     * @return Returns the TravelExpenseService.
     */
    public TravelExpenseService getTravelExpenseService() {
        return SpringContext.getBean(TravelExpenseService.class);
    }


}
