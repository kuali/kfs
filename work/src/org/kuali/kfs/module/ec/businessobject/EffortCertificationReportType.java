/*
 * Copyright 2006-2007 The Kuali Foundation
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
