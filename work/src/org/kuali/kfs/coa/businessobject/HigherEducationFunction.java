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

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 *
 */
public class HigherEducationFunction extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String financialHigherEdFunctionCd;
    protected String financialHigherEdFunctionNm;
    protected String finUnivBdgtOfficeFunctionCd;
    protected String finAicpaFunctionCode;
    protected String financialFederalFunctionCode;
    protected boolean active;

    protected UniversityBudgetOfficeFunction universityBudgetOfficeFunction;
    protected FederalFunction federalFunction;
    protected AICPAFunction aicpaFunction; // American Institute of Certified Public Accountants

    /**
     * Default no-arg constructor.
     */
    public HigherEducationFunction() {

    }

    /**
     * Gets the financialHigherEdFunctionCd attribute.
     *
     * @return Returns the financialHigherEdFunctionCd
     */
    public String getFinancialHigherEdFunctionCd() {
        return financialHigherEdFunctionCd;
    }

    /**
     * Sets the financialHigherEdFunctionCd attribute.
     *
     * @param financialHigherEdFunctionCd The financialHigherEdFunctionCd to set.
     */
    public void setFinancialHigherEdFunctionCd(String financialHigherEdFunctionCd) {
        this.financialHigherEdFunctionCd = financialHigherEdFunctionCd;
    }

    /**
     * Gets the financialHigherEdFunctionNm attribute.
     *
     * @return Returns the financialHigherEdFunctionNm
     */
    public String getFinancialHigherEdFunctionNm() {
        return financialHigherEdFunctionNm;
    }

    /**
     * Sets the financialHigherEdFunctionNm attribute.
     *
     * @param financialHigherEdFunctionNm The financialHigherEdFunctionNm to set.
     */
    public void setFinancialHigherEdFunctionNm(String financialHigherEdFunctionNm) {
        this.financialHigherEdFunctionNm = financialHigherEdFunctionNm;
    }

    /**
     * Gets the finUnivBdgtOfficeFunctionCd attribute.
     *
     * @return Returns the finUnivBdgtOfficeFunctionCd
     */
    public String getFinUnivBdgtOfficeFunctionCd() {
        return finUnivBdgtOfficeFunctionCd;
    }

    /**
     * Sets the finUnivBdgtOfficeFunctionCd attribute.
     *
     * @param finUnivBdgtOfficeFunctionCd The finUnivBdgtOfficeFunctionCd to set.
     */
    public void setFinUnivBdgtOfficeFunctionCd(String finUnivBdgtOfficeFunctionCd) {
        this.finUnivBdgtOfficeFunctionCd = finUnivBdgtOfficeFunctionCd;
    }

    /**
     * Gets the finAicpaFunctionCode attribute.
     *
     * @return Returns the finAicpaFunctionCode
     */
    public String getFinAicpaFunctionCode() {
        return finAicpaFunctionCode;
    }

    /**
     * Sets the finAicpaFunctionCode attribute.
     *
     * @param finAicpaFunctionCode The finAicpaFunctionCode to set.
     */
    public void setFinAicpaFunctionCode(String finAicpaFunctionCode) {
        this.finAicpaFunctionCode = finAicpaFunctionCode;
    }

    /**
     * Gets the financialFederalFunctionCode attribute.
     *
     * @return Returns the financialFederalFunctionCode
     */
    public String getFinancialFederalFunctionCode() {
        return financialFederalFunctionCode;
    }

    /**
     * Sets the financialFederalFunctionCode attribute.
     *
     * @param financialFederalFunctionCode The financialFederalFunctionCode to set.
     */
    public void setFinancialFederalFunctionCode(String financialFederalFunctionCode) {
        this.financialFederalFunctionCode = financialFederalFunctionCode;
    }

    /**
     * @return Returns the universityBudgetOfficeFunction.
     */
    public UniversityBudgetOfficeFunction getUniversityBudgetOfficeFunction() {
        return universityBudgetOfficeFunction;
    }

    /**
     * @param universityBudgetOfficeFunction The universityBudgetOfficeFunction to set.
     * @deprecated
     */
    @Deprecated
    public void setUniversityBudgetOfficeFunction(UniversityBudgetOfficeFunction universityBudgetOfficeFunction) {
        this.universityBudgetOfficeFunction = universityBudgetOfficeFunction;
    }

    /**
     * @return Returns the federalFunction.
     */
    public FederalFunction getFederalFunction() {
        return federalFunction;
    }

    /**
     * @param federalFunction The federalFunction to set.
     * @deprecated
     */
    @Deprecated
    public void setFederalFunction(FederalFunction federalFunction) {
        this.federalFunction = federalFunction;
    }

    /**
     * @return Returns the aicpaFunction.
     */
    public AICPAFunction getAicpaFunction() {
        return aicpaFunction;
    }

    /**
     * @param aicpaFunction The aicpaFunction to set.
     * @deprecated
     */
    @Deprecated
    public void setAicpaFunction(AICPAFunction aicpaFunction) {
        this.aicpaFunction = aicpaFunction;
    }

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        String theString = getFinancialHigherEdFunctionCd() + " - " + getFinancialHigherEdFunctionNm();
        return theString;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialHigherEdFunctionCd", this.financialHigherEdFunctionCd);
        return m;
    }

    /**
     * Gets the active attribute.
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }


}
