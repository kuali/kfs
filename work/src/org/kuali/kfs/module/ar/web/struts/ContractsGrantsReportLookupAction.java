/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.web.struts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportSearchCriteriaDataHolder;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.DynamicCollectionComparator.SortOrder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.action.KualiLookupAction;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * This Action Class defines all the core methods for Contracts and Grants Lookup.
 */
public class ContractsGrantsReportLookupAction extends KualiLookupAction {

    protected static final String SORT_INDEX_SESSION_KEY = "sortIndex";
    protected static final String NUM_SORT_INDEX_CLICK_SESSION_KEY = "numberOfSortClicked";

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiLookupAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // d-16544-s is field name from display table tab.
        String sortIndexParameter = request.getParameter("d-16544-s");
        if (sortIndexParameter != null) {
            // to store how many times user clicks sort links
            Integer clickedSession = ObjectUtils.isNull(GlobalVariables.getUserSession().retrieveObject(NUM_SORT_INDEX_CLICK_SESSION_KEY)) ? new Integer(1) : (Integer) GlobalVariables.getUserSession().retrieveObject(NUM_SORT_INDEX_CLICK_SESSION_KEY);
            if (ObjectUtils.isNotNull(GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY)) && GlobalVariables.getUserSession().retrieveObject(SORT_INDEX_SESSION_KEY).toString().equals(sortIndexParameter)) {
                GlobalVariables.getUserSession().addObject(NUM_SORT_INDEX_CLICK_SESSION_KEY, new Integer(clickedSession + 1));
            }
            GlobalVariables.getUserSession().addObject(SORT_INDEX_SESSION_KEY, sortIndexParameter);
        }
        return super.execute(mapping, form, request, response);
    }

    /**
     * @param index
     * @param businessObjectName
     * @return
     */
    protected String getFieldNameForSorting(int index, String businessObjectName) {
        BusinessObjectEntry boe = (BusinessObjectEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(businessObjectName);
        List<String> lookupResultFields = boe.getLookupDefinition().getResultFieldNames();
        return lookupResultFields.get(index);
    }

    /**
     * @param list
     * @param propertyName
     * @return
     */
    protected List<String> getListOfValuesSortedProperties(List list, String propertyName) {
        List<String> returnList = new ArrayList<String>();
        for (Object object : list) {
            if (!returnList.contains(getPropertyValue(object, propertyName))) {
                returnList.add(getPropertyValue(object, propertyName));
            }
        }
        return returnList;
    }

    /**
     * @param object
     * @param propertyName
     * @return
     */
    protected String getPropertyValue(Object object, String propertyName) {
        Object fieldValue = ObjectUtils.getPropertyValue(object, propertyName);
        return (ObjectUtils.isNull(fieldValue)) ? "" : StringUtils.trimAllWhitespace(fieldValue.toString());
    }

    /**
     * @param searchCriteria
     * @param fieldsForLookup
     */
    protected void buildReportForSearchCriteia(List<ContractsGrantsReportSearchCriteriaDataHolder> searchCriteria, Map fieldsForLookup, Class dataObjectClass) {
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        for (Object field : fieldsForLookup.keySet()) {

            String fieldString = (ObjectUtils.isNull(field)) ? "" : field.toString();
            String valueString = (ObjectUtils.isNull(fieldsForLookup.get(field))) ? "" : fieldsForLookup.get(field).toString();

            if (!fieldString.equals("") && !valueString.equals("") && !ArConstants.ReportsConstants.reportSearchCriteriaExceptionList.contains(fieldString)) {
                ContractsGrantsReportSearchCriteriaDataHolder criteriaData = new ContractsGrantsReportSearchCriteriaDataHolder();
                String label = dataDictionaryService.getAttributeLabel(dataObjectClass, fieldString);
                criteriaData.setSearchFieldLabel(label);
                criteriaData.setSearchFieldValue(valueString);
                searchCriteria.add(criteriaData);
            }
        }
    }

    /**
     * @param displayList
     * @param sortPropertyName
     */
    protected void sortReport(List displayList, String sortPropertyName) {
        Integer numSortIndexClick = (ObjectUtils.isNull(GlobalVariables.getUserSession().retrieveObject(NUM_SORT_INDEX_CLICK_SESSION_KEY))) ? 1 : new Integer(GlobalVariables.getUserSession().retrieveObject(NUM_SORT_INDEX_CLICK_SESSION_KEY).toString());
        if (((numSortIndexClick) % 2) == 0) {
            DynamicCollectionComparator.sort(displayList, SortOrder.DESC, sortPropertyName);
        }
        else {
            DynamicCollectionComparator.sort(displayList, SortOrder.ASC, sortPropertyName);
        }
    }

}
