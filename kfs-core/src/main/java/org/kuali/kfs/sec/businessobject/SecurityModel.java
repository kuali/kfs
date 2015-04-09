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
package org.kuali.kfs.sec.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Represents the assignment of one or more definitions to one or more members (principal, group, or role). A model becomes a role in KIM
 */
public class SecurityModel extends PersistableBusinessObjectBase implements MutableInactivatable {
    protected KualiInteger id;
    protected String name;
    protected String description;
    protected String roleId;
    protected boolean active;

    protected List<SecurityModelDefinition> modelDefinitions = new org.apache.ojb.broker.util.collections.ManageableArrayList();// = new ArrayList<SecurityModelDefinition>();
    protected List<SecurityModelMember> modelMembers = new org.apache.ojb.broker.util.collections.ManageableArrayList();// = new ArrayList<SecurityModelMember>();

    /**
     * Gets the id attribute.
     * 
     * @return Returns the id.
     */
    public KualiInteger getId() {
        return id;
    }


    /**
     * Sets the id attribute value.
     * 
     * @param id The id to set.
     */
    public void setId(KualiInteger id) {
        this.id = id;
    }


    /**
     * Gets the name attribute.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the name attribute value.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Gets the description attribute.
     * 
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets the description attribute value.
     * 
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Gets the roleId attribute.
     * 
     * @return Returns the roleId.
     */
    public String getRoleId() {
        return roleId;
    }


    /**
     * Sets the roleId attribute value.
     * 
     * @param roleId The roleId to set.
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the modelDefinitions attribute.
     * 
     * @return Returns the modelDefinitions.
     */
    public List<SecurityModelDefinition> getModelDefinitions() {
        return modelDefinitions;
    }


    /**
     * Sets the modelDefinitions attribute value.
     * 
     * @param modelDefinitions The modelDefinitions to set.
     */
    public void setModelDefinitions(List<SecurityModelDefinition> modelDefinitions) {
        this.modelDefinitions = modelDefinitions;
    }


    /**
     * Gets the modelMembers attribute.
     * 
     * @return Returns the modelMembers.
     */
    public List<SecurityModelMember> getModelMembers() {
        return modelMembers;
    }


    /**
     * Sets the modelMembers attribute value.
     * 
     * @param modelMembers The modelMembers to set.
     */
    public void setModelMembers(List<SecurityModelMember> modelMembers) {
        this.modelMembers = modelMembers;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecurityModel [");
        if (id != null) {
            builder.append("id=");
            builder.append(id);
            builder.append(", ");
        }
        if (name != null) {
            builder.append("name=");
            builder.append(name);
            builder.append(", ");
        }
        if (description != null) {
            builder.append("description=");
            builder.append(description);
            builder.append(", ");
        }
        if (roleId != null) {
            builder.append("roleId=");
            builder.append(roleId);
            builder.append(", ");
        }
        builder.append("active=");
        builder.append(active);
        builder.append("]");
        return builder.toString();
    }

    

}
