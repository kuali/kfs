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
import org.kuali.kfs.module.tem.TemPropertyConstants.TemProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.kfs.module.tem.businessobject.TemProfileArranger;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TemProfileRule extends MaintenanceDocumentRuleBase{
    protected static final String TRAVEL_ARRANGERS_SECTION_ID = "TemProfileArrangers";
    protected volatile static TemProfileService temProfileService;
    protected volatile static DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#dataDictionaryValidate(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean dataDictionaryValidate(MaintenanceDocument document) {
        BusinessObjectService businessObjectService = getBusinessObjectService();
        TemProfile profile = (TemProfile) document.getNewMaintainableObject().getBusinessObject();
        TravelService travelService = SpringContext.getBean(TravelService.class);

        boolean success = true;
        List<String> paths = GlobalVariables.getMessageMap().getErrorPath();
        Map<String,Object> fieldValues = new HashMap<String,Object>();
        paths.add("document");
        paths.add("newMaintainableObject");


        if (!StringUtils.isEmpty(profile.getPhoneNumber())){
            String errorMessage = travelService.validatePhoneNumber(profile.getCountryCode(), profile.getPhoneNumber(), TemKeyConstants.ERROR_PHONE_NUMBER);
            if (!StringUtils.isBlank(errorMessage)) {
                GlobalVariables.getMessageMap().putError(TemProfileProperties.PHONE_NUMBER, errorMessage, new String[] { "Phone Number"});
                success = false;
            }
        }

        if (!StringUtils.isEmpty(profile.getDefaultChartCode())){
            fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, profile.getDefaultChartCode());
            List<Chart> chartList = (List<Chart>) businessObjectService.findMatching(Chart.class, fieldValues);
            if (chartList.size() == 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TemProfileProperties.DEFAULT_CHART_CODE,
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
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TemProfileProperties.DEFAULT_ACCOUNT_NUMBER,
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
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TemProfileProperties.DEFAULT_SUB_ACCOUNT_NUMBER,
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
            List<ProjectCode> subAccountList = (List<ProjectCode>) businessObjectService.findMatching(ProjectCode.class, fieldValues);
            if (subAccountList.size() == 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TemProfileProperties.DEFAULT_PROJECT_CODE,
                        TemKeyConstants.ERROR_TEM_PROFILE_PROJECT_CODE_NOT_EXIST, profile.getDefaultProjectCode());
                success = false;
            }
        }
        else{
            profile.setProject(null);
        }

        for (int i=0;i<profile.getAccounts().size();i++){
            paths.add(TemPropertyConstants.TemProfileProperties.ACCOUNTS + "[" + i + "]");
            TemProfileAccount account = profile.getAccounts().get(i);
            paths.remove(paths.size()-1);
        }

        //Arranger section validation
        if(profile.getArrangers() != null){
            int arrangerPrimaryCount = 0;
            Set<String> arrangerId = new HashSet<String>();
            for (TemProfileArranger arranger : profile.getArrangers()){
                if(arranger.getPrimary()){
                    arrangerPrimaryCount++;
                }

                arrangerId.add(arranger.getPrincipalName());

                paths.add(TemPropertyConstants.TemProfileProperties.ARRANGERS);

                paths.remove(paths.size()-1);
            }

            //check for multiple primary arrangers; only 1 primary allowed.
            if(arrangerPrimaryCount > 1){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TemProfileProperties.ARRANGERS,
                        TemKeyConstants.ERROR_TEM_PROFILE_ARRANGER_PRIMARY);
                success = false;
            }

            //check for duplicate arrangers
            if(profile.getArrangers().size() != arrangerId.size()){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TemProfileProperties.ARRANGERS,
                        TemKeyConstants.ERROR_TEM_PROFILE_ARRANGER_DUPLICATE);
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
        TemProfile profile = (TemProfile)document.getNewMaintainableObject().getBusinessObject();

        //set other arranger as primary false if the new arranger is the primary
        if (line instanceof TemProfileArranger){
            TemProfileArranger arranger = (TemProfileArranger)line;

            //check that the arranger's org is under the initiator's
            if (TemConstants.NONEMP_TRAVELER_TYP_CD.equals(profile.getTravelerTypeCode())) {
                TravelService travelService = SpringContext.getBean(TravelService.class);
                String initiatorId = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
                final Person initiator = SpringContext.getBean(PersonService.class).getPerson(initiatorId);

            }

            if (arranger.getPrimary()){
                for (TemProfileArranger tempArranger : ((TemProfile)document.getNewMaintainableObject().getBusinessObject()).getArrangers()){
                    tempArranger.setPrimary(false);
                }

                if (TemConstants.NONEMP_TRAVELER_TYP_CD.equals(profile.getTravelerTypeCode())) {
                    String profileChart = profile.getHomeDeptChartOfAccountsCode();
                    String profileOrg = profile.getHomeDeptOrgCode();

                    final Person arrangerPerson = SpringContext.getBean(PersonService.class).getPerson(arranger.getPrincipalId());
                    String primaryDeptCode[] = arrangerPerson.getPrimaryDepartmentCode().split("-");
                    if(primaryDeptCode != null && primaryDeptCode.length == 2){
                        profile.setHomeDeptChartOfAccountsCode(primaryDeptCode[0]);
                        profile.setHomeDeptOrgCode(primaryDeptCode[1]);
                    }
                }

            }
        }
        // check for CreditCardAgency accounts
        else if (line instanceof TemProfileAccount){
            TemProfileAccount account = (TemProfileAccount) line;
            return checkNewTemProfileAccount(profile, account);
        }

        return super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
    }

    /**
     * Performs rules against a new TEM Profile account
     *
     * @param account
     * @return
     */
    protected boolean checkNewTemProfileAccount(TemProfile temProfile, TemProfileAccount account) {
        boolean success = true;
        //minimum length
        if (!StringUtils.isBlank(account.getAccountNumber())){
            final Integer minLength = getDataDictionaryService().getAttributeMaxLength(TemProfile.class, TemPropertyConstants.TemProfileProperties.ACCOUNT_NUMBER);
            if (minLength != null && account.getAccountNumber().length() < minLength.intValue()) {
                final String label = getDataDictionaryService().getAttributeLabel(TemProfile.class, TemPropertyConstants.TemProfileProperties.ACCOUNT_NUMBER);

                String errorMessage[] = null;
                errorMessage = new String[] { label, minLength.toString() };
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TemProfileProperties.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_MIN_LENGTH, errorMessage);
                success = false;
            }

            //do not duplicate an existing account in the system
            if (doesProfileAccountMatchExisting(temProfile, account)){
                if (account.getCreditCardOrAgencyCode() != null) {
                    account.refreshReferenceObject(TemPropertyConstants.CREDIT_CARD_AGENCY);
                }
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TemProfileProperties.ACCOUNT_NUMBER, TemKeyConstants.ERROR_TEM_PROFILE_ACCOUNT_ID_DUPLICATE, new String[] { account.getCreditCardAgency().getCreditCardOrAgencyName(), account.getAccountNumber() });
                success = false;
            }
        }

        return success;
    }

    /**
     * Checks if the given newAccount matches - by account number and credit card agency - an already existing profile account on the profile
     * @param profile the profile with existing profile accounts
     * @param newAccount the account to see if it exists among the existing accounts
     * @return true if the newAccount is already on the profile; false otherwise
     */
    protected boolean doesProfileAccountMatchExisting(TemProfile profile, TemProfileAccount newAccount) {
        if (profile.getAccounts() == null || profile.getAccounts().isEmpty()) {
            return getTemProfileService().doesProfileAccountExist(newAccount, profile);
        }
        for (TemProfileAccount existingAccount : profile.getAccounts()) {
            if (StringUtils.equals(existingAccount.getAccountNumber(), newAccount.getAccountNumber()) && StringUtils.equals(existingAccount.getCreditCardOrAgencyCode(), newAccount.getCreditCardOrAgencyCode())) {
                return true;
            }
        }
        return getTemProfileService().doesProfileAccountExist(newAccount, profile);
    }

    /**
     * Checks that, if the profile represents a non-employee, the profile has an active arranger
     * @param profile the profile to check
     * @return true if the profile passed the validation, false otherwise
     */
    protected boolean checkActiveArrangersForNonEmployees(TemProfile profile) {
        boolean success = true;
        if (profile != null && getTemProfileService().isProfileNonEmploye(profile)) {
            // we've got a non-employee.  let's see if they have at least one active arranger
            if (!getTemProfileService().hasActiveArrangers(profile)) {
                GlobalVariables.getMessageMap().putErrorForSectionId(TRAVEL_ARRANGERS_SECTION_ID, TemKeyConstants.ERROR_TEM_PROFILE_NONEMPLOYEE_MUST_HAVE_ACTIVE_ARRANGER);
                success = false;
            }
        }
        return success;
    }

    /**
     * Checks that, if the profile represents a non-employee, the profile has an email address
     * @param profile the profile to check
     * @return true if the profile passed the validation, false otherwise
     */
    protected boolean checkEmailAddressForNonEmployees(TemProfile profile) {
        boolean success = true;
        if (profile != null && getTemProfileService().isProfileNonEmploye(profile)) {
            // we've got a non-employee.  let's see if they have an email address
            if (StringUtils.isBlank(profile.getEmailAddress())) {
                GlobalVariables.getMessageMap().putError("document.newMaintainableObject."+TemPropertyConstants.TemProfileProperties.EMAIL_ADDRESS, TemKeyConstants.ERROR_TEM_PROFILE_NONEMPLOYEE_MUST_HAVE_EMAIL);
                success = false;
            }
        }
        return success;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        TemProfile profile = (TemProfile) document.getNewMaintainableObject().getBusinessObject();
        if (ObjectUtils.isNotNull(profile.getHomeDeptOrg())){
            if (!profile.getHomeDeptOrg().isActive()) {
                putFieldError(TemProfileProperties.HOME_DEPARTMENT, TemKeyConstants.ERROR_TEM_PROFILE_ORGANIZATION_INACTIVE, profile.getHomeDeptChartOfAccountsCode()+"-"+profile.getHomeDeptOrgCode());
                success = false;
            }
        }
        success &= checkActiveArrangersForNonEmployees(profile);
        success &= checkEmailAddressForNonEmployees(profile);
        return success;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    protected TemProfileService getTemProfileService() {
        if (temProfileService == null) {
            temProfileService = SpringContext.getBean(TemProfileService.class);
        }
        return temProfileService;
    }

    @Override
    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }

}
