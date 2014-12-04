/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.gl.businessobject;

import java.text.MessageFormat;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * Holds summary information for the Ledger report
 */
public class LedgerBalanceTypeSummaryTotalLine extends LedgerSummaryTotalLine {
    private String financialBalanceTypeCode;
    
    /**
     * Constructs a LedgerBalanceTypeSummaryTotalLine
     * @param balanceType the balance type summarized by this total line summarizer
     */
    public LedgerBalanceTypeSummaryTotalLine(String balanceType) {
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
        final String message = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.MESSAGE_REPORT_NIGHTLY_OUT_LEDGER_BALANCE_TYPE_TOTAL);
        return MessageFormat.format(message, financialBalanceTypeCode);
    }
}
