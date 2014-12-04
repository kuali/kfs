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
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * Represents a poster output summary line which holds values for a specific balance type
 */
public class PosterOutputSummaryBalanceTypeTotal extends PosterOutputSummaryTotal {
    private String balanceTypeCode;
    
    /**
     * Constructs a PosterOutputSummaryBalanceTypeTotal
     * @param balanceTypeCode the balance type code totalled by this totaller
     */
    public PosterOutputSummaryBalanceTypeTotal(String balanceTypeCode) {
        super();
        this.balanceTypeCode = balanceTypeCode;
    }
    
    /**
     * @return the balance type code associated with this total line
     */
    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }
    
    /**
     * Returns the summary for this total line
     * @see org.kuali.kfs.gl.businessobject.PosterOutputSummaryTotal#getSummary()
     */
    
    public String getSummary() {
        final String message = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(getSummaryMessageName());
        return MessageFormat.format(message, (Object[])getSummaryMessageParameters());
    }
    
    /**
     * @return the name of the summary message
     */
    protected String getSummaryMessageName() {
        return KFSKeyConstants.MESSAGE_REPORT_POSTER_OUTPUT_SUMMARY_BALANCE_TYPE_TOTAL;
    }
    
    /**
     * @return the values that should be formatted into the message
     */
    protected String[] getSummaryMessageParameters() {
        return new String[] { getBalanceTypeCode() };
    }
    
    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("balanceTypeCode",this.getBalanceTypeCode());
        pks.put("objectTypeCode",this.getObjectTypeCode());
        pks.put("creditAmount",this.getCreditAmount());
        pks.put("debitAmount",this.getDebitAmount());
        pks.put("budgetAmount",this.getBudgetAmount());
        pks.put("netAmount",this.getNetAmount());
        return pks;
    }
}
