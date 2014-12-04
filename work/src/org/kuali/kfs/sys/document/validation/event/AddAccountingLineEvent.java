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
package org.kuali.kfs.sys.document.validation.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;

public class AddAccountingLineEvent extends AttributedDocumentEventBase implements AccountingLineEvent {
    private final AccountingLine accountingLine;
    
    /**
     * Constructs an AddAccountingLineEvent with the given errorPathPrefix, document, and accountingLine.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public AddAccountingLineEvent(String errorPathPrefix, Document document, AccountingLine accountingLine) {
        super("adding accountingLine to document " + getDocumentId(document), errorPathPrefix, document);
        this.accountingLine = accountingLine;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.AccountingLineEvent#getAccountingLine()
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    /**
     * Overridden to call parent and then clean up the error messages.
     * @see org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    @Override
    public boolean invokeRuleMethod(BusinessRule rule) {
        boolean result = super.invokeRuleMethod(rule);
        cleanErrorMessages();
        return result;
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
        for (Iterator i = GlobalVariables.getMessageMap().getPropertiesWithErrors().iterator(); i.hasNext();) {
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
                    for (ListIterator errors = GlobalVariables.getMessageMap().getMessages(property).listIterator(); errors.hasNext();) {
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
