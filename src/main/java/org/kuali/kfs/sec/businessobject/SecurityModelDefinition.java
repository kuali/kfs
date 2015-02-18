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
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * Associates a security definition with a model with qualification. The qualifications become the qualifications on the KIM assignment of the model role to the definition role
 */
public class SecurityModelDefinition extends AbstractSecurityModelDefinition {
    protected KualiInteger modelDefinitionId;
    protected KualiInteger modelId;

    /**
     * Gets the modelDefinitionId attribute.
     * 
     * @return Returns the modelDefinitionId.
     */
    public KualiInteger getModelDefinitionId() {
        return modelDefinitionId;
    }


    /**
     * Sets the modelDefinitionId attribute value.
     * 
     * @param modelDefinitionId The modelDefinitionId to set.
     */
    public void setModelDefinitionId(KualiInteger modelDefinitionId) {
        this.modelDefinitionId = modelDefinitionId;
    }


    /**
     * Gets the modelId attribute.
     * 
     * @return Returns the modelId.
     */
    public KualiInteger getModelId() {
        return modelId;
    }


    /**
     * Sets the modelId attribute value.
     * 
     * @param modelId The modelId to set.
     */
    public void setModelId(KualiInteger modelId) {
        this.modelId = modelId;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecurityModelDefinition [");
        if (modelDefinitionId != null) {
            builder.append("modelDefinitionId=");
            builder.append(modelDefinitionId);
            builder.append(", ");
        }
        if (modelId != null) {
            builder.append("modelId=");
            builder.append(modelId);
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
