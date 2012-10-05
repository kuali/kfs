/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument;
import org.kuali.kfs.module.endow.document.service.RegistrationCodeService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class EndowmentTransactionalDocumentBaseRule extends TransactionalDocumentRuleBase {
    /**
     * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field, but
     * applicable to the whole document).
     *
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     */
    protected void putGlobalError(String errorConstant) {
        if (!errorAlreadyExists(KRADConstants.DOCUMENT_ERRORS, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KRADConstants.DOCUMENT_ERRORS, errorConstant);
        }
    }

    /**
     * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field, but
     * applicable to the whole document).
     *
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameter - Replacement value for part of the error message.
     */
    protected void putGlobalError(String errorConstant, String parameter) {
        if (!errorAlreadyExists(KRADConstants.DOCUMENT_ERRORS, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KRADConstants.DOCUMENT_ERRORS, errorConstant, parameter);
        }
    }

    /**
     * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field, but
     * applicable to the whole document).
     *
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameters - Array of replacement values for part of the error message.
     */
    protected void putGlobalError(String errorConstant, String[] parameters) {
        if (!errorAlreadyExists(KRADConstants.DOCUMENT_ERRORS, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KRADConstants.DOCUMENT_ERRORS, errorConstant, parameters);
        }
    }

    /**
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     */
    protected void putFieldError(String propertyName, String errorConstant) {
        if (!errorAlreadyExists(propertyName, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(propertyName, errorConstant);
        }
    }

    /**
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameter - Single parameter value that can be used in the message so that you can display specific values to the
     *        user.
     */
    protected void putFieldError(String propertyName, String errorConstant, String parameter) {
        if (!errorAlreadyExists(propertyName, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(propertyName, errorConstant, parameter);
        }
    }

    /**
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameters - Array of strings holding values that can be used in the message so that you can display specific values
     *        to the user.
     */
    protected void putFieldError(String propertyName, String errorConstant, String[] parameters) {
        if (!errorAlreadyExists(propertyName, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(propertyName, errorConstant, parameters);
        }
    }

    /**
     * This method is a convenience method to add a property-specific document error to the global errors list. This method makes
     * sure that the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameter - Single parameter value that can be used in the message so that you can display specific values to the
     *        user.
     */
    protected void putDocumentError(String propertyName, String errorConstant, String parameter) {
        if (!errorAlreadyExists(MaintenanceDocumentRuleBase.DOCUMENT_ERROR_PREFIX + propertyName, errorConstant)) {
            GlobalVariables.getMessageMap().putError(MaintenanceDocumentRuleBase.DOCUMENT_ERROR_PREFIX + propertyName, errorConstant, parameter);
        }
    }

    /**
     * This method is a convenience method to add a property-specific document error to the global errors list. This method makes
     * sure that the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameters - Array of String parameters that can be used in the message so that you can display specific values to the
     *        user.
     */
    protected void putDocumentError(String propertyName, String errorConstant, String[] parameters) {
        GlobalVariables.getMessageMap().putError(MaintenanceDocumentRuleBase.DOCUMENT_ERROR_PREFIX + propertyName, errorConstant, parameters);
    }

    /**
     * Convenience method to determine whether the field already has the message indicated. This is useful if you want to suppress
     * duplicate error messages on the same field.
     *
     * @param propertyName - propertyName you want to test on
     * @param errorConstant - errorConstant you want to test
     * @return returns True if the propertyName indicated already has the errorConstant indicated, false otherwise
     */
    protected boolean errorAlreadyExists(String propertyName, String errorConstant) {

        if (GlobalVariables.getMessageMap().fieldHasMessage(propertyName, errorConstant)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * This method specifically doesn't put any prefixes before the error so that the developer can do things specific to the
     * globals errors (like newDelegateChangeDocument errors)
     *
     * @param propertyName
     * @param errorConstant
     */
    protected void putGlobalsError(String propertyName, String errorConstant) {
        if (!errorAlreadyExists(propertyName, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(propertyName, errorConstant);
        }
    }

    /**
     * This method specifically doesn't put any prefixes before the error so that the developer can do things specific to the
     * globals errors (like newDelegateChangeDocument errors)
     *
     * @param propertyName
     * @param errorConstant
     * @param parameter
     */
    protected void putGlobalsError(String propertyName, String errorConstant, String parameter) {
        if (!errorAlreadyExists(propertyName, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(propertyName, errorConstant, parameter);
        }
    }


    /**
     * This method obtains the Prefix for displaying errors on the UI for Source & Target lines.
     *
     * @param document
     * @param isSource
     * @return
     */
    protected String getEndowmentTransactionSecurityPrefix(EndowmentTransactionalDocument document, boolean isSource) {
        if (isSource) {
            return EndowPropertyConstants.TRANSACTION_SOURCE_SECURITY_PREFIX;
        }
        else {
            return EndowPropertyConstants.TRANSACTION_TARGET_SECURITY_PREFIX;
        }
    }

    /**
     * This method returns the Security line associated with a Transaction.
     *
     * @param document
     * @param isSource
     * @return
     */
    protected EndowmentTransactionSecurity getEndowmentTransactionSecurity(EndowmentTransactionalDocument document, boolean isSource) {
        if (isSource) {
            return ((EndowmentSecurityDetailsDocument) document).getSourceTransactionSecurity();
        }
        else {
            return ((EndowmentSecurityDetailsDocument) document).getTargetTransactionSecurity();
        }
    }

    /**
     * This method validate the Security code.
     *
     * @param tranSecurity
     * @return
     */
    protected boolean isSecurityCodeEmpty(EndowmentTransactionalDocument document, boolean isSource) {
        EndowmentTransactionSecurity tranSecurity = getEndowmentTransactionSecurity(document, isSource);

        if (StringUtils.isEmpty(tranSecurity.getSecurityID())) {
            putFieldError(getEndowmentTransactionSecurityPrefix(document, isSource) + EndowPropertyConstants.TRANSACTION_SECURITY_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_REQUIRED);
            return true;
        }

        return false;
    }

    /**
     * This method validates the Registration code.
     *
     * @param tranSecurity
     * @return
     */
    protected boolean isRegistrationCodeEmpty(EndowmentTransactionalDocument document, boolean isSource) {
        EndowmentTransactionSecurity tranSecurity = getEndowmentTransactionSecurity(document, isSource);

        if (StringUtils.isEmpty(tranSecurity.getRegistrationCode())) {
            putFieldError(getEndowmentTransactionSecurityPrefix(document, isSource) + EndowPropertyConstants.TRANSACTION_REGISTRATION_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_REGISTRATION_CODE_REQUIRED);
            return true;
        }

        return false;
    }


    /**
     * This method validates the Security code by trying to create a Security object from the code.
     *
     * @param document
     * @param isSource
     * @return
     */
    protected boolean validateSecurityCode(EndowmentSecurityDetailsDocument document, boolean isSource) {
        boolean success = true;
        String prefix = null;

        EndowmentTransactionSecurity tranSecurity = getEndowmentTransactionSecurity(document, isSource);

        Security security = SpringContext.getBean(SecurityService.class).getByPrimaryKey(tranSecurity.getSecurityID());
        tranSecurity.setSecurity(security);
        if (null == security) {
            putFieldError(getEndowmentTransactionSecurityPrefix(document, isSource) + EndowPropertyConstants.TRANSACTION_SECURITY_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_INVALID);
            success = false;
        }

        return success;
    }

    /**
     * This method validates the Registration code by trying to create a RegistrationCode object from the code.
     *
     * @param document
     * @param isSource
     * @return
     */
    protected boolean validateRegistrationCode(EndowmentSecurityDetailsDocument document, boolean isSource) {
        boolean success = true;
        String prefix = null;

        EndowmentTransactionSecurity tranSecurity = getEndowmentTransactionSecurity(document, isSource);

        RegistrationCode registrationCode = SpringContext.getBean(RegistrationCodeService.class).getByPrimaryKey(tranSecurity.getRegistrationCode());
        tranSecurity.setRegistrationCodeObj(registrationCode);
        if (null == registrationCode) {
            putFieldError(getEndowmentTransactionSecurityPrefix(document, isSource) + EndowPropertyConstants.TRANSACTION_REGISTRATION_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_REGISTRATION_CODE_INVALID);
            success = false;
        }

        return success;
    }

    /**
     * This method checks if the Security is Active.
     *
     * @param document
     * @param isSource
     * @return
     */
    protected boolean isSecurityActive(EndowmentSecurityDetailsDocument document, boolean isSource) {
        EndowmentTransactionSecurity tranSecurity = getEndowmentTransactionSecurity(document, isSource);

        if (tranSecurity.getSecurity().isActive()) {
            return true;
        }
        else {
            putFieldError(getEndowmentTransactionSecurityPrefix(document, isSource) + EndowPropertyConstants.TRANSACTION_SECURITY_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_INACTIVE);
            return false;
        }
    }

    /**
     * Validates that the security class code type is not Liability.
     *
     * @param endowmentTransactionSecurity
     * @return true is valid, false otherwise
     */
    protected boolean validateSecurityClassCodeTypeNotLiability(EndowmentSecurityDetailsDocument document, boolean isSource) {
        boolean isValid = true;
        EndowmentTransactionSecurity endowmentTransactionSecurity = getEndowmentTransactionSecurity(document, isSource);
        Security security = endowmentTransactionSecurity.getSecurity();
        if (ObjectUtils.isNotNull(security)) {
            ClassCode classCode = security.getClassCode();
            if (ObjectUtils.isNotNull(classCode)) {
                String classCodeType = classCode.getClassCodeType();
                if (EndowConstants.ClassCodeTypes.LIABILITY.equalsIgnoreCase(classCodeType)) {
                    isValid = false;

                    if (isSource) {
                        putFieldError(EndowPropertyConstants.TRANSACTION_SOURCE_SECURITY_PREFIX + EndowPropertyConstants.TRANSACTION_SECURITY_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_NOT_LIABILITY);
                    }
                    else {
                        putFieldError(EndowPropertyConstants.TRANSACTION_TARGET_SECURITY_PREFIX + EndowPropertyConstants.TRANSACTION_SECURITY_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_NOT_LIABILITY);
                    }
                }
            }
        }

        return isValid;

    }

    /**
     * This method checks if the Registration Code is Active.
     *
     * @param document
     * @param isSource
     * @return
     */
    protected boolean isRegistrationCodeActive(EndowmentSecurityDetailsDocument document, boolean isSource) {
        EndowmentTransactionSecurity tranSecurity = getEndowmentTransactionSecurity(document, isSource);

        if (tranSecurity.getRegistrationCodeObj().isActive()) {
            return true;
        }
        else {
            putFieldError(getEndowmentTransactionSecurityPrefix(document, isSource) + EndowPropertyConstants.TRANSACTION_REGISTRATION_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_REGISTRATION_CODE_INACTIVE);
            return false;
        }
    }

    /**
     * This method validates the Security Class Type Code.
     *
     * @param document
     * @param isSource
     * @param classCodeType
     * @return
     */
    protected boolean validateSecurityClassTypeCode(EndowmentSecurityDetailsDocument document, boolean isSource, String classCodeType) {
        EndowmentTransactionSecurity tranSecurity = getEndowmentTransactionSecurity(document, isSource);
        tranSecurity.getSecurity().refreshNonUpdateableReferences();

        if (tranSecurity.getSecurity().getClassCode().getClassCodeType().equalsIgnoreCase(classCodeType)) {
            return true;
        }
        else {
            putFieldError(getEndowmentTransactionSecurityPrefix(document, isSource) + EndowPropertyConstants.TRANSACTION_SECURITY_ID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_CLASS_CODE_MISMATCH);
            return false;
        }
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        EndowmentTransactionalDocument endowmentTransactionalDocument = null;

        if (isValid) {
            endowmentTransactionalDocument = (EndowmentTransactionalDocument) document;

            // Validates Tx Sub Type Code
            isValid &= isSubTypeEmpty(endowmentTransactionalDocument);
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * This method validates the Sub Type Code.
     *
     * @param document
     * @return
     */
    protected boolean isSubTypeEmpty(EndowmentTransactionalDocument document) {
        boolean success = true;

        if (StringUtils.isEmpty(document.getTransactionSubTypeCode())) {
            putFieldError(EndowConstants.TRANSACTION_DETAILS_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_DETAILS_SUB_TYPE_REQUIRED);
            success = false;
        }

        return success;
    }

}
