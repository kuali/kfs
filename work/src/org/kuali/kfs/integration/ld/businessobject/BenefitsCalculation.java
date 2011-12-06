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
package org.kuali.kfs.integration.ld.businessobject;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.ld.LaborBenefitRateCategory;
import org.kuali.kfs.integration.ld.LaborBenefitsCalculation;
import org.kuali.kfs.integration.ld.LaborLedgerBenefitsType;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.kns.util.KualiPercent;

/**
 * 
 */
public class BenefitsCalculation implements LaborBenefitsCalculation {

    /**
     * @see org.kuali.rice.kns.bo.BusinessObject#refresh()
     */
    @Override
    public void refresh() {

    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObject#prepareForWorkflow()
     */
    @Override
    public void prepareForWorkflow() {

    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getUniversityFiscalYear()
     */
    @Override
    public Integer getUniversityFiscalYear() {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setUniversityFiscalYear(java.lang.Integer)
     */
    @Override
    public void setUniversityFiscalYear(Integer universityFiscalYear) {

    }


    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setChartOfAccountsCode(java.lang.String)
     */
    @Override
    public void setChartOfAccountsCode(String chartOfAccountsCode) {

    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getPositionBenefitTypeCode()
     */
    @Override
    public String getPositionBenefitTypeCode() {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setPositionBenefitTypeCode(java.lang.String)
     */
    @Override
    public void setPositionBenefitTypeCode(String positionBenefitTypeCode) {

    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getPositionFringeBenefitPercent()
     */
    @Override
    public KualiPercent getPositionFringeBenefitPercent() {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setPositionFringeBenefitPercent(org.kuali.rice.kns.util.KualiPercent)
     */
    @Override
    public void setPositionFringeBenefitPercent(KualiPercent positionFringeBenefitPercent) {

    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getPositionFringeBenefitObjectCode()
     */
    @Override
    public String getPositionFringeBenefitObjectCode() {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setPositionFringeBenefitObjectCode(java.lang.String)
     */
    @Override
    public void setPositionFringeBenefitObjectCode(String positionFringeBenefitObjectCode) {

    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getPositionFringeBenefitObject()
     */
    @Override
    public ObjectCode getPositionFringeBenefitObject() {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setPositionFringeBenefitObject(org.kuali.kfs.coa.businessobject.ObjectCode)
     */
    @Override
    public void setPositionFringeBenefitObject(ObjectCode positionFringeBenefitObject) {

    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getChartOfAccounts()
     */
    @Override
    public Chart getChartOfAccounts() {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setChartOfAccounts(org.kuali.kfs.coa.businessobject.Chart)
     */
    @Override
    public void setChartOfAccounts(Chart chartOfAccounts) {

    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getUniversityFiscal()
     */
    @Override
    public SystemOptions getUniversityFiscal() {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setUniversityFiscal(org.kuali.kfs.sys.businessobject.SystemOptions)
     */
    @Override
    public void setUniversityFiscal(SystemOptions universityFiscal) {

    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getLaborBenefitsCalculationViewer()
     */
    @Override
    public String getLaborBenefitsCalculationViewer() {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#isActive()
     */
    @Override
    public boolean isActive() {
        return false;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setActive(boolean)
     */
    @Override
    public void setActive(boolean active) {

    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getLaborLedgerBenefitsType()
     */
    @Override
    public LaborLedgerBenefitsType getLaborLedgerBenefitsType() {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setLaborLedgerBenefitsType(org.kuali.kfs.integration.ld.LaborLedgerBenefitsType)
     */
    @Override
    public void setLaborLedgerBenefitsType(LaborLedgerBenefitsType laborLedgerBenefitsType) {

    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getLaborBenefitRateCategory()
     */
    @Override
    public LaborBenefitRateCategory getLaborBenefitRateCategory() {

        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setLaborBenefitRateCategory(org.kuali.kfs.integration.ld.LaborBenefitRateCategory)
     */
    @Override
    public void setLaborBenefitRateCategory(LaborBenefitRateCategory laborBenefitRateCategory) {


    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#getLaborBenefitRateCategoryCode()
     */
    @Override
    public String getLaborBenefitRateCategoryCode() {

        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ld.LaborBenefitsCalculation#setLaborBenefitRateCategoryCode(java.lang.String)
     */
    @Override
    public void setLaborBenefitRateCategoryCode(String laborBenefitRateCategoryCode) {


    }

    @Override
    public String getChartOfAccountsCode() {
        // TODO Auto-generated method stub
        return null;
    }

}
