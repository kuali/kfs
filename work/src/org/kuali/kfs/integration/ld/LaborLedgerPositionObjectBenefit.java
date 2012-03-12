/*
 * Copyright 2008 The Kuali Foundation
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
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

public interface LaborLedgerPositionObjectBenefit extends PersistableBusinessObject, ExternalizableBusinessObject {
    /**
     * Gets the universityFiscalYear
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear();

    /**
     * Sets the universityFiscalYear
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear);

    /**
     * Gets the chartOfAccountsCode
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode();

    /**
     * Sets the chartOfAccountsCode
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode);

    /**
     * Gets the financialObjectCode
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode();

    /**
     * Sets the financialObjectCode
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode);

    /**
     * Gets the financialObjectBenefitsTypeCode
     * 
     * @return Returns the financialObjectBenefitsTypeCode
     */
    public String getFinancialObjectBenefitsTypeCode();

    /**
     * Sets the financialObjectBenefitsTypeCode
     * 
     * @param financialObjectBenefitsTypeCode The financialObjectBenefitsTypeCode to set.
     */
    public void setFinancialObjectBenefitsTypeCode(String financialObjectBenefitsTypeCode);

    /**
     * Gets the financialObject
     * 
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject();

    /**
     * Sets the financialObject
     * 
     * @param financialObject The financialObject to set.
     */
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject);

    /**
     * Gets the chartOfAccounts
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts();

    /**
     * Sets the chartOfAccounts
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts);

    /**
     * Gets the universityFiscal
     * 
     * @return Returns the universityFiscal.
     */
    public SystemOptions getUniversityFiscal();

    /**
     * Sets the universityFiscal
     * 
     * @param universityFiscal The universityFiscal to set.
     */
    @Deprecated
    public void setUniversityFiscal(SystemOptions universityFiscal);

    /**
     * Gets the laborLedgerBenefitsCalculation
     * 
     * @return Returns the laborLedgerBenefitsCalculation.
     */
    public LaborLedgerBenefitsCalculation getLaborLedgerBenefitsCalculation();
    
    /**
     * Gets the laborLedgerBenefitsCalculation
     * 
     * @return Returns the laborLedgerBenefitsCalculation.
     */
    public LaborLedgerBenefitsCalculation getLaborLedgerBenefitsCalculation(String laborBenefitRateCategoryCode);

    /**
     * Sets the laborLedgerBenefitsCalculation
     * 
     * @param laborLedgerBenefitsCalculation The laborLedgerBenefitsCalculation to set.
     */
    @Deprecated
    public void setLaborLedgerBenefitsCalculation(LaborLedgerBenefitsCalculation laborLedgerBenefitsCalculation);
}
