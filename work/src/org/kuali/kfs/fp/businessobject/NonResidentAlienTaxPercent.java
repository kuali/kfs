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

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a non-resident alien tax percent.  This is the percentage of a total 
 * reimbursement that is collected in taxes for non-resident aliens.
 */
public class NonResidentAlienTaxPercent extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String incomeClassCode;
    private String incomeTaxTypeCode;
    private boolean active;
    
    private KualiDecimal incomeTaxPercent;

    private TaxIncomeClassCode incomeClass;

    /**
     * Default no-arg constructor.
     */
    public NonResidentAlienTaxPercent() {

    }

    /**
     * Gets the incomeClassCode attribute.
     * 
     * @return Returns the incomeClassCode
     */
    public String getIncomeClassCode() {
        return incomeClassCode;
    }


    /**
     * Sets the incomeClassCode attribute.
     * 
     * @param incomeClassCode The incomeClassCode to set.
     */
    public void setIncomeClassCode(String incomeClassCode) {
        this.incomeClassCode = incomeClassCode;
    }

    /**
     * Gets the incomeTaxTypeCode attribute.
     * 
     * @return Returns the incomeTaxTypeCode
     */
    public String getIncomeTaxTypeCode() {
        return incomeTaxTypeCode;
    }


    /**
     * Sets the incomeTaxTypeCode attribute.
     * 
     * @param incomeTaxTypeCode The incomeTaxTypeCode to set.
     */
    public void setIncomeTaxTypeCode(String incomeTaxTypeCode) {
        this.incomeTaxTypeCode = incomeTaxTypeCode;
    }

    /**
     * Gets the incomeTaxPercent attribute.
     * 
     * @return Returns the incomeTaxPercent
     */
    public KualiDecimal getIncomeTaxPercent() {
        return incomeTaxPercent;
    }


    /**
     * Sets the incomeTaxPercent attribute.
     * 
     * @param incomeTaxPercent The incomeTaxPercent to set.
     */
    public void setIncomeTaxPercent(KualiDecimal incomeTaxPercent) {
        this.incomeTaxPercent = incomeTaxPercent;
    }

    /**
     * Gets the incomeClass attribute.
     * 
     * @return Returns the incomeClass
     */
    public TaxIncomeClassCode getIncomeClass() {
        return incomeClass;
    }


    /**
     * Sets the incomeClass attribute.
     * 
     * @param incomeClass The incomeClass to set.
     * @deprecated
     */
    public void setIncomeClass(TaxIncomeClassCode incomeClass) {
        this.incomeClass = incomeClass;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("incomeClassCode", this.incomeClassCode);
        m.put("incomeTaxTypeCode", this.incomeTaxTypeCode);
        if (this.incomeTaxPercent != null) {
            m.put("incomeTaxPercent", this.incomeTaxPercent.toString());
        }
        return m;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
