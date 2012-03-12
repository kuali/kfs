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
package org.kuali.rice.core.api.parameter;

import java.io.Serializable;

/**
 * This is a stateful wrapper for Parameters, which provides convenient methods to evaluate a constrained value against a Parameter.
 */
public interface ParameterEvaluator extends Serializable {
    /**
     * This method determines whether the constrainedValue specified when the ParameterEvaluator was created matches the parameter.
     * 
     * @return boolean indicating whether the constrained value adheres to the restriction specified by the combination of the
     *         parameter constraint and the parameter value
     */
    public boolean evaluationSucceeds();

    /**
     * This method uses the evaluateAndAddError method. It passes the constrainedPropertyName as both the constrainedPropertyName
     * and the userEditablePropertyName, i.e. it should be used when they are one and the same.
     * 
     * @param businessObjectOrDocumentClass
     * @param constrainedPropertyName
     * @return boolean indicating whether evaluation succeeded (see evaluationSucceeds)
     */
    public boolean evaluateAndAddError(Class<? extends Object> businessObjectOrDocumentClass, String constrainedPropertyName);

    /**
     * This method uses the evaluationSucceeds method to evaluate the constrainedValue. If evaluation does not succeed, it adds an
     * error for the user. The businessObjectOrDocumentClass, nameOfConstrainedProperty and userEditablePropertyName are used by
     * ParameterEvaluatorImpl to retrieve user friendly labels for the error message. The constrainedPropertyName corresponds to the
     * field that has the value that the parameter is evaluating. The userEditablePropertyName corresponds to the field that has the
     * value the user needs to correct to resolve the error. For example, the object type may be invalid, but the user needs to
     * change the object code in order to remedy that.
     * 
     * @param businessObjectOrDocumentClass
     * @param userEditableFieldToHighlight
     * @param nameOfconstrainedProperty
     * @return boolean indicating whether evaluation succeeded (see evaluationSucceeds)
     */
    public boolean evaluateAndAddError(Class<? extends Object> businessObjectOrDocumentClass, String constrainedPropertyName, String userEditablePropertyName);

    /**
     * This method determines whether the parameter lists allowed values or denied values.
     * 
     * @return boolean indicating whether the parameter lists allowed values
     */
    public boolean constraintIsAllow();

    /**
     * This method creates a pretty String representation of parameter values for the user messages.
     *
     * @return user-friendly String representation of Parameter values
     */
    public String getParameterValuesForMessage();

    /**
     * This method returns the value of the correspnding Parameter.
     * 
     * @return String value of underlying Parameter
     */
    public String getValue();
}
