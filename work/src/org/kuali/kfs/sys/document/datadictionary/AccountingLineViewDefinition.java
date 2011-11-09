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
package org.kuali.kfs.sys.document.datadictionary;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;

/**
 * Data dictionary definition of information about how to render an accounting line.
 */
public class AccountingLineViewDefinition extends DataDictionaryDefinitionBase {
    private List<AccountingLineViewRenderableElementDefinition> elements;

    /**
     * Checks that this accounting line view has at least one child renderable element.  Also checks
     * that none of its direct children elements are "line" elements 
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (elements == null || elements.size() == 0) {
            // there's not even one element. not even one.
            throw new AttributeValidationException("Please specify at least one element to be rendered for an accounting line view.");
        }
        for (AccountingLineViewRenderableElementDefinition elementDefinition : elements) {
            if (elementDefinition instanceof AccountingLineViewLineDefinition) {
                throw new AttributeValidationException("AccountingViewLine definitions must always be wrapped by AccountingLineViewLines definitions");
            }
        }
    }

    /**
     * Gets the elements attribute. 
     * @return Returns the elements.
     */
    public List<AccountingLineViewRenderableElementDefinition> getElements() {
        return elements;
    }

    /**
     * Sets the elements attribute value.
     * @param elements The elements to set.
     */
    public void setElements(List<AccountingLineViewRenderableElementDefinition> elements) {
        this.elements = elements;
    }
    
    /**
     * Creates a list of layout elements for this accounting line view
     * @param accountingLineClass the class of the accounting line to be rendered by this view
     * @return a List of TableJoining layout elements that represent how the accounting line should be rendered
     */
    public List<TableJoining> getAccountingLineLayoutElements(Class<? extends AccountingLine> accountingLineClass) {
        List<TableJoining> layoutElements = new ArrayList<TableJoining>();
        for (AccountingLineViewRenderableElementDefinition layoutElementDefinition : elements) {
            final TableJoining layoutElement = layoutElementDefinition.createLayoutElement(accountingLineClass);
            if (layoutElement != null) {
                layoutElements.add(layoutElement);
            }
        }
        return layoutElements;
    }
}
