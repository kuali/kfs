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
package org.kuali.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

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
        List<String> parameterValues = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(this.componentClass, this.parameterName) );
        if (insertBlankRow) {
            keyLabels.add(new ConcreteKeyValue("", ""));
        }
        if (parameterValues != null) {
            for (String parameterValue : parameterValues) {
                keyLabels.add(new ConcreteKeyValue(parameterValue, parameterValue));
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
