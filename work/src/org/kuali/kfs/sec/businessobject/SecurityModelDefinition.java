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
