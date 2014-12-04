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
