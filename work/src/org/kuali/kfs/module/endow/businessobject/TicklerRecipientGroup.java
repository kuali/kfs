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
package org.kuali.kfs.module.endow.businessobject;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.framework.group.GroupEbo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;

public class TicklerRecipientGroup extends PersistableBusinessObjectBase implements MutableInactivatable
{
    protected String number;
    protected boolean active;
    protected String groupId;
    protected String groupName;

    protected String assignedToGroupNamespaceForLookup;
    protected String assignedToGroupNameForLookup;
    protected GroupEbo assignedToGroup;

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
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

    public GroupEbo getAssignedToGroup() {
        if( assignedToGroup == null || !StringUtils.equals(groupId, assignedToGroup.getId() ) ) {
            if ( StringUtils.isBlank(groupId) ) {
                assignedToGroup = null;
            } else {
                  ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(GroupEbo.class);
                  if ( moduleService != null ) {
                      Map<String,Object> keys = new HashMap<String, Object>(1);
                      keys.put(KimConstants.PrimaryKeyConstants.ID, groupId);
                      assignedToGroup = moduleService.getExternalizableBusinessObject(GroupEbo.class, keys);
                  } else {
                      throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                  }

          }
        }
        return assignedToGroup;
    }

    public void setAssignedToGroup(GroupEbo assignedToGroup) {
        this.assignedToGroup = assignedToGroup;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
