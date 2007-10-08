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
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.service.ParameterEvaluator;

public class ParameterEvaluatorImpl implements ParameterEvaluator {
    private KualiConfigurationService configurationService;
    private Parameter parameter;
    private String constrainedValue;
    private String constrainingValue;

    public boolean evaluationSucceeds() {
        if (StringUtils.isBlank(constrainingValue)) {
            return configurationService.succeedsRule(parameter, constrainedValue);
        }
        return configurationService.evaluateConstrainedParameter(parameter, constrainingValue, constrainedValue);
    }

    public boolean constraintIsAllow() {
        return configurationService.isAllowedRule(parameter);
    }

    public List<String> getParameterValues() {
        if (StringUtils.isBlank(constrainingValue)) {
            return configurationService.getParameterValuesAsList(parameter);
        }
        return configurationService.getConstrainedValues(parameter, constrainingValue);
    }
    
    public String getParameterValuesForMessage() {
        StringBuilder buf = new StringBuilder();
        String[] values = configurationService.getParameterValues(parameter);
        for (int i = 0; i < values.length; i++) {
            buf.append(values[i]);
            if (i != values.length - 1) {
                // don't print comma if the last value
                buf.append(", ");
            }
        }
        return buf.toString();
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setConstrainedValue(String constrainedValue) {
        this.constrainedValue = constrainedValue;
    }

    public String getConstrainedValue() {
        return constrainedValue;
    }

    public void setConstrainingValue(String constrainingValue) {
        this.constrainingValue = constrainingValue;
    }

    public String getConstrainingValue() {
        return constrainingValue;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }
    
    public String getParameterName() {
        return parameter.getParameterName();
    }
    
    public String getParameterNamespaceAndComponent() {
        return parameter.getParameterNamespaceCode() + "/" + parameter.getParameterDetailTypeCode();
    }
    
    public boolean evaluationTrivallySucceeds() {
        if (!evaluationSucceeds()) {
            return false;
        }
        List<String> values = getParameterValues();
        return values.size() == 0 || (values.size() == 1 && "".equals(values.get(0)));
    }
}
