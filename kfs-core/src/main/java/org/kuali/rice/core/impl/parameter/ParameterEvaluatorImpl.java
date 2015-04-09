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
package org.kuali.rice.core.impl.parameter;

import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

public class ParameterEvaluatorImpl implements ParameterEvaluator {
	private static final long serialVersionUID = -758645169354452022L;
	private Parameter parameter;
	private boolean constraintIsAllow;
	private String constrainedValue;
	private List<String> values;

	private static DataDictionaryService dataDictionaryService;
	
	/**
	 * If the constraint is allow and the constrainedValue is in the list of
	 * allowed values specified by the parameter this will return true, and if
	 * the constraint is deny and the constrainedValue is not in the list of
	 * denied values specified by the parameter this method will return true.
	 * 
	 * @return boolean indicating whether the constrained value adheres to the
	 *         restriction specified by the combination of the parameter
	 *         constraint and the parameter value
	 */
	public boolean evaluationSucceeds() {
		if (constraintIsAllow()) {
			return values.contains(constrainedValue);
		} else {
			return !values.contains(constrainedValue);
		}
	}

	public boolean evaluateAndAddError(Class<? extends Object> businessObjectOrDocumentClass, String constrainedPropertyName) {
		return evaluateAndAddError(businessObjectOrDocumentClass, constrainedPropertyName, constrainedPropertyName);
	}

	/**
	 * This method uses the evaluationSucceeds method to evaluate the
	 * constrainedValue. If evaluation does not succeed, it adds an error to
	 * GlobalVariables.getMessageMap(). The businessObjectOrDocumentClass,
	 * nameOfConstrainedProperty and userEditablePropertyName are used to
	 * retrieve the appropriate labels from the DataDictionary.
	 * 
	 * @param businessObjectOrDocumentClass
	 * @return boolean indicating whether evaluation succeeded (see
	 *         evaluationSucceeds)
	 */
	public boolean evaluateAndAddError(Class<? extends Object> businessObjectOrDocumentClass,
			String constrainedPropertyName, String userEditablePropertyName) {
		if (!evaluationSucceeds()) {
			GlobalVariables.getMessageMap().putError(
					userEditablePropertyName,
					constraintIsAllow() ? RiceKeyConstants.ERROR_DOCUMENT_INVALID_VALUE_ALLOWED_VALUES_PARAMETER : RiceKeyConstants.ERROR_DOCUMENT_INVALID_VALUE_DENIED_VALUES_PARAMETER,
					new String[] {
							getDataDictionaryService().getAttributeLabel( businessObjectOrDocumentClass, constrainedPropertyName),
							constrainedValue,
							toStringForMessage(),
							getParameterValuesForMessage(),
							getDataDictionaryService().getAttributeLabel( businessObjectOrDocumentClass, userEditablePropertyName) 
							} );
			return false;
		}
		return true;
	}

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

	public String getValue() {
		return parameter.getValue();
	}

	public String toString() {
		return new StringBuffer("ParameterEvaluator").append("\n\tParameter: ")
				.append("module=").append(parameter.getNamespaceCode())
				.append(", component=").append(parameter.getComponentCode())
				.append(", name=").append(parameter.getName())
				.append(", value=").append(parameter.getValue())
				.append("\n\tConstraint Is Allow: ").append(constraintIsAllow)
				.append("\n\tConstrained Value: ").append(constrainedValue)
				.append("\n\tValues: ").append(values.toString())
				.toString();
	}

	private String toStringForMessage() {
		return new StringBuffer("parameter: ").append(parameter.getName())
				.append(", module: ").append(parameter.getNamespaceCode())
				.append(", component: ").append(parameter.getComponentCode())
				.toString();
	}

	public String getModuleAndComponent() {
		return parameter.getNamespaceCode() + ": " + parameter.getComponentCode();
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

	/**
	 * @return the dataDictionaryService
	 */
	protected DataDictionaryService getDataDictionaryService() {
		if ( dataDictionaryService == null ) {
			dataDictionaryService = SpringContext.getBean(org.kuali.rice.kns.service.DataDictionaryService.class);
		}
		return dataDictionaryService;
	}
}
