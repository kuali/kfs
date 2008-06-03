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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.KualiModule;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.ParameterDetailType;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.datadictionary.TransactionalDocumentEntry;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.util.cache.MethodCacheInterceptor;
import org.kuali.core.util.cache.MethodCacheNoCopyInterceptor;
import org.kuali.core.util.spring.CacheNoCopy;
import org.kuali.core.util.spring.Cached;
import org.kuali.kfs.batch.Step;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants.COMPONENT;
import org.kuali.kfs.service.impl.ParameterConstants.NAMESPACE;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * See ParameterService. The componentClass must be the business object, document, or step class that the parameter is associated
 * with. Implementations of this class know how to translate that to a namespace (for ParameterService Impl, determine what module
 * the Class is associated with by parsing the package) and detail type (for ParameterServiceImpl, document Class --> use simple
 * class name minus the word Document / business object Class --> use simple class name, batch step class --> use the simple class
 * name). In cases where the parameter is applicable to all documents, all lookups, all batch steps, or all components in a
 * particular module, you should pass in the appropriate constant class in ParameterConstants for the component Class (e.g. all
 * purchasing documents = PURCHASING_DOCUMENT.class, all purchasing lookups = PURCHASING_LOOKUP.class, all purchasing batch steps =
 * PURCHASING_BATCH.class, and all purchasing components = PURCHASING_ALL.class). In addition, certain methods take
 * constrainingValue and constrainedValue Strings. The constrainedValue is the value that you want to compare to the Parameter
 * value, and the constrainingValue is used for complex parameters that limit one field value based on the value of another field,
 * e.g VALID_OBJECT_LEVELS_BY_OBJECT_TYPE.
 */
public class ParameterServiceImpl implements ParameterService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ParameterServiceImpl.class);
    private static List<ParameterDetailType> components = new ArrayList<ParameterDetailType>();
    private DataDictionaryService dataDictionaryService;
    private KualiModuleService moduleService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.service.ParameterService#parameterExists(java.lang.Class componentClass, java.lang.String parameterName)
     */
    @CacheNoCopy
    public boolean parameterExists(Class componentClass, String parameterName) {
        return getParameterWithoutExceptions(getNamespace(componentClass), getDetailType(componentClass), parameterName) != null;
    }

    /**
     * This method provides a convenient way to access the value of indicator parameters with Y/N values. Y is translated to true
     * and N is translated to false.
     * 
     * @param componentClass
     * @param parameterName
     * @return boolean value of Yes/No indicator parameter
     */
    @CacheNoCopy
    public boolean getIndicatorParameter(Class componentClass, String parameterName) {
        return "Y".equals(getParameter(componentClass, parameterName).getParameterValue());
    }

    /**
     * @see org.kuali.kfs.service.ParameterService#getParameterValue(java.lang.Class componentClass, java.lang.String parameterName)
     */
    @CacheNoCopy
    public String getParameterValue(Class componentClass, String parameterName) {
        return getParameter(componentClass, parameterName).getParameterValue();
    }

    /**
     * This will look for constrainingValue=<value to return> within the parameter text and return that if it is found. Otherwise,
     * it will return null. Note, that if constrainingValue=value1,value2... (commas specific to the ParameterServiceImpl
     * implementation) is found it will still return null, because calling this method states the assumption that there is only one
     * value within the parameter text that corresponds to the constraining value.
     * 
     * @param componentClass
     * @param parameterName
     * @param constrainingValue
     * @return derived value String or null
     */
    @CacheNoCopy
    public String getParameterValue(Class componentClass, String parameterName, String constrainingValue) {
        List<String> parameterValues = getParameterValues(componentClass, parameterName, constrainingValue);
        if (parameterValues.size() == 1) {
            return parameterValues.get(0);
        }
        return null;
    }

    /**
     * This method can be used to parse the value of a parameter by splitting on a semi-colon.
     * 
     * @param componentClass
     * @param parameterName
     * @return parsed List of String parameter values
     */
    @CacheNoCopy
    public List<String> getParameterValues(Class componentClass, String parameterName) {
        return Collections.unmodifiableList( getParameterValues(getParameter(componentClass, parameterName)) );
    }

    /**
     * This method looks for constrainingValue=<some text> within the parameter text and splits that text on a comma to generate
     * the List to return.
     * 
     * @param componentClass
     * @param parameterName
     * @param constrainingValue
     * @return derived values List<String> or an empty list if no values are found
     */
    @CacheNoCopy
    public List<String> getParameterValues(Class componentClass, String parameterName, String constrainingValue) {
        return Collections.unmodifiableList( getParameterValues(getParameter(componentClass, parameterName), constrainingValue) );
    }

    /**
     * This method will return an instance of the parameterEvaluator bean defined in Spring, initialized with the Parameter
     * corresponding to the specified componentClass and parameterName and the values of the Parameter.
     * 
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator instance initialized with the Parameter corresponding to the specified componentClass and
     *         parameterName and the values of the Parameter
     */
    @Cached
    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName) {
        return getParameterEvaluator(getParameter(componentClass, parameterName));
    }

    /**
     * This method will return an instance of the parameterEvaluator bean defined in Spring, initialized with the Parameter
     * corresponding to the specified componentClass and parameterName, the values of the Parameter, the knowledge of whether the
     * values are allowed or denied, and the constrainedValue.
     * 
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator instance initialized with the Parameter corresponding to the specified componentClass and
     *         parameterName, the values of the Parameter, the knowledge of whether the values are allowed or denied, and the
     *         constrainedValue
     */
    @Cached
    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainedValue) {
        return getParameterEvaluator(getParameter(componentClass, parameterName), constrainedValue);
    }

    /**
     * This method will return an instance of the parameterEvaluator bean defined in Spring, initialized with the Parameter
     * corresponding to the specified componentClass and parameterName, the values of the Parameter that correspond to the specified
     * constrainingValue, the knowledge of whether the values are allowed or denied, and the constrainedValue.
     * 
     * @param componentClass
     * @param parameterName
     * @return ParameterEvaluator instance initialized with the Parameter corresponding to the specified componentClass and
     *         parameterName, the values of the Parameter that correspond to the specified constrainingValue, the knowledge of
     *         whether the values are allowed or denied, and the constrainedValue
     */
    @Cached
    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainingValue, String constrainedValue) {
        return getParameterEvaluator(getParameter(componentClass, parameterName), constrainingValue, constrainedValue);
    }

    /**
     * This method will return an instance of the parameterEvaluator bean defined in Spring, initialized with the Parameter
     * corresponding to the specified componentClass and allowParameterName or to the specified componentClass and denyParameterName
     * (depending on which restricts based on the constraining value) or an instance of AlwaysSucceedParameterEvaluatorImpl if
     * neither restricts, the values of the Parameter that correspond to the specified constrainingValue, the knowledge of whether
     * the values are allowed or denied, and the constrainedValue.
     * 
     * @param componentClass
     * @param allowParameterName
     * @param denyParameterName
     * @param constrainingValue
     * @param constrainedValue
     * @return AlwaysSucceedParameterEvaluatorImpl or ParameterEvaluator instance initialized with the Parameter that corresponds to
     *         the constrainingValue restriction, the values of the Parameter that correspond to the specified constrainingValue,
     *         the knowledge of whether the values are allowed or denied, and the constrainedValue
     */
    @Cached
    public ParameterEvaluator getParameterEvaluator(Class componentClass, String allowParameterName, String denyParameterName, String constrainingValue, String constrainedValue) {
        Parameter allowParameter = getParameter(componentClass, allowParameterName);
        Parameter denyParameter = getParameter(componentClass, denyParameterName);
        if (!getParameterValues(allowParameter, constrainingValue).isEmpty() && !getParameterValues(denyParameter, constrainingValue).isEmpty()) {
            throw new IllegalArgumentException("The getParameterEvaluator(Class componentClass, String allowParameterName, String denyParameterName, String constrainingValue, String constrainedValue) method of ParameterServiceImpl does not facilitate evaluation of combination allow and deny parameters that both have values for the constraining value: " + allowParameterName + " / " + denyParameterName + " / " + constrainingValue);
        }
        if (getParameterValues(allowParameter, constrainingValue).isEmpty() && getParameterValues(denyParameter, constrainingValue).isEmpty()) {
            return AlwaysSucceedParameterEvaluatorImpl.getInstance();
        }
        return getParameterEvaluator(getParameterValues(denyParameter, constrainingValue).isEmpty() ? allowParameter : denyParameter, constrainingValue, constrainedValue);
    }

    /**
     * @see org.kuali.kfs.service.ParameterService#getParameterEvaluators(java.lang.Class componentClass, java.lang.String
     *      constrainedValue)
     */
    @Cached
    public List<ParameterEvaluator> getParameterEvaluators(Class componentClass, String constrainedValue) {
        List<ParameterEvaluator> parameterEvaluators = new ArrayList<ParameterEvaluator>();
        for (Parameter parameter : getParameters(componentClass)) {
            parameterEvaluators.add(getParameterEvaluator(parameter, constrainedValue));
        }
        return parameterEvaluators;
    }

    /**
     * @see org.kuali.kfs.service.ParameterService#getParameterEvaluators(java.lang.Class componentClass, java.lang.String
     *      constrainingValue, java.lang.String constrainedValue)
     */
    @Cached
    public List<ParameterEvaluator> getParameterEvaluators(Class componentClass, String constrainingValue, String constrainedValue) {
        List<ParameterEvaluator> parameterEvaluators = new ArrayList<ParameterEvaluator>();
        for (Parameter parameter : getParameters(componentClass)) {
            parameterEvaluators.add(getParameterEvaluator(parameter, constrainingValue, constrainedValue));
        }
        return parameterEvaluators;
    }

    /**
     * This method derived ParameterDetailedTypes from the DataDictionary for all BusinessObjects and Documents and from Spring for
     * all batch Steps.
     * 
     * @return List<ParameterDetailedType> containing the detailed types derived from the data dictionary and Spring
     */
    @Cached
    public List<ParameterDetailType> getNonDatabaseDetailTypes() {
        if (components.isEmpty()) {
            Map<String, ParameterDetailType> uniqueParameterDetailTypeMap = new HashMap<String, ParameterDetailType>();
            //dataDictionaryService.getDataDictionary().forceCompleteDataDictionaryLoad();
            for (BusinessObjectEntry businessObjectEntry : dataDictionaryService.getDataDictionary().getBusinessObjectEntries().values()) {
                ParameterDetailType parameterDetailType = getParameterDetailType(businessObjectEntry.getBusinessObjectClass());
                try {
                    uniqueParameterDetailTypeMap.put(parameterDetailType.getParameterDetailTypeCode(), parameterDetailType);
                }
                catch (Exception e) {
                    LOG.error("The getDataDictionaryAndSpringComponents method of ParameterUtils encountered an exception while trying to create the detail type for business object class: " + businessObjectEntry.getBusinessObjectClass(), e);
                }
            }
            for (DocumentEntry documentEntry : dataDictionaryService.getDataDictionary().getDocumentEntries().values()) {
                if (documentEntry instanceof TransactionalDocumentEntry) {
                    ParameterDetailType parameterDetailType = getParameterDetailType(documentEntry.getDocumentClass());
                    try {
                        uniqueParameterDetailTypeMap.put(parameterDetailType.getParameterDetailTypeCode(), parameterDetailType);
                    }
                    catch (Exception e) {
                        LOG.error("The getDataDictionaryAndSpringComponents method of ParameterUtils encountered an exception while trying to create the detail type for transactional document class: " + documentEntry.getDocumentClass(), e);
                    }
                }
            }
            for (Step step : SpringContext.getBeansOfType(Step.class).values()) {
                ParameterDetailType parameterDetailType = getParameterDetailType(step.getClass());
                try {
                    uniqueParameterDetailTypeMap.put(parameterDetailType.getParameterDetailTypeCode(), parameterDetailType);
                }
                catch (Exception e) {
                    LOG.error("The getDataDictionaryAndSpringComponents method of ParameterUtils encountered an exception while trying to create the detail type for step class: " + step.getClass(), e);
                }
            }
            components.addAll(uniqueParameterDetailTypeMap.values());
        }
        return Collections.unmodifiableList(components);
    }

    /**
     * @see org.kuali.kfs.service.ParameterService#setParameterForTesting(java.lang.Class componentClass, java.lang.String
     *      parameterName, java.lang.String parameterText)
     */
    public void setParameterForTesting(Class componentClass, String parameterName, String parameterText) {
        Parameter parameter = (Parameter) getParameter(componentClass, parameterName);
        parameter.setParameterValue(parameterText);
        SpringContext.getBean(BusinessObjectService.class).save(parameter);
        try {
            removeCachedMethod(ParameterService.class.getMethod("getParameterValue", new Class[] { Class.class, String.class }), new Object[] { componentClass, parameterName });
            removeCachedMethod(ParameterService.class.getMethod("getIndicatorParameter", new Class[] { Class.class, String.class }), new Object[] { componentClass, parameterName });
            removeCachedMethod(ParameterService.class.getMethod("getParameterValues", new Class[] { Class.class, String.class }), new Object[] { componentClass, parameterName });
        }
        catch (Exception e) {
            throw new RuntimeException(new StringBuffer("The setParameterForTesting of ParameterServiceImpl failed: ").append(componentClass).append(" / ").append(parameterName).toString(), e);
        }
    }

    @CacheNoCopy
    private String getNamespace(Class documentOrStepClass) {
        if (documentOrStepClass != null) {
            if (documentOrStepClass.isAnnotationPresent(NAMESPACE.class)) {
                return ((NAMESPACE) documentOrStepClass.getAnnotation(NAMESPACE.class)).namespace();
            }
            KualiModule module = moduleService.getResponsibleModule(documentOrStepClass);
            if (module != null) {
                return ParameterConstants.FINANCIAL_NAMESPACE_PREFIX + module.getModuleCode();
            }
            if (documentOrStepClass.getName().startsWith("org.kuali.core")) {
                return ParameterConstants.NERVOUS_SYSTEM_NAMESPACE;
            }
            if (documentOrStepClass.getName().startsWith("org.kuali.kfs")) {
                return ParameterConstants.FINANCIAL_SYSTEM_NAMESPACE;
            }
            throw new IllegalArgumentException("The getNamespace method of ParameterUtils requires documentOrStepClass with a package prefix of org.kuali.core, org.kuali.kfs, or org.kuali.module");
        }
        else {
            throw new IllegalArgumentException("The getNamespace method of ParameterUtils requires non-null documentOrStepClass");
        }
    }

    @CacheNoCopy
    private String getDetailType(Class documentOrStepClass) {
        if (documentOrStepClass.isAnnotationPresent(COMPONENT.class)) {
            return ((COMPONENT) documentOrStepClass.getAnnotation(COMPONENT.class)).component();
        }
        if (TransactionalDocument.class.isAssignableFrom(documentOrStepClass)) {
            return documentOrStepClass.getSimpleName().replace("Document", "");
        }
        else if (BusinessObject.class.isAssignableFrom(documentOrStepClass) || Step.class.isAssignableFrom(documentOrStepClass)) {
            return documentOrStepClass.getSimpleName();
        }
        throw new IllegalArgumentException("The getDetailType method of ParameterServiceImpl requires TransactionalDocument, BusinessObject, or Step class");
    }

    @CacheNoCopy
    private String getDetailTypeName(Class documentOrStepClass) {
        if (documentOrStepClass.isAnnotationPresent(COMPONENT.class)) {
            BusinessObjectEntry boe = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(documentOrStepClass.getName());
            if (boe != null) {
                return boe.getObjectLabel();
            }
            else {
                return ((COMPONENT) documentOrStepClass.getAnnotation(COMPONENT.class)).component();
            }
        }
        if (TransactionalDocument.class.isAssignableFrom(documentOrStepClass)) {
            return dataDictionaryService.getDocumentLabelByClass(documentOrStepClass);
        }
        else if (BusinessObject.class.isAssignableFrom(documentOrStepClass) || Step.class.isAssignableFrom(documentOrStepClass)) {
            BusinessObjectEntry boe = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(documentOrStepClass.getName());
            if (boe != null) {
                return boe.getObjectLabel();
            }
            else {
                return documentOrStepClass.getSimpleName();
            }
        }
        throw new IllegalArgumentException("The getDetailTypeName method of ParameterServiceImpl requires TransactionalDocument, BusinessObject, or Step class");
    }

    private ParameterEvaluator getParameterEvaluator(Parameter parameter) {
        ParameterEvaluatorImpl parameterEvaluator = new ParameterEvaluatorImpl();
        parameterEvaluator.setParameter(parameter);
        parameterEvaluator.setConstraintIsAllow(constraintIsAllow(parameter));
        parameterEvaluator.setValues(getParameterValues(parameter));
        return parameterEvaluator;
    }

    private ParameterEvaluator getParameterEvaluator(Parameter parameter, String constrainedValue) {
        ParameterEvaluator parameterEvaluator = getParameterEvaluator(parameter);
        parameterEvaluator.setConstrainedValue(constrainedValue);
        return parameterEvaluator;
    }

    private ParameterEvaluator getParameterEvaluator(Parameter parameter, String constrainingValue, String constrainedValue) {
        ParameterEvaluator parameterEvaluator = getParameterEvaluator(parameter, constrainedValue);
        ((ParameterEvaluatorImpl) parameterEvaluator).setValues(getParameterValues(parameter, constrainingValue));
        return parameterEvaluator;
    }

    private ParameterDetailType getParameterDetailType(Class documentOrStepClass) {
        String detailTypeString = getDetailType(documentOrStepClass);
        String detailTypeName = getDetailTypeName(documentOrStepClass);
        ParameterDetailType detailType = new ParameterDetailType(getNamespace(documentOrStepClass), detailTypeString, (detailTypeName == null) ? detailTypeString : detailTypeName);
        detailType.refreshNonUpdateableReferences();
        return detailType;
    }

    private Parameter getParameter(Class componentClass, String parameterName) {
        Parameter parameter = getParameter(getNamespace(componentClass), getDetailType(componentClass), parameterName);
        if (parameter == null) {
            throw new IllegalArgumentException("The getParameter method of ParameterServiceImpl requires a componentClass and parameterName that correspond to an existing parameter");
        }
        return parameter;
    }

    private List<String> getParameterValues(Parameter parameter, String constrainingValue) {
        List<String> constraintValuePairs = getParameterValues(parameter);
        for (String pair : constraintValuePairs) {
            if (constrainingValue.equals(StringUtils.substringBefore(pair, "="))) {
                return Arrays.asList(StringUtils.substringAfter(pair, "=").split(","));
            }
        }
        return Collections.EMPTY_LIST;
    }

    private List<String> getParameterValues(Parameter parameter) {
        if (parameter == null || StringUtils.isBlank(parameter.getParameterValue())) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(parameter.getParameterValue().split(";"));
    }

    private List<Parameter> getParameters(Class componentClass) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("parameterNamespaceCode", getNamespace(componentClass));
        fieldValues.put("parameterDetailTypeCode", getDetailType(componentClass));
        return new ArrayList<Parameter>(businessObjectService.findMatching(Parameter.class, fieldValues));
    }

    private Parameter getParameter(String namespaceCode, String detailTypeCode, String parameterName) {
        if (StringUtils.isBlank(namespaceCode) || StringUtils.isBlank(detailTypeCode) || StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("The getParameter method of KualiConfigurationServiceImpl requires a non-blank namespaceCode, parameterDetailTypeCode, and parameterName");
        }
        Parameter param = getParameterWithoutExceptions(namespaceCode, detailTypeCode, parameterName);
        if (param == null) {
            throw new IllegalArgumentException("The getParameter method of KualiConfigurationServiceImpl was unable to find parameter: " + namespaceCode + " / " + detailTypeCode + " / " + parameterName);
        }
        return param;
    }

    private Parameter getParameterWithoutExceptions(String namespaceCode, String detailTypeCode, String parameterName) {
        HashMap<String, String> crit = new HashMap<String, String>(3);
        crit.put("parameterNamespaceCode", namespaceCode);
        crit.put("parameterDetailTypeCode", detailTypeCode);
        crit.put("parameterName", parameterName);
        Parameter param = (Parameter) businessObjectService.findByPrimaryKey(Parameter.class, crit);
        return param;
    }

    private void removeCachedMethod(Method method, Object[] arguments) {
        MethodCacheInterceptor methodCacheInterceptor = SpringContext.getBean(MethodCacheInterceptor.class);
        
        String cacheKey = methodCacheInterceptor.buildCacheKey(method.toString(), arguments);
        methodCacheInterceptor.removeCacheKey(cacheKey);

        MethodCacheNoCopyInterceptor methodCacheNoCopyInterceptor = SpringContext.getBean(MethodCacheNoCopyInterceptor.class);
        methodCacheNoCopyInterceptor.removeCacheKey(cacheKey);
    }

    private boolean constraintIsAllow(Parameter parameter) {
        return KNSConstants.APC_ALLOWED_OPERATOR.equals(parameter.getParameterConstraintCode());
    }


    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setModuleService(KualiModuleService moduleService) {
        this.moduleService = moduleService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}