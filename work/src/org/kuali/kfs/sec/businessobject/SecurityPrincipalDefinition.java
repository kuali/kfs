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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;


/**
 * Attribute Security Principal Definition
 */
public class SecurityPrincipalDefinition extends PersistableBusinessObjectBase implements Inactivateable {
    private KualiInteger principalDefinitionId;
    private String principalId;
    private KualiInteger definitionId;
    private String constraintCode;
    private String operatorCode;
    private String attributeValue;
    private boolean overrideDeny;
    private boolean active;

    private SecurityDefinition securityDefinition;

    public SecurityPrincipalDefinition() {
        super();

        overrideDeny = false;
    }


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

        m.put(KFSPropertyConstants.PRINCIPAL_ID, this.principalId);
        m.put(SecPropertyConstants.DEFINITION_ID, this.definitionId);

        return m;
    }

}
