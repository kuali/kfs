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
