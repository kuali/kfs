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

import java.sql.Timestamp;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Base class containing fields and methods that are common among templates.
 */
public abstract class TemplateBase extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String billByChartOfAccountCode;
    protected String billedByOrganizationCode;
    protected Chart billByChartOfAccount;
    protected Organization billedByOrganization;
    protected boolean restrictUseByChartOrg;
    protected String filename;
    protected Timestamp uploadDate;

    protected boolean active;

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
     * Gets the uploadDate attribute.
     *
     * @return Returns the uploadDate.
     */
    public Timestamp getUploadDate() {
        return uploadDate;
    }

    /**
     * Sets the uploadDate attribute value.
     *
     * @param uploadDate The uploadDate to set.
     */
    public void setUploadDate(Timestamp uploadDate) {
        this.uploadDate = uploadDate;
    }

    /**
     * Gets the restrictUseByChartOrg attribute.
     *
     * @return Returns the restrictUseByChartOrg.
     */
    public boolean isRestrictUseByChartOrg() {
        return restrictUseByChartOrg;
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
     * Sets the restrictUseByChartOrg attribute value.
     *
     * @param restrictUseByChartOrg The restrictUseByChartOrg to set.
     */
    public void setRestrictUseByChartOrg(boolean restrictUseByChartOrg) {
        this.restrictUseByChartOrg = restrictUseByChartOrg;
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

}
