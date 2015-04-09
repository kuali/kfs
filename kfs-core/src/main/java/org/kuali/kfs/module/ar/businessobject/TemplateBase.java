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
