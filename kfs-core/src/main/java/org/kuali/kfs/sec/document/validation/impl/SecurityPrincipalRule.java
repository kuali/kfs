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
