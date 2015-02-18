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

package org.kuali.kfs.module.ec.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object for the Effort Certification Report Type Table.
 */
public class EffortCertificationReportType extends PersistableBusinessObjectBase implements MutableInactivatable {
    private String effortCertificationReportTypeCode;
    private String effortCertificationReportDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public EffortCertificationReportType() {

    }

    /**
     * Gets the effortCertificationReportTypeCode attribute.
     * 
     * @return Returns the effortCertificationReportTypeCode
     */
    public String getEffortCertificationReportTypeCode() {
        return effortCertificationReportTypeCode;
    }

    /**
     * Sets the effortCertificationReportTypeCode attribute.
     * 
     * @param effortCertificationReportTypeCode The effortCertificationReportTypeCode to set.
     */
    public void setEffortCertificationReportTypeCode(String effortCertificationReportTypeCode) {
        this.effortCertificationReportTypeCode = effortCertificationReportTypeCode;
    }


    /**
     * Gets the effortCertificationReportDescription attribute.
     * 
     * @return Returns the effortCertificationReportDescription
     */
    public String getEffortCertificationReportDescription() {
        return effortCertificationReportDescription;
    }

    /**
     * Sets the effortCertificationReportDescription attribute.
     * 
     * @param effortCertificationReportDescription The effortCertificationReportDescription to set.
     */
    public void setEffortCertificationReportDescription(String effortCertificationReportDescription) {
        this.effortCertificationReportDescription = effortCertificationReportDescription;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("effortCertificationLaborReportTypeCode", this.effortCertificationReportTypeCode);
        return m;
    }
}
