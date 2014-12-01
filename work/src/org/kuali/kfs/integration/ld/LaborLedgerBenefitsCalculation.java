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
package org.kuali.kfs.integration.ld;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

public interface LaborLedgerBenefitsCalculation extends PersistableBusinessObject, ExternalizableBusinessObject {

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public abstract Integer getUniversityFiscalYear();

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public abstract void setUniversityFiscalYear(Integer universityFiscalYear);

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public abstract String getChartOfAccountsCode();

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public abstract void setChartOfAccountsCode(String chartOfAccountsCode);

    /**
     * Gets the positionBenefitTypeCode attribute.
     * 
     * @return Returns the positionBenefitTypeCode
     */
    public abstract String getPositionBenefitTypeCode();

    /**
     * Sets the positionBenefitTypeCode attribute.
     * 
     * @param positionBenefitTypeCode The positionBenefitTypeCode to set.
     */
    public abstract void setPositionBenefitTypeCode(String positionBenefitTypeCode);

    /**
     * Gets the positionFringeBenefitPercent attribute.
     * 
     * @return Returns the positionFringeBenefitPercent
     */
    public abstract KualiPercent getPositionFringeBenefitPercent();

    /**
     * Sets the positionFringeBenefitPercent attribute.
     * 
     * @param positionFringeBenefitPercent The positionFringeBenefitPercent to set.
     */
    public abstract void setPositionFringeBenefitPercent(KualiPercent positionFringeBenefitPercent);

    /**
     * Gets the positionFringeBenefitObjectCode attribute.
     * 
     * @return Returns the positionFringeBenefitObjectCode
     */
    public abstract String getPositionFringeBenefitObjectCode();

    /**
     * Sets the positionFringeBenefitObjectCode attribute.
     * 
     * @param positionFringeBenefitObjectCode The positionFringeBenefitObjectCode to set.
     */
    public abstract void setPositionFringeBenefitObjectCode(String positionFringeBenefitObjectCode);

    /**
     * Gets the positionFringeBenefitObject attribute.
     * 
     * @return Returns the positionFringeBenefitObject
     */
    public abstract ObjectCode getPositionFringeBenefitObject();

    /**
     * Sets the positionFringeBenefitObject attribute.
     * 
     * @param positionFringeBenefitObject The positionFringeBenefitObject to set.
     */
    @Deprecated
    public abstract void setPositionFringeBenefitObject(ObjectCode positionFringeBenefitObject);

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public abstract Chart getChartOfAccounts();

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public abstract void setChartOfAccounts(Chart chartOfAccounts);

    /**
     * Gets the laborLedgerBenefitsType attribute.
     * 
     * @return Returns the laborLedgerBenefitsType.
     */
    public abstract LaborLedgerBenefitsType getLaborLedgerBenefitsType();

    /**
     * Sets the laborLedgerBenefitsType attribute value.
     * 
     * @param laborLedgerBenefitsType The positionBenefitType to set.
     */
    @Deprecated
    public abstract void setLaborLedgerBenefitsType(LaborLedgerBenefitsType laborLedgerBenefitsType);

    /**
     * Gets the universityFiscal attribute.
     * 
     * @return Returns the universityFiscal.
     */
    public abstract SystemOptions getUniversityFiscal();

    /**
     * Sets the universityFiscal attribute value.
     * 
     * @param universityFiscal The universityFiscal to set.
     */
    public abstract void setUniversityFiscal(SystemOptions universityFiscal);

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public abstract boolean isActive();

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public abstract void setActive(boolean active);

}
