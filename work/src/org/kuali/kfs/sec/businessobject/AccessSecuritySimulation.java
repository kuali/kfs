/*
 * Copyright 2010 The Kuali Foundation.
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

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Holds fields needed to run a security simulation and the display results
 */
public class AccessSecuritySimulation extends TransientBusinessObjectBase {
    // search fields
    protected String principalId;
    protected String templateId;
    protected String attributeName;
    protected String financialSystemDocumentTypeCode;
    protected String inquiryNamespaceCode;

    // return fields
    protected String attributeValue;
    protected String attributeValueName;

    protected Person securityPerson;

    public AccessSecuritySimulation() {

    }


    /**
     * Gets the attributeName attribute.
     *
     * @return Returns the attributeName.
     */
    public String getAttributeName() {
        return attributeName;
    }


    /**
     * Sets the attributeName attribute value.
     *
     * @param attributeName The attributeName to set.
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
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
     * Gets the templateId attribute.
     *
     * @return Returns the templateId.
     */
    public String getTemplateId() {
        return templateId;
    }


    /**
     * Sets the templateId attribute value.
     *
     * @param templateId The templateId to set.
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }


    /**
     * Gets the financialSystemDocumentTypeCode attribute.
     *
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public String getFinancialSystemDocumentTypeCode() {
        return financialSystemDocumentTypeCode;
    }


    /**
     * Sets the financialSystemDocumentTypeCode attribute value.
     *
     * @param financialSystemDocumentTypeCode The financialSystemDocumentTypeCode to set.
     */
    public void setFinancialSystemDocumentTypeCode(String financialSystemDocumentTypeCode) {
        this.financialSystemDocumentTypeCode = financialSystemDocumentTypeCode;
    }


    /**
     * Gets the inquiryNamespaceCode attribute.
     *
     * @return Returns the inquiryNamespaceCode.
     */
    public String getInquiryNamespaceCode() {
        return inquiryNamespaceCode;
    }


    /**
     * Sets the inquiryNamespaceCode attribute value.
     *
     * @param inquiryNamespaceCode The inquiryNamespaceCode to set.
     */
    public void setInquiryNamespaceCode(String inquiryNamespaceCode) {
        this.inquiryNamespaceCode = inquiryNamespaceCode;
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
     * Gets the attributeValueName attribute.
     *
     * @return Returns the attributeValueName.
     */
    public String getAttributeValueName() {
        return attributeValueName;
    }


    /**
     * Sets the attributeValueName attribute value.
     *
     * @param attributeValueName The attributeValueName to set.
     */
    public void setAttributeValueName(String attributeValueName) {
        this.attributeValueName = attributeValueName;
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

}
