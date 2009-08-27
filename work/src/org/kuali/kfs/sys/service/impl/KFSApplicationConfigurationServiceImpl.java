/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.ParameterDetailType;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.ParameterConstants.COMPONENT;
import org.kuali.rice.kns.service.impl.RiceApplicationConfigurationServiceImpl;
import org.kuali.rice.kns.util.KNSUtils;

public class KFSApplicationConfigurationServiceImpl extends RiceApplicationConfigurationServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KFSApplicationConfigurationServiceImpl.class);
    
    public List<ParameterDetailType> getNonDatabaseComponents() {
        if (components.isEmpty()) {
            List<ParameterDetailType> baseClassTypes = super.getNonDatabaseComponents();
            Map<String, ParameterDetailType> uniqueParameterDetailTypeMap = new HashMap<String, ParameterDetailType>();
            components.addAll(baseClassTypes);
            for (Step step : SpringContext.getBeansOfType(Step.class).values()) {
                try {
                    ParameterDetailType parameterDetailType = getParameterDetailType(step.getClass());
                    uniqueParameterDetailTypeMap.put(parameterDetailType.getParameterDetailTypeCode(), parameterDetailType);
                }
                catch (Exception e) {
                    LOG.error("The getDataDictionaryAndSpringComponents method of ParameterUtils encountered an exception while trying to create the detail type for step class: " + step.getClass(), e);
                }
            }
            components.addAll(uniqueParameterDetailTypeMap.values());
        }
        return components;
    }

    public String getDetailType(Class documentOrStepClass) {
        if (documentOrStepClass.isAnnotationPresent(COMPONENT.class)) {
            return ((COMPONENT) documentOrStepClass.getAnnotation(COMPONENT.class)).component();
        }
        if (TransactionalDocument.class.isAssignableFrom(documentOrStepClass)) {
            return documentOrStepClass.getSimpleName().replace("Document", "");
        }
        else if (BusinessObject.class.isAssignableFrom(documentOrStepClass) || Step.class.isAssignableFrom(documentOrStepClass)) {
            return documentOrStepClass.getSimpleName();
        }
        throw new IllegalArgumentException("The getDetailType method of ParameterServiceImpl requires a TransactionalDocument, BusinessObject, or Step class.");
    }

    protected String getDetailTypeName(Class documentOrStepClass) {
        if (documentOrStepClass.isAnnotationPresent(COMPONENT.class)) {
            BusinessObjectEntry boe = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(documentOrStepClass.getName());
            if (boe != null) {
                return boe.getObjectLabel();
            }
            else {
                return ((COMPONENT) documentOrStepClass.getAnnotation(COMPONENT.class)).component();
            }
        }
        if (TransactionalDocument.class.isAssignableFrom(documentOrStepClass)) {
            return getDataDictionaryService().getDocumentLabelByClass(documentOrStepClass);
        }
        else if (BusinessObject.class.isAssignableFrom(documentOrStepClass) || Step.class.isAssignableFrom(documentOrStepClass)) {
            BusinessObjectEntry boe = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(documentOrStepClass.getName());
            if (boe != null) {
                return boe.getObjectLabel();
            }
            else {
                return KNSUtils.getBusinessTitleForClass(documentOrStepClass);
            }
        }
        throw new IllegalArgumentException("The getDetailTypeName method of ParameterServiceImpl requires a TransactionalDocument, BusinessObject, or Step class.");
    }
}
