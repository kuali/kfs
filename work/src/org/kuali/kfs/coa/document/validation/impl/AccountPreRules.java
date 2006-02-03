/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.rules;

import java.sql.Timestamp;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.kuali.core.bo.PostalZipCode;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rule.PreRulesCheck;
import org.kuali.core.rule.event.PreRulesCheckEvent;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;

public class AccountPreRules implements PreRulesCheck {

    private static final String CHART_MAINTENANCE_EDOC = "ChartMaintenanceEDoc";
    private static final String DEFAULT_STATE_CODE = "Account.Defaults.StateCode";
    private static final String DEFAULT_ACCOUNT_TYPE_CODE = "Account.Defaults.AccountType";
    
    private KualiConfigurationService configService;
    private Account newAccount;
    
    private static final String CONTRACTS_GRANTS_CD = "CG";
    private static final String GENERAL_FUND_CD = "GF";
    private static final String RESTRICTED_FUND_CD = "RF";
    private static final String ENDOWMENT_FUND_CD = "EN";
    private static final String PLANT_FUND_CD = "PF";
    
    private static final String RESTRICTED_CD_RESTRICTED = "R";
    private static final String RESTRICTED_CD_UNRESTRICTED = "U";
    private static final String RESTRICTED_CD_TEMPORARILY_RESTRICTED = "T";
    private static final String RESTRICTED_CD_NOT_APPLICABLE = "N";
    
    public AccountPreRules() {
        configService = SpringServiceLocator.getKualiConfigurationService();
    }

    /**
     * This processes certain rules that need to occur at the UI level or actually need to modify the Account object
     * before being passed on down the rules chain
     * @see org.kuali.core.rule.PreRulesCheck#processPreRuleChecks(org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, org.kuali.core.rule.event.PreRulesCheckEvent)
     */
    public boolean processPreRuleChecks(ActionForm form, HttpServletRequest request, PreRulesCheckEvent event) {
        MaintenanceDocument document = (MaintenanceDocument) event.getDocument();
        setupConvenienceObjects(document);
        newAccountDefaults(document);
        setStateFromZip(document);
        setRestrictedCodeDefaults(document);
        return true;
    }
    
    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you
     * have short and easy handles to the new and old objects contained in the 
     * maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load 
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {
        
        //	setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAccount = (Account) document.getNewMaintainableObject().getBusinessObject();
        newAccount.refresh();
    }
    
    /**
     * 
     * This method sets the default values for RestrictedStatusCode, based on the 
     * FundGroups.
     * 
     * @param document - the MaintenanceDocument being evaluated
     * 
     */
    private void setRestrictedCodeDefaults(MaintenanceDocument document) {
        
        String fundGroupCode = "";
        
        //	if subFundGroupCode was not entered, then we have nothing 
        // to do here, so exit
        if (ObjectUtils.isNull(newAccount.getSubFundGroup()) || 
                StringUtils.isEmpty(newAccount.getSubFundGroupCode())) {
            return;
        }
        fundGroupCode = newAccount.getSubFundGroup().getFundGroupCode();
       
        if (!StringUtils.isEmpty(fundGroupCode)) {
            
	        //	on the account screen, if the fund group of the account is CG (contracts & grants) or 
	        // RF (restricted funds), the restricted status code is set to 'R'.
	        if (fundGroupCode.equalsIgnoreCase(CONTRACTS_GRANTS_CD) || fundGroupCode.equalsIgnoreCase(RESTRICTED_FUND_CD)) {
	            newAccount.setAccountRestrictedStatusCode(RESTRICTED_CD_RESTRICTED);
	        }
	
	        //	If the fund group is EN (endowment) or PF (plant fund) the value is not set by the system and 
	        // must be set by the user 
	        else if (fundGroupCode.equalsIgnoreCase(ENDOWMENT_FUND_CD) || fundGroupCode.equalsIgnoreCase(PLANT_FUND_CD)) {
	            // do nothing, must be set by user
	        }
	        
	        //	for all other fund groups the value is set to 'U'. R being restricted,U being unrestricted.
	        else {
	            newAccount.setAccountRestrictedStatusCode(RESTRICTED_CD_UNRESTRICTED);
	        }
        }
    }
    
    /**
     * 
     * This method sets up some defaults for new Account
     * @param maintenanceDocument
     */
    private void newAccountDefaults(MaintenanceDocument maintenanceDocument) {
        
        //On new Accounts acct_effect_date is defaulted to the doc creation date
        if (newAccount.getAccountEffectiveDate() == null) {
            Timestamp ts = maintenanceDocument.getDocumentHeader().getWorkflowDocument().getCreateDate();
            if (ts != null) {
                newAccount.setAccountEffectiveDate(ts);
            }
        }
        
        //On new Accounts acct_state_cd is defaulted to the value of "IN"
        if (StringUtils.isEmpty(newAccount.getAccountStateCode())) {
            String defaultStateCode = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, 
    				DEFAULT_STATE_CODE);
    		if (StringUtils.isEmpty(defaultStateCode)) {
    			throw new RuntimeException("Expected ConfigurationService.ApplicationParameterValue was not found " + 
    										"for ScriptName = '" + CHART_MAINTENANCE_EDOC + "' and " + 
    										"Parameter = '" + DEFAULT_STATE_CODE + "'");
    		}
            newAccount.setAccountStateCode(defaultStateCode);
        }
        
        //if the account type code is left blank it will default to NA.
        if (StringUtils.isEmpty(newAccount.getAccountTypeCode())) {
            String defaultAccountTypeCode = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, 
    				DEFAULT_ACCOUNT_TYPE_CODE);
    		if (StringUtils.isEmpty(defaultAccountTypeCode)) {
    			throw new RuntimeException("Expected ConfigurationService.ApplicationParameterValue was not found " + 
    										"for ScriptName = '" + CHART_MAINTENANCE_EDOC + "' and " + 
    										"Parameter = '" + DEFAULT_ACCOUNT_TYPE_CODE + "'");
    		}
            newAccount.setAccountTypeCode(defaultAccountTypeCode);
        }
    }
    
    //	lookup state and city from populated zip, set the values on the form
    private void setStateFromZip(MaintenanceDocument maintenanceDocument) {
        
        //	acct_zip_cd, acct_state_cd, acct_city_nm all are populated by looking up 
        // the zip code and getting the state and city from that
        if (!StringUtils.isEmpty(newAccount.getAccountZipCode())) {

            HashMap primaryKeys = new HashMap();
            primaryKeys.put("postalZipCode", newAccount.getAccountZipCode());
            PostalZipCode zip = (PostalZipCode) SpringServiceLocator.getBusinessObjectService()
            									.findByPrimaryKey(PostalZipCode.class, primaryKeys);
            
            //TODO: finish this
            
            //	set the state field
            
            //	set the city field
            
        }
    }

}
