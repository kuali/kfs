/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.ld.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.ld.LaborLedgerBenefitsCalculation;
import org.kuali.kfs.integration.ld.LaborLedgerBenefitsType;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Labor business object for Benefits Calculation.
 */
public class BenefitsCalculation extends PersistableBusinessObjectBase implements LaborLedgerBenefitsCalculation, MutableInactivatable, FiscalYearBasedBusinessObject {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String positionBenefitTypeCode;
    private KualiPercent positionFringeBenefitPercent;
    private String positionFringeBenefitObjectCode;
    private ObjectCode positionFringeBenefitObject;
    private Chart chartOfAccounts;
    private BenefitsType positionBenefitType;
    private transient SystemOptions universityFiscal;
    private boolean active;
    private LaborObject laborObject;
    private LaborBenefitRateCategory laborBenefitRateCategory;
    private String laborBenefitRateCategoryCode;
    private Account laborAccountOffset;
    private ObjectCode laborObjectCodeOffset;
    
    private String accountCodeOffset;
    private String objectCodeOffset;
   
    /**
     * Default constructor.
     */
    public BenefitsCalculation() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the positionBenefitTypeCode attribute.
     * 
     * @return Returns the positionBenefitTypeCode
     */
    public String getPositionBenefitTypeCode() {
        return positionBenefitTypeCode;
    }

    /**
     * Sets the positionBenefitTypeCode attribute.
     * 
     * @param positionBenefitTypeCode The positionBenefitTypeCode to set.
     */
    public void setPositionBenefitTypeCode(String positionBenefitTypeCode) {
        this.positionBenefitTypeCode = positionBenefitTypeCode;
    }

    /**
     * Gets the positionFringeBenefitPercent attribute.
     * 
     * @return Returns the positionFringeBenefitPercent
     */
    public KualiPercent getPositionFringeBenefitPercent() {
        return positionFringeBenefitPercent;
    }

    /**
     * Sets the positionFringeBenefitPercent attribute.
     * 
     * @param positionFringeBenefitPercent The positionFringeBenefitPercent to set.
     */
    public void setPositionFringeBenefitPercent(KualiPercent positionFringeBenefitPercent) {
        this.positionFringeBenefitPercent = positionFringeBenefitPercent;
    }

    /**
     * Gets the positionFringeBenefitObjectCode attribute.
     * 
     * @return Returns the positionFringeBenefitObjectCode
     */
    public String getPositionFringeBenefitObjectCode() {
        return positionFringeBenefitObjectCode;
    }

    /**
     * Sets the positionFringeBenefitObjectCode attribute.
     * 
     * @param positionFringeBenefitObjectCode The positionFringeBenefitObjectCode to set.
     */
    public void setPositionFringeBenefitObjectCode(String positionFringeBenefitObjectCode) {
        this.positionFringeBenefitObjectCode = positionFringeBenefitObjectCode;
    }

    /**
     * Gets the positionFringeBenefitObject attribute.
     * 
     * @return Returns the positionFringeBenefitObject
     */
    public ObjectCode getPositionFringeBenefitObject() {
        return positionFringeBenefitObject;
    }

    /**
     * Sets the positionFringeBenefitObject attribute.
     * 
     * @param positionFringeBenefitObject The positionFringeBenefitObject to set.
     */
    @Deprecated
    public void setPositionFringeBenefitObject(ObjectCode positionFringeBenefitObject) {
        this.positionFringeBenefitObject = positionFringeBenefitObject;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the positionBenefitType attribute.
     * 
     * @return Returns the positionBenefitType.
     */
    public BenefitsType getPositionBenefitType() {
        return positionBenefitType;
    }

    /**
     * Sets the positionBenefitType attribute value.
     * 
     * @param positionBenefitType The positionBenefitType to set.
     */
    @Deprecated
    public void setPositionBenefitType(BenefitsType positionBenefitType) {
        this.positionBenefitType = positionBenefitType;
    }

    /**
     * Gets the laborObject
     * 
     * @return Returns the laborObject.
     */
    public LaborObject getLaborObject() {
        return laborObject;
    }
    
    /**
     * Sets the laborObject
     * 
     * @param laborObject The laborObject to set.
     */
    @Deprecated
    public void setLaborObject(LaborObject laborObject) {
        this.laborObject = laborObject;
    }
    
    /**
     * Gets the universityFiscal attribute.
     * 
     * @return Returns the universityFiscal.
     */
    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute value.
     * 
     * @param universityFiscal The universityFiscal to set.
     */
    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
    }
    
