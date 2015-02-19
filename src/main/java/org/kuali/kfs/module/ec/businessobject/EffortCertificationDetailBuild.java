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
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;

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
     * 
     * @return Returns the effortCertificationDocumentBuild.
     */
    public EffortCertificationDocumentBuild getEffortCertificationDocumentBuild() {
        return effortCertificationDocumentBuild;
    }

    /**
     * Sets the effortCertificationDocumentBuild attribute value.
     * 
     * @param effortCertificationDocumentBuild The effortCertificationDocumentBuild to set.
     */
    @Deprecated
    public void setEffortCertificationDocumentBuild(EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        this.effortCertificationDocumentBuild = effortCertificationDocumentBuild;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object otherEntry) {
        return ObjectUtil.equals(this, otherEntry, getKeyList());
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
     * 
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
