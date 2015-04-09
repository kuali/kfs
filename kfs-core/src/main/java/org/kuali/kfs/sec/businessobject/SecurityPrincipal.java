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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Represents the assignment of one or more security definitions and one or more models to a principal
 */
public class SecurityPrincipal extends PersistableBusinessObjectBase {
    protected String principalId;

    protected Person securityPerson;

    protected List<SecurityPrincipalDefinition> principalDefinitions = new ArrayList<SecurityPrincipalDefinition>();
    protected List<SecurityModelMember> principalModels = new ArrayList<SecurityModelMember>();

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }


    /**
     * Sets the principalId attribute value.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the securityPerson attribute.
     * 
     * @return Returns the securityPerson.
     */
    public Person getSecurityPerson() {
        securityPerson = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, securityPerson);
        return securityPerson;
    }


    /**
     * Sets the securityPerson attribute value.
     * 
     * @param securityPerson The securityPerson to set.
     */
    public void setSecurityPerson(Person securityPerson) {
        this.securityPerson = securityPerson;
    }


    /**
     * Gets the principalDefinitions attribute.
     * 
     * @return Returns the principalDefinitions.
     */
    public List<SecurityPrincipalDefinition> getPrincipalDefinitions() {
        return principalDefinitions;
    }


    /**
     * Sets the principalDefinitions attribute value.
     * 
     * @param principalDefinitions The principalDefinitions to set.
     */
    public void setPrincipalDefinitions(List<SecurityPrincipalDefinition> principalDefinitions) {
        this.principalDefinitions = principalDefinitions;
    }


    /**
     * Gets the principalModels attribute.
     * 
     * @return Returns the principalModels.
     */
    public List<SecurityModelMember> getPrincipalModels() {
        return principalModels;
    }


    /**
     * Sets the principalModels attribute value.
     * 
     * @param principalModels The principalModels to set.
     */
    public void setPrincipalModels(List<SecurityModelMember> principalModels) {
        this.principalModels = principalModels;
    }

    /**
     * Returns String of definition names assigned to principal
     */
    public String getPrincipalDefinitionNames() {
        String definitionNames = "";

        for (SecurityPrincipalDefinition definition : principalDefinitions) {
            if (StringUtils.isNotBlank(definitionNames)) {
                definitionNames += ", ";
            }
            definitionNames += definition.getSecurityDefinition().getName();
        }

        return definitionNames;
    }

    /**
     * Returns String of model names assigned to principal
     */
    public String getPrincipalModelNames() {
        String modelNames = "";

        for (SecurityModelMember modelMember : principalModels) {
            if (StringUtils.isNotBlank(modelNames)) {
                modelNames += ", ";
            }
            modelNames += modelMember.getSecurityModel().getName();
        }

        return modelNames;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecurityPrincipal [");
        if (principalId != null) {
            builder.append("principalId=");
            builder.append(principalId);
            builder.append(", ");
        }
        if (getPrincipalDefinitionNames() != null) {
            builder.append("getPrincipalDefinitionNames()=");
            builder.append(getPrincipalDefinitionNames());
            builder.append(", ");
        }
        if (getPrincipalModelNames() != null) {
            builder.append("getPrincipalModelNames()=");
            builder.append(getPrincipalModelNames());
        }
        builder.append("]");
        return builder.toString();
    }

    
}