    /**
     * This method (a hack by any other name...) returns a string so that an Labor Benefits Calculation can have a link to view its own
     * inquiry page after a look up
     * 
     * @return the String "View Labor Benefits Calculation"
     */
    public String getLaborBenefitsCalculationViewer() {
        return "View Labor Benefits Calculation";
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("positionBenefitTypeCode", this.positionBenefitTypeCode);
        m.put("laborBenefitRateCategoryCode", this.laborBenefitRateCategoryCode);
        return m;
    }

    /**
     * @see org.kuali.kfs.integration.businessobject.LaborLedgerBenefitsCalculation#getLaborLedgerBenefitsType()
     */
    public LaborLedgerBenefitsType getLaborLedgerBenefitsType() {
        return this.getPositionBenefitType();
    }

    /**
     * @see org.kuali.kfs.integration.businessobject.LaborLedgerBenefitsCalculation#setLaborLedgerBenefitsType(org.kuali.module.labor.bo.LaborLedgerBenefitsType)
     */
    public void setLaborLedgerBenefitsType(LaborLedgerBenefitsType laborLedgerBenefitsType) {
        this.setPositionBenefitType((BenefitsType)laborLedgerBenefitsType);
    }

    /**
     * Gets the laborBenefitRateCategory attribute. 
     * @return Returns the laborBenefitRateCategory.
     */
    public LaborBenefitRateCategory getLaborBenefitRateCategory() {
        return this.laborBenefitRateCategory;
    }

    /**
     * Sets the laborBenefitRateCategory attribute value.
     * @param laborBenefitRateCategory The laborBenefitRateCategory to set.
     */
    public void setLaborBenefitRateCategory(LaborBenefitRateCategory laborBenefitRateCategory) {
        this.laborBenefitRateCategory = laborBenefitRateCategory;
    }

    /**
     * Gets the laborBenefitRateCategoryCode attribute. 
     * @return Returns the laborBenefitRateCategoryCode.
     */
    public String getLaborBenefitRateCategoryCode() {
        return this.laborBenefitRateCategoryCode;
    }

    /**
     * Sets the laborBenefitRateCategoryCode attribute value.
     * @param laborBenefitRateCategoryCode The laborBenefitRateCategoryCode to set.
     */
    public void setLaborBenefitRateCategoryCode(String laborBenefitRateCategoryCode) {
        this.laborBenefitRateCategoryCode = laborBenefitRateCategoryCode;
    }
    /**
     * Gets the laborAccountOffset attribute. 
     * @return Returns the laborAccountOffset.
     */
    public Account getLaborAccountOffset() {
        return laborAccountOffset;
    }
    /**
     * Sets the laborAccountOffset attribute value.
     * @param laborAccountOffset The laborAccountOffset to set.
     */
    public void setLaborAccountOffset(Account laborAccountOffset) {
        this.laborAccountOffset = laborAccountOffset;
    }
    /**
     * Gets the laborObjectCodeOffset attribute. 
     * @return Returns the laborObjectCodeOffset.
     */
    public ObjectCode getLaborObjectCodeOffset() {
        return laborObjectCodeOffset;
    }
    /**
     * Sets the laborObjectCodeOffset attribute value.
     * @param laborObjectCodeOffset The laborObjectCodeOffset to set.
     */
    public void setLaborObjectCodeOffset(ObjectCode laborObjectCodeOffset) {
        this.laborObjectCodeOffset = laborObjectCodeOffset;
    }
 

    /**
     * Gets the accountCodeOffset attribute. 
     * @return Returns the accountCodeOffset.
     */
    public String getAccountCodeOffset() {
        return accountCodeOffset;
    }

    /**
     * Sets the accountCodeOffset attribute value.
     * @param accountCodeOffset The accountCodeOffset to set.
     */
    public void setAccountCodeOffset(String accountCodeOffset) {
        this.accountCodeOffset = accountCodeOffset;
    }

    /**
     * Gets the objectCodeOffset attribute. 
     * @return Returns the objectCodeOffset.
     */
    public String getObjectCodeOffset() {
        return objectCodeOffset;
    }

    /**
     * Sets the objectCodeOffset attribute value.
     * @param objectCodeOffset The objectCodeOffset to set.
     */
    public void setObjectCodeOffset(String objectCodeOffset) {
        this.objectCodeOffset = objectCodeOffset;
    }

}
