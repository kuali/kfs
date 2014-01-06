/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.defaultvalue.CurrentUserChartValueFinder;
import org.kuali.kfs.coa.businessobject.defaultvalue.CurrentUserOrgValueFinder;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines the letter templates that will be assigned to the appropriate dunning letter campaigns.
 *
 * @author mpritmani
 */
public class DunningLetterTemplate extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String letterTemplateCode;
    private String letterTemplateDescription;
    private boolean active;

    /* field added for Dunning Letter Template Upload */

    protected String billByChartOfAccountCode;
    protected String billedByOrganizationCode;
    protected Chart billByChartOfAccount;
    protected Organization billedByOrganization;
    private boolean accessRestrictedInd;
    private String filename;
    private String date;

    /**
     * Default constructor
     */
    public DunningLetterTemplate() {
        super();
    }

    /**
     * Gets the letterTemplateCode attribute.
     *
     * @return Returns letterTemplateCode.
     */
    public String getLetterTemplateCode() {
        return letterTemplateCode;
    }

    /**
     * Sets the letterTemplateCode attribute.
     *
     * @param letterTemplateCode The letterTemplateCode attribute to set.
     */
    public void setLetterTemplateCode(String letterTemplateCode) {
        this.letterTemplateCode = letterTemplateCode;
    }

    /**
     * Gets the letterTemplateDescription attribute.
     *
     * @return Returns the letterTemplateDescription attribute.
     */
    public String getLetterTemplateDescription() {
        return letterTemplateDescription;
    }

    /**
     * Sets the letterTemplateDescription attribute.
     *
     * @param letterTemplateDescription The letterTemplateDescription attribute to set.
     */
    public void setLetterTemplateDescription(String letterTemplateDescription) {
        this.letterTemplateDescription = letterTemplateDescription;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns active attribute.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active attribute to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

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
     * Gets the accessRestrictedInd attribute.
     *
     * @return Returns the accessRestrictedInd.
     */
    public boolean isAccessRestrictedInd() {
        return accessRestrictedInd;
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
     * Sets the accessRestrictedInd attribute value.
     *
     * @param accessRestrictedInd The accessRestrictedInd to set.
     */
    public void setAccessRestrictedInd(boolean accessRestrictedInd) {
        this.accessRestrictedInd = accessRestrictedInd;
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
     * This method returns if the current user has a valid organization as compared to the organization associated with this letter
     * template template
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


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap toStringMap = new LinkedHashMap();
        toStringMap.put("letterTemplateCode", this.letterTemplateCode);
        toStringMap.put("filename", filename);
        return toStringMap;
    }

}
