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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Parameter;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;

public class ParameterEvaluatorImpl implements ParameterEvaluator {
    private Parameter parameter;
    private boolean constraintIsAllow;
    private String constrainedValue;
    private List<String> values;

    /**
     * If the constraint is allow and the constrainedValue is in the list of allowed values specified by the parameter this will
     * return true, and if the constraint is deny and the constrainedValue is not in the list of denied values specified by the
     * parameter this method will return true.
     * 
     * @return boolean indicating whether the constrained value adheres to the restriction specified by the combination of the
     *         parameter constraint and the parameter value
     */
    public boolean evaluationSucceeds() {
        if (constraintIsAllow()) {
            return values.contains(constrainedValue);
        }
        else {
            return !values.contains(constrainedValue);
        }
    }

    /**
     * @see org.kuali.kfs.service.ParameterEvaluator#evaluateAndAddError(java.lang.Class businessObjectOrDocumentClass,
     *      java.lang.String constrainedPropertyName)
     */
    public boolean evaluateAndAddError(Class businessObjectOrDocumentClass, String constrainedPropertyName) {
        return evaluateAndAddError(businessObjectOrDocumentClass, constrainedPropertyName, constrainedPropertyName);
    }

    /**
     * This method uses the evaluationSucceeds method to evaluate the constrainedValue. If evaluation does not succeed, it adds an
     * error to GlobalVariables.getErrorMap(). The businessObjectOrDocumentClass, nameOfConstrainedProperty and
     * userEditablePropertyName are used to retrieve the appropriate labels from the DataDictionary.
     * 
     * @param businessObjectOrDocumentClass
     * @param userEditableFieldToHighlight
     * @param nameOfconstrainedProperty
     * @return boolean indicating whether evaluation succeeded (see evaluationSucceeds)
     */
    public boolean evaluateAndAddError(Class businessObjectOrDocumentClass, String constrainedPropertyName, String userEditablePropertyName) {
        if (!evaluationSucceeds()) {
            GlobalVariables.getErrorMap().putError(userEditablePropertyName, KFSKeyConstants.ERROR_DOCUMENT_INVALID_VALUE, new String[] { SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(businessObjectOrDocumentClass, constrainedPropertyName), constrainedValue, toStringForMessage(), constraintIsAllow() ? "allowed" : "not allowed", getParameterValuesForMessage(), SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(businessObjectOrDocumentClass, userEditablePropertyName) });
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.service.ParameterEvaluator#constraintIsAllow()
     */
    public boolean constraintIsAllow() {
        return constraintIsAllow;
    }

    /**
     * This method uses the List toString method and eliminates the [].
     *
     * @return user-friendly String representation of Parameter values
     */
    public String getParameterValuesForMessage() {
        return values.toString().replace("[", "").replace("]", "");
    }

    /**
     * @see org.kuali.kfs.service.ParameterEvaluator#getValue()
     */
    public String getValue() {
        return parameter.getParameterValue();
    }

    public String toString() {
        return new StringBuffer("ParameterEvaluator").append("\n\tParameter: ").append("module=").append(parameter.getParameterNamespaceCode()).append(", component=").append(parameter.getParameterDetailTypeCode()).append(", name=").append(parameter.getParameterName()).append(", value=").append(parameter.getParameterValue()).append("\n\tConstraint Is Allow: ").append(constraintIsAllow).append("\n\tConstrained Value: ").append(constrainedValue).append("\n\tValues: ").append(values.toString()).toString();
    }

    private String toStringForMessage() {
        return new StringBuffer("parameter ").append(parameter.getParameterName()).append(" (module: ").append(parameter.getParameterNamespaceCode()).append(" / component: ").append(parameter.getParameterDetailTypeCode()).append(")").toString();
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
