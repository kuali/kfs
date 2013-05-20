/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.defaultvalue.CurrentUserChartValueFinder;
import org.kuali.kfs.coa.businessobject.defaultvalue.CurrentUserOrgValueFinder;
import org.kuali.kfs.integration.cg.ContractsAndGrantsInvoiceTemplate;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Invoice Types under Contracts and Grants section.
 */

public class InvoiceTemplate extends PersistableBusinessObjectBase implements ContractsAndGrantsInvoiceTemplate {

    private String invoiceTemplateCode;
    private String invoiceTemplateDescription;
    private boolean active;

    /* field added for Invoice Template Upload */

    protected String billByChartOfAccountCode;
    protected String billedByOrganizationCode;
    protected Chart billByChartOfAccount;
    protected Organization billedByOrganization;
    private boolean accessRestricted;
    private String filename;
    private String filepath;
    private String date;

    /**
     * Gets the date attribute.
     * 
     * @return Returns the date.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date attribute value.
     * 
     * @param date The date to set.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the filepath attribute.
     * 
     * @return Returns the filepath.
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * Sets the filepath attribute value.
     * 
     * @param filepath The filepath to set.
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    /**
     * Gets the accessRestricted attribute.
     * 
     * @return Returns the accessRestricted.
     */
    public boolean isAccessRestricted() {
        return accessRestricted;
    }

    /**
     * Gets the billByChartOfAccountCode attribute.
     * 
     * @return Returns the billByChartOfAccountCode.
     */
    public String getBillByChartOfAccountCode() {
        return billByChartOfAccountCode;
    }

    /**
     * Sets the billByChartOfAccountCode attribute value.
     * 
     * @param billByChartOfAccountCode The billByChartOfAccountCode to set.
     */
    public void setBillByChartOfAccountCode(String billByChartOfAccountCode) {
        this.billByChartOfAccountCode = billByChartOfAccountCode;
    }

    /**
     * Gets the billedByOrganizationCode attribute.
     * 
     * @return Returns the billedByOrganizationCode.
     */
    public String getBilledByOrganizationCode() {
        return billedByOrganizationCode;
    }

    /**
     * Sets the billedByOrganizationCode attribute value.
     * 
     * @param billedByOrganizationCode The billedByOrganizationCode to set.
     */
    public void setBilledByOrganizationCode(String billedByOrganizationCode) {
        this.billedByOrganizationCode = billedByOrganizationCode;
    }

    /**
     * Gets the billByChartOfAccount attribute.
     * 
     * @return Returns the billByChartOfAccount.
     */
    public Chart getBillByChartOfAccount() {
        return billByChartOfAccount;
    }

    /**
     * Sets the billByChartOfAccount attribute value.
     * 
     * @param billByChartOfAccount The billByChartOfAccount to set.
     */
    public void setBillByChartOfAccount(Chart billByChartOfAccount) {
        this.billByChartOfAccount = billByChartOfAccount;
    }

    /**
     * Gets the billedByOrganization attribute.
     * 
     * @return Returns the billedByOrganization.
     */
    public Organization getBilledByOrganization() {
        return billedByOrganization;
    }

    /**
     * Sets the billedByOrganization attribute value.
     * 
     * @param billedByOrganization The billedByOrganization to set.
     */
    public void setBilledByOrganization(Organization billedByOrganization) {
        this.billedByOrganization = billedByOrganization;
    }

    /**
     * Sets the accessRestricted attribute value.
     * 
     * @param accessRestricted The accessRestricted to set.
     */
    public void setAccessRestricted(boolean accessRestricted) {
        this.accessRestricted = accessRestricted;
    }

    /**
     * Gets the filename attribute.
     * 
     * @return Returns the filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename attribute value.
     * 
     * @param filename The filename to set.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Default constructor.
     */
    public InvoiceTemplate() {
    }

    public String getInvoiceTemplateCode() {
        return invoiceTemplateCode;
    }

    public void setInvoiceTemplateCode(String invoiceTemplateCode) {
        this.invoiceTemplateCode = invoiceTemplateCode;
    }

    public String getInvoiceTemplateDescription() {
        return invoiceTemplateDescription;
    }

    public void setInvoiceTemplateDescription(String invoiceTemplateDescription) {
        this.invoiceTemplateDescription = invoiceTemplateDescription;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * This method returns if the current user has a valid organization as compared to the organization associated with this invoice
     * template
     * 
     * @return
     */
    public boolean isValidOrganization() {
        if (ObjectUtils.isNotNull(this.getBillByChartOfAccountCode()) && ObjectUtils.isNotNull(this.getBilledByOrganizationCode())) {
            if (this.getBillByChartOfAccountCode().equals((new CurrentUserChartValueFinder()).getValue()) && this.getBilledByOrganizationCode().equals((new CurrentUserOrgValueFinder()).getValue())) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap toStringMap = new LinkedHashMap();
        toStringMap.put("invoiceTemplateCode", this.invoiceTemplateCode);
        toStringMap.put("filename", filename);
        return toStringMap;
    }
}
