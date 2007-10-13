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

/**
 * This is a stateful SOA wrapper for Parameters, which provides convenient methods to evaluate a constrained value against a
 * Parameter.
 */
public interface ParameterEvaluator {
    /**
     * This method determines whether the constrainedValue specified when the ParameterEvaluator was created matches the parameter,
     * i.e. for ParameterEvaluatorImpl if the constraint is allow and the constrainedValue is in the list of allowed values
     * specified by the parameter this will return true, and if the constraint is deny and the constrainedValue is not in the list
     * of denied values specified by the parameter this method will return true.
     * 
     * @return boolean indicating whether the constrained value adheres to the restriction specified by the combination of the
     *         parameter constraint and the parameter value
     */
    public boolean evaluationSucceeds();

    /**
     * This method uses the evaluationSucceeds method to evaluate the constrainedValue. If evaluation does not succeed, it adds an
     * error to the global error map. The businessObjectOrDocumentClass, nameOfConstrainedProperty and userEditablePropertyName are
     * used by ParameterEvaluatorImpl to retrieve the appropriate labels from the DataDictionary. The constrainedPropertyName
     * corresponds to the field that has the value that the parameter is evaluating. The userEditablePropertyName corresponds to the
     * field that has the value the user needs to correct to resolve the error. For example, the object type may be invalid, but the
     * user needs to change the object code in order to remedy that.
     * 
     * @param businessObjectOrDocumentClass
     * @param userEditableFieldToHighlight
     * @param nameOfconstrainedProperty
     * @return boolean indicating whether evaluation succeeded (see evaluationSucceeds)
     */
    public boolean evaluateAndAddError(Class businessObjectOrDocumentClass, String constrainedPropertyName, String userEditablePropertyName);

    /**
     * This method uses the evaluateAndAddError method. It passes the constrainedPropertyName as both the constrainedPropertyName
     * and the userEditablePropertyName, i.e. it should be used when they are one and the same.
     * 
     * @param businessObjectOrDocumentClass
     * @param constrainedPropertyName
     * @return boolean indicating whether evaluation succeeded (see evaluationSucceeds)
     */
    public boolean evaluateAndAddError(Class businessObjectOrDocumentClass, String constrainedPropertyName);

    /**
     * This method determines whether the parameter lists allowed values or denied values.
     * 
     * @return boolean indicating whether the parameter lists allowed values
     */
    public boolean constraintIsAllow();

    public String getParameterValuesForMessage();

    public String getName();

    public String getValue();

    public void setParameter(Parameter parameter);

    public void setConstraintIsAllow(boolean constraintIsAllow);

    public void setConstrainedValue(String constrainedValue);

    public void setValues(List<String> values);
}
