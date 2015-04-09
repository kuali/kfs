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
package org.kuali.kfs.sys.document.service.impl;

import java.util.Map;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * A field transformer that populates a field with its default value
 */
public class DefaultValuePopulationAccountingLineFieldRenderingTransformationImpl implements AccountingLineFieldRenderingTransformation {

    /**
     * Using the data dictionary definition for the field, determines what the default value for this field would be should there be a default value defined;
     * note that this value may be wiped out by the value from the business object during that transformation (which presumably happends after this one)
     * @see org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation#transformField(org.kuali.kfs.sys.document.web.AccountingLineViewField)
     */
    public void transformField(AccountingLine accountingLine, Field field, MaintainableFieldDefinition fieldDefinition, Map unconvertedValues) {
        populateFieldWithDefault(field, fieldDefinition);
    }

    /**
     * Populates a maintenance field with its default value
     * @param field the field to populate with a default value
     * @param fieldDefinition the data dictionary definition of the field to transform
     * 
     * KRAD Conversion: Performs the customization of the field properties
     */
    protected void populateFieldWithDefault(Field field, MaintainableFieldDefinition fieldDefinition) {
        try {
            Class defaultValueFinderClass = fieldDefinition.getDefaultValueFinderClass();
            if (defaultValueFinderClass != null) {
                field.setPropertyValue(((ValueFinder) defaultValueFinderClass.newInstance()).getValue());
            }
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Default Value Finder Class "+fieldDefinition.getDefaultValueFinderClass().getName()+" for property "+field.getPropertyName()+" could not be instantiated");
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Default Value Finder Class "+fieldDefinition.getDefaultValueFinderClass().getName()+" for property "+field.getPropertyName()+" was accessed illegally");
        }
    }
}
