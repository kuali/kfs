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
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.util.cache.MethodCacheInterceptor;
import org.kuali.kfs.batch.Step;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants.COMPONENT;
import org.kuali.kfs.service.impl.ParameterConstants.NAMESPACE;

public class ParameterServiceImpl implements ParameterService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ParameterServiceImpl.class);
    private static List<ParameterDetailType> components = new ArrayList<ParameterDetailType>();
    private KualiConfigurationService configurationService;
    private DataDictionaryService dataDictionaryService;
    private KualiModuleService moduleService;
    private BusinessObjectService businessObjectService;
    private Class parameterEvaluatorClass;

    public boolean parameterExists(Class componentClass, String parameterName) {
        return configurationService.parameterExists(getNamespace(componentClass), getDetailType(componentClass), parameterName);
    }

    public boolean getIndicatorParameter(Class componentClass, String parameterName) {
        return "Y".equals(getParameter(componentClass, parameterName).getParameterValue());
    }

    public String getParameterValue(Class componentClass, String parameterName) {
        return getParameter(componentClass, parameterName).getParameterValue();
    }

    public String getParameterValue(Class componentClass, String parameterName, String constrainingValue) {
        List<String> parameterValues = getParameterValues(componentClass, parameterName, constrainingValue);
        if (parameterValues.size() == 1) {
            return parameterValues.get(0);
        }
        return null;
    }

    public List<String> getParameterValues(Class componentClass, String parameterName) {
        return configurationService.getParameterValues(getParameter(componentClass, parameterName));
    }

    public List<String> getParameterValues(Class componentClass, String parameterName, String constrainingValue) {
        return getParameterValues(getParameter(componentClass, parameterName), constrainingValue);
    }

    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName) {
        return getParameterEvaluator(getParameter(componentClass, parameterName));
    }

    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainedValue) {
        return getParameterEvaluator(getParameter(componentClass, parameterName), constrainedValue);
    }

    public ParameterEvaluator getParameterEvaluator(Class componentClass, String parameterName, String constrainingValue, String constrainedValue) {
        return getParameterEvaluator(getParameter(componentClass, parameterName), constrainingValue, constrainedValue);
    }

    public ParameterEvaluator getParameterEvaluator(Class componentClass, String allowParameterName, String denyParameterName, String constrainingValue, String constrainedValue) {
        Parameter allowParameter = getParameter(componentClass, allowParameterName);
        Parameter denyParameter = getParameter(componentClass, denyParameterName);
        if (!getParameterValues(allowParameter, constrainingValue).isEmpty() && !getParameterValues(denyParameter, constrainingValue).isEmpty()) {
            throw new IllegalArgumentException("The getParameterEvaluator(Class componentClass, String allowParameterName, String denyParameterName, String constrainingValue, String constrainedValue) method of ParameterServiceImpl does not facilitate evaluation of combination allow and deny parameters that both have values for the constraining value: " + allowParameterName + " / " + denyParameterName + " / " + constrainingValue);
        }
        return getParameterEvaluator(getParameterValues(denyParameter, constrainingValue).isEmpty() ? allowParameter : denyParameter, constrainingValue, constrainedValue);
    }

    public List<ParameterEvaluator> getParameterEvaluators(Class componentClass, String constrainedValue) {
        List<ParameterEvaluator> parameterEvaluators = new ArrayList<ParameterEvaluator>();
        for (Parameter parameter : getParameters(componentClass)) {
            parameterEvaluators.add(getParameterEvaluator(parameter, constrainedValue));
        }
        return parameterEvaluators;
    }

    public List<ParameterEvaluator> getParameterEvaluators(Class componentClass, String constrainingValue, String constrainedValue) {
        List<ParameterEvaluator> parameterEvaluators = new ArrayList<ParameterEvaluator>();
        for (Parameter parameter : getParameters(componentClass)) {
            parameterEvaluators.add(getParameterEvaluator(parameter, constrainingValue, constrainedValue));
        }
        return parameterEvaluators;
    }

    public List<ParameterDetailType> getNonDatabaseDetailTypes() {
        if (components.isEmpty()) {
            Map<String, ParameterDetailType> uniqueParameterDetailTypeMap = new HashMap<String, ParameterDetailType>();
            dataDictionaryService.getDataDictionary().forceCompleteDataDictionaryLoad();
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

    private void removeCachedMethod(Method method, Object[] arguments) {
        for (MethodCacheInterceptor methodCacheInterceptor : SpringContext.getMethodCacheInterceptors()) {
            if (methodCacheInterceptor.containsCacheKey(methodCacheInterceptor.buildCacheKey(method.toString(), arguments))) {
                methodCacheInterceptor.removeCacheKey(methodCacheInterceptor.buildCacheKey(method.toString(), arguments));
            }
        }
    }

    public void setParameterForTesting(Class componentClass, String parameterName, String parameterText) {
        Parameter parameter = (Parameter) getParameter(componentClass, parameterName);
        parameter.setParameterValue(parameterText);
        SpringContext.getBean(BusinessObjectService.class).save(parameter);
        try {
            String namespace = getNamespace(componentClass);
            String detailType = getDetailType(componentClass);
            removeCachedMethod(KualiConfigurationService.class.getMethod("getParameter", new Class[] { String.class, String.class, String.class }), new Object[] { namespace, detailType, parameterName });
            removeCachedMethod(KualiConfigurationService.class.getMethod("getIndicatorParameter", new Class[] { String.class, String.class, String.class }), new Object[] { namespace, detailType, parameterName });
            removeCachedMethod(KualiConfigurationService.class.getMethod("getParameterValues", new Class[] { String.class, String.class, String.class }), new Object[] { namespace, detailType, parameterName });
            removeCachedMethod(KualiConfigurationService.class.getMethod("getParameterValue", new Class[] { String.class, String.class, String.class }), new Object[] { namespace, detailType, parameterName });
        }
        catch (Exception e) {
            throw new RuntimeException(new StringBuffer("The setParameterForTesting of ParameterServiceImpl failed: ").append(componentClass).append(" / ").append(parameterName).toString(), e);
        }
    }

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
        throw new IllegalArgumentException("The getDetailedType method of ParameterUtils requires TransactionalDocument, BusinessObject, or Step class");
    }

    private ParameterEvaluator getParameterEvaluator(Parameter parameter) {
        ParameterEvaluator parameterEvaluator = SpringContext.getBean(ParameterEvaluator.class);
        parameterEvaluator.setParameter(parameter);
        parameterEvaluator.setConstraintIsAllow(configurationService.constraintIsAllow(parameter));
        parameterEvaluator.setValues(configurationService.getParameterValues(parameter));
        return parameterEvaluator;
    }

    private ParameterEvaluator getParameterEvaluator(Parameter parameter, String constrainedValue) {
        ParameterEvaluator parameterEvaluator = getParameterEvaluator(parameter);
        parameterEvaluator.setConstrainedValue(constrainedValue);
        return parameterEvaluator;
    }

    private ParameterEvaluator getParameterEvaluator(Parameter parameter, String constrainingValue, String constrainedValue) {
        ParameterEvaluator parameterEvaluator = getParameterEvaluator(parameter, constrainedValue);
        parameterEvaluator.setValues(getParameterValues(parameter, constrainingValue));
        return parameterEvaluator;
    }

    private ParameterDetailType getParameterDetailType(Class documentOrStepClass) {
        String detailTypeString = getDetailType(documentOrStepClass);
        ParameterDetailType detailType = new ParameterDetailType(getNamespace(documentOrStepClass), detailTypeString, detailTypeString);
        detailType.refreshNonUpdateableReferences();
        return detailType;
    }

    private Parameter getParameter(Class componentClass, String parameterName) {
        Parameter parameter = configurationService.getParameter(getNamespace(componentClass), getDetailType(componentClass), parameterName);
        if (parameter == null) {
            throw new IllegalArgumentException("The getParameter method of ParameterServiceImpl requires a componentClass and parameterName that correspond to an existing parameter");
        }
        return parameter;
    }

    private List<String> getParameterValues(Parameter parameter, String constrainingValue) {
        List<String> constraintValuePairs = configurationService.getParameterValues(parameter);
        for (String pair : constraintValuePairs) {
            if (constrainingValue.equals(StringUtils.substringBefore(pair, "="))) {
                return Arrays.asList(StringUtils.substringAfter(pair, "=").split(","));
            }
        }
        return Collections.EMPTY_LIST;
    }

    private List<Parameter> getParameters(Class componentClass) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("parameterNamespaceCode", getNamespace(componentClass));
        fieldValues.put("parameterDetailTypeCode", getDetailType(componentClass));
        return configurationService.getParameters(fieldValues);
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
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