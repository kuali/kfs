/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.gl.businessobject;

import java.text.MessageFormat;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KualiConfigurationService;

/**
 * Holds summary information for the Pending Entry Ledger report
 */
public class NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine extends NightlyOutPendingEntryLedgerSummaryTotalLine {
    private String financialBalanceTypeCode;
    
    /**
     * Constructs a NightlyOutServiceImpl
     * @param balanceType the balance type summarized by this total line summarizer
     */
    public NightlyOutPendingEntryLedgerBalanceTypeSummaryTotalLine(String balanceType) {
        this.financialBalanceTypeCode = balanceType;
    }

    /**
     * Gets the balanceType attribute. 
     * @return Returns the balanceType.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }
    
    /**
     * @return the summary for this balance type summary total line
     */
    public String getSummary() {
        final String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.MESSAGE_REPORT_NIGHTLY_OUT_LEDGER_BALANCE_TYPE_TOTAL);
        return MessageFormat.format(message, financialBalanceTypeCode);
    }
}
