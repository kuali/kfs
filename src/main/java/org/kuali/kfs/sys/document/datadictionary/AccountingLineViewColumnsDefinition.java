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
import org.kuali.kfs.sys.document.web.AccountingLineViewColumns;
import org.kuali.kfs.sys.document.web.AccountingLineViewField;
import org.kuali.kfs.sys.document.web.AccountingLineViewLineFillingElement;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;

/**
 * 
 */
public class AccountingLineViewColumnsDefinition extends DataDictionaryDefinitionBase implements AccountingLineViewLineFillingDefinition {
    private int columnCount = 1;
    private List<AccountingLineViewFieldDefinition> fields;
    private String name;
    
    /**
     * 
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement(java.lang.Class)
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        List<AccountingLineViewField> layoutFields = new ArrayList<AccountingLineViewField>();
        
        for (AccountingLineViewFieldDefinition fieldDefinition : fields) {
            final AccountingLineViewField field = (AccountingLineViewField)fieldDefinition.createLayoutElement(accountingLineClass);
            if (field != null) {
                layoutFields.add(field);
            }
        }
        
        return new AccountingLineViewColumns(this, layoutFields);
    }

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewLineFillingDefinition#createLineFillingLayoutElement(java.lang.Class)
     */
    public AccountingLineViewLineFillingElement createLineFillingLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        return (AccountingLineViewLineFillingElement)createLayoutElement(accountingLineClass);
    }

    /**
     * 
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (StringUtils.isBlank(name)) {
            throw new AttributeValidationException("name for "+rootBusinessObjectClass.getName()+" accounting line view columns definition must be defined");
        }
        if (columnCount < 1) {
            throw new AttributeValidationException("columnCount for "+rootBusinessObjectClass.getName()+" accounting line view columns data dictionary definition must be one or greater");
        }
        if (fields == null || fields.size() == 0) {
            throw new AttributeValidationException("Please add at least one field to the "+rootBusinessObjectClass.getName()+" accounting line view columns definition");
        }
    }

    /**
     * Gets the columnCount attribute. 
     * @return Returns the columnCount.
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Sets the columnCount attribute value.
     * @param columnCount The columnCount to set.
     */
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    /**
     * Gets the fields attribute. 
     * @return Returns the fields.
     */
    public List<AccountingLineViewFieldDefinition> getFields() {
        return fields;
    }

    /**
     * Sets the fields attribute value.
     * @param fields The fields to set.
     */
    public void setFields(List<AccountingLineViewFieldDefinition> fields) {
        this.fields = fields;
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
