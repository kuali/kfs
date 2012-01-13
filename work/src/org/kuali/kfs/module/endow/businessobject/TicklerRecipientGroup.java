/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class TicklerRecipientGroup extends PersistableBusinessObjectBase implements Inactivatable
{
    private String number;
    private boolean active;
    private String groupId;
    private String groupName;
    
    protected String assignedToGroupNamespaceForLookup;
    protected String assignedToGroupNameForLookup;
    protected Group assignedToGroup;
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() 
    {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.TICKLER_NUMBER,getNumber());
        m.put(EndowPropertyConstants.TICKLER_RECIPIENT_GROUPID,getGroupId());
        return m;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAssignedToGroupNamespaceForLookup() {
        return assignedToGroupNamespaceForLookup;
    }

    public void setAssignedToGroupNamespaceForLookup(String assignedToGroupNamespaceForLookup) {
        this.assignedToGroupNamespaceForLookup = assignedToGroupNamespaceForLookup;
    }

    public String getAssignedToGroupNameForLookup() {
        return assignedToGroupNameForLookup;
    }

    public void setAssignedToGroupNameForLookup(String assignedToGroupNameForLookup) {
        this.assignedToGroupNameForLookup = assignedToGroupNameForLookup;
    }

    public Group getAssignedToGroup() 
    {
        if(assignedToGroup == null)
        {
            Group groupInfo = getGroup(getGroupId());
            return groupInfo;
        }
        return assignedToGroup;
    }

    public Group getGroup(String groupId) {
        return (Group) SpringContext.getBean(IdentityManagementService.class).getGroup(groupId);
    }
    
    public void setAssignedToGroup(Group assignedToGroup) {
        this.assignedToGroup = assignedToGroup;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    protected GroupService getGroupService()
    {
        return SpringContext.getBean(GroupService.class);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
}
