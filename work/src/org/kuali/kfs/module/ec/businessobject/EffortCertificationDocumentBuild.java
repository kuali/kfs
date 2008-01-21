/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.effort.bo;

import java.util.ArrayList;
import java.util.List;

import org.kuali.module.effort.document.EffortCertificationDocument;

/**
 * Business Object for the Effort Certification Document Build Table.
 */
public class EffortCertificationDocumentBuild extends EffortCertificationDocument {
    private Long effortCertificationBuildNumber;
 
    private List<EffortCertificationDetailBuild> effortCertificationDetailLinesBuild;

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
