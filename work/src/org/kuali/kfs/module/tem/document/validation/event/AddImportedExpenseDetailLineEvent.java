/*
 * Copyright 2011 The Kuali Foundation.
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

import org.kuali.kfs.module.tem.businessobject.AbstractExpense;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;

/**
 * Event triggered when an {@link ImportedExpense} instance
 * is added to a {@link Document}
 */
public class AddImportedExpenseDetailLineEvent<E extends AbstractExpense> extends AttributedDocumentEventBase implements TEMExpenseLineEvent<E> {

    private final E importedExpense;
    
    /**
     * Constructs an AddExpenseLineEvent with the given errorPathPrefix, document, and otherExpense.
     * 
     * @param errorPathPrefix
     * @param document
     * @param groupTraveler
     */
    public AddImportedExpenseDetailLineEvent(String errorPathPrefix, Document document, E importedExpense) {
        super("adding importedExpenseLine to document " + getDocumentId(document), errorPathPrefix, document);
        this.importedExpense = importedExpense;
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

    }

    @Override
    public E getExpenseLine() {
        // TODO Auto-generated method stub
        return importedExpense;
    } 

}
