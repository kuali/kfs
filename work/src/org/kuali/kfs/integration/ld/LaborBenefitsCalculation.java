/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.integration.ld;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;


public interface LaborBenefitsCalculation extends ExternalizableBusinessObject  {

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
     * Gets the positionBenefitType attribute.
     * 
     * @return Returns the positionBenefitType.
     */
   // public abstract BenefitsType getPositionBenefitType();

    /**
     * Sets the positionBenefitType attribute value.
     * 
     * @param positionBenefitType The positionBenefitType to set.
     */
    @Deprecated
    //public abstract void setPositionBenefitType(BenefitsType positionBenefitType);

    /**
     * Gets the laborObject
     * 
     * @return Returns the laborObject.
     */
    //public abstract LaborObject getLaborObject();

    /**
     * Sets the laborObject
     * 
     * @param laborObject The laborObject to set.
     */
   // @Deprecated
    //public abstract void setLaborObject(LaborObject laborObject);

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
     * This method (a hack by any other name...) returns a string so that an Labor Benefits Calculation can have a link to view its own
     * inquiry page after a look up
     * 
     * @return the String "View Labor Benefits Calculation"
     */
    public abstract String getLaborBenefitsCalculationViewer();

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

    /**
     * @see org.kuali.kfs.integration.businessobject.LaborLedgerBenefitsCalculation#getLaborLedgerBenefitsType()
     */
    public abstract LaborLedgerBenefitsType getLaborLedgerBenefitsType();

    /**
     * @see org.kuali.kfs.integration.businessobject.LaborLedgerBenefitsCalculation#setLaborLedgerBenefitsType(org.kuali.module.labor.bo.LaborLedgerBenefitsType)
     */
    public abstract void setLaborLedgerBenefitsType(LaborLedgerBenefitsType laborLedgerBenefitsType);

    /**
     * Gets the laborBenefitRateCategory attribute. 
     * @return Returns the laborBenefitRateCategory.
     */
    public abstract LaborBenefitRateCategory getLaborBenefitRateCategory();

    /**
     * Sets the laborBenefitRateCategory attribute value.
     * @param laborBenefitRateCategory The laborBenefitRateCategory to set.
     */
    public abstract void setLaborBenefitRateCategory(LaborBenefitRateCategory laborBenefitRateCategory);

    /**
     * Gets the laborBenefitRateCategoryCode attribute. 
     * @return Returns the laborBenefitRateCategoryCode.
     */
    public abstract String getLaborBenefitRateCategoryCode();

    /**
     * Sets the laborBenefitRateCategoryCode attribute value.
     * @param laborBenefitRateCategoryCode The laborBenefitRateCategoryCode to set.
     */
    public abstract void setLaborBenefitRateCategoryCode(String laborBenefitRateCategoryCode);

}