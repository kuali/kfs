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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * KRA Audit Cluster; container for related set of audit errors.
 */
public class AuditCluster {

    private String label;
    private List auditErrorList;
    private boolean softAudits;

    public AuditCluster() {
        this.auditErrorList = new ArrayList();
    }

    public AuditCluster(String label, List auditErrorList) {
        this.label = label;
        this.auditErrorList = auditErrorList;
    }

    public AuditCluster(String label, List auditErrorList, boolean softAudits) {
        this(label, auditErrorList);
        this.softAudits = softAudits;
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
     * Gets the softAudits attribute.
     * 
     * @return Returns the softAudits.
     */
    public boolean isSoftAudits() {
        return softAudits;
    }

    /**
     * Sets the softAudits attribute value.
     * 
     * @param softAudits The softAudits to set.
     */
    public void setSoftAudits(boolean softAudits) {
        this.softAudits = softAudits;
    }

    /**
     * Returns the number of audit errors in the cluster.
     * 
     * @return int size
     */
    public int getSize() {
        return this.getAuditErrorList().size();
    }
}
