/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.lookup.keyvalues.FiscalYearComparator;
import org.kuali.kfs.lookup.valuefinder.FiscalYearFinder;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.IcrAutomatedEntry;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.SubFundGroupService;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.service.impl.FiscalYearFunctionControlServiceImpl;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 * 
 * 
 */
public class AccountingPeriodRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingPeriodRule.class);

    private static final String ACCT_PREFIX_RESTRICTION = "Account.PrefixRestriction";
    private static final String ACCT_CAPITAL_SUBFUNDGROUP = "Account.CapitalSubFundGroup";

    private static final String GENERAL_FUND_CD = "GF";
    private static final String RESTRICTED_FUND_CD = "RF";
    private static final String ENDOWMENT_FUND_CD = "EN";
    private static final String PLANT_FUND_CD = "PF";

    private static final String RESTRICTED_CD_RESTRICTED = "R";
    private static final String RESTRICTED_CD_UNRESTRICTED = "U";
    private static final String RESTRICTED_CD_TEMPORARILY_RESTRICTED = "T";
    private static final String SUB_FUND_GROUP_MEDICAL_PRACTICE_FUNDS = "MPRACT";
    private static final String BUDGET_RECORDING_LEVEL_MIXED = "M";

    private AccountingPeriod oldAccountingPeriod;
    private AccountingPeriod newAccountingPeriod;

    public AccountingPeriodRule() {
    }

    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    public void setupConvenienceObjects() {

        // setup oldAccountingPeriod convenience objects, make sure all possible sub-objects are populated
        oldAccountingPeriod = (AccountingPeriod) super.getOldBo();

        // setup newAccountingPeriod convenience objects, make sure all possible sub-objects are populated
        newAccountingPeriod = (AccountingPeriod) super.getNewBo();
    }

    /**
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        
        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();
        
        Boolean foundYear = false;
        
        KeyValuesService boService = SpringServiceLocator.getKeyValuesService();
        List optionList = (List) boService.findAll(Options.class);
        for (Iterator iter = optionList.iterator(); iter.hasNext();) {
            Options options = (Options) iter.next();
            if (options.getUniversityFiscalYear().compareTo(newAccountingPeriod.getUniversityFiscalYear()) == 0) {
                foundYear = true;
                break;
            }
        }
       
        if (!foundYear) {
            // display an error
            putFieldError("universityFiscalYear", KFSKeyConstants.ERROR_DOCUMENT_FISCAL_PERIOD_YEAR_DOESNT_EXIST);
        }

        return foundYear;
    }

}