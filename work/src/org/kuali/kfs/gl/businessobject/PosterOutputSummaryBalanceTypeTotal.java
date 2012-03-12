/*
 * Copyright 2009 The Kuali Foundation
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
