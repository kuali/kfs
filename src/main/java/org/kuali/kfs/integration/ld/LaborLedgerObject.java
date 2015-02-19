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


public interface LaborLedgerObject extends PersistableBusinessObject, ExternalizableBusinessObject {

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
     * Gets the detailPositionRequiredIndicator
     * 
     * @return Returns the detailPositionRequiredIndicator
     */
    public boolean isDetailPositionRequiredIndicator();

    /**
     * Sets the detailPositionRequiredIndicator
     * 
     * @param detailPositionRequiredIndicator The detailPositionRequiredIndicator to set.
     */
    public void setDetailPositionRequiredIndicator(boolean detailPositionRequiredIndicator);

    /**
     * Gets the financialObjectHoursRequiredIndicator
     * 
     * @return Returns the financialObjectHoursRequiredIndicator
     */
    public boolean isFinancialObjectHoursRequiredIndicator();

    /**
     * Sets the financialObjectHoursRequiredIndicator
     * 
     * @param financialObjectHoursRequiredIndicator The financialObjectHoursRequiredIndicator to set.
     */
    public void setFinancialObjectHoursRequiredIndicator(boolean financialObjectHoursRequiredIndicator);

    /**
     * Gets the financialObjectPayTypeCode
     * 
     * @return Returns the financialObjectPayTypeCode
     */
    public String getFinancialObjectPayTypeCode();

    /**
     * Sets the financialObjectPayTypeCode
     * 
     * @param financialObjectPayTypeCode The financialObjectPayTypeCode to set.
     */
    public void setFinancialObjectPayTypeCode(String financialObjectPayTypeCode);

    /**
     * Gets the financialObjectFringeOrSalaryCode
     * 
     * @return Returns the financialObjectFringeOrSalaryCode
     */
    public String getFinancialObjectFringeOrSalaryCode();

    /**
     * Sets the financialObjectFringeOrSalaryCode
     * 
     * @param financialObjectFringeOrSalaryCode The financialObjectFringeOrSalaryCode to set.
     */
    public void setFinancialObjectFringeOrSalaryCode(String financialObjectFringeOrSalaryCode);

    /**
     * Gets the positionObjectGroupCode
     * 
     * @return Returns the positionObjectGroupCode
     */
    public String getPositionObjectGroupCode();

    /**
     * Sets the positionObjectGroupCode
     * 
     * @param positionObjectGroupCode The positionObjectGroupCode to set.
     */
    public void setPositionObjectGroupCode(String positionObjectGroupCode);

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
     * Gets the laborLedgerpositionObjectGroup
     * 
     * @return Returns the laborLedgerpositionObjectGroup.
     */
    public LaborLedgerPositionObjectGroup getLaborLedgerPositionObjectGroup();

    /**
     * Sets the laborLedgerpositionObjectGroup
     * 
     * @param positionObjectGroup The laborLedgerpositionObjectGroup to set.
     */
    public void setLaborLedgerPositionObjectGroup(LaborLedgerPositionObjectGroup laborLedgerpositionObjectGroup);

    /**
     * Gets the option
     * 
     * @return Returns the option.
     */
    public SystemOptions getOption();

    /**
     * Sets the option
     * 
     * @param option The option to set.
     */
    public void setOption(SystemOptions option);

}
