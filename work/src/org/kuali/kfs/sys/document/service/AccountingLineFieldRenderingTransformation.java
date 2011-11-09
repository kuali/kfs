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
package org.kuali.kfs.sys.document.service;

import java.util.Map;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.web.ui.Field;

/**
 * An interface that represents a transformation on a single field; they can be grouped
 * together and run at one time by FieldChangingAccountingLineRenderTransformImpl.
 */
public interface AccountingLineFieldRenderingTransformation {
   /**
     * Performs a transformation on the field
     * @param accountingLine the accounting line that is being rendered
     * @param field the field to transform
     * @param fieldDefinition the accounting line definition of the accounting line being rendered
     * @param unconvertedValues a map of unconverted values
     */
    public abstract void transformField(AccountingLine accountingLine, Field field, MaintainableFieldDefinition fieldDefinition, Map unconvertedValues);
}
