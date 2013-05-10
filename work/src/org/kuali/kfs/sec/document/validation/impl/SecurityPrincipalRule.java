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
package org.kuali.kfs.sec.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sec.businessobject.SecurityPrincipal;
import org.kuali.kfs.sec.businessobject.SecurityPrincipalDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * Implements business rules checks on the SecurityPrincipal maintenance document
 */
public class SecurityPrincipalRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityPrincipalRule.class);

    private SecurityPrincipal oldSecurityPrincipal;
    private SecurityPrincipal newSecurityPrincipal;

    public SecurityPrincipalRule() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(document);

        isValid &= validateSecurityPrincipal();

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        isValid &= validateSecurityPrincipal();

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);

        if (SecPropertyConstants.PRINCIPAL_DEFINITIONS.equals(collectionName)) {
            isValid &= validatePrincipalDefinition((SecurityPrincipalDefinition) line, "");
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        oldSecurityPrincipal = (SecurityPrincipal) super.getOldBo();
        newSecurityPrincipal = (SecurityPrincipal) super.getNewBo();
    }

    /**
     * Validates the new security principal record
     * 
     * @return boolean true if validation was successful, false if there are errors
     */
    protected boolean validateSecurityPrincipal() {
        boolean isValid = true;

        int index = 0;
        for (SecurityPrincipalDefinition principalDefinition : newSecurityPrincipal.getPrincipalDefinitions()) {
            String errorKeyPrefix = KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + SecPropertyConstants.PRINCIPAL_DEFINITIONS + "[" + index + "].";

            boolean principalDefinitionValid = validatePrincipalDefinition(principalDefinition, errorKeyPrefix);
            if (!principalDefinitionValid) {
                isValid = false;
            }

            index++;
        }

        return isValid;
    }

    /**
     * Validates a definition assignment to the principal
     * 
     * @param principalDefinition SecurityPrincipalDefinition to validate
     * @param errorKeyPrefix String errorPrefix to use if any errors are found
     * @return boolean true if validation was successful, false if there are errors
     */
    protected boolean validatePrincipalDefinition(SecurityPrincipalDefinition principalDefinition, String errorKeyPrefix) {
        boolean isValid = true;

        principalDefinition.refreshNonUpdateableReferences();
        
        if (ObjectUtils.isNull(principalDefinition.getSecurityDefinition())) {
            return false;
        }

        String attributeName = principalDefinition.getSecurityDefinition().getSecurityAttribute().getName();
        String attributeValue = principalDefinition.getAttributeValue();
        
        // if value is blank (which is allowed) no need to validate
        if (StringUtils.isBlank(attributeValue)) {
            return true;
        }

        // descend attributes do not allow multiple values or wildcards, and operator must be equal
        if (SecConstants.SecurityAttributeNames.CHART_DESCEND_HIERARCHY.equals(attributeName) || SecConstants.SecurityAttributeNames.ORGANIZATION_DESCEND_HIERARCHY.equals(attributeName)) {
            if (StringUtils.contains(attributeValue, SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER)) {
                GlobalVariables.getMessageMap().putError(errorKeyPrefix + SecPropertyConstants.ATTRIBUTE_VALUE, SecKeyConstants.ERROR_MODEL_DEFINITION_MULTI_ATTR_VALUE, attributeName);
                isValid = false;
            }

            if (StringUtils.contains(attributeValue, SecConstants.SecurityValueSpecialCharacters.WILDCARD_CHARACTER)) {
                GlobalVariables.getMessageMap().putError(errorKeyPrefix + SecPropertyConstants.ATTRIBUTE_VALUE, SecKeyConstants.ERROR_MODEL_DEFINITION_WILDCARD_ATTR_VALUE, attributeName);
                isValid = false;
            }

            if (!SecConstants.SecurityDefinitionOperatorCodes.EQUAL.equals(principalDefinition.getOperatorCode())) {
                GlobalVariables.getMessageMap().putError(errorKeyPrefix + SecPropertyConstants.OPERATOR_CODE, SecKeyConstants.ERROR_MODEL_DEFINITION_OPERATOR_CODE_NOT_EQUAL, attributeName);
                isValid = false;
            }
        }

        // validate attribute value for existence
        isValid = isValid && SecurityValidationUtil.validateAttributeValue(attributeName, attributeValue, errorKeyPrefix);

        return isValid;
    }

}
