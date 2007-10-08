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
package org.kuali.kfs.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Parameter;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.service.ParameterEvaluator;

public class ParameterEvaluatorImpl implements ParameterEvaluator {
    private Parameter parameter;
    private boolean constraintIsAllow;
    private String constrainedValue;
    private List<String> values;

    public boolean evaluationSucceeds() {
        if (constraintIsAllow()) {
            return values.contains(constrainedValue);
        }
        else {
            return !values.contains(constrainedValue);
        }
    }

    public boolean evaluateAndAddError(String errorMessageKey, String errorFieldName, String errorParameterLabel) {
        if (!evaluationSucceeds()) {
            GlobalVariables.getErrorMap().putError(errorFieldName, errorMessageKey, new String[] { errorParameterLabel, constrainedValue, getName(), getModuleAndComponent(), getParameterValuesForMessage() });
        }
        return evaluationSucceeds();
    }

    public boolean constraintIsAllow() {
        return constraintIsAllow;
    }

    public List<String> getParameterValues() {
        return values;
    }

    public String getParameterValuesForMessage() {
        String parameterValues = values.toString().replace("[", "").replace("]", "");
        return StringUtils.substringBefore(parameterValues, ",");
    }

    public String getName() {
        return parameter.getParameterName();
    }

    public String getValue() {
        return parameter.getParameterValue();
    }

    public String toString() {
        return new StringBuffer("ParameterEvaluator").append("\n\tParameter: ").append("module=").append(parameter.getParameterNamespaceCode()).append(", component=").append(parameter.getParameterDetailTypeCode()).append(", name=").append(parameter.getParameterName()).append(", value=").append(parameter.getParameterValue()).append("\n\tConstraint Is Allow: ").append(constraintIsAllow).append("\n\tConstrained Value: ").append(constrainedValue).append("\n\tValues: ").append(values.toString()).toString();
    }

    private String getModuleAndComponent() {
        return parameter.getParameterNamespaceCode() + ": " + parameter.getParameterDetailTypeCode();
    }

    public void setConstrainedValue(String constrainedValue) {
        this.constrainedValue = constrainedValue;
    }

    public void setConstraintIsAllow(boolean constraintIsAllow) {
        this.constraintIsAllow = constraintIsAllow;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
