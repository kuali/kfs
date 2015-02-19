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
