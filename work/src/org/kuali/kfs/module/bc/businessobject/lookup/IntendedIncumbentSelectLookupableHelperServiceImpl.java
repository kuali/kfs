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
package org.kuali.module.budget.web.lookupable;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.comparator.CellComparatorHelper;
import org.kuali.core.web.format.BooleanFormatter;
import org.kuali.core.web.format.DateFormatter;
import org.kuali.core.web.format.Formatter;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Column;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbentSelect;
import org.kuali.module.budget.web.struts.form.TempListLookupForm;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class is used by the BC Organization Salary Setting process to customize the Intended Incumbent Selection
 * lookup operations.  The lookup operations here are different than the standard lookups in that there is no
 * value returned and the lookupable is for a table built on the fly and that only the rows associated with the user
 * are operated on.
 * 
 */
public class IntendedIncumbentSelectLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

    /**
     * This method differs from the one found in AbstractLookupableHelperServiceImpl in that it also uses a LookupForm
     * object to help set some of the values in the inquiryURL from instance vars found there.
     * 
     * @param bo
     * @param propertyName
     * @param lookupForm
     * @return
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.core.bo.BusinessObject, java.lang.String)
     */
    public String getInquiryUrl(BusinessObject bo, String propertyName, LookupForm lookupForm) {
        String lookupUrl;
        
        if (propertyName.equals("dummyBusinessObject.linkButtonOption")){
            
            TempListLookupForm tempListLookupForm = (TempListLookupForm) lookupForm; 
            BudgetConstructionIntendedIncumbentSelect intendedIncumbentSelect = (BudgetConstructionIntendedIncumbentSelect) bo;  

            String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.INCUMBENT_SALARY_SETTING_METHOD);

            parameters.put("emplid",intendedIncumbentSelect.getEmplid());
            //TODO BCFY needs added as hidden to all previous expansion/lookup screens
            parameters.put("universityFiscalYear", tempListLookupForm.getUniversityFiscalYear().toString());
            parameters.put("budgetByAccountMode","false");
            parameters.put("addLine","false");
            
            // anchor, if it exists
//            if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
//                parameters.put(BCConstants.RETURN_ANCHOR, ((KualiForm) form).getAnchor());
//            }

            // should be no return needed if opened in new window
            parameters.put(BCConstants.RETURN_FORM_KEY, "88888888");
                
            lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.INCUMBENT_SALARY_SETTING_ACTION, parameters);

        } else {
            // TODO Auto-generated method stub
            lookupUrl =  super.getInquiryUrl(bo, propertyName);
        }
        return lookupUrl;
        
    }

    /**
     * This method overrides the one in AbstractLookupableHelperServiceImpl so as to call getInquiryURL with the
     * LookupForm object added.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#performLookup(org.kuali.core.web.struts.form.LookupForm, java.util.Collection, boolean)
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection displayList;
        
        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }

        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            String returnUrl = getReturnUrl(element, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName());
            String actionUrls = getActionUrls(element);

            List<Column> columns = getColumns();
            List<Column> rowColumns = new ArrayList<Column>();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                
                Column col = (Column) iterator.next();
                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = RiceConstants.EMPTY_STRING;
                Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());
                
                // set comparator and formatter based on property type
                Class propClass = null;
                try {
                    propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
                }
                catch (Exception e) {
                    throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
                }

                // formatters
                if (prop != null) {
                    // for Booleans, always use BooleanFormatter
                    if (prop instanceof Boolean) {
                        formatter = new BooleanFormatter();
                    }
                    
                    // for Dates, always use DateFormatter
                    if (prop instanceof Date) {
                        formatter = new DateFormatter();
                    }

                    if (formatter != null) {
                        propValue = (String) formatter.format(prop);
                    }
                    else {
                        propValue = prop.toString();
                    }
                }

                // comparator
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));
                
                // check security on field and do masking if necessary
                boolean viewAuthorized = KNSServiceLocator.getAuthorizationService().isAuthorizedToViewAttribute(GlobalVariables.getUserSession().getUniversalUser(), element.getClass().getName(), col.getPropertyName());
                if (!viewAuthorized) {
                    Mask displayMask = getDataDictionaryService().getAttributeDisplayMask(element.getClass().getName(), col.getPropertyName());
                    propValue = displayMask.maskValue(propValue);
                }
                col.setPropertyValue(propValue);


                if (StringUtils.isNotBlank(propValue)) {
                    col.setPropertyURL(getInquiryUrl(element, col.getPropertyName(), lookupForm));
                }

                rowColumns.add(col);
            }

            ResultRow row = new ResultRow(rowColumns, returnUrl, actionUrls);
            if ( element instanceof PersistableBusinessObject ) {
                row.setObjectId(((PersistableBusinessObject)element).getObjectId());
            }
            resultTable.add(row);
        }

        return displayList;
    }

    /**
     * This overrides the method in AbstractLookupableHelperServiceImpl so as to not clear criteria fields
     * that are marked as hidden in the datadictionary for the associated lookupable.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        //TODO may want to push this method to a parent class to be used by all BC temp table lookups

        // We need to keep the personUniversalIdentifier hidden field in the criteria when
        // operating against BC temp lookup tables that are built on the fly. This field is
        // set behind the scenes so as to operate on only those rows associated with the current user.
        //LookupUtils.removeHiddenCriteriaFields( getBusinessObjectClass(), fieldValues );

        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));
        
        List searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, false);
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }

}
