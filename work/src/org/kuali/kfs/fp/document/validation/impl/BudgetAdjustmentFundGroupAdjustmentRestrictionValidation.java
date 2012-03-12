/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.fp.document.validation.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentAccountingLine;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Validation for Budget Adjustment document that checks that the fund groups are correctly adjusted.
 */
public class BudgetAdjustmentFundGroupAdjustmentRestrictionValidation extends GenericValidation {
    private BudgetAdjustmentDocument accountingDocumentForValidation;
    AccountingLineRuleHelperService accountingLineRuleHelperService;
    
    /**
     * Retrieves the fund group and sub fund group for each accounting line. Then verifies that the codes associated with the
     * 'Budget Adjustment Restriction Code' field are met.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        MessageMap errors = GlobalVariables.getMessageMap();

        boolean isAdjustmentAllowed = true;

        List accountingLines = new ArrayList();
        accountingLines.addAll(getAccountingDocumentForValidation().getSourceAccountingLines());
        accountingLines.addAll(getAccountingDocumentForValidation().getTargetAccountingLines());

        // fund group is global restriction
        boolean restrictedToSubFund = false;
        boolean restrictedToChart = false;
        boolean restrictedToOrg = false;
        boolean restrictedToAccount = false;

        // fields to help with error messages
        String accountRestrictingSubFund = "";
        String accountRestrictingChart = "";
        String accountRestrictingOrg = "";
        String accountRestrictingAccount = "";

        // first find the restriction level required by the fund or sub funds used on document
        String restrictionLevel = "";
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            SubFundGroup subFund = line.getAccount().getSubFundGroup();
            if (!KFSConstants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_NONE.equals(subFund.getFundGroupBudgetAdjustmentRestrictionLevelCode())) {
                restrictionLevel = subFund.getFundGroupBudgetAdjustmentRestrictionLevelCode();
                restrictedToSubFund = true;
                accountRestrictingSubFund = line.getAccountNumber();
            }
            else {
                restrictionLevel = subFund.getFundGroup().getFundGroupBudgetAdjustmentRestrictionLevelCode();
            }

            if (KFSConstants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_CHART.equals(restrictionLevel)) {
                restrictedToChart = true;
                accountRestrictingChart = line.getAccountNumber();
            }
            else if (KFSConstants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_ORGANIZATION.equals(restrictionLevel)) {
                restrictedToOrg = true;
                accountRestrictingOrg = line.getAccountNumber();
            }
            else if (KFSConstants.BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_ACCOUNT.equals(restrictionLevel)) {
                restrictedToAccount = true;
                accountRestrictingAccount = line.getAccountNumber();
            }

            // if we have a sub fund restriction, this overrides anything coming later
            if (restrictedToSubFund) {
                break;
            }
        }

        String fundLabel = accountingLineRuleHelperService.getFundGroupCodeLabel();
        String subFundLabel = accountingLineRuleHelperService.getSubFundGroupCodeLabel();
        String chartLabel = accountingLineRuleHelperService.getChartLabel();
        String orgLabel = accountingLineRuleHelperService.getOrganizationCodeLabel();
        String acctLabel = accountingLineRuleHelperService.getAccountLabel();

        /*
         * now iterate through the accounting lines again and check each record against the previous to verify the restrictions are
         * met
         */
        BudgetAdjustmentAccountingLine previousLine = null;
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();

            if (previousLine != null) {
                String currentFundGroup = line.getAccount().getSubFundGroup().getFundGroupCode();
                String previousFundGroup = previousLine.getAccount().getSubFundGroup().getFundGroupCode();

                if (!currentFundGroup.equals(previousFundGroup)) {
                    errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_MIXED_FUND_GROUPS);
                    isAdjustmentAllowed = false;
                    break;
                }

                if (restrictedToSubFund) {
                    if (!line.getAccount().getSubFundGroupCode().equals(previousLine.getAccount().getSubFundGroupCode())) {
                        errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingSubFund, subFundLabel });
                        isAdjustmentAllowed = false;
                        break;
                    }
                }

                if (restrictedToChart) {
                    if (!line.getChartOfAccountsCode().equals(previousLine.getChartOfAccountsCode())) {
                        if (restrictedToSubFund) {
                            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingChart, subFundLabel + " and " + chartLabel });
                        }
                        else {
                            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingChart, fundLabel + " and " + chartLabel });
                        }
                        isAdjustmentAllowed = false;
                        break;
                    }
                }

                if (restrictedToOrg) {
                    if (!line.getAccount().getOrganizationCode().equals(previousLine.getAccount().getOrganizationCode())) {
                        if (restrictedToSubFund) {
                            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingOrg, subFundLabel + " and " + orgLabel });
                        }
                        else {
                            errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingOrg, fundLabel + " and " + orgLabel });
                        }
                        isAdjustmentAllowed = false;
                        break;
                    }
                }

                if (restrictedToAccount) {
                    if (!line.getAccountNumber().equals(previousLine.getAccountNumber())) {
                        errors.putErrorWithoutFullErrorPath(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BA_RESTRICTION_LEVELS, new String[] { accountRestrictingAccount, acctLabel });
                        isAdjustmentAllowed = false;
                        break;
                    }
                }
            }

            previousLine = line;
        }

        return isAdjustmentAllowed;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public BudgetAdjustmentDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(BudgetAdjustmentDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the accountingLineRuleHelperService attribute. 
     * @return Returns the accountingLineRuleHelperService.
     */
    public AccountingLineRuleHelperService getAccountingLineRuleHelperService() {
        return accountingLineRuleHelperService;
    }

    /**
     * Sets the accountingLineRuleHelperService attribute value.
     * @param accountingLineRuleHelperService The accountingLineRuleHelperService to set.
     */
    public void setAccountingLineRuleHelperService(AccountingLineRuleHelperService accountingLineRuleHelperService) {
        this.accountingLineRuleHelperService = accountingLineRuleHelperService;
    }
}
