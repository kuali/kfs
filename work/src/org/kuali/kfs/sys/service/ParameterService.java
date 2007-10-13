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

import org.kuali.core.bo.ParameterDetailType;

/**
 * This class provides methods to verify the existence of Parameters, get the value(s), and get ParameterEvaluators. For the most
 * part, your code should be asking for a ParameterEvaluator and interacting with that. For optional parameters (ones that may not
 * exist but should be processed if they do), you will want to use the parameterExists method before using other methods, since an
 * exception will be thrown by the other methods if the referenced parameter does not exist. In some cases you may need to just pull
 * the value(s) of a parameter via the getParameterValue(s) or getIndicatorParameter methods. All of the methods that you will want
 * to use take a Class componentClass and String parameterName argument. The componentClass must be the business object, document,
 * or step class that the parameter is associated with. Implementations of this class know how to translate that to a namespace (for
 * ParameterService Impl, determine what module the Class is associated with by parsing the package) and detail type (for
 * ParameterServiceImpl, document Class --> use simple class name minus the word Document / business object Class --> use simple
 * class name, batch step class --> use the simple class name). In cases where the parameter is applicable to all documents, all
 * lookups, all batch steps, or all components in a particular module, you should pass in the appropriate constant class in
 * ParameterConstants for the component Class (e.g. all purchasing documents = PURCHASING_DOCUMENT.class, all purchasing lookups =
 * PURCHASING_LOOKUP.class, all purchasing batch steps = PURCHASING_BATCH.class, and all purchasing components =
 * PURCHASING_ALL.class). In addition, certain methods take constrainingValue and constrainedValue Strings. The constrainedValue is
 * the value that you want to compare to the Parameter value, and the constrainingValue is used for complex parameters that limit
 * one field value based on the value of another field, e.g VALID_OBJECT_LEVELS_BY_OBJECT_TYPE.
 */
public interface ParameterService {
    /**
     * This method provides an exception free way to ensure that a parameter exists.
     * 
     * @param componentClass
     * @param parameterName
     * @return boolean indicating whether or not the parameter exists
     */
    public boolean parameterExists(Class componentClass, String parameterName);

    /**
     * This method provides a convenient way to access the value of indicator parameters with Y/N values. Y is translated to true
     * and N is translated to false.
     * 
     * @param componentClass
     * @param parameterName
     * @return boolean value of Yes/No indicator parameter
     */
    public boolean getIndicatorParameter(Class componentClass, String parameterName);

    /**
     * This method returns the unprocessed text value of a parameter.
     * 
     * @param componentClass
     * @param parameterName
     * @return unprocessed string value os a parameter
     */
    public String getParameterValue(Class componentClass, String parameterName);

    /**
     * This method returns the unprocessed subset of a
     * 
     * @param componentClass
     * @param parameterName
     * @param constrainingValue
     * @return
     */
    public String getParameterValue(Class componentClass, String parameterName, String constrainingValue);

    public List<String> getParameterValues(Class componentClass, String parameterName);

    public List<String> getParameterValues(Class componentClass, String parameterName, String constrainingValue);

    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName);

    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainedValue);

    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainingValue, String constrainedValue);

    public ParameterEvaluator getParameterEvaluator(Class componentClass, String allowParameterName, String denyParameterName, String constrainingValue, String constrainedValue);

    public List<ParameterEvaluator> getParameterEvaluators(Class componentClass, String constrainedValue);

    public List<ParameterEvaluator> getParameterEvaluators(Class componentClass, String constrainingValue, String constrainedValue);

    public List<ParameterDetailType> getNonDatabaseDetailTypes();

    public void setParameterForTesting(Class componentClass, String parameterName, String parameterText);
}
