/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;

/**
 * This class gets all the values of a parameter and then builds a list of key label pairs out of them, using each parameter value
 * as both key and label
 */
public class ParameterValuesFinder extends KeyValuesBase {
    private Class componentClass;
    private String parameterName;
    private boolean insertBlankRow = true;

    public ParameterValuesFinder() {
    }

    public ParameterValuesFinder(Class componentClass, String parameterName) {
        this.componentClass = componentClass;
        this.parameterName = parameterName;
    }

    public List getKeyValues() {
        List keyLabels = new ArrayList();
        List<String> parameterValues = SpringContext.getBean(ParameterService.class).getParameterValues(this.componentClass, this.parameterName);
        if (insertBlankRow) {
            keyLabels.add(new KeyLabelPair("", ""));
        }
        if (parameterValues != null) {
            for (String parameterValue : parameterValues) {
                keyLabels.add(new KeyLabelPair(parameterValue, parameterValue));
            }
        }
        return keyLabels;
    }

    /**
     * Gets the insertBlankRow attribute.
     * 
     * @return Returns the insertBlankRow.
     */
    public boolean shouldInsertBlankRow() {
        return insertBlankRow;
    }

    /**
     * Sets the insertBlankRow attribute value.
     * 
     * @param insertBlankRow The insertBlankRow to set.
     */
    public void setInsertBlankRow(boolean insertBlankRow) {
        this.insertBlankRow = insertBlankRow;
    }

    /**
     * Gets the componentClass attribute.
     * 
     * @return Returns the componentClass.
     */
    public Class getComponentClass() {
        return componentClass;
    }

    /**
     * Gets the parameterName attribute.
     * 
     * @return Returns the parameterName.
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Sets the componentClass attribute value.
     * 
     * @param componentClass The componentClass to set.
     */
    public void setComponentClass(Class componentClass) {
        this.componentClass = componentClass;
    }

    /**
     * Sets the parameterName attribute value.
     * 
     * @param parameterName The parameterName to set.
     */
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }


}
