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
package org.kuali.kfs.module.ar.identity;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

/**
 * Class that contains KIM Attributes used by the Accounts Receivable module and leveraged by the CGB Collector role.
 */
public class ArKimAttributes extends KfsKimAttributes {

    public static final String BILLING_CHART_OF_ACCOUNTS_CODE = "billingChartOfAccountsCode";
    public static final String BILLING_ORGANIZATION_CODE = "billingOrganizationCode";
    public static final String PROCESSING_CHART_OF_ACCOUNTS_CODE = "processingChartOfAccountsCode";
    public static final String PROCESSING_ORGANIZATION_CODE = "processingOrganizationCode";
    public static final String CUSTOMER_NAME_STARTING_LETTER = "customerNameStartingLetter";
    public static final String CUSTOMER_NAME_ENDING_LETTER = "customerNameEndingLetter";
    public static final String CUSTOMER_NAME = "customerName";

    protected String billingChartOfAccountsCode;
    protected String billingOrganizationCode;
    protected String processingChartOfAccountsCode;
    protected String processingOrganizationCode;
    protected String customerNameStartingLetter;
    protected String customerNameEndingLetter;
    protected String customerName;

    protected Chart billingChart;
    protected Organization billingOrganization;
    protected Chart processingChart;
    protected Organization processingOrganization;

    public String getBillingChartOfAccountsCode() {
        return billingChartOfAccountsCode;
    }

    public void setBillingChartOfAccountsCode(String billingChartOfAccountsCode) {
        this.billingChartOfAccountsCode = billingChartOfAccountsCode;
    }

    public String getBillingOrganizationCode() {
        return billingOrganizationCode;
    }

    public void setBillingOrganizationCode(String billingOrganizationCode) {
        this.billingOrganizationCode = billingOrganizationCode;
    }

    public Chart getBillingChart() {
        return billingChart;
    }

    public void setBillingChart(Chart billingChart) {
        this.billingChart = billingChart;
    }

    public Organization getBillingOrganization() {
        return billingOrganization;
    }

    public void setBillingOrganization(Organization billingOrganization) {
        this.billingOrganization = billingOrganization;
    }

    public String getProcessingChartOfAccountsCode() {
        return processingChartOfAccountsCode;
    }

    public void setProcessingChartOfAccountsCode(String processingChartOfAccountsCode) {
        this.processingChartOfAccountsCode = processingChartOfAccountsCode;
    }

    public String getProcessingOrganizationCode() {
        return processingOrganizationCode;
    }

    public void setProcessingOrganizationCode(String processingOrganizationCode) {
        this.processingOrganizationCode = processingOrganizationCode;
    }

    public Chart getProcessingChart() {
        return processingChart;
    }

    public void setProcessingChart(Chart processingChart) {
        this.processingChart = processingChart;
    }

    public Organization getProcessingOrganization() {
        return processingOrganization;
    }

    public void setProcessingOrganization(Organization processingOrganization) {
        this.processingOrganization = processingOrganization;
    }

    public String getCustomerNameStartingLetter() {
        return customerNameStartingLetter;
    }

    public void setCustomerNameStartingLetter(String customerNameStartingLetter) {
        this.customerNameStartingLetter = customerNameStartingLetter;
    }

    public String getCustomerNameEndingLetter() {
        return customerNameEndingLetter;
    }

    public void setCustomerNameEndingLetter(String customerNameEndingLetter) {
        this.customerNameEndingLetter = customerNameEndingLetter;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

}
