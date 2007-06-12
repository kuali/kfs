/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.batch.closing.year.service.impl.helper;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.PriorYearAccount;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.A21SubAccountService;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.chart.service.SubFundGroupService;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.util.FatalErrorException;
import org.kuali.module.gl.util.ObjectHelper;

/**
 * A helper class which contains the more complicated logic involved in the year end encumbrance closing process. This logic is
 * likely going to need to be modular which is why it's in its own class.
 * 
 */

public class EncumbranceClosingRuleHelper {

    static private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceClosingRuleHelper.class);

    private A21SubAccountService a21SubAccountService;
    private PriorYearAccountService priorYearAccountService;
    private SubFundGroupService subFundGroupService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * Field accessor for A21SubAccountService.
     * 
     * @param a21SubAccountService
     */
    public void setA21SubAccountService(A21SubAccountService a21SubAccountService) {
        this.a21SubAccountService = a21SubAccountService;
    }

    /**
     * Field accessor for PriorYearAccountService.
     * 
     * @param priorYearAccountService
     */
    public void setPriorYearAccountService(PriorYearAccountService priorYearAccountService) {
        this.priorYearAccountService = priorYearAccountService;
    }

    /**
     * Field accessor for SubFundGroupService.
     * 
     * @param subFundGroupService
     */
    public void setSubFundGroupService(SubFundGroupService subFundGroupService) {
        this.subFundGroupService = subFundGroupService;
    }

    /**
     * Determine whether or not an encumbrance should be carried forward from one fiscal year to the next.
     * 
     * @param encumbrance
     * @return true if the encumbrance should be rolled forward from the closing fiscal year to the opening fiscal year.
     */
    public boolean anEntryShouldBeCreatedForThisEncumbrance(Encumbrance encumbrance) {

        // null guard
        if (null == encumbrance) {

            return false;

        }

        // internal encumbrance or labor distribution
        if (KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE.equals(encumbrance.getBalanceTypeCode()) && KFSConstants.LABOR_DISTRIBUTION_ORIGIN_CODE.equals(encumbrance.getOriginCode())) {
            return false;

        }

        if (KFSConstants.BALANCE_TYPE_COST_SHARE_ENCUMBRANCE.equals(encumbrance.getBalanceTypeCode())) {

            return false;

        }

        // closed encumbrances aren't carried forward
        if (ObjectHelper.isOneOf(encumbrance.getBalanceTypeCode(), new String[] { KFSConstants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE, KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE })) {

            return isEncumbranceClosed(encumbrance);

        }

        // pre-encumbrances
        if (KFSConstants.BALANCE_TYPE_PRE_ENCUMBRANCE.equals(encumbrance.getBalanceTypeCode())) {

            PriorYearAccount priorYearAccount = priorYearAccountService.getByPrimaryKey(encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber());

            // the account on the encumbrance must be valid
            if (null == priorYearAccount) {

                LOG.info("No prior year account for chart \"" + encumbrance.getChartOfAccountsCode() + "\" and account \"" + encumbrance.getAccountNumber() + "\"");

                return false;

            }

            // the sub fund group must exist for the prior year account and the
            // encumbrance must not be closed.
            if (priorYearAccount.isForContractsAndGrants()) {
                return isEncumbranceClosed(encumbrance);

            }
            else {

                return false;

            }

        }

        return false;

    }

    /**
     * Determine whether or not the encumbrance has been fully relieved.
     * 
     * @param encumbrance
     * @return true if the amount closed on the encumbrance is NOT equal to the amount of the encumbrance itself, e.g. if the
     *         encumbrance has not yet been paid off.
     */
    private boolean isEncumbranceClosed(Encumbrance encumbrance) {

        if (encumbrance.getAccountLineEncumbranceAmount().doubleValue() == encumbrance.getAccountLineEncumbranceClosedAmount().doubleValue()) {

            return false;

        }

        return true;

    }

    /**
     * Do some validation and make sure that the encumbrance A21SubAccount is a cost share sub-account.
     * 
     * @param entry
     * @param offset
     * @param encumbrance
     * @param objectTypeCode
     * @return true if the encumbrance is eligible for cost share.
     * @throws FatalErrorException
     */
    public boolean isEncumbranceEligibleForCostShare(OriginEntry entry, OriginEntry offset, Encumbrance encumbrance, String objectTypeCode) throws FatalErrorException {

        PriorYearAccount priorYearAccount = priorYearAccountService.getByPrimaryKey(encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber());

        // the sub fund group for the prior year account must exist.
        String subFundGroupCode = null;
        if (null != priorYearAccount) {

            subFundGroupCode = priorYearAccount.getSubFundGroupCode();

        }
        else {

            // this message was carried over from the cobol.
            throw new FatalErrorException("ERROR ACCESSING PRIOR YR ACCT TABLE FOR " + encumbrance.getAccountNumber());

        }

        SubFundGroup subFundGroup = subFundGroupService.getByPrimaryId(subFundGroupCode);

        if (null != subFundGroup) {

            if (!priorYearAccount.isForContractsAndGrants()) {

                return false;

            }

        }
        else {

            throw new FatalErrorException("ERROR ACCESSING SUB FUND GROUP TABLE FOR " + subFundGroupCode);

        }

        // I think this is redundant to the statement a few lines above here.
        // In any case, the sub fund group must not be contracts and grants.
        if (!priorYearAccount.isForContractsAndGrants()) {

            return false;

        }

        String[] expenseObjectCodeTypes = kualiConfigurationService.getApplicationParameterValues("SYSTEM", "ExpenseObjectTypeCodes");
        String[] encumbranceBalanceTypeCodes = { "EX","IE","PE" };

        // the object type code must be an expense and the encumbrance balance type code must correspond to an internal, external or
        // pre-encumbrance
        if (!(ArrayUtils.contains(expenseObjectCodeTypes, objectTypeCode) && ArrayUtils.contains(encumbranceBalanceTypeCodes, encumbrance.getBalanceTypeCode()))) {

            return false;

        }
        else {

            A21SubAccount a21SubAccount = a21SubAccountService.getByPrimaryKey(encumbrance.getChartOfAccountsCode(), encumbrance.getAccountNumber(), encumbrance.getSubAccountNumber());

            if (null == a21SubAccount) {

                // Error message carried over from cobol. not very well descriptive.
                // Just indicates that the a21 sub account doesn't exist.
                throw new FatalErrorException("ERROR ACCESSING A21 SUB ACCOUNT TABLE FOR ENCUMBRANCE "+encumbrance.getChartOfAccountsCode()+"-"+encumbrance.getAccountNumber()+" "+encumbrance.getSubAccountNumber());

            }

            // everything is valid, return true if the a21 sub account is a cost share sub-account
            return KFSConstants.COST_SHARE.equals(a21SubAccount.getSubAccountTypeCode());

        }

    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
