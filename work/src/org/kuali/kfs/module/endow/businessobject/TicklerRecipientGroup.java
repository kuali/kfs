/*
 * Copyright 2009 The Kuali Foundation.
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
