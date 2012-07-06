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
package org.kuali.kfs.module.tem.document.validation.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;

public class AddTravelAdvanceLineEvent extends AttributedDocumentEventBase implements TravelAdvanceLineEvent {

    private final TravelAdvance travelAdvance;
    
    /**
     * Constructs an AddTravelAdvanceLineEvent with the given errorPathPrefix, document, and travelAdvance.
     * 
     * @param errorPathPrefix
     * @param document
     * @param travelAdvanceLine
     */
    public AddTravelAdvanceLineEvent(String errorPathPrefix, Document document, TravelAdvance travelAdvance) {
        super("adding travelAdvanceLine to document " + getDocumentId(document), errorPathPrefix, document);
        this.travelAdvance = travelAdvance;
    }
    
    @Override
    public TravelAdvance getTravelAdvance() {
        return travelAdvance;
    }
    
    /**
     * Overridden to call parent and then clean up the error messages.
     * @see org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
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
        /*// create a list of accounting line attribute keys
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
        }*/
    }

}
