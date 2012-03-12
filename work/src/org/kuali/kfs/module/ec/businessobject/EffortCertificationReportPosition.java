/*
 * Copyright 2007-2009 The Kuali Foundation
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

import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectGroup;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * Business Object for the Effort Certification Report Position Table.
 */
public class EffortCertificationReportPosition extends PersistableBusinessObjectBase implements MutableInactivatable, FiscalYearBasedBusinessObject {
    private Integer universityFiscalYear;
    private String effortCertificationReportNumber;
    private String effortCertificationReportPositionObjectGroupCode;
    private boolean active;

    private LaborLedgerPositionObjectGroup positionObjectGroup;
    private EffortCertificationReportDefinition effortCertificationReportDefinition;
    private SystemOptions options;

    /**
     * Default constructor.
     */
    public EffortCertificationReportPosition() {
        super();
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the effortCertificationReportNumber attribute.
     * 
     * @return Returns the effortCertificationReportNumber.
     */
    public String getEffortCertificationReportNumber() {
        return effortCertificationReportNumber;
    }

    /**
     * Sets the effortCertificationReportNumber attribute value.
     * 
     * @param effortCertificationReportNumber The effortCertificationReportNumber to set.
     */
    public void setEffortCertificationReportNumber(String effortCertificationReportNumber) {
        this.effortCertificationReportNumber = effortCertificationReportNumber;
    }

    /**
     * Gets the effortCertificationReportPositionObjectGroupCode attribute.
     * 
     * @return Returns the effortCertificationReportPositionObjectGroupCode.
     */
    public String getEffortCertificationReportPositionObjectGroupCode() {
        return effortCertificationReportPositionObjectGroupCode;
    }

    /**
     * Sets the effortCertificationReportPositionObjectGroupCode attribute value.
     * 
     * @param effortCertificationReportPositionObjectGroupCode The effortCertificationReportPositionObjectGroupCode to set.
     */
    public void setEffortCertificationReportPositionObjectGroupCode(String effortCertificationReportPositionObjectGroupCode) {
        this.effortCertificationReportPositionObjectGroupCode = effortCertificationReportPositionObjectGroupCode;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * gets the positionObjetGroup
     * 
     * @return the positionObjetGroup
     */
    public LaborLedgerPositionObjectGroup getPositionObjectGroup() {
        positionObjectGroup = (LaborLedgerPositionObjectGroup) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(LaborLedgerPositionObjectGroup.class).retrieveExternalizableBusinessObjectIfNecessary(this, positionObjectGroup, LaborPropertyConstants.POSITION_OBJECT_GROUP);

        return positionObjectGroup;
    }

    /**
     * Gets the effortCertificationReportDefinition attribute.
     * 
     * @return Returns the effortCertificationReportDefinition.
     */
    public EffortCertificationReportDefinition getEffortCertificationReportDefinition() {
        return effortCertificationReportDefinition;
    }

    /**
     * Sets the effortCertificationReportDefinition attribute value.
     * 
     * @param effortCertificationReportDefinition The effortCertificationReportDefinition to set.
     */
    public void setEffortCertificationReportDefinition(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        this.effortCertificationReportDefinition = effortCertificationReportDefinition;
    }

    /**
     * Gets the options attribute.
     * 
     * @return Returns the options.
     */
    public SystemOptions getOptions() {
        return options;
    }

    /**
     * Sets the options attribute value.
     * 
     * @param options The options to set.
     */
    public void setOptions(SystemOptions options) {
        this.options = options;
    }

    /**
     * Sets the positionObjectGroup attribute value.
     * 
     * @param positionObjectGroup The positionObjectGroup to set.
     */
    public void setPositionObjectGroup(LaborLedgerPositionObjectGroup positionObjectGroup) {
        this.positionObjectGroup = positionObjectGroup;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("effortCertificationReportNumber", this.effortCertificationReportNumber);
        m.put("effortCertificationReportPositionObjectGroupCode", this.effortCertificationReportPositionObjectGroupCode);
        return m;
    }
}
