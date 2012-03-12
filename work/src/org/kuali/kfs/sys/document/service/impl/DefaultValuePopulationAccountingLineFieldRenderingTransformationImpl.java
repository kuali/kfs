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
