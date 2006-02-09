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
package org.kuali.module.financial.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.financial.bo.BankAccount;

public class BankAccountRule extends MaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        // TODO Auto-generated method stub
        return super.processCustomApproveDocumentBusinessRules(document);
    }

    /**
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        //default to success
        boolean success = true;
        
        success &= checkSubObjectExistence(document);
        return success;
    }

    /**
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // TODO Auto-generated method stub
        return super.processCustomSaveDocumentBusinessRules(document);
    }
    
    /**
     * 
     * This method...
     * @param document
     * @return
     */
    private boolean checkSubObjectExistence(MaintenanceDocument document) {
        //default to success
        boolean success = true; 
        BankAccount newBankAcct = (BankAccount)document.getNewMaintainableObject().getBusinessObject();
        newBankAcct.refresh();
        //existence check on bank code
        
        
        if (StringUtils.isNotEmpty(newBankAcct.getFinancialDocumentBankCode())) {
            if (ObjectUtils.isNull(newBankAcct.getBank())) {
                success &= false;
                putFieldError("financialDocumentBankCode", KeyConstants.ERROR_DOCUMENT_BANKACCMAINT_INVALID_BANK);
            } 
        }
        
        
        //existence check on organization if data was entered
        if(StringUtils.isNotEmpty(newBankAcct.getChartOfAccountsCode()) &&
                StringUtils.isNotEmpty(newBankAcct.getOrganizationCode())) {
            if (ObjectUtils.isNull(newBankAcct.getOrganization())) {
                success &= false;
                putFieldError("organizationCode", KeyConstants.ERROR_DOCUMENT_BANKACCMAINT_INVALID_ORG);
            } else if(!newBankAcct.getOrganization().isOrganizationActiveIndicator()){
                success &= false;
                putFieldError("organizationCode", KeyConstants.ERROR_DOCUMENT_BANKACCMAINT_INACTIVE_ORG);
            }
        }
        
        //existence check on university account if data was entered
        if(StringUtils.isNotEmpty(newBankAcct.getUniversityAcctChartOfAcctCd()) &&
                StringUtils.isNotEmpty(newBankAcct.getUniversityAccountNumber())) {
            if (ObjectUtils.isNull(newBankAcct.getUniversityAccount())) {
                success &= false;
                putFieldError("universityAccountNumber", KeyConstants.ERROR_DOCUMENT_BANKACCMAINT_INVALID_UNIV_ACCT);
            } else if(newBankAcct.getUniversityAccount().isAccountClosedIndicator()){
                success &= false;
                putFieldError("universityAccountNumber", KeyConstants.ERROR_DOCUMENT_BANKACCMAINT_INACTIVE_UNIV_ACCT);
            }
        }
        
        return success;
    }

}
