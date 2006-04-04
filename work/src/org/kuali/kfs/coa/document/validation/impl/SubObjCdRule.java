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

import org.kuali.KeyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubObjCd;

public class SubObjCdRule extends MaintenanceDocumentRuleBase {

    private static final String ACCOUNT_ORG_RULE_KEY = "SubObjectCode.AccountOrgsAllowingClosedAccounts";
    
    private SubObjCd oldSubObjectCode;
    private SubObjCd newSubObjectCode;
    
    public SubObjCdRule() {
        super();
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("Entering processCustomApproveDocumentBusinessRules()");
    
        //  check that all sub-objects whose keys are specified have matching objects in the db
        checkExistenceAndActive();

        return true;
    }
    
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        
        LOG.info("Entering processCustomRouteDocumentBusinessRules()");

        //  check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkExistenceAndActive();

        return success;
    }
    
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        
        LOG.info("Entering processCustomSaveDocumentBusinessRules()");

        //  check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkExistenceAndActive();

        return success;
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
    public void setupConvenienceObjects() {
        
        //  setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldSubObjectCode = (SubObjCd) super.getOldBo();

        //  setup newAccount convenience objects, make sure all possible sub-objects are populated
        newSubObjectCode = (SubObjCd) super.getNewBo();
    }
    
    protected boolean checkExistenceAndActive() {
        
        LOG.info("Entering checkExistenceAndActive()");
        boolean success = true;
        
        //  disallow closed accounts unless in certain orgs
        if (ObjectUtils.isNotNull(newSubObjectCode.getAccount())) {
            Account account = newSubObjectCode.getAccount();
            
            //  if the account is closed
            if (account.isAccountClosedIndicator()) {
                    putFieldError("accountNumber", KeyConstants.ERROR_DOCUMENT_SUBOBJECTMAINT_ACCOUNT_MAY_NOT_BE_CLOSED);
                    success &= false;
            }
        }
        return success;
    }

}
