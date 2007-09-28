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
package org.kuali.module.gl.batch;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.closing.year.service.YearEndService;
import org.kuali.module.gl.batch.closing.year.service.impl.helper.BalanceForwardRuleHelper;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.util.Summary;

public class BalanceForwardStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceForwardStep.class);
    
    private YearEndService yearEndService;
    private DateTimeService dateTimeService;
    private KualiConfigurationService kualiConfigurationService;
    private OriginEntryGroupService originEntryGroupService;
    
    public static final String TRANSACTION_DATE_FORMAT_STRING = "yyyy-MM-dd";

    public boolean execute(String jobName) {
        Date varTransactionDate;
        try {
            DateFormat transactionDateFormat = new SimpleDateFormat(TRANSACTION_DATE_FORMAT_STRING);
            varTransactionDate = new Date(transactionDateFormat.parse(kualiConfigurationService.getParameterValue(KFSConstants.GL_NAMESPACE, KFSConstants.Components.BATCH, GLConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM)).getTime());
        }
        catch (ParseException e) {
            LOG.error("forwardBalances() Unable to parse transaction date", e);
            throw new IllegalArgumentException("Unable to parse transaction date");
        }

        Integer varFiscalYear = new Integer(kualiConfigurationService.getParameterValue(KFSConstants.GL_NAMESPACE, KFSConstants.Components.BATCH, GLConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));
        
        yearEndService.logAllMissingPriorYearAccounts(varFiscalYear);
        yearEndService.logAllMissingSubFundGroups(varFiscalYear);

        OriginEntryGroup balanceForwardsUnclosedPriorYearAccountGroup = originEntryGroupService.createGroup(varTransactionDate, OriginEntrySource.YEAR_END_BEGINNING_BALANCE, true, false, true);
        OriginEntryGroup balanceForwardsClosedPriorYearAccountGroup = originEntryGroupService.createGroup(varTransactionDate, OriginEntrySource.YEAR_END_BEGINNING_BALANCE_PRIOR_YEAR, true, false, true);

        BalanceForwardRuleHelper balanceForwardRuleHelper = new BalanceForwardRuleHelper(varFiscalYear, varTransactionDate, balanceForwardsClosedPriorYearAccountGroup, balanceForwardsUnclosedPriorYearAccountGroup);
        
        yearEndService.forwardBalances(balanceForwardsUnclosedPriorYearAccountGroup, balanceForwardsClosedPriorYearAccountGroup, balanceForwardRuleHelper);
        
        yearEndService.generateForwardBalanceReports(balanceForwardsUnclosedPriorYearAccountGroup, balanceForwardsClosedPriorYearAccountGroup, balanceForwardRuleHelper);

        return true;
    }

    public void setYearEndService(YearEndService yearEndService) {
        this.yearEndService = yearEndService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }
}
