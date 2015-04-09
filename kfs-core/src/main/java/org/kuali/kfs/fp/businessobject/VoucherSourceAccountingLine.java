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
package org.kuali.kfs.fp.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * Special case <code>{@link SourceAccountingLine}</code> type for <code>{@link org.kuali.kfs.fp.document.VoucherDocument}</code>
 */
public class VoucherSourceAccountingLine extends SourceAccountingLine {
    private String objectTypeCode;
    private ObjectType objectType;

    /**
     * Constructs a VoucherSourceAccountingLine.java.
     */
    public VoucherSourceAccountingLine() {
        super();

        // default is debit. This is important for single sided accounting lines (example: JV w/BB) so that
        // totals get calculated correctly
        this.setDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
    }

    /**
     * Gets the objectType attribute.
     * 
     * @return Returns the objectType.
     */
    
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * Sets the objectType attribute value.
     * 
     * @param objectType The objectType to set.
     */
    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    /**
     * Gets the objectTypeCode attribute.
     * 
     * @return Returns the objectTypeCode.
     */
    @Override
    public String getObjectTypeCode() {
        return objectTypeCode;
    }

    /**
     * Sets the objectTypeCode attribute value.
     * 
     * @param objectTypeCode The objectTypeCode to set.
     */
    public void setObjectTypeCode(String objectTypeCode) {
        this.objectTypeCode = objectTypeCode;
    }

    /**
     * Overridden to automatically set the object type code on the setting of the object code - if the object type code is blank
     * 
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#setFinancialObjectCode(java.lang.String)
     */
    @Override
    public void setFinancialObjectCode(String financialObjectCode) {
        super.setFinancialObjectCode(financialObjectCode);
        if (StringUtils.isBlank(getObjectTypeCode()) && !StringUtils.isBlank(getFinancialObjectCode())) {
            refreshReferenceObject("objectCode");
            if (!ObjectUtils.isNull(getObjectCode())) {
                setObjectTypeCode(getObjectCode().getFinancialObjectTypeCode());
            }
        }
    }

}
