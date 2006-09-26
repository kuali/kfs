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
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubObjCd;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team ()
 */
public class SubObjectPreRules extends MaintenancePreRulesBase {
    private SubObjCd newAccount;
    private SubObjCd copyAccount;


    public SubObjectPreRules() {

    }

    protected boolean doCustomPreRules(MaintenanceDocument document) {

        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");


        return true;
    }

    private void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");

        if (StringUtils.isNotBlank(newAccount.getAccountNumber())) {
            Account account = checkForContinuationAccount("Account Number", newAccount.getChartOfAccountsCode(), newAccount.getAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newAccount.setAccountNumber(account.getAccountNumber());
                newAccount.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAccount = (SubObjCd) document.getNewMaintainableObject().getBusinessObject();
        copyAccount = (SubObjCd) ObjectUtils.deepCopy(newAccount);
        copyAccount.refresh();
    }
}
