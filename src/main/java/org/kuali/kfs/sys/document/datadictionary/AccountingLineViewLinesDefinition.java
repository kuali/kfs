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
import org.kuali.kfs.sys.document.web.AccountingLineViewLineFillingElement;
import org.kuali.kfs.sys.document.web.AccountingLineViewLines;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;

/**
 * Data dictionary definition for a group of multiple lines to render.  This also renders blocks - though each block will be rendered as a line with an embedded table
 */
public class AccountingLineViewLinesDefinition extends DataDictionaryDefinitionBase implements AccountingLineViewRenderableElementDefinition {
    private List<AccountingLineViewLineFillingDefinition> lines;
    private String elementName;

    /**
     * Validates that:
     * 1) there is at least one child line
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (lines == null || lines.size() == 0) {
            throw new AttributeValidationException("Please specify at least one child line for the lines definition");
        }
    }

    /**
     * Gets the lines attribute. 
     * @return Returns the lines.
     */
    public List<AccountingLineViewLineFillingDefinition> getLines() {
        return lines;
    }

    /**
     * Sets the lines attribute value.
     * @param lines The lines to set.
     */
    public void setLines(List<AccountingLineViewLineFillingDefinition> lines) {
        this.lines = lines;
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
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement()
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        AccountingLineViewLines layoutElement = new AccountingLineViewLines();
        layoutElement.setDefinition(this);
        layoutElement.setElements(getLayoutElementsForLines(accountingLineClass));
        return layoutElement;
    }
    
    /**
     * Generates layout elements for all the child lines of this lines definition
     * @return a List with the line elements for all child lines of this element definition 
     */
    protected List<AccountingLineViewLineFillingElement> getLayoutElementsForLines(Class<? extends AccountingLine> accountingLineClass) {
        List<AccountingLineViewLineFillingElement> elements = new ArrayList<AccountingLineViewLineFillingElement>();
        for (AccountingLineViewLineFillingDefinition line : lines) {
            elements.add(line.createLineFillingLayoutElement(accountingLineClass));
        }
        return elements;
    }
}
