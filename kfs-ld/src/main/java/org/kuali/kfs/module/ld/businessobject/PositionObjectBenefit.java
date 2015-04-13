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

package org.kuali.kfs.module.ld.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.ld.LaborLedgerBenefitsCalculation;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Labor business object for Modeling of Position Object Benefit
 */
public class PositionObjectBenefit extends PersistableBusinessObjectBase implements LaborLedgerPositionObjectBenefit, MutableInactivatable, FiscalYearBasedBusinessObject {
    protected Integer universityFiscalYear;
    protected String chartOfAccountsCode;
    protected String financialObjectCode;
    protected String financialObjectBenefitsTypeCode;
    protected Chart chartOfAccounts;
    protected boolean active;
  
    protected ObjectCode financialObject;
    protected transient SystemOptions universityFiscal;
    protected BenefitsCalculation benefitsCalculation;
    protected BenefitsType financialObjectBenefitsType;
    protected LaborObject laborObject;
    private String laborBenefitRateCategoryCode;
    /**
     * Default constructor.
     */
    public PositionObjectBenefit() {

    }

    /**
     * Gets the universityFiscalYear
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the financialObjectCode
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectBenefitsTypeCode
     * 
     * @return Returns the financialObjectBenefitsTypeCode
     */
    public String getFinancialObjectBenefitsTypeCode() {
        return financialObjectBenefitsTypeCode;
    }

    /**
     * Sets the financialObjectBenefitsTypeCode
     * 
     * @param financialObjectBenefitsTypeCode The financialObjectBenefitsTypeCode to set.
     */
    public void setFinancialObjectBenefitsTypeCode(String financialObjectBenefitsTypeCode) {
        this.financialObjectBenefitsTypeCode = financialObjectBenefitsTypeCode;
    }

    /**
     * Gets the financialObject
     * 
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject
     * 
     * @param financialObject The financialObject to set.
     */
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the chartOfAccounts
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the universityFiscal
     * 
     * @return Returns the universityFiscal.
     */
    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal
     * 
     * @param universityFiscal The universityFiscal to set.
     */
    @Deprecated
    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

    /**
     * Gets the benefitsCalculation
     * 
     * @return Returns the benefitsCalculation.
     */
    public BenefitsCalculation getBenefitsCalculation(String laborBenefitRateCategoryCode) {
        BenefitsCalculation bc = SpringContext.getBean(LaborBenefitsCalculationService.class).getBenefitsCalculation(universityFiscalYear, chartOfAccountsCode, financialObjectBenefitsTypeCode, laborBenefitRateCategoryCode);
        //if we can't find a match, create a new benefit calculation with a 0.00 benefit percent
        if(bc == null){
            bc = new BenefitsCalculation();
            bc.setPositionFringeBenefitPercent(new KualiPercent(0));
        }
        return bc;
    }
    
    /**
     * Gets the benefitsCalculation
     * 
     * @return Returns the benefitsCalculation.
     */
    public BenefitsCalculation getBenefitsCalculation() {
        if(benefitsCalculation == null){
            benefitsCalculation = this.getBenefitsCalculation(this.getLaborBenefitRateCategoryCode());
        }
        return benefitsCalculation;
    }

    /**
     * Sets the benefitsCalculation
     * 
     * @param benefitsCalculation The benefitsCalculation to set.
     */
    @Deprecated
    public void setBenefitsCalculation(BenefitsCalculation benefitsCalculation) {
        this.benefitsCalculation = benefitsCalculation;
    }

    /**
     * Gets the financialObjectBenefitsType
     * 
     * @return financialObjectBenefitsType
     */
    public BenefitsType getFinancialObjectBenefitsType() {
        return financialObjectBenefitsType;
    }

    /**
     * Sets financialObjectBenefitsType
     * 
     * @param financialObjectBenefitsType The financialObjectBenefitsType to be set
     */
    @Deprecated
    public void setFinancialObjectBenefitsType(BenefitsType financialObjectBenefitsType) {
        this.financialObjectBenefitsType = financialObjectBenefitsType;
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
     * This method (a hack by any other name...) returns a string so that an Labor Object Code Benefits can have a link to view its own
     * inquiry page after a look up
     * 
     * @return the String "View Labor Object Code Benefits"
     */
    public String getLaborObjectCodeBenefitsViewer() {
        return "View Labor Object Code Benefits";
    }
    
    /**
     * construct the key list of the business object.
     * 
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialObjectBenefitsTypeCode", this.financialObjectBenefitsTypeCode);

        return m;
    }

    /**
     * @see org.kuali.kfs.integration.businessobject.LaborLedgerPositionObjectBenefit#getLaborLedgerBenefitsCalculation()
     */
    public LaborLedgerBenefitsCalculation getLaborLedgerBenefitsCalculation() {
        return SpringContext.getBean(LaborBenefitsCalculationService.class).getBenefitsCalculation(universityFiscalYear, chartOfAccountsCode, financialObjectBenefitsTypeCode);
    }
    
    /**
     * @see org.kuali.kfs.integration.businessobject.LaborLedgerPositionObjectBenefit#getLaborLedgerBenefitsCalculation()
     */
    public LaborLedgerBenefitsCalculation getLaborLedgerBenefitsCalculation(String laborBenefitRateCategoryCode) {
        return SpringContext.getBean(LaborBenefitsCalculationService.class).getBenefitsCalculation(universityFiscalYear, chartOfAccountsCode, financialObjectBenefitsTypeCode,laborBenefitRateCategoryCode);
    }
   

    /**
     * @see org.kuali.kfs.integration.businessobject.LaborLedgerPositionObjectBenefit#setLaborLedgerBenefitsCalculation(org.kuali.kfs.integration.businessobject.LaborLedgerBenefitsCalculation)
     */
    public void setLaborLedgerBenefitsCalculation(LaborLedgerBenefitsCalculation laborLedgerBenefitsCalculation) {
        benefitsCalculation = (BenefitsCalculation)laborLedgerBenefitsCalculation;
    }

    /**
     * Gets the laborBenefitRateCategoryCode attribute. 
     * @return Returns the laborBenefitRateCategoryCode.
     */
    public String getLaborBenefitRateCategoryCode() {
        if(StringUtils.isEmpty(laborBenefitRateCategoryCode)){
            ParameterService parameterService = SpringContext.getBean(ParameterService.class);
            laborBenefitRateCategoryCode = StringUtils.defaultString(parameterService.getParameterValueAsString(Account.class, LaborConstants.BenefitCalculation.DEFAULT_BENEFIT_RATE_CATEGORY_CODE_PARAMETER));
        }
        return laborBenefitRateCategoryCode;
    }

    /**
     * Sets the laborBenefitRateCategoryCode attribute value.
     * @param laborBenefitRateCategoryCode The laborBenefitRateCategoryCode to set.
     */
    public void setLaborBenefitRateCategoryCode(String laborBenefitRateCategoryCode) {
        this.laborBenefitRateCategoryCode = laborBenefitRateCategoryCode;
    }
}
