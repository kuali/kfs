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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.kfs.module.tem.businessobject.TEMProfileArranger;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.util.ObjectUtils;

public class TEMProfileValidation extends MaintenanceDocumentRuleBase{
    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#dataDictionaryValidate(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean dataDictionaryValidate(MaintenanceDocument document) {
        BusinessObjectService businessObjectService = getBusinessObjectService();
        TEMProfile profile = (TEMProfile) document.getNewMaintainableObject().getBusinessObject();
        TravelService travelService = SpringContext.getBean(TravelService.class);

        boolean success = true;
        List<String> paths = GlobalVariables.getMessageMap().getErrorPath();
        Map<String,Object> fieldValues = new HashMap<String,Object>();
        paths.add("document");
        paths.add("newMaintainableObject");

        if (!StringUtils.isEmpty(profile.getPhoneNumber())){
            String errorMessage = travelService.validatePhoneNumber(profile.getCountryCode(), profile.getPhoneNumber(), TemKeyConstants.ERROR_PHONE_NUMBER);
            if (!StringUtils.isBlank(errorMessage)) {
                GlobalVariables.getMessageMap().putError(TEMProfileProperties.PHONE_NUMBER, errorMessage, new String[] { "Phone Number"});
                success = false;
            }
        }

        if (!StringUtils.isEmpty(profile.getDefaultChartCode())){
            fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, profile.getDefaultChartCode());
            List<Chart> chartList = (List<Chart>) businessObjectService.findMatching(Chart.class, fieldValues);
            if (chartList.size() == 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.DEFAULT_CHART_CODE,
                        TemKeyConstants.ERROR_TEM_PROFILE_CHART_NOT_EXIST, profile.getDefaultChartCode());
                success = false;
            }
        }
        else{
            profile.setChart(null);
        }

        if (success && !StringUtils.isEmpty(profile.getDefaultAccount())){
            fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, profile.getDefaultAccount());
            List<Account> accountList = (List<Account>) businessObjectService.findMatching(Account.class, fieldValues);
            if (accountList.size() == 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.DEFAULT_ACCOUNT_NUMBER,
                        TemKeyConstants.ERROR_TEM_PROFILE_ACCOUNT_NOT_EXIST, profile.getDefaultAccount());
                success = false;
            }
        }
        else{
            profile.setAccount(null);
        }

        if (success && !StringUtils.isEmpty(profile.getDefaultSubAccount())){
            fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, profile.getDefaultSubAccount());
            List<SubAccount> subAccountList = (List<SubAccount>) businessObjectService.findMatching(SubAccount.class, fieldValues);
            if (subAccountList.size() == 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.DEFAULT_SUB_ACCOUNT_NUMBER,
                        TemKeyConstants.ERROR_TEM_PROFILE_SUB_ACCOUNT_NOT_EXIST, profile.getDefaultSubAccount());
                success = false;
            }
        }
        else{
            profile.setSubAccount(null);
        }

        if (!StringUtils.isEmpty(profile.getDefaultProjectCode())){
            fieldValues.clear();
            fieldValues.put(KFSConstants.GENERIC_CODE_PROPERTY_NAME, profile.getDefaultProjectCode());
            List<SubAccount> subAccountList = (List<SubAccount>) businessObjectService.findMatching(ProjectCode.class, fieldValues);
            if (subAccountList.size() == 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.DEFAULT_PROJECT_CODE,
                        TemKeyConstants.ERROR_TEM_PROFILE_PROJECT_CODE_NOT_EXIST, profile.getDefaultProjectCode());
                success = false;
            }
        }
        else{
            profile.setProject(null);
        }

        for (int i=0;i<profile.getAccounts().size();i++){
            paths.add(TemPropertyConstants.TEMProfileProperties.ACCOUNTS + "[" + i + "]");
            TEMProfileAccount account = profile.getAccounts().get(i);
            paths.remove(paths.size()-1);
        }

        //Arranger section validation
        if(profile.getArrangers() != null){
            int arrangerPrimaryCount = 0;
            Set<String> arrangerId = new HashSet<String>();
            for (TEMProfileArranger arranger : profile.getArrangers()){
                if(arranger.getPrimary()){
                    arrangerPrimaryCount++;
                }

                arrangerId.add(arranger.getPrincipalName());

                paths.add(TemPropertyConstants.TEMProfileProperties.ARRANGERS);

                paths.remove(paths.size()-1);
            }

            //check for multiple primary arrangers; only 1 primary allowed.
            if(arrangerPrimaryCount > 1){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.ARRANGERS,
                        TemKeyConstants.ERROR_TEM_PROFILE_ARRANGER_PRIMARY);
                success = false;
            }

            //check for duplicate arrangers
            if(profile.getArrangers().size() != arrangerId.size()){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.ARRANGERS,
                        TemKeyConstants.ERROR_TEM_PROFILE_ARRANGER_DUPLICATE);
                success = false;
            }

            //require an arranger if the profile is non-employee
            if(StringUtils.isNotEmpty(profile.getTravelerTypeCode()) && profile.getTravelerTypeCode().equalsIgnoreCase(TemConstants.NONEMP_TRAVELER_TYP_CD) && ObjectUtils.isEmpty(profile.getArrangers().toArray())) {
            	GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.ARRANGERS,
                        TemKeyConstants.ERROR_TEM_PROFILE_ARRANGER_NONEMPLOYEE);
                success = false;
            }
        }

        paths.clear();
        success = success && super.dataDictionaryValidate(document);
        if(success){
            Person user = GlobalVariables.getUserSession().getPerson();
            profile.setUpdatedBy(user.getPrincipalName());
            Date current = new Date();
            profile.setLastUpdate(new java.sql.Date(current.getTime()));
        }
        return success;

    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {

        //set other arranger as primary false if the new arranger is the primary
        if (line instanceof TEMProfileArranger){
            TEMProfileArranger arranger = (TEMProfileArranger)line;
            if (arranger.getPrimary()){
                for (TEMProfileArranger tempArranger : ((TEMProfile)document.getNewMaintainableObject().getBusinessObject()).getArrangers()){
                    tempArranger.setPrimary(false);
                }
            }
        }
        // check for CreditCardAgency accounts
        else if (line instanceof TEMProfileAccount){
            TEMProfileAccount account = (TEMProfileAccount) line;
            //minimum length
            if (StringUtils.isNotEmpty(account.getAccountNumber()) && account.getAccountNumber().length() < 4){
            	String errorMessage[] = null;
                errorMessage = new String[] { "Account Number", "4" };
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_MIN_LENGTH, errorMessage);
                return false;
            }

            //not duplicate an existing account in the system
            if (StringUtils.isNotEmpty(account.getAccountNumber())){
                Map<String,Object> criteria = new HashMap<String,Object>();
                criteria.put(TemPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
                criteria.put(TemPropertyConstants.CREDIT_CARD_AGENCY_ID, account.getCreditCardAgencyId());
                Collection<TEMProfileAccount> profileAccounts = getBusinessObjectService().findMatching(TEMProfileAccount.class, criteria);
                if (!profileAccounts.isEmpty()){
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.ACCOUNT_NUMBER, TemKeyConstants.ERROR_TEM_PROFILE_ACCOUNT_ID_DUPLICATE, account.getAccountNumber());
                    return false;
                }
            }
        }

        return super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
    }

    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

}
