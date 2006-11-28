/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/InstitutionCostSharePersonnel.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.kra.budget.bo;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Chart;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class InstitutionCostSharePersonnel extends BusinessObjectBase {

    private String documentNumber;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String budgetInstitutionCostSharePersonnelDescription;
    private Chart chartOfAccounts;

    /**
     * Default no-arg constructor.
     */
    public InstitutionCostSharePersonnel() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     * 
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     * 
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     * 
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the budgetInstitutionCostSharePersonnelDescription attribute.
     * 
     * @return Returns the budgetInstitutionCostSharePersonnelDescription
     * 
     */
    public String getBudgetInstitutionCostSharePersonnelDescription() {
        return budgetInstitutionCostSharePersonnelDescription;
    }

    /**
     * Sets the budgetInstitutionCostSharePersonnelDescription attribute.
     * 
     * @param budgetInstitutionCostSharePersonnelDescription The budgetInstitutionCostSharePersonnelDescription to set.
     * 
     */
    public void setBudgetInstitutionCostSharePersonnelDescription(String budgetInstitutionCostSharePersonnelDescription) {
        this.budgetInstitutionCostSharePersonnelDescription = budgetInstitutionCostSharePersonnelDescription;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * 
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);

        return m;
    }

    /**
     * Implementing equals so that contains behaves reasonable.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                InstitutionCostSharePersonnel other = (InstitutionCostSharePersonnel) obj;

                if (this.getDocumentNumber().equals(other.getDocumentNumber()) && this.getChartOfAccountsCode().equals(other.getChartOfAccountsCode()) && this.getOrganizationCode().equals(other.getOrganizationCode()) && StringUtils.equals(this.getBudgetInstitutionCostSharePersonnelDescription(), other.getBudgetInstitutionCostSharePersonnelDescription())) {
                    equal = true;
                }
            }
        }

        return equal;
    }

    /**
     * Calculates hashCode based on current values of documentNumber, chartOfAccountsCode and organizationCode fields. This is
     * based on Account.hashCode().
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String hashString = getDocumentNumber() + "|" + getChartOfAccountsCode() + "|" + getOrganizationCode();

        return hashString.hashCode();
    }
}