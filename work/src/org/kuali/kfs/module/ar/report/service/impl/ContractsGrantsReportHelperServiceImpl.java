/*
 * Copyright 2014 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * A service which basically encapsulates a Map of ContractsGrantsReportDataBuilderService to return the right one to an Action
 */
public class ContractsGrantsReportHelperServiceImpl implements ContractsGrantsReportHelperService {
    protected DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#getFieldNameForSorting(int, java.lang.String)
     */
    @Override
    public String getFieldNameForSorting(int index, String businessObjectName) {
        BusinessObjectEntry boe = (BusinessObjectEntry) getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObjectName);
        List<String> lookupResultFields = boe.getLookupDefinition().getResultFieldNames();
        return lookupResultFields.get(index);
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#getListOfValuesSortedProperties(java.util.List, java.lang.String)
     */
    @Override
    public List<String> getListOfValuesSortedProperties(List list, String propertyName) {
        List<String> returnList = new ArrayList<String>();
        for (Object object : list) {
            if (!returnList.contains(getPropertyValue(object, propertyName))) {
                returnList.add(getPropertyValue(object, propertyName));
            }
        }
        return returnList;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService#getPropertyValue(java.lang.Object, java.lang.String)
     */
    @Override
    public String getPropertyValue(Object object, String propertyName) {
        Object fieldValue = ObjectUtils.getPropertyValue(object, propertyName);
        return (ObjectUtils.isNull(fieldValue)) ? "" : StringUtils.trimAllWhitespace(fieldValue.toString());
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}