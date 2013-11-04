/*
 * Copyright 2011 The Kuali Foundation.
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
