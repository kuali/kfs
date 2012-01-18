/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.core.impl.parameter;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.api.parameter.EvaluationOperator;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class ParameterEvaluatorServiceImpl implements ParameterEvaluatorService {

    protected ParameterService parameterService;

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
	 * This method will return an instance of the parameterEvaluator bean defined in Spring, initialized with the Parameter
	 * corresponding to the specified componentClass and parameterName and the values of the Parameter.
	 *
	 * @param componentClass
	 * @param parameterName
	 * @return ParameterEvaluator instance initialized with the Parameter corresponding to the specified componentClass and
	 *         parameterName and the values of the Parameter
	 */
	@Override
	public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName) {
	    return getParameterEvaluator(parameterService.getParameter(componentClass, parameterName));
	}

	/**
     * This method will return an instance of the parameterEvaluator bean defined in Spring, initialized with the Parameter
     * corresponding to the specified componentClass and parameterName and the values of the Parameter.
     *
     * @param namespaceCode
     * @param detailTypeCode
     * @param parameterName
     * @return ParameterEvaluator instance initialized with the Parameter corresponding to the specified componentClass and
     *         parameterName and the values of the Parameter
     */
    @Override
	public ParameterEvaluator getParameterEvaluator(String namespaceCode, String detailTypeCode, String parameterName) {
        return getParameterEvaluator(parameterService.getParameter(namespaceCode, detailTypeCode, parameterName));
    }

	/**
	 * This method will return an instance of the parameterEvaluator bean defined in Spring, initialized with the Parameter
	 * corresponding to the specified componentClass and parameterName, the values of the Parameter, the knowledge of whether the
	 * values are allowed or denied, and the constrainedValue.
	 *
	 * @param componentClass
	 * @param parameterName
	 * @return ParameterEvaluator instance initialized with the Parameter corresponding to the specified componentClass and
	 *         parameterName, the values of the Parameter, the knowledge of whether the values are allowed or denied, and the
	 *         constrainedValue
	 */
	@Override
	public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainedValue) {
	    return getParameterEvaluator(parameterService.getParameter(componentClass, parameterName), constrainedValue);
	}

	/**
     * This method will return an instance of the parameterEvaluator bean defined in Spring, initialized with the Parameter
     * corresponding to the specified componentClass and parameterName and the values of the Parameter.
     *
     * @param namespaceCode
     * @param detailTypeCode
     * @param parameterName
     * @return ParameterEvaluator instance initialized with the Parameter corresponding to the specified componentClass and
     *         parameterName and the values of the Parameter
     */
	@Override
    public ParameterEvaluator getParameterEvaluator(String namespaceCode, String detailTypeCode, String parameterName, String constrainedValue) {
        return getParameterEvaluator(parameterService.getParameter(namespaceCode, detailTypeCode, parameterName), constrainedValue);
    }

	/**
	 * This method will return an instance of the parameterEvaluator bean defined in Spring, initialized with the Parameter
	 * corresponding to the specified componentClass and parameterName, the values of the Parameter that correspond to the specified
	 * constrainingValue, the knowledge of whether the values are allowed or denied, and the constrainedValue.
	 *
	 * @param componentClass
	 * @param parameterName
	 * @return ParameterEvaluator instance initialized with the Parameter corresponding to the specified componentClass and
	 *         parameterName, the values of the Parameter that correspond to the specified constrainingValue, the knowledge of
	 *         whether the values are allowed or denied, and the constrainedValue
	 */
	@Override
	public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainingValue,
			String constrainedValue) {
			    return getParameterEvaluator(parameterService.getParameter(componentClass, parameterName), constrainingValue, constrainedValue);
			}

	/**
	 * This method will return an instance of the parameterEvaluator bean defined in Spring, initialized with the Parameter
	 * corresponding to the specified componentClass and allowParameterName or to the specified componentClass and denyParameterName
	 * (depending on which restricts based on the constraining value) or an instance of AlwaysSucceedParameterEvaluatorImpl if
	 * neither restricts, the values of the Parameter that correspond to the specified constrainingValue, the knowledge of whether
	 * the values are allowed or denied, and the constrainedValue.
	 *
	 * @param componentClass
	 * @param allowParameterName
	 * @param denyParameterName
	 * @param constrainingValue
	 * @param constrainedValue
	 * @return AlwaysSucceedParameterEvaluatorImpl or ParameterEvaluator instance initialized with the Parameter that corresponds to
	 *         the constrainingValue restriction, the values of the Parameter that correspond to the specified constrainingValue,
	 *         the knowledge of whether the values are allowed or denied, and the constrainedValue
	 */
	@Override
	public ParameterEvaluator getParameterEvaluator(Class componentClass, String allowParameterName, String denyParameterName,
			String constrainingValue, String constrainedValue) {
			    Parameter allowParameter = parameterService.getParameter(componentClass, allowParameterName);
			    Parameter denyParameter = parameterService.getParameter(componentClass, denyParameterName);
			    if (!getParameterValuesAsString(allowParameter, constrainingValue).isEmpty() && !getParameterValuesAsString(denyParameter, constrainingValue).isEmpty()) {
			        throw new IllegalArgumentException("The getParameterEvaluator(Class componentClass, String allowParameterName, String denyParameterName, String constrainingValue, String constrainedValue) method of ParameterServiceImpl does not facilitate evaluation of combination allow and deny parameters that both have values for the constraining value: " + allowParameterName + " / " + denyParameterName + " / " + constrainingValue);
			    }
			    if (getParameterValuesAsString(allowParameter, constrainingValue).isEmpty() && getParameterValuesAsString(denyParameter, constrainingValue).isEmpty()) {
			        return AlwaysSucceedParameterEvaluatorImpl.getInstance();
			    }
			    return getParameterEvaluator(getParameterValuesAsString(denyParameter, constrainingValue).isEmpty() ? allowParameter : denyParameter, constrainingValue, constrainedValue);
	}

    protected List<String> getParameterValuesAsString(Parameter parameter, String constrainingValue) {
	    List<String> constraintValuePairs = getParameterValuesAsString(parameter);
	    for (String pair : constraintValuePairs) {
	        if (StringUtils.equals(constrainingValue, StringUtils.substringBefore(pair, "="))) {
	            return Arrays.asList(StringUtils.substringAfter(pair, "=").split(","));
	        }
	    }
	    return Collections.emptyList();
	}

    private List<String> getParameterValuesAsString(Parameter parameter) {
	    if (parameter == null || StringUtils.isBlank(parameter.getValue())) {
	        return Collections.emptyList();
	    }
	    return Arrays.asList(parameter.getValue().split(";"));
	}

    protected ParameterEvaluatorImpl getParameterEvaluator(Parameter parameter) {
	    ParameterEvaluatorImpl parameterEvaluator = new ParameterEvaluatorImpl();
	    parameterEvaluator.setParameter(parameter);
	    parameterEvaluator.setConstraintIsAllow(constraintIsAllow(parameter));
	    parameterEvaluator.setValues(getParameterValuesAsString(parameter));
	    return parameterEvaluator;
	}

	protected ParameterEvaluatorImpl getParameterEvaluator(Parameter parameter, String constrainedValue) {
	    ParameterEvaluatorImpl parameterEvaluator = getParameterEvaluator(parameter);
	    parameterEvaluator.setConstrainedValue(constrainedValue);
	    return parameterEvaluator;
	}

	protected ParameterEvaluatorImpl getParameterEvaluator(Parameter parameter, String constrainingValue,
			String constrainedValue) {
	    ParameterEvaluatorImpl parameterEvaluator = getParameterEvaluator(parameter, constrainedValue);
	    parameterEvaluator.setValues(getParameterValuesAsString(parameter, constrainingValue));
	    return parameterEvaluator;
	}

    private boolean constraintIsAllow(Parameter parameter) {
	    return EvaluationOperator.ALLOW.equals(parameter.getEvaluationOperator());
	}
}
