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
