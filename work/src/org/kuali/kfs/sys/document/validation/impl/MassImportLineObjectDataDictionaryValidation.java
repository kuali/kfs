package org.kuali.kfs.sys.document.validation.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.document.MassImportDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.util.AutoPopulatingList;

/**
 * A validation to have the data dictionary perform its validations upon a business object
 */
public class MassImportLineObjectDataDictionaryValidation extends GenericValidation {
    private static Logger LOG = Logger.getLogger(MassImportLineObjectDataDictionaryValidation.class);
    // Mass upload sub-account, sub-object, project
    private DictionaryValidationService dictionaryValidationService;
    private List<MassImportLineBase> importedLines;
    private MassImportLineBase importedLineForValidation;


    // map for mapping original DD validation error to mass import error key
    private static final Map<String, String> errorKeyMapKNSToImport = new HashMap<String, String>();
    static {
        errorKeyMapKNSToImport.put(RiceKeyConstants.ERROR_MAX_LENGTH, KFSKeyConstants.ERROR_MASSIMPORT_MAXLENGTH);
        errorKeyMapKNSToImport.put(RiceKeyConstants.ERROR_EXCLUSIVE_MIN, KFSKeyConstants.ERROR_MASSIMPORT_EXCLUSIVEMIN);
        errorKeyMapKNSToImport.put(RiceKeyConstants.ERROR_INCLUSIVE_MAX, KFSKeyConstants.ERROR_MASSIMPORT_INCLUSIVEMAX);
        errorKeyMapKNSToImport.put(RiceKeyConstants.ERROR_REQUIRED, KFSKeyConstants.ERROR_MASSIMPORT_REQUIRED);
    }

    /**
     * Validates a business object against the data dictionary <strong>expects a business object to be the first parameter</strong>
     *
     * @see org.kuali.kfs.sys.document.validation.GenericValidation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        if (importedLines != null && !importedLines.isEmpty()) {
            valid &= validateAllImportedLines(event, valid);
        }
        else {
            valid &= getDictionaryValidationService().isBusinessObjectValid(importedLineForValidation);
        }

        return valid;
    }

    /**
     * Validate all imported lines
     *
     * @param event
     * @param valid
     * @return
     */
    protected boolean validateAllImportedLines(AttributedDocumentEvent event, boolean valid) {
        int importedLineNumber = 0;

        String errorPathPrefix = ((MassImportDocument) event.getDocument()).getErrorPathPrefix();
        for (MassImportLineBase importedLine : importedLines) {
            String errorPath = errorPathPrefix + Integer.toString(importedLineNumber) + KFSConstants.SubAccountImportConstants.IMPORT_LINE_ERROR_SUFFIX;
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
            if (!getDictionaryValidationService().isBusinessObjectValid(importedLine)) {
                valid = false;
            }
            importedLineNumber++;
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
        }
        if (!valid) {
            replaceErrorMessageForMassImport(errorPathPrefix);
        }
        return valid;
    }

    /**
     * Replace the DD generated error message by customized mass import error
     */
    protected void replaceErrorMessageForMassImport(String errorPathPrefix) {
        List<String> errorProperties = GlobalVariables.getMessageMap().getPropertiesWithErrors();

        for (String errorProperty : errorProperties) {
            if (errorProperty.contains(errorPathPrefix)) {
                // get the line number from error message
                String importedLineNumber = StringUtils.substringBetween(errorProperty, errorPathPrefix, "]");
                AutoPopulatingList<ErrorMessage> errorMessages = GlobalVariables.getMessageMap().getErrorMessagesForProperty(errorProperty);
                for (Iterator iterator = errorMessages.iterator(); iterator.hasNext();) {
                    ErrorMessage errorMessage = (ErrorMessage) iterator.next();
                    for (String oriErrorKey : errorKeyMapKNSToImport.keySet()) {
                        if (StringUtils.equalsIgnoreCase(oriErrorKey, errorMessage.getErrorKey())) {
                            // replace error key with line number specified
                            errorMessage.setErrorKey(errorKeyMapKNSToImport.get(oriErrorKey));

                            // replace error parameters with line number as the first parameter
                            String[] messageParms = new String[(errorMessage.getMessageParameters().length + 1)];
                            // to display the exact line number starting from 1
                            messageParms[0] = String.valueOf(Integer.parseInt(importedLineNumber) + 1);
                            for (int j = 1; j <= errorMessage.getMessageParameters().length; j++) {
                                messageParms[j] = errorMessage.getMessageParameters()[j - 1];
                            }
                            errorMessage.setMessageParameters(messageParms);
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the dictionaryValidationService attribute.
     *
     * @return Returns the dictionaryValidationService.
     */
    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * Sets the dictionaryValidationService attribute value.
     *
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    /**
     * Gets the importedLines attribute.
     *
     * @return Returns the importedLines.
     */
    public List<MassImportLineBase> getImportedLines() {
        return importedLines;
    }

    /**
     * Sets the importedLines attribute value.
     *
     * @param importedLines The importedLines to set.
     */
    public void setImportedLines(List<MassImportLineBase> importedLines) {
        this.importedLines = importedLines;
    }

    /**
     * Gets the importedLineForValidation attribute.
     *
     * @return Returns the importedLineForValidation.
     */
    public MassImportLineBase getImportedLineForValidation() {
        return importedLineForValidation;
    }

    /**
     * Sets the importedLineForValidation attribute value.
     *
     * @param importedLineForValidation The importedLineForValidation to set.
     */
    public void setImportedLineForValidation(MassImportLineBase importedLineForValidation) {
        this.importedLineForValidation = importedLineForValidation;
    }


}
