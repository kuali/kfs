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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class...
 */
public class SecurityPreRule extends MaintenancePreRulesBase {

    private Security newSecurity;

    /**
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {

        boolean preRulesOK = true;
        setupConvenienceObjects(maintenanceDocument);

        if (KFSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceDocument.getNewMaintainableObject().getMaintenanceAction()) || KFSConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceDocument.getNewMaintainableObject().getMaintenanceAction())) {

            String securityID = newSecurity.getUserEnteredSecurityIDprefix();

            // the user should enter the first 8 characters of the security and the system will compute the ninth digit
            if (StringUtils.isEmpty(securityID) || securityID.length() != 8) {
                GlobalVariables.getMessageMap().putError(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + EndowPropertyConstants.SECURITY_USER_ENTERED_ID_PREFIX, EndowKeyConstants.SecurityConstants.ERROR_ENTERED_SECURITY_ID_SHOULD_BE_8_CHARS);
            }
            else {
                // compute the ninth digit of the security ID using the mod10 algorithm
                KEMService kemService = SpringContext.getBean(KEMService.class);

                securityID += kemService.mod10(securityID);

                newSecurity.setId(securityID);
            }

            // set default unit value to be -1 if Liability and 1 otherwise when creating a new security
            if (ObjectUtils.isNotNull(newSecurity.getClassCode())) {

                if (EndowConstants.ClassCodeTypes.LIABILITY.equals(newSecurity.getClassCode().getClassCodeType())) {
                    newSecurity.setUnitValue(new BigDecimal(-1));
                }
                else {
                    // when a security has a class code set to allow market instead of unit valuation, the system is setting
                    // the unit value to null
                    if (EndowConstants.ValuationMethod.MARKET.equalsIgnoreCase(newSecurity.getClassCode().getValuationMethod())) {
                        newSecurity.setUnitValue(null);
                    }
                    // by default unit value is 1
                    else {
                        newSecurity.setUnitValue(BigDecimal.ONE);
                    }
                }
            }
        }

        preRulesOK &= conditionallyAskQuestion(maintenanceDocument);
        return preRulesOK;
    }

    /**
     * Ask conditionally question after the security ID is computed using the mod10 method.
     * 
     * @param maintenanceDocument the Security Maintenance document
     * @return true
     */
    private boolean conditionallyAskQuestion(MaintenanceDocument maintenanceDocument) {

        Security newSecurity = (Security) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        boolean shouldAskQuestion = maintenanceDocument.isNew() && GlobalVariables.getMessageMap().hasNoErrors();

        if (shouldAskQuestion) {
            String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(EndowKeyConstants.SecurityConstants.MESSAGE_SECURITY_IS_NINTH_DIGIT_CORRECT);
            questionText = StringUtils.replace(questionText, "{0}", newSecurity.getId());
            boolean confirm = super.askOrAnalyzeYesNoQuestion(EndowKeyConstants.SecurityConstants.GENERATE_SECURITY_ID_QUESTION_ID, questionText);
            if (!confirm) {
                super.abortRulesCheck();
            }
        }
        return true;
    }

    /**
     * Set value for newSecurity.
     * 
     * @param document the Security Maintenance document
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newSecurity convenience objects, make sure all possible sub-objects are populated
        newSecurity = (Security) document.getNewMaintainableObject().getBusinessObject();
        newSecurity.refreshNonUpdateableReferences();
    }

}
