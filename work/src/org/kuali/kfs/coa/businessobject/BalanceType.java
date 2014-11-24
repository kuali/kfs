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
package org.kuali.kfs.coa.businessobject;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.KualiCodeBase;


/**
 * This class is the business object for the Balance Type object.
 */
public class BalanceType extends KualiCodeBase implements MutableInactivatable {

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "BalanceType";

    protected String financialBalanceTypeShortNm;
    protected boolean financialOffsetGenerationIndicator;
    protected boolean finBalanceTypeEncumIndicator;

    /**
     * Constructs a BalanceTyp.java.
     */
    public BalanceType() {
        super.setActive(true); // always active, plus no column in the table
    }

    /**
     * @param typeCode
     */
    public BalanceType(String typeCode) {
        this();
        setCode(typeCode);
    }

    /**
     * @return Returns the financialBalanceTypeName.
     */

    public String getFinancialBalanceTypeName() {
        return this.getName();
    }

    /**
     * @param financialBalanceTypeName The financialBalanceTypeName to set.
     */
    public void setFinancialBalanceTypeName(String financialBalanceTypeName) {
        this.setName(financialBalanceTypeName);
    }

    /**
     * @return Returns the financialBalanceTypeCode.
     */

    public String getFinancialBalanceTypeCode() {
        return this.getCode();
    }

    /**
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.setCode(financialBalanceTypeCode);
    }

    /**
     * @return Returns the finBalanceTypeEncumIndicator.
     */
    public boolean isFinBalanceTypeEncumIndicator() {
        return finBalanceTypeEncumIndicator;
    }

    /**
     * @param finBalanceTypeEncumIndicator The finBalanceTypeEncumIndicator to set.
     */
    public void setFinBalanceTypeEncumIndicator(boolean finBalanceTypeEncumIndicator) {
        this.finBalanceTypeEncumIndicator = finBalanceTypeEncumIndicator;
    }

    /**
     * @return Returns the financialBalanceTypeShortNm.
     */

    public String getFinancialBalanceTypeShortNm() {
        return financialBalanceTypeShortNm;
    }

    /**
     * @param financialBalanceTypeShortNm The financialBalanceTypeShortNm to set.
     */
    public void setFinancialBalanceTypeShortNm(String financialBalanceTypeShortNm) {
        this.financialBalanceTypeShortNm = financialBalanceTypeShortNm;
    }

    /**
     * @return Returns the financialOffsetGenerationIndicator.
     */

    public boolean isFinancialOffsetGenerationIndicator() {
        return financialOffsetGenerationIndicator;
    }

    /**
     * @param financialOffsetGenerationIndicator The financialOffsetGenerationIndicator to set.
     */
    public void setFinancialOffsetGenerationIndicator(boolean financialOffsetGenerationIndicator) {
        this.financialOffsetGenerationIndicator = financialOffsetGenerationIndicator;
    }

}
