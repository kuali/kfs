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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.AccountingLineViewLine;
import org.kuali.kfs.sys.document.web.AccountingLineViewLineFillingElement;
import org.kuali.kfs.sys.document.web.RenderableElement;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;

/**
 * Data dictionary definition of a collection of elements which will be rendered as one table row in the table of each accounting line.
 */
public class AccountingLineViewLineDefinition extends DataDictionaryDefinitionBase implements AccountingLineViewLineFillingDefinition {
    private List<? extends AccountingLineViewRenderableElementDefinition> cells;
    private String elementName;

    /**
     * Validates that:
     * 1) there is at least one child element
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (cells == null || cells.size() == 0) {
            throw new AttributeValidationException("At least one field must be specified to live within an AccountingLineViewLine"+(!StringUtils.isBlank(elementName) ? " ("+elementName+")" : ""));
        }
    }

    /**
     * Gets the fields attribute. 
     * @return Returns the fields.
     */
    public List<? extends AccountingLineViewRenderableElementDefinition> getFields() {
        return cells;
    }

    /**
     * Sets the fields attribute value.
     * @param fields The fields to set.
     */
    public void setFields(List<? extends AccountingLineViewRenderableElementDefinition> fields) {
        this.cells = fields;
    }

    /**
     * Gets the elementName attribute. 
     * @return Returns the elementName.
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * Sets the elementName attribute value.
     * @param elementName The elementName to set.
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement(java.lang.Class)
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        AccountingLineViewLine line = new AccountingLineViewLine();
        line.setDefinition(this);
        line.setElements(getChildrenRenderableElements(accountingLineClass));
        return line;
    }
    
    /**
     * Creates children renderable elements for all children of this line definition
     * @param accountingLineClass accounting line class to pass through
     * @return a List of renderable children elements
     */
    protected List<RenderableElement> getChildrenRenderableElements(Class<? extends AccountingLine> accountingLineClass) {
        List<RenderableElement> elements = new ArrayList<RenderableElement>();
        for (AccountingLineViewRenderableElementDefinition cellDefinition : cells) {
            final RenderableElement element = (RenderableElement)cellDefinition.createLayoutElement(accountingLineClass);
            if (element != null) {
                elements.add(element);
            }
        }
        return elements;
    }

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewLineFillingDefinition#createLineFillingLayoutElement(java.lang.Class)
     */
    public AccountingLineViewLineFillingElement createLineFillingLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        return (AccountingLineViewLineFillingElement)createLayoutElement(accountingLineClass);
    }
    
}
