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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileArranger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rule.event.PromptBeforeValidationEvent;
import org.kuali.rice.core.api.config.property.ConfigurationService;

public class TemProfilePreRules extends MaintenancePreRulesBase {
            
    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#setAccountService(org.kuali.kfs.coa.service.AccountService)
     */
    @Override
    public void setAccountService(AccountService accountService) {
        // TODO Auto-generated method stub
        super.setAccountService(accountService);
    }

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#setConfigService(org.kuali.rice.kns.service.ConfigurationService)
     */
    @Override
    public void setConfigService(ConfigurationService configService) {
        // TODO Auto-generated method stub
        super.setConfigService(configService);
    }

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doPrompts(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        // TODO Auto-generated method stub
        return super.doPrompts(document);
    }

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {
        boolean askQuestion = true;
        
        TEMProfile profile = (TEMProfile) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        if (profile.getArrangers().size() > 0){
            for (TEMProfileArranger arranger : profile.getArrangers()){
                if (arranger.getActive()){
                    askQuestion = false;
                }
            }
        }
        
        if (StringUtils.isNotEmpty(profile.getTravelerTypeCode()) && !profile.getTravelerTypeCode().equalsIgnoreCase(TemConstants.NONEMP_TRAVELER_TYP_CD) && askQuestion){
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(TemKeyConstants.TEM_PROFILE_ARRANGERS_QUESTION);
            boolean confirm = super.askOrAnalyzeYesNoQuestion(TemKeyConstants.GENERATE_TEM_PROFILE_ID_QUESTION_ID, questionText);
            if (!confirm) {
                super.abortRulesCheck();
            }
        }
        return super.doCustomPreRules(maintenanceDocument);
    }

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#checkForContinuationAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    protected Account checkForContinuationAccount(String accName, String chart, String accountNumber, String accountName, boolean allowExpiredAccount) {
        // TODO Auto-generated method stub
        return super.checkForContinuationAccount(accName, chart, accountNumber, accountName, allowExpiredAccount);
    }

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#checkForContinuationAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected Account checkForContinuationAccount(String accName, String chart, String accountNumber, String accountName) {
        // TODO Auto-generated method stub
        return super.checkForContinuationAccount(accName, chart, accountNumber, accountName);
    }

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#buildContinuationConfirmationQuestion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    protected String buildContinuationConfirmationQuestion(String accName, String expiredAccount, String continuationAccount) {
        // TODO Auto-generated method stub
        return super.buildContinuationConfirmationQuestion(accName, expiredAccount, continuationAccount);
    }

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#getAccountService()
     */
    @Override
    public AccountService getAccountService() {
        // TODO Auto-generated method stub
        return super.getAccountService();
    }

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#getConfigService()
     */
    @Override
    public ConfigurationService getConfigService() {
        // TODO Auto-generated method stub
        return super.getConfigService();
    }

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#processPrompts(org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, org.kuali.rice.kns.rule.event.PromptBeforeValidationEvent)
     */
    @Override
    public boolean processPrompts(ActionForm form, HttpServletRequest request, PromptBeforeValidationEvent event) {
        // TODO Auto-generated method stub
        return super.processPrompts(form, request, event);
    }

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#abortRulesCheck()
     */
    @Override
    public void abortRulesCheck() {
        // TODO Auto-generated method stub
        super.abortRulesCheck();
    }



}
