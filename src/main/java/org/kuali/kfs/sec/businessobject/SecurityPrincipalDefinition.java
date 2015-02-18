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

import java.util.LinkedHashMap;

import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * Attribute Security Principal Definition
 */
public class SecurityPrincipalDefinition extends AbstractSecurityModelDefinition {
    protected KualiInteger principalDefinitionId;
    protected String principalId;

    /**
     * Gets the principalDefinitionId attribute.
     * 
     * @return Returns the principalDefinitionId.
     */
    public KualiInteger getPrincipalDefinitionId() {
        return principalDefinitionId;
    }


    /**
     * Sets the principalDefinitionId attribute value.
     * 
     * @param principalDefinitionId The principalDefinitionId to set.
     */
    public void setPrincipalDefinitionId(KualiInteger principalDefinitionId) {
        this.principalDefinitionId = principalDefinitionId;
    }


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


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecurityPrincipalDefinition [");
        if (principalDefinitionId != null) {
            builder.append("principalDefinitionId=");
            builder.append(principalDefinitionId);
            builder.append(", ");
        }
        if (principalId != null) {
            builder.append("principalId=");
            builder.append(principalId);
            builder.append(", ");
        }
        if (definitionId != null) {
            builder.append("definitionId=");
            builder.append(definitionId);
            builder.append(", ");
        }
        if (constraintCode != null) {
            builder.append("constraintCode=");
            builder.append(constraintCode);
            builder.append(", ");
        }
        if (operatorCode != null) {
            builder.append("operatorCode=");
            builder.append(operatorCode);
            builder.append(", ");
        }
        if (attributeValue != null) {
            builder.append("attributeValue=");
            builder.append(attributeValue);
            builder.append(", ");
        }
        builder.append("overrideDeny=");
        builder.append(overrideDeny);
        builder.append("]");
        return builder.toString();
    }

    
}
