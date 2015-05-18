/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.util;

import java.util.ArrayList;
import java.util.List;

/**
 * KRA Audit Cluster; container for related set of audit errors.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */ 
public class AuditCluster {

    private String label;
    private List auditErrorList;
    private String category;

    public AuditCluster() {
        this.auditErrorList = new ArrayList();
    }

    public AuditCluster(String label, List auditErrorList, String category) {
        this.label = label;
        this.auditErrorList = auditErrorList;
        this.category = category;
    }

    /**
     * Gets the label attribute.
     * 
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label attribute value.
     * 
     * @param label The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the auditErrorList attribute.
     * 
     * @return Returns the auditErrorList.
     */
    public List getAuditErrorList() {
        return auditErrorList;
    }

    /**
     * Sets the auditErrorList attribute value.
     * 
     * @param auditErrorList The auditErrorList to set.
     */
    public void setAuditErrorList(List auditErrorList) {
        this.auditErrorList = auditErrorList;
    }

    /**
     * Returns the number of audit errors in the cluster.
     * 
     * @return int size
     */
    public int getSize() {
        return this.getAuditErrorList().size();
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

