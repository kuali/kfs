/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.ValidationFieldConvertible;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation to have the data dictionary perform its validations upon a business object
 */
public class BusinessObjectDataDictionaryValidation extends GenericValidation {
    private DictionaryValidationService dictionaryValidationService;
    private PersistableBusinessObject businessObjectForValidation;
    private boolean attemptDeterminationOfErrorPrefix = false;
    private final static String PREFIX_FINDER_PATTERN = "^.*\\.?document";
    private final static Pattern PREFIX_FINDER = Pattern.compile(PREFIX_FINDER_PATTERN);

    /**
     * Validates a business object against the data dictionary
     * <strong>expects a business object to be the first parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.GenericValidation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        String determinedPrefix = null;
        if (isAttemptDeterminationOfErrorPrefix() && couldPossiblyMaybeUseAPrefixSure()) {
            determinedPrefix = determinePrefixIfPossible();
            if (!StringUtils.isBlank(determinedPrefix)) {
                GlobalVariables.getMessageMap().addToErrorPath(determinedPrefix);
            }
        }
        boolean result = getDictionaryValidationService().isBusinessObjectValid(businessObjectForValidation);
        if (!StringUtils.isBlank(determinedPrefix)) {
            GlobalVariables.getMessageMap().removeFromErrorPath(determinedPrefix);
        }
        return result;
    }

    /**
     * @return false if any prefix in the GlobalVariables message map error path includes "document", false otherwise
     */
    protected boolean couldPossiblyMaybeUseAPrefixSure() {
        for (String path : GlobalVariables.getMessageMap().getErrorPath()) {
            if (path.contains(KFSPropertyConstants.DOCUMENT)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return determines the prefix from the "businessObjectForValidation" parameter property if possible
     */
    protected String determinePrefixIfPossible() {
        String prefix = null;
        ValidationFieldConvertible convertible = findParameterForBusinessObjectForValidation();
        if (convertible != null) {
            if (convertible.getSourceEventProperty().contains(KFSPropertyConstants.DOCUMENT)) {
                Matcher prefixCleanMatch = PREFIX_FINDER.matcher(convertible.getSourceEventProperty());
                if (prefixCleanMatch != null) {
                    return prefixCleanMatch.replaceFirst(KFSPropertyConstants.DOCUMENT);
                } else {
                    return convertible.getSourceEventProperty();
                }
            }
        }
        return prefix;
    }

    /**
     * @return the validation parameter property which had a target validation property of "businessObjectForValidation"
     */
    protected ValidationFieldConvertible findParameterForBusinessObjectForValidation() {
        for (ValidationFieldConvertible convertible : getParameterProperties()) {
            if (KFSPropertyConstants.BUSINESS_OBJECT_FOR_VALIDATION.equals(convertible.getTargetValidationProperty())) {
                return convertible;
            }
        }
        return null;
    }

    /**
     * Gets the dictionaryValidationService attribute.
     * @return Returns the dictionaryValidationService.
     */
    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * Sets the dictionaryValidationService attribute value.
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    /**
     * Gets the businessObjectForValidation attribute.
     * @return Returns the businessObjectForValidation.
     */
    public PersistableBusinessObject getBusinessObjectForValidation() {
        return businessObjectForValidation;
    }

    /**
     * Sets the businessObjectForValidation attribute value.
     * @param businessObjectForValidation The businessObjectForValidation to set.
     */
    public void setBusinessObjectForValidation(PersistableBusinessObject businessObjectForValidation) {
        this.businessObjectForValidation = businessObjectForValidation;
    }

    /**
     * @return whether this validation should attempt to determine the error prefix for the check
     */
    public boolean isAttemptDeterminationOfErrorPrefix() {
        return attemptDeterminationOfErrorPrefix;
    }

    /**
     * Sets whether this validation should attempt to determine the error prefix for the check
     * @param attemptDeterminationOfErrorPrefix true if the error prefix should be automatically determined if possible; if false, automatic determination will be skipped - and that is the default behavior
     */
    public void setAttemptDeterminationOfErrorPrefix(boolean attemptDeterminationOfErrorPrefix) {
        this.attemptDeterminationOfErrorPrefix = attemptDeterminationOfErrorPrefix;
    }
}
