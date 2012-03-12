/*
 * Copyright 2007-2009 The Kuali Foundation
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


import org.kuali.rice.core.api.parameter.ParameterEvaluator;

/**
 * This implementation of ParameterEvaluator is returned by ParameterServiceImpl when evaluation involves a constraining value and
 * neither the allow nor deny parameter have restrictions for that value.
 */
public class AlwaysSucceedParameterEvaluatorImpl implements ParameterEvaluator {
    private static final AlwaysSucceedParameterEvaluatorImpl instance = new AlwaysSucceedParameterEvaluatorImpl();

    public static ParameterEvaluator getInstance() {
        return instance;
    }

    private AlwaysSucceedParameterEvaluatorImpl() {
    }

    public boolean constraintIsAllow() {
        return Boolean.TRUE;
    }


    public boolean evaluateAndAddError(Class businessObjectOrDocumentClass, String constrainedPropertyName, String userEditablePropertyName) {
        return evaluationSucceeds();
    }

    public boolean evaluateAndAddError(Class businessObjectOrDocumentClass, String constrainedPropertyName) {
        return evaluationSucceeds();
    }

    public boolean evaluationSucceeds() {
        return Boolean.TRUE;
    }

    public String getName() {
        return AlwaysSucceedParameterEvaluatorImpl.class.getName();
    }

    public String getParameterValuesForMessage() {
        return AlwaysSucceedParameterEvaluatorImpl.class.getName();
    }

    public String getValue() {
        return AlwaysSucceedParameterEvaluatorImpl.class.getName();
    }

    public void setConstrainedValue(String constrainedValue) {
    }
}
