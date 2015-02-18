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
