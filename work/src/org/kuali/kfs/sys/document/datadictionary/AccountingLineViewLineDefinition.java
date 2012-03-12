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
