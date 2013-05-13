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
