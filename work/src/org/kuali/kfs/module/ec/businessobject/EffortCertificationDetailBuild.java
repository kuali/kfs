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
import java.util.Map;

import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.util.ObjectUtil;

/**
 * Business Object for the Effort Certification Detail Build Table.
 */
public class EffortCertificationDetailBuild extends EffortCertificationDetail {
    private Long effortCertificationBuildNumber;
    
    private EffortCertificationDocumentBuild effortCertificationDocumentBuild;

    /**
     * Default constructor.
     */
    public EffortCertificationDetailBuild() {
        super();
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
     * Gets the effortCertificationDocumentBuild attribute. 
     * @return Returns the effortCertificationDocumentBuild.
     */
    public EffortCertificationDocumentBuild getEffortCertificationDocumentBuild() {
        return effortCertificationDocumentBuild;
    }

    /**
     * Sets the effortCertificationDocumentBuild attribute value.
     * @param effortCertificationDocumentBuild The effortCertificationDocumentBuild to set.
     */
    @Deprecated
    public void setEffortCertificationDocumentBuild(EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        this.effortCertificationDocumentBuild = effortCertificationDocumentBuild;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = super.toStringMapper();
        if (this.effortCertificationBuildNumber != null) {
            m.put("effortCertificationBuildNumber", this.effortCertificationBuildNumber.toString());
        }
        return m;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object otherEntry) {
        return ObjectUtil.compareObject(this, otherEntry, getKeyList());
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ObjectUtil.generateHashCode(this, getKeyList());
    }

    /**
     * get the field name list of the key fields of the Class
     * @return the field name list of the key fields of the Class
     */
    public static List<String> getKeyList() {
        List<String> keyList = new ArrayList<String>();
        keyList.add(EffortPropertyConstants.EFFORT_CERTIFICATION_BUILD_NUMBER);
        keyList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keyList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keyList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        keyList.add(KFSPropertyConstants.POSITION_NUMBER);
        keyList.add(EffortPropertyConstants.SOURCE_CHART_OF_ACCOUNTS_CODE);
        keyList.add(EffortPropertyConstants.SOURCE_ACCOUNT_NUMBER);
        return keyList;
    }
}
