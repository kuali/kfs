/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.form;

import org.kuali.rice.krad.datadictionary.HelpDefinition;

/**
 * Holds help parameters and found text.
 * 
 * 
 */
public class KualiHelpForm extends KualiForm {
    private static final long serialVersionUID = 1L;
    private String businessObjectClassName;
    private String attributeName;
    private String helpLabel;
    private String helpSummary;
    private String helpDescription;
    private String resourceKey;
    private String documentTypeName;
    private String helpDataType;
    private String helpRequired;
    private String helpMaxLength;
    private String helpVPatName;
    private HelpDefinition helpDefinition;
    private String helpParameterNamespace;
    private String helpParameterDetailType;
    private String helpParameterName;
    private String pageName;
    private String lookupBusinessObjectClassName;
    private String searchDocumentTypeName;

    /**
     * @return Returns the attributeName.
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @param attributeName The attributeName to set.
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * @return Returns the businessObjectClassName.
     */
    public String getBusinessObjectClassName() {
        return businessObjectClassName;
    }

    /**
     * @param businessObjectClassName The businessObjectClassName to set.
     */
    public void setBusinessObjectClassName(String businessObjectClassName) {
        this.businessObjectClassName = businessObjectClassName;
    }

    /**
     * @return Returns the helpDescription.
     */
    public String getHelpDescription() {
        return helpDescription;
    }

    /**
     * @param helpDescription The helpDescription to set.
     */
    public void setHelpDescription(String helpDescription) {
        this.helpDescription = helpDescription;
    }

    /**
     * @return Returns the helpLabel.
     */
    public String getHelpLabel() {
        return helpLabel;
    }

    /**
     * @param helpLabel The helpLabel to set.
     */
    public void setHelpLabel(String helpLabel) {
        this.helpLabel = helpLabel;
    }

    /**
     * @return Returns the helpSummary.
     */
    public String getHelpSummary() {
        return helpSummary;
    }

    /**
     * @param helpSummary The helpSummary to set.
     */
    public void setHelpSummary(String helpSummary) {
        this.helpSummary = helpSummary;
    }

    /**
     * @return Returns the resourceKey.
     */
    public String getResourceKey() {
        return resourceKey;
    }

    /**
     * @param resourceKey The resourceKey to set.
     */
    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * @return Returns the documentTypeName.
     */
    public String getDocumentTypeName() {
        return documentTypeName;
    }

    /**
     * @param documentTypeName The documentTypeName to set.
     */
    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    /**
     * Form field accessor for Required
     * 
     * @param r
     */
    public void setHelpRequired(String r) {
        helpRequired = r;
    }

    /**
     * Form field accessor for Required
     * 
     * @return String
     */
    public String getHelpRequired() {
        return helpRequired;
    }

    /**
     * Form field accessor for DataType
     * 
     * @param s
     */
    public void setHelpDataType(String s) {
        helpDataType = s;
    }

    /**
     * Form field accessor for DataType
     * 
     * @return String
     */
    public String getHelpDataType() {
        return helpDataType;
    }

    /**
     * Form field accessor for Maximum Length
     * 
     * @param m
     */
    public void setHelpMaxLength(String m) {
        helpMaxLength = m;
    }

    /**
     * Form field accessor for Maximum Length
     * 
     * @return String
     */
    public String getHelpMaxLength() {
        return helpMaxLength;
    }

    /**
     * Form field accessor for name of Validation Pattern
     * 
     * @param v
     */
    public void setValidationPatternName(String v) {
        helpVPatName = v;
    }

    /**
     * Form field accessor for name of Validation Pattern
     * 
     * @return String
     */
    public String getValidationPatternName() {
        return helpVPatName;
    }

    /**
     * Form field accessor for system parameter help.
     * 
     * @return helpDefinition
     */
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * Form field accessor for system parameter help.
     * 
     * @param helpDefinition
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    public String getHelpParameterName() {
        return helpParameterName;
    }

    public void setHelpParameterName(String helpParameterName) {
        this.helpParameterName = helpParameterName;
    }

    public String getHelpParameterNamespace() {
        return helpParameterNamespace;
    }

    public void setHelpParameterNamespace(String helpSecurityGroupName) {
        this.helpParameterNamespace = helpSecurityGroupName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getHelpParameterDetailType() {
        return this.helpParameterDetailType;
    }

    public void setHelpParameterDetailType(String helpParameterDetailType) {
        this.helpParameterDetailType = helpParameterDetailType;
    }

    /**
     * @return the lookupBusinessObjectClassName
     */
    public String getLookupBusinessObjectClassName() {
        return this.lookupBusinessObjectClassName;
    }

    /**
     * @param lookupBusinessObjectClassName the lookupBusinessObjectClassName to set
     */
    public void setLookupBusinessObjectClassName(String lookupBusinessObjectClassName) {
        this.lookupBusinessObjectClassName = lookupBusinessObjectClassName;
    }

    /**
     * @param searchDocumentTypeName the searchDocumentTypeName to set
     */
    public void setSearchDocumentTypeName(String searchDocumentTypeName) {
        this.searchDocumentTypeName = searchDocumentTypeName;
    }

    /**
     * @return the searchDocumentTypeName
     */
    public String getSearchDocumentTypeName() {
        return this.searchDocumentTypeName;
    }
}
