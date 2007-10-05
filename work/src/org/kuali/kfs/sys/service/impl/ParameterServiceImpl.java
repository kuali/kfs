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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public boolean parameterExists(Class componentClass, String parameterName) {
        return configurationService.parameterExists(getNamespace(componentClass), getDetailType(componentClass), parameterName);
    }

    public String getParameterValue(Class componentClass, String parameterName) {
        return configurationService.getParameterValue(getNamespace(componentClass), getDetailType(componentClass), parameterName);
    }

    public boolean getIndicatorParameter(Class componentClass, String parameterName) {
        return configurationService.getIndicatorParameter(getNamespace(componentClass), getDetailType(componentClass), parameterName);
    }

    public List<String> getParameterValues(Class componentClass, String parameterName) {
        return configurationService.getParameterValuesAsList(getNamespace(componentClass), getDetailType(componentClass), parameterName);
    }

    public boolean evaluateConstrainedValue(Class componentClass, String parameterName, String constrainedValue) {
        return configurationService.succeedsRule(getNamespace(componentClass), getDetailType(componentClass), parameterName, constrainedValue);
    }

    public boolean evaluateConstrainedValue(Parameter parameter, String constrainedValue) {
        return configurationService.succeedsRule(parameter, constrainedValue);
    }

    public boolean evaluateConstrainedValue(Class componentClass, String parameterName, String constrainingValue, String constrainedValue) {
        return configurationService.evaluateConstrainedParameter(getNamespace(componentClass), getDetailType(componentClass), parameterName, constrainingValue, constrainedValue);
    }

    public boolean evaluateConstrainedValue(Class componentClass, String allowConstraintParameterName, String denyConstraintParameterName, String constrainingValue, String constrainedValue) {
        return configurationService.evaluateConstrainedParameter(getNamespace(componentClass), getDetailType(componentClass), allowConstraintParameterName, denyConstraintParameterName, constrainingValue, constrainedValue);
    }

    public boolean evaluateConstrainedValue(Parameter allowParameter, Parameter denyParameter, String constrainingValue, String constrainedValue) {
        return configurationService.evaluateConstrainedParameter(allowParameter, denyParameter, constrainingValue, constrainedValue);
    }

    public List<String> deriveConstrainedValues(Class componentClass, String parameterName, String constrainingValue) {
        return configurationService.getConstrainedValues(getNamespace(componentClass), getDetailType(componentClass), parameterName, constrainingValue);
    }

    public List<String> deriveConstrainedValues(Parameter parameter, String constrainingValue) {
        return configurationService.getConstrainedValues(parameter, constrainingValue);
    }

    public String getConstrainedValuesString(Class componentClass, String parameterName, String constrainingValue) {
        return deriveConstrainedValues(componentClass, parameterName, constrainingValue).toString().replace("[", "").replace("]", "");
    }

    public List<Parameter> getParameters(Class componentClass) {
        Map fieldValues = new HashMap();
        fieldValues.put("parameterNamespaceCode", getNamespace(componentClass));
        fieldValues.put("parameterDetailTypeCode", getDetailType(componentClass));
        return (List<Parameter>) businessObjectService.findMatching(Parameter.class, fieldValues);
    }

    public Parameter getParameter(Class componentClass, String parameterName) {
        Parameter parameter = new Parameter();
        parameter.setParameterNamespaceCode(getNamespace(componentClass));
        parameter.setParameterDetailTypeCode(getDetailType(componentClass));
        parameter.setParameterName(parameterName);
        return (Parameter) businessObjectService.retrieve(parameter);
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

    public void clearCache(Class componentClass, String parameterName) {
        try {
            removeCachedMethod(KualiConfigurationService.class.getMethod("getParameterValues", new Class[] { String.class, String.class, String.class }), new Object[] { getNamespace(componentClass), getDetailType(componentClass), parameterName });
            removeCachedMethod(KualiConfigurationService.class.getMethod("getParameterValuesAsList", new Class[] { String.class, String.class, String.class }), new Object[] { getNamespace(componentClass), getDetailType(componentClass), parameterName });
            removeCachedMethod(KualiConfigurationService.class.getMethod("getParameterValuesAsSet", new Class[] { String.class, String.class, String.class }), new Object[] { getNamespace(componentClass), getDetailType(componentClass), parameterName });
            removeCachedMethod(KualiConfigurationService.class.getMethod("getParameterValue", new Class[] { String.class, String.class, String.class }), new Object[] { getNamespace(componentClass), getDetailType(componentClass), parameterName });
        }
        catch (Exception e) {
            throw new RuntimeException(new StringBuffer("The clearCache of ParameterServiceImpl failed: ").append(componentClass).append(" / ").append(parameterName).toString(), e);
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
        else if (BusinessObject.class.isAssignableFrom(documentOrStepClass)) {
            return documentOrStepClass.getSimpleName();
        }
        else if (Step.class.isAssignableFrom(documentOrStepClass)) {
            return documentOrStepClass.getSimpleName();
        }
        throw new IllegalArgumentException("The getDetailedType method of ParameterUtils requires TransactionalDocument, BusinessObject, or Step class");
    }

    private ParameterDetailType getParameterDetailType(Class documentOrStepClass) {
        String detailTypeString = getDetailType(documentOrStepClass);
        ParameterDetailType detailType = new ParameterDetailType(getNamespace(documentOrStepClass), detailTypeString, detailTypeString);
        detailType.refreshNonUpdateableReferences();
        return detailType;
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