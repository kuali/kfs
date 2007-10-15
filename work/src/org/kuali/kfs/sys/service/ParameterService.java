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
 * to use take a Class componentClass and String parameterName argument. Implementations of this class know how to translate these
 * appropriately to retrieve Parameters and construct ParameterEvaluators.
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
     * This method provides a convenient way to access the a parameter that signifies true or false.
     * 
     * @param componentClass
     * @param parameterName
     * @return boolean value of indicator parameter
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
     * This method can be used to derive a value based on another value.
     * 
     * @param componentClass
     * @param parameterName
     * @param constrainingValue
     * @return derived value
     */
    public String getParameterValue(Class componentClass, String parameterName, String constrainingValue);

    /**
     * This method can be used to parse the value of a parameter.
     * 
     * @param componentClass
     * @param parameterName
     * @return parsed List of String parameter values
     */
    public List<String> getParameterValues(Class componentClass, String parameterName);

    /**
     * This method can be used to derive a set of values based on another value.
     * 
     * @param componentClass
     * @param parameterName
     * @param constrainingValue
     * @return derived values List<String>
     */
    public List<String> getParameterValues(Class componentClass, String parameterName, String constrainingValue);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter and provide convenient
     * evaluation methods.
     * 
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter and constrainedValue
     * and provide convenient evaluation methods.
     * 
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainedValue);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter, constrainingValue, and
     * constrainedValue and provide convenient evaluation methods.
     * 
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainingValue, String constrainedValue);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap an allow Parameter, a deny
     * Parameter, constrainingValue, and constrainedValue and provide convenient evaluation methods.
     * 
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class componentClass, String allowParameterName, String denyParameterName, String constrainingValue, String constrainedValue);

    /**
     * This method returns ParameterEvaluators initialized per public ParameterEvaluator getParameterEvaluator(Class componentClass,
     * String parameterName, String constrainedValue)
     * 
     * @param componentClass
     * @param constrainedValue
     * @return List<ParameterEvaluator> initialized per public ParameterEvaluator getParameterEvaluator(Class componentClass,
     *         String parameterName, String constrainedValue)
     */
    public List<ParameterEvaluator> getParameterEvaluators(Class componentClass, String constrainedValue);

    /**
     * This method returns ParameterEvaluators initialized per public ParameterEvaluator getParameterEvaluator(Class componentClass,
     * String parameterName, String constrainingValue, String constrainedValue)
     * 
     * @param componentClass
     * @param constrainedValue
     * @return List<ParameterEvaluator> initialized per public ParameterEvaluator getParameterEvaluator(Class componentClass,
     *         String parameterName, String constrainingValue, String constrainedValue)
     */
    public List<ParameterEvaluator> getParameterEvaluators(Class componentClass, String constrainingValue, String constrainedValue);

    /**
     * This method can be used to supplement the list of ParameterDetailTypes defined in the database from other sources.
     * 
     * @return List<ParameterDetailedType> containing the detailed types configured in non-database sources
     */
    public List<ParameterDetailType> getNonDatabaseDetailTypes();

    /**
     * This method can be used to change the value of a Parameter for unit testing purposes.
     * 
     * @param componentClass
     * @param parameterName
     * @param parameterText
     */
    public void setParameterForTesting(Class componentClass, String parameterName, String parameterText);
}
