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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.AccountingLineViewLineFillingElement;
import org.kuali.kfs.sys.document.web.HideShowLayoutElement;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;

/**
 * Defines a set of lines of are displayed within a hide/show block
 */
public class AccountingLineViewHideShowLinesDefinition extends DataDictionaryDefinitionBase implements AccountingLineViewLineFillingDefinition {
    private String label;
    private String name;
    private List<AccountingLineViewLineFillingDefinition> lines;

    /**
     * Validates that name has been set and that at least one line element has been specified
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (StringUtils.isBlank(name)) {
            throw new AttributeValidationException("Please specify a name for the Hide/Show lines element");
        }
        if (lines == null || lines.size() == 0) {
            throw new AttributeValidationException("Please specify at least one child line for the Hide/Show lines element");
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement(java.lang.Class)
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        HideShowLayoutElement hideShowElement = new HideShowLayoutElement();
        hideShowElement.setDefinition(this);
        for (AccountingLineViewLineFillingDefinition line : lines) {
            hideShowElement.addLine(line.createLineFillingLayoutElement(accountingLineClass));
        }
        return hideShowElement;
    }

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewLineFillingDefinition#createLineFillingLayoutElement(java.lang.Class)
     */
    public AccountingLineViewLineFillingElement createLineFillingLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        return (AccountingLineViewLineFillingElement)createLayoutElement(accountingLineClass);
    }

    /**
     * Gets the label attribute. 
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label attribute value.
     * @param label The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
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
     * Gets the name attribute. 
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute value.
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
}
