/*
 * Copyright 2012 The Kuali Foundation.
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

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class AbstractSecurityModelDefinition extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected KualiInteger definitionId;
    protected String constraintCode;
    protected String operatorCode;
    protected String attributeValue;
    protected boolean overrideDeny = false;
    protected boolean active = true;

    protected SecurityDefinition securityDefinition;

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
    
    public String getPrettyPrint() {
        if ( ObjectUtils.isNotNull(securityDefinition) && ObjectUtils.isNotNull(securityDefinition.getSecurityAttribute()) ) {
            return securityDefinition.getSecurityAttribute().getName() + " " + operatorCode + " " + attributeValue + "(" + constraintCode + ")";
        }
        return "";
    }
}
