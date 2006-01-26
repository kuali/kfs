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
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubFundGroup;

public class AccountPreRules implements PreRulesCheck {

    private Account oldAccount;
    private Account newAccount;
    
    /**
     * This processes certain rules that need to occur at the UI level or actually need to modify the Account object
     * before being passed on down the rules chain
     * @see org.kuali.core.rule.PreRulesCheck#processPreRuleChecks(org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, org.kuali.core.rule.event.PreRulesCheckEvent)
     */
    public boolean processPreRuleChecks(ActionForm form, HttpServletRequest request, PreRulesCheckEvent event) {
        MaintenanceDocument document = (MaintenanceDocument) event.getDocument();
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
        
        //	setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldAccount = (Account) document.getOldMaintainableObject().getBusinessObject();
        oldAccount.refresh();

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
        
        SubFundGroup subFundGroup;
        String fundGroupCode = "";
        String restrictedStatusCode;
        
        if (!ObjectUtils.isNull(newAccount.getSubFundGroup())) {
            fundGroupCode = newAccount.getSubFundGroup().getFundGroupCode();
        }
        restrictedStatusCode = newAccount.getAccountRestrictedStatusCode();
       
        if (!StringUtils.isEmpty(fundGroupCode)) {
            
	        //	on the account screen, if the fund group of the account is CG (contracts & grants) or 
	        // RF (restricted funds), the restricted status code is set to 'R'.
	        if (fundGroupCode.equalsIgnoreCase("CG") || fundGroupCode.equalsIgnoreCase("RF")) {
	            newAccount.setAccountRestrictedStatusCode("R");
	        }
	
	        //	If the fund group is EN (endowment) or PF (plant fund) the value is not set by the system and 
	        // must be set by the user 
	        else if (fundGroupCode.equalsIgnoreCase("EN") || fundGroupCode.equalsIgnoreCase("PF")) {
	            // do nothing, must be set by user
	        }
	        
	        //	for all other fund groups the value is set to 'U'. R being restricted,U being unrestricted.
	        else {
	            newAccount.setAccountRestrictedStatusCode("U");
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
            newAccount.setAccountStateCode("IN");
        }
        
        //if the account type code is left blank it will default to NA.
        if (StringUtils.isEmpty(newAccount.getAccountTypeCode())) {
            newAccount.setAccountTypeCode("NA");
        }
    }
    
    //TODO - lookup state and city from populated zip
    private void setStateFromZip(MaintenanceDocument maintenanceDocument) {
        
        //acct_zip_cd, acct_state_cd, acct_city_nm all are populated by looking up the zip code and getting the state and city from that
        if (!StringUtils.isEmpty(newAccount.getAccountZipCode())) {

            HashMap primaryKeys = new HashMap();
            primaryKeys.put("postalZipCode", newAccount.getAccountZipCode());
            PostalZipCode zip = (PostalZipCode)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(PostalZipCode.class, primaryKeys);
            //TODO- now what do i do with this exactly?
        }
    }

}
