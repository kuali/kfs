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
