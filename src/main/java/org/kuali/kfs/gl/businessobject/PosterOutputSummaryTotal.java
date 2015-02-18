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

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class PosterOutputSummaryTotal extends TransientBusinessObjectBase implements PosterOutputSummaryAmountHolder {
    private KualiDecimal creditAmount;
    private KualiDecimal debitAmount;
    private KualiDecimal budgetAmount;
    private KualiDecimal netAmount;
    
    private String objectTypeCode;
    
    private final String[] assetExpenseObjectTypeCodes;
    
    public PosterOutputSummaryTotal() {
        creditAmount = KualiDecimal.ZERO;
        debitAmount = KualiDecimal.ZERO;
        budgetAmount = KualiDecimal.ZERO;
        netAmount = KualiDecimal.ZERO;
        
        ObjectTypeService objectTypeService = (ObjectTypeService) SpringContext.getBean(ObjectTypeService.class);
        List<String> objectTypes = objectTypeService.getCurrentYearExpenseObjectTypes();
        objectTypes.add(objectTypeService.getCurrentYearAssetObjectType());

        assetExpenseObjectTypeCodes = objectTypes.toArray(new String[0]);
    }
    
    /**
     * This method sets the amounts for this poster output summary entry.
     * 
     * @param debitCreditCode credit code used to determine whether amounts is debit or credit
     * @param objectTypeCode object type code associated with amount
     * @param amount amount to add
     */
    public void addAmount(String debitCreditCode, String objectTypeCode, KualiDecimal amount) {

        if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            creditAmount = creditAmount.add(amount);
            if (ArrayUtils.contains(assetExpenseObjectTypeCodes, objectTypeCode)) {
                netAmount = netAmount.subtract(amount);
            }
            else {
                netAmount = netAmount.add(amount);
            }
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(debitCreditCode)) {
            debitAmount = debitAmount.add(amount);
            if (ArrayUtils.contains(assetExpenseObjectTypeCodes, objectTypeCode)) {
                netAmount = netAmount.add(amount);
            }
            else {
                netAmount = netAmount.subtract(amount);
            }
        }
        else {
            netAmount = netAmount.add(amount);
            budgetAmount = budgetAmount.add(amount);
        }
    }
    
    /**
     * Adds the totals from the entry to the totals this total line carries
     * @param entry the entry to add totals from
     */
    public void addAmount(PosterOutputSummaryEntry entry) {
        debitAmount = debitAmount.add(entry.getDebitAmount());
        creditAmount = creditAmount.add(entry.getCreditAmount());
        budgetAmount = budgetAmount.add(entry.getBudgetAmount());
        netAmount = netAmount.add(entry.getNetAmount());
    }
    
    public KualiDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public KualiDecimal getCreditAmount() {
        return creditAmount;
    }

    public KualiDecimal getDebitAmount() {
        return debitAmount;
    }
    
    public KualiDecimal getNetAmount() {
        return netAmount;
    }
    
    public String getObjectTypeCode() {
        return objectTypeCode;
    }

    public void setObjectTypeCode(String objectTypeCode) {
        this.objectTypeCode = objectTypeCode;
    }
    
    /**
     * @return a summary of this total line
     */
    public String getSummary() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.MESSAGE_REPORT_POSTER_OUTPUT_SUMMARY_TOTAL);
    }

    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("objectTypeCode",this.getObjectTypeCode());
        pks.put("creditAmount",this.getCreditAmount());
        pks.put("debitAmount",this.getDebitAmount());
        pks.put("budgetAmount",this.getBudgetAmount());
        pks.put("netAmount",this.getNetAmount());
        return pks;
    }

}
