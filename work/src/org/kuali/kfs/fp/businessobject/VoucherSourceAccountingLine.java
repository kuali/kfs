/*
 * Copyright 2007 The Kuali Foundation
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
