/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetObjectCode extends PersistableBusinessObjectBase implements MutableInactivatable, FiscalYearBasedBusinessObject {

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "AssetObjectCode";
    
    protected Integer universityFiscalYear;
    protected String chartOfAccountsCode;
    protected String financialObjectSubTypeCode;
    protected String capitalizationFinancialObjectCode;
    protected String accumulatedDepreciationFinancialObjectCode;
    protected String depreciationExpenseFinancialObjectCode;
    protected boolean active;

    protected transient SystemOptions universityFiscal;
    protected ObjectCode accumulatedDepreciationFinancialObject;
    protected ObjectCode capitalizationFinancialObject;
    protected ObjectCode depreciationExpenseFinancialObject;
    protected List<ObjectCode> objectCode;
    protected Chart chartOfAccounts;
    protected ObjectSubType financialObjectSubType;

    /**
     * Default constructor.
     */
    public AssetObjectCode() {

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
     * Gets the financialObjectSubTypeCode attribute.
     * 
     * @return Returns the financialObjectSubTypeCode
     */
    public String getFinancialObjectSubTypeCode() {
        return financialObjectSubTypeCode;
    }

    /**
     * Sets the financialObjectSubTypeCode attribute.
     * 
     * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
     */
    public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
        this.financialObjectSubTypeCode = financialObjectSubTypeCode;
    }


    /**
     * Gets the capitalizationFinancialObjectCode attribute.
     * 
     * @return Returns the capitalizationFinancialObjectCode
     */
    public String getCapitalizationFinancialObjectCode() {
        return capitalizationFinancialObjectCode;
    }

    /**
     * Sets the capitalizationFinancialObjectCode attribute.
     * 
     * @param capitalizationFinancialObjectCode The capitalizationFinancialObjectCode to set.
     */
    public void setCapitalizationFinancialObjectCode(String capitalizationFinancialObjectCode) {
        this.capitalizationFinancialObjectCode = capitalizationFinancialObjectCode;
    }


    /**
     * Gets the accumulatedDepreciationFinancialObjectCode attribute.
     * 
     * @return Returns the accumulatedDepreciationFinancialObjectCode
     */
    public String getAccumulatedDepreciationFinancialObjectCode() {
        return accumulatedDepreciationFinancialObjectCode;
    }

    /**
     * Sets the accumulatedDepreciationFinancialObjectCode attribute.
     * 
     * @param accumulatedDepreciationFinancialObjectCode The accumulatedDepreciationFinancialObjectCode to set.
     */
    public void setAccumulatedDepreciationFinancialObjectCode(String accumulatedDepreciationFinancialObjectCode) {
        this.accumulatedDepreciationFinancialObjectCode = accumulatedDepreciationFinancialObjectCode;
    }


    /**
     * Gets the depreciationExpenseFinancialObjectCode attribute.
     * 
     * @return Returns the depreciationExpenseFinancialObjectCode
     */
    public String getDepreciationExpenseFinancialObjectCode() {
        return depreciationExpenseFinancialObjectCode;
    }

    /**
     * Sets the depreciationExpenseFinancialObjectCode attribute.
     * 
     * @param depreciationExpenseFinancialObjectCode The depreciationExpenseFinancialObjectCode to set.
     */
    public void setDepreciationExpenseFinancialObjectCode(String depreciationExpenseFinancialObjectCode) {
        this.depreciationExpenseFinancialObjectCode = depreciationExpenseFinancialObjectCode;
    }


    /**
     * Gets the accumulatedDepreciationFinancialObject attribute.
     * 
     * @return Returns the accumulatedDepreciationFinancialObject
     */
    public ObjectCode getAccumulatedDepreciationFinancialObject() {
        return accumulatedDepreciationFinancialObject;
    }

    /**
     * Sets the accumulatedDepreciationFinancialObject attribute.
     * 
     * @param accumulatedDepreciationFinancialObject The accumulatedDepreciationFinancialObject to set.
     * @deprecated
     */
    public void setAccumulatedDepreciationFinancialObject(ObjectCode accumulatedDepreciationFinancialObject) {
        this.accumulatedDepreciationFinancialObject = accumulatedDepreciationFinancialObject;
    }

    /**
     * Gets the capitalizationFinancialObject attribute.
     * 
     * @return Returns the capitalizationFinancialObject
     */
    public ObjectCode getCapitalizationFinancialObject() {
        return capitalizationFinancialObject;
    }

    /**
     * Sets the capitalizationFinancialObject attribute.
     * 
     * @param capitalizationFinancialObject The capitalizationFinancialObject to set.
     * @deprecated
     */
    public void setCapitalizationFinancialObject(ObjectCode capitalizationFinancialObject) {
        this.capitalizationFinancialObject = capitalizationFinancialObject;
    }

    /**
     * Gets the depreciationExpenseFinancialObject attribute.
     * 
     * @return Returns the depreciationExpenseFinancialObject
     */
    public ObjectCode getDepreciationExpenseFinancialObject() {
        return depreciationExpenseFinancialObject;
    }

    /**
     * Sets the depreciationExpenseFinancialObject attribute.
     * 
     * @param depreciationExpenseFinancialObject The depreciationExpenseFinancialObject to set.
     * @deprecated
     */
    public void setDepreciationExpenseFinancialObject(ObjectCode depreciationExpenseFinancialObject) {
        this.depreciationExpenseFinancialObject = depreciationExpenseFinancialObject;
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
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the financialObjectSubType attribute.
     * 
     * @return Returns the financialObjectSubType.
     */
    public ObjectSubType getFinancialObjectSubType() {
        return financialObjectSubType;
    }

    /**
     * Sets the financialObjectSubType attribute value.
     * 
     * @param financialObjectSubType The financialObjectSubType to set.
     * @deprecated
     */
    public void setFinancialObjectSubType(ObjectSubType financialObjectSubType) {
        this.financialObjectSubType = financialObjectSubType;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectSubTypeCode", this.financialObjectSubTypeCode);
        return m;
    }

    public List<ObjectCode> getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(List<ObjectCode> objectCode) {
        this.objectCode = objectCode;
    }

}
