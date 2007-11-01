/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiInteger;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormOrganizationCreditPercent extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String chartOfAccountsCode;
    private String organizationCode;
    private KualiInteger organizationCreditPercent;
    private KualiInteger organizationFinancialAidPercent;
    private String organizationCreditRoleText;

    private Chart chartOfAccounts;
    private Org organization;
    private RoutingFormDocument routingFormDocument;

    /**
     * Default constructor.
     */
    public RoutingFormOrganizationCreditPercent() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the organizationCreditPercent attribute.
     * 
     * @return Returns the organizationCreditPercent
     */
    public KualiInteger getOrganizationCreditPercent() {
        return organizationCreditPercent;
    }

    /**
     * Sets the organizationCreditPercent attribute.
     * 
     * @param organizationCreditPercent The organizationCreditPercent to set.
     */
    public void setOrganizationCreditPercent(KualiInteger organizationCreditPercent) {
        this.organizationCreditPercent = organizationCreditPercent;
    }


    /**
     * Gets the organizationFinancialAidPercent attribute.
     * 
     * @return Returns the organizationFinancialAidPercent
     */
    public KualiInteger getOrganizationFinancialAidPercent() {
        return organizationFinancialAidPercent;
    }

    /**
     * Sets the organizationFinancialAidPercent attribute.
     * 
     * @param organizationFinancialAidPercent The organizationFinancialAidPercent to set.
     */
    public void setOrganizationFinancialAidPercent(KualiInteger organizationFinancialAidPercent) {
        this.organizationFinancialAidPercent = organizationFinancialAidPercent;
    }


    /**
     * Gets the organizationCreditRoleText attribute.
     * 
     * @return Returns the organizationCreditRoleText
     */
    public String getOrganizationCreditRoleText() {
        return organizationCreditRoleText;
    }

    /**
     * Sets the organizationCreditRoleText attribute.
     * 
     * @param organizationCreditRoleText The organizationCreditRoleText to set.
     */
    public void setOrganizationCreditRoleText(String organizationCreditRoleText) {
        this.organizationCreditRoleText = organizationCreditRoleText;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Org getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * Gets the routingFormDocument attribute.
     * 
     * @return Returns the routingFormDocument.
     */
    public RoutingFormDocument getRoutingFormDocument() {
        return routingFormDocument;
    }

    /**
     * Sets the routingFormDocument attribute value.
     * 
     * @param routingFormDocument The routingFormDocument to set.
     * @deprecated
     */
    public void setRoutingFormDocument(RoutingFormDocument routingFormDocument) {
        this.routingFormDocument = routingFormDocument;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        return m;
    }

}
