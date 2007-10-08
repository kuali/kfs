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
package org.kuali.kfs.service;

import java.util.List;

import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.ParameterDetailType;

public interface ParameterService {
    public boolean parameterExists(Class componentClass, String parameterName);

    public boolean getIndicatorParameter(Class componentClass, String parameterName);

	@Deprecated
    public List<String> getParameterValues(Class componentClass, String parameterName);

    @Deprecated
    public boolean evaluateConstrainedValue(Class componentClass, String parameterName, String constrainedValue);

    @Deprecated
    public boolean evaluateConstrainedValue(Parameter parameter, String constrainedValue);
    
    @Deprecated
    public boolean evaluateConstrainedValue(Class componentClass, String allowConstraintParameterName, String denyConstraintParameterName, String constrainingValue, String constrainedValue);
    
    @Deprecated
    public boolean evaluateConstrainedValue(Parameter allowParameter, Parameter denyParameter, String constrainingValue, String constrainedValue);
    
    @Deprecated
    public ParameterEvaluator getParameterEvaluator(Class componentClass, String allowParameterName, String denyParameterName, String constrainingValue, String constrainedValue);
    
    @Deprecated
    public boolean evaluateConstrainedValue(Class componentClass, String parameterName, String constrainingValue, String constrainedValue);
        
    public String getParameterValue(Class componentClass, String parameterName);
    
    public List<String> getConstrainedParameterValues(Class componentClass, String parameterName, String constrainingValue);

    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainedValue);
    
    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainingValue, String constrainedValue);

    public List<ParameterEvaluator> getParameterEvaluators(Class componentClass, String constrainedValue);

    @Deprecated
    public List<String> deriveConstrainedValues(Class componentClass, String parameterName, String constrainingValue);

    @Deprecated
    public List<String> deriveConstrainedValues(Parameter parameter, String constrainingValue);
    
    public List<ParameterEvaluator> getParameterEvaluators(Class componentClass, String constrainingValue, String constrainedValue);

    @Deprecated
    public String getConstrainedValuesString(Class componentClass, String parameterName, String constrainingValue);

    @Deprecated
    public List<Parameter> getParameters(Class componentClass);

    @Deprecated
    public Parameter getParameter(Class componentClass, String parameterName);

    public List<ParameterDetailType> getNonDatabaseDetailTypes();

    public void clearCache(Class componentClass, String parameterName);
    
    public ParameterEvaluator mergeEvaluators(ParameterEvaluator evaluator1, ParameterEvaluator evaluator2);
}
