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
package org.kuali.kfs.sec.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;


/**
 * Associates a security definition with a model with qualification. The qualifications become the qualifications on the KIM assignment of the model role to the definition role
 */
public class SecurityModelDefinition extends PersistableBusinessObjectBase implements Inactivateable {
    private KualiInteger modelDefinitionId;
    private KualiInteger modelId;
    private KualiInteger definitionId;
    private String constraintCode;
    private String operatorCode;
    private String attributeValue;
    private boolean overrideDeny;
    private boolean active;

    private SecurityDefinition securityDefinition;


    public SecurityModelDefinition() {
        super();

        overrideDeny = false;
    }


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


    /**
     * Gets the definitionId attribute.
     * 
     * @return Returns the definitionId.
     */
    public KualiInteger getDefinitionId() {
        return definitionId;
    }


    /**
     * Sets the definitionId attribute value.
     * 
     * @param definitionId The definitionId to set.
     */
    public void setDefinitionId(KualiInteger definitionId) {
        this.definitionId = definitionId;
    }


    /**
     * Gets the attributeValue attribute.
     * 
     * @return Returns the attributeValue.
     */
    public String getAttributeValue() {
        return attributeValue;
    }


    /**
     * Sets the attributeValue attribute value.
     * 
     * @param attributeValue The attributeValue to set.
     */
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }


    /**
     * Gets the operatorCode attribute.
     * 
     * @return Returns the operatorCode.
     */
    public String getOperatorCode() {
        return operatorCode;
    }


    /**
     * Sets the operatorCode attribute value.
     * 
     * @param operatorCode The operatorCode to set.
     */
    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }


    /**
     * Gets the constraintCode attribute.
     * 
     * @return Returns the constraintCode.
     */
    public String getConstraintCode() {
        return constraintCode;
    }


    /**
     * Sets the constraintCode attribute value.
     * 
     * @param constraintCode The constraintCode to set.
     */
    public void setConstraintCode(String constraintCode) {
        this.constraintCode = constraintCode;
    }


    /**
     * Gets the overrideDeny attribute.
     * 
     * @return Returns the overrideDeny.
     */
    public boolean isOverrideDeny() {
        return overrideDeny;
    }


    /**
     * Sets the overrideDeny attribute value.
     * 
     * @param overrideDeny The overrideDeny to set.
     */
    public void setOverrideDeny(boolean overrideDeny) {
        this.overrideDeny = overrideDeny;
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
     * Gets the securityDefinition attribute.
     * 
     * @return Returns the securityDefinition.
     */
    public SecurityDefinition getSecurityDefinition() {
        return securityDefinition;
    }


    /**
     * Sets the securityDefinition attribute value.
     * 
     * @param securityDefinition The securityDefinition to set.
     */
    public void setSecurityDefinition(SecurityDefinition securityDefinition) {
        this.securityDefinition = securityDefinition;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(SecPropertyConstants.MODEL_ID, this.modelId);
        m.put(SecPropertyConstants.DEFINITION_ID, this.definitionId);

        return m;
    }

}
