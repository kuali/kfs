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
