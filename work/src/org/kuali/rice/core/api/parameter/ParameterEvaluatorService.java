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
package org.kuali.rice.core.api.parameter;

public interface ParameterEvaluatorService {
        /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter and provide convenient
     * evaluation methods.
     *
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class<? extends Object> componentClass, String parameterName);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter and provide convenient
     * evaluation methods.
     *
     * @param namespaceCode
     * @param detailTypeCode
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(String namespaceCode, String detailTypeCode, String parameterName);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter and constrainedValue
     * and provide convenient evaluation methods.
     *
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class<? extends Object> componentClass, String parameterName, String constrainedValue);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter and constrainedValue
     * and provide convenient evaluation methods.
     *
     * @param namespaceCode
     * @param detailTypeCode
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(String namespaceCode, String detailTypeCode, String parameterName, String constrainedValue);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter, constrainingValue, and
     * constrainedValue and provide convenient evaluation methods.
     *
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class<? extends Object> componentClass, String parameterName, String constrainingValue, String constrainedValue);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap an allow Parameter, a deny
     * Parameter, constrainingValue, and constrainedValue and provide convenient evaluation methods.
     *
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class<? extends Object> componentClass, String allowParameterName, String denyParameterName, String constrainingValue, String constrainedValue);
}
