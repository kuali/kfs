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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.COMPONENT;

/**
 * Business Object for the Effort Certification Document Build Table.
 */
@COMPONENT(component="EffortCertificationDocumentBuild")
public class EffortCertificationDocumentBuild extends EffortCertificationDocument {
    protected Long effortCertificationBuildNumber;
 
    protected List<EffortCertificationDetailBuild> effortCertificationDetailLinesBuild;

    /**
     * Default constructor.
     */
    public EffortCertificationDocumentBuild() {
        super();
        effortCertificationDetailLinesBuild = new ArrayList<EffortCertificationDetailBuild>();
    }

    /**
     * Gets the effortCertificationBuildNumber attribute.
     * 
     * @return Returns the effortCertificationBuildNumber.
     */
    public Long getEffortCertificationBuildNumber() {
        return effortCertificationBuildNumber;
    }

    /**
     * Sets the effortCertificationBuildNumber attribute value.
     * 
     * @param effortCertificationBuildNumber The effortCertificationBuildNumber to set.
     */
    public void setEffortCertificationBuildNumber(Long effortCertificationBuildNumber) {
        this.effortCertificationBuildNumber = effortCertificationBuildNumber;
    }

    /**
     * Gets the effortCertificationDetailLinesBuild attribute.
     * 
     * @return Returns the effortCertificationDetailLinesBuild.
     */
    public List<EffortCertificationDetailBuild> getEffortCertificationDetailLinesBuild() {
        return effortCertificationDetailLinesBuild;
    }

    /**
     * Sets the effortCertificationDetailLinesBuild attribute value.
     * 
     * @param effortCertificationDetailLinesBuild The effortCertificationDetailLinesBuild to set.
     */
    public void setEffortCertificationDetailLinesBuild(List<EffortCertificationDetailBuild> effortCertificationDetailLinesBuild) {
        this.effortCertificationDetailLinesBuild = effortCertificationDetailLinesBuild;
    }
}
