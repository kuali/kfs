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

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateTypeInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.KualiCodeBase;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;

public class TicklerRecipientGroup extends PersistableBusinessObjectBase implements Inactivateable
{
    private String number;
    private boolean active;
    private String groupId;
    private String groupName;
    
    protected String assignedToGroupNamespaceForLookup;
    protected String assignedToGroupNameForLookup;
    protected Group assignedToGroup;
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap<String, String> toStringMapper() 
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
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KimConstants.PrimaryKeyConstants.GROUP_ID, getGroupId());
            Group groupInfo = getGroup(fieldValues);
            return groupInfo;
        }
        return assignedToGroup;
    }

    public Group getGroup(Map<String, Object> fieldValues) {
        return (Group) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Group.class).getExternalizableBusinessObject(Group.class, fieldValues);
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
        return KIMServiceLocator.getGroupService();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
}
