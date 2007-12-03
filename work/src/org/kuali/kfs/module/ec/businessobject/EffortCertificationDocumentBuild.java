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
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.module.effort.document.EffortCertificationDocument;

/**
 * Business Object for the Effort Certification Document Build Table.
 */
public class EffortCertificationDocumentBuild extends EffortCertificationDocument {
    private Long a21LaborBuildNumber;

    private List a21DetailLineBuild;

    /**
     * Default constructor.
     */
    public EffortCertificationDocumentBuild() {
        a21DetailLineBuild = new ArrayList();
    }

    /**
     * Gets the a21LaborBuildNumber attribute.
     * 
     * @return Returns the a21LaborBuildNumber.
     */
    public Long getA21LaborBuildNumber() {
        return a21LaborBuildNumber;
    }

    /**
     * Sets the a21LaborBuildNumber attribute value.
     * 
     * @param a21LaborBuildNumber The a21LaborBuildNumber to set.
     */
    public void setA21LaborBuildNumber(Long a21LaborBuildNumber) {
        this.a21LaborBuildNumber = a21LaborBuildNumber;
    }


    /**
     * Gets the a21DetailLineBuild attribute.
     * 
     * @return Returns the a21DetailLineBuild.
     */
    public List getA21DetailLineBuild() {
        return a21DetailLineBuild;
    }

    /**
     * Sets the a21DetailLineBuild attribute value.
     * 
     * @param detailLineBuild The a21DetailLineBuild to set.
     */
    public void setA21DetailLineBuild(List detailLineBuild) {
        a21DetailLineBuild = detailLineBuild;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.a21LaborBuildNumber != null) {
            m.put("a21LaborBuildNumber", this.a21LaborBuildNumber.toString());
        }
        return m;
    }

}
