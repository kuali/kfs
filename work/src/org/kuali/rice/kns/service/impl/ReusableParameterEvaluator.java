/*
 * Copyright 2011 The Regents of the University of California.
 */
package org.kuali.rice.kns.service.impl;

import org.kuali.rice.core.impl.parameter.ParameterEvaluatorImpl;


public class ReusableParameterEvaluator {
	private static final long serialVersionUID = 3488211569145798810L;

	protected ParameterEvaluatorImpl evaluator;

	public ReusableParameterEvaluator( ParameterEvaluatorImpl evaluator ) {
		this.evaluator = evaluator;
	}

	public boolean constraintIsAllow() {
		return evaluator.constraintIsAllow();
	}

	public boolean evaluateAndAddError( String constrainedValue, Class<? extends Object> businessObjectOrDocumentClass,
			String constrainedPropertyName, String userEditablePropertyName) {
		evaluator.setConstrainedValue(constrainedValue);
		return evaluator.evaluateAndAddError(businessObjectOrDocumentClass, constrainedPropertyName, userEditablePropertyName);
	}

	public boolean evaluateAndAddError(String constrainedValue, Class<? extends Object> businessObjectOrDocumentClass, String constrainedPropertyName) {
		evaluator.setConstrainedValue(constrainedValue);
		return evaluator.evaluateAndAddError(businessObjectOrDocumentClass, constrainedPropertyName);
	}

	public boolean evaluationSucceeds(String constrainedValue) {
		evaluator.setConstrainedValue(constrainedValue);
		return evaluator.evaluationSucceeds();
	}

	public String getParameterValuesForMessage() {
		return evaluator.getParameterValuesForMessage();
	}

	public String getValue() {
		return evaluator.getValue();
	}


}
