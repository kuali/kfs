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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubAccount;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class SubAccountPreRules extends MaintenancePreRulesBase {

    private SubAccount newSubAccount;

    // private SubAccount copyAccount;

    public SubAccountPreRules() {

    }

    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");

        copyICRFromAccount(document);

        return true;
    }

    private void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");

        if (StringUtils.isNotBlank(newSubAccount.getAccountNumber())) {
            Account account = checkForContinuationAccount("Account Number", newSubAccount.getChartOfAccountsCode(), newSubAccount.getAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newSubAccount.setAccountNumber(account.getAccountNumber());
                newSubAccount.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newSubAccount = (SubAccount) document.getNewMaintainableObject().getBusinessObject();
        // copyAccount = (SubAccount) ObjectUtils.deepCopy(newAccount);
        // copyAccount.refresh();
    }

    private void copyICRFromAccount(MaintenanceDocument document) {
        KualiUser user = GlobalVariables.getUserSession().getKualiUser();

        // get the correct documentAuthorizer for this document
        MaintenanceDocumentAuthorizer documentAuthorizer = (MaintenanceDocumentAuthorizer) getDocumentAuthorizationService().getDocumentAuthorizer(document);

        // get a new instance of MaintenanceDocumentAuthorizations for this context
        MaintenanceDocumentAuthorizations auths = documentAuthorizer.getFieldAuthorizations(document, user);

        // don't need to copy if the user does not have the authority to edit the fields
        if (!auths.getAuthFieldAuthorization("a21SubAccount.financialIcrSeriesIdentifier").isReadOnly()) {
            // only need to do this of the account sub type is EX
            A21SubAccount a21SubAccount = newSubAccount.getA21SubAccount();
            Account account = newSubAccount.getAccount();
            if (a21SubAccount.getSubAccountTypeCode().equals(SubAccountRule.CG_A21_TYPE_ICR)) {
                if (account == null || StringUtils.isBlank(account.getAccountNumber())) {
                    account = getAccountService().getByPrimaryId(newSubAccount.getChartOfAccountsCode(), newSubAccount.getAccountNumber());
                    if (ObjectUtils.isNotNull(account)) {
                        if (StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryAccountNumber())) {
                            a21SubAccount.setIndirectCostRecoveryAccountNumber(account.getIndirectCostRecoveryAcctNbr());
                            a21SubAccount.setIndirectCostRecoveryChartOfAccountsCode(account.getIndirectCostRcvyFinCoaCode());
                        }
                        if (StringUtils.isBlank(a21SubAccount.getFinancialIcrSeriesIdentifier())) {
                            a21SubAccount.setFinancialIcrSeriesIdentifier(account.getFinancialIcrSeriesIdentifier());
                            a21SubAccount.setOffCampusCode(account.isAccountOffCampusIndicator());
                        }
                        if (StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryTypeCode())) {
                            a21SubAccount.setIndirectCostRecoveryTypeCode(account.getAcctIndirectCostRcvyTypeCd());
                        }
                    }
                }
            }
        }
    }
}
