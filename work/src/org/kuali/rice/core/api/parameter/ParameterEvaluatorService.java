package org.kuali.rice.core.api.parameter;

public interface ParameterEvaluatorService {
        /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter and provide convenient
     * evaluation methods.
     *
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class<? extends Object> componentClass, String parameterName);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter and provide convenient
     * evaluation methods.
     *
     * @param namespaceCode
     * @param detailTypeCode
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(String namespaceCode, String detailTypeCode, String parameterName);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter and constrainedValue
     * and provide convenient evaluation methods.
     *
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class<? extends Object> componentClass, String parameterName, String constrainedValue);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter and constrainedValue
     * and provide convenient evaluation methods.
     *
     * @param namespaceCode
     * @param detailTypeCode
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(String namespaceCode, String detailTypeCode, String parameterName, String constrainedValue);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap a Parameter, constrainingValue, and
     * constrainedValue and provide convenient evaluation methods.
     *
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class<? extends Object> componentClass, String parameterName, String constrainingValue, String constrainedValue);

    /**
     * This method will return an instance of a ParameterEvaluator implementation that will wrap an allow Parameter, a deny
     * Parameter, constrainingValue, and constrainedValue and provide convenient evaluation methods.
     *
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator
     */
    public ParameterEvaluator getParameterEvaluator(Class<? extends Object> componentClass, String allowParameterName, String denyParameterName, String constrainingValue, String constrainedValue);
}
