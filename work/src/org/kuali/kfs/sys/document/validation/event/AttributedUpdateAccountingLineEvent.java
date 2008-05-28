/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.rule.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;

public class AttributedUpdateAccountingLineEvent extends AttributedDocumentEventBase {
    private final AccountingLine accountingLine;
    private final AccountingLine updatedAccountingLine;

    /**
     * Constructs an UpdateAccountingLineEvent with the given errorPathPrefix, document, and accountingLine.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     * @param newAccountingLine
     */
    public AttributedUpdateAccountingLineEvent(String errorPathPrefix, Document document, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        super("updating accountingLine in document " + getDocumentId(document), errorPathPrefix, document);
        this.accountingLine = originalAccountingLine;
        this.updatedAccountingLine = updatedAccountingLine;
    }
    
    /**
     * @see org.kuali.core.rule.event.AccountingLineEvent#getAccountingLine()
     */
    public AccountingLine getOriginalAccountingLine() {
        return accountingLine;
    }

    /**
     * @return updated accountingLine associated with this event
     */
    public AccountingLine getUpdatedAccountingLine() {
        return updatedAccountingLine;
    }
    
    /**
     * @see org.kuali.kfs.rule.event.AttributedDocumentEventBase#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        boolean result = super.invokeRuleMethod(rule);
        cleanErrorMessages();
        return result;
    }

    /**
     * @return the original accounting line, by a more traditional name
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }
    
    /**
     * Logic to replace generic amount error messages, especially those where extraordinarily large amounts caused format errors
     */
    public void cleanErrorMessages() {
        // create a list of accounting line attribute keys
        ArrayList linePatterns = new ArrayList();
        // source patterns: removing wildcards
        linePatterns.addAll(Arrays.asList(StringUtils.replace(KFSConstants.SOURCE_ACCOUNTING_LINE_ERROR_PATTERN, "*", "").split(",")));
        // target patterns: removing wildcards
        linePatterns.addAll(Arrays.asList(StringUtils.replace(KFSConstants.TARGET_ACCOUNTING_LINE_ERROR_PATTERN, "*", "").split(",")));

        // see if any lines have errors
        for (Iterator i = GlobalVariables.getErrorMap().getPropertiesWithErrors().iterator(); i.hasNext();) {
            String property = (String) i.next();
            // only concerned about amount field errors
            if (property.endsWith("." + KFSConstants.AMOUNT_PROPERTY_NAME)) {
                // check if the amount field is associated with an accounting line
                boolean isLineProperty = true;
                for (Iterator linePatternsIterator = linePatterns.iterator(); i.hasNext() && !isLineProperty;) {
                    isLineProperty = property.startsWith((String) linePatternsIterator.next());
                }
                if (isLineProperty) {
                    // find the specific error messages for the property
                    for (ListIterator errors = GlobalVariables.getErrorMap().getMessages(property).listIterator(); errors.hasNext();) {
                        ErrorMessage error = (ErrorMessage) errors.next();
                        String errorKey = null;
                        String[] params = new String[2];
                        if (StringUtils.equals(KFSKeyConstants.ERROR_INVALID_FORMAT, error.getErrorKey())) {
                            errorKey = KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_FORMAT;
                            params[1] = accountingLine.getAmount().toString();
                        }
                        else {
                            if (StringUtils.equals(KFSKeyConstants.ERROR_MAX_LENGTH, error.getErrorKey())) {
                                errorKey = KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_MAX_LENGTH;

                                // String value = ObjectUtils.getPropertyValue(accountingLine,
                                // KFSConstants.AMOUNT_PROPERTY_NAME)

                            }
                        }
                        if (errorKey != null) {
                            // now replace error message
                            error.setErrorKey(errorKey);
                            // replace parameters
                            params[0] = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(accountingLine.getClass(), KFSConstants.AMOUNT_PROPERTY_NAME);
                            error.setMessageParameters(params);
                            // put back where it came form
                            errors.set(error);
                        }
                    }
                }
            }
        }
    }
}
