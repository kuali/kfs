/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business object class for Collector Information.
 */
public class CollectorInformation extends PersistableBusinessObjectBase {

    private String headPrincipalId;

    private String principalId;
    private String principalName;
    private boolean active;

    private Person collector;
    private CollectorHierarchy collectorHead;

    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;

    /**
     * Default constructor
     */
    public CollectorInformation() {
        super();
    }

    /**
     * Gets the headPrincipalId attribute.
     * 
     * @return Returns the headPrincipalId.
     */
    public String getHeadPrincipalId() {
        return headPrincipalId;
    }

    /**
     * Sets the headPrincipalId attribute.
     * 
     * @param headPrincipalId The headPrincipalId to set.
     */
    public void setHeadPrincipalId(String headPrincipalId) {
        this.headPrincipalId = headPrincipalId;
    }

    /**
     * Gets the collectorHead attribute.
     * 
     * @return Returns the collectorHead.
     */
    public CollectorHierarchy getCollectorHead() {
        return collectorHead;
    }

    /**
     * Sets the collectorHead attribute.
     * 
     * @param collectorHead The collectorHead to set.
     */
    public void setCollectorHead(CollectorHierarchy collectorHead) {
        this.collectorHead = collectorHead;
    }

    /**
     * Gets the principalId.
     * 
     * @see org.kuali.kfs.module.ar.businessobject.ARCollector#getPrincipalId()
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId.
     * 
     * @see org.kuali.kfs.module.ar.businessobject.ARCollector#setPrincipalId(java.lang.String)
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns active attribute.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the collector object.
     * 
     * @see org.kuali.kfs.module.ar.businessobject.ARCollector#getCollector()
     */
    public Person getCollector() {
        collector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, collector);
        return collector;
    }

    /**
     * Sets the collector object.
     * 
     * @see org.kuali.kfs.module.ar.businessobject.ARCollector#setCollector(org.kuali.rice.kim.api.identity.Person)
     */
    public void setCollector(Person collector) {
        this.collector = collector;
    }

    /**
     * Gets the principalName attribute.
     * 
     * @return Returns principalName attribute
     */
    public String getPrincipalName() {
        return principalName;
    }

    /**
     * Sets the principalName
     * 
     * @param principalName
     */
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     * 
     * @return Returns userLookupRoleNamespaceCode attribute
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Gets the userLookupRoleName attribute.
     * 
     * @return Returns userLookupRoleName attribute.
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        return m;
    }

}
