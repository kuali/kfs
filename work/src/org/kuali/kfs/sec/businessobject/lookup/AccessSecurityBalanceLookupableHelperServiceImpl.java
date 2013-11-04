/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject.lookup;

import java.beans.PropertyDescriptor;
import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ld.SegmentedBusinessObject;
import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sec.util.SecUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.CollectionFormatter;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.document.authorization.FieldRestriction;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.LookupableHelperService;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * Wraps balance inquiry lookupables so that access security can be applied to the results
 */
public class AccessSecurityBalanceLookupableHelperServiceImpl implements LookupableHelperService {
    protected static final String ACTION_URLS_EMPTY = "&nbsp;";

    protected AccessSecurityService accessSecurityService;
    protected LookupableHelperService lookupableHelperService;
    protected BusinessObjectMetaDataService businessObjectMetaDataService;
    protected BusinessObjectAuthorizationService businessObjectAuthorizationService;
    protected PersistenceStructureService persistenceStructureService;

    protected boolean glInquiry;
    protected boolean laborInquiry;

    public AccessSecurityBalanceLookupableHelperServiceImpl() {
        glInquiry = false;
        laborInquiry = false;
    }

    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {
        return lookupableHelperService.allowsMaintenanceNewOrCopyAction();
    }

    @Override
    public boolean allowsNewOrCopyAction(String documentTypeName) {
        return lookupableHelperService.allowsNewOrCopyAction(documentTypeName);
    }

    @Override
    public void applyFieldAuthorizationsFromNestedLookups(Field field) {
        lookupableHelperService.applyFieldAuthorizationsFromNestedLookups(field);
    }

    @Override
    public boolean checkForAdditionalFields(Map fieldValues) {
        return lookupableHelperService.checkForAdditionalFields(fieldValues);
    }

    @Override
    public String getActionUrls(BusinessObject businessObject, List pkNames, BusinessObjectRestrictions businessObjectRestrictions) {
        return lookupableHelperService.getActionUrls(businessObject, pkNames, businessObjectRestrictions);
    }

    @Override
    public String getBackLocation() {
        return lookupableHelperService.getBackLocation();
    }

    @Override
    public Class getBusinessObjectClass() {
        return lookupableHelperService.getBusinessObjectClass();
    }

    @Override
    public BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return lookupableHelperService.getBusinessObjectDictionaryService();
    }

    @Override
    public List getColumns() {
        return lookupableHelperService.getColumns();
    }

    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        return lookupableHelperService.getCustomActionUrls(businessObject, pkNames);
    }

    @Override
    public DataDictionaryService getDataDictionaryService() {
        return lookupableHelperService.getDataDictionaryService();
    }

    @Override
    public List getDefaultSortColumns() {
        return lookupableHelperService.getDefaultSortColumns();
    }

    @Override
    public String getDocFormKey() {
        return lookupableHelperService.getDocFormKey();
    }

    @Override
    public String getDocNum() {
        return lookupableHelperService.getDocNum();
    }

    @Override
    public Field getExtraField() {
        return lookupableHelperService.getExtraField();
    }

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String propertyName) {
        return lookupableHelperService.getInquiryUrl(businessObject, propertyName);
    }

    @Override
    public String getMaintenanceUrl(BusinessObject businessObject, HtmlData htmlData, List pkNames, BusinessObjectRestrictions businessObjectRestrictions) {
        return lookupableHelperService.getMaintenanceUrl(businessObject, htmlData, pkNames, businessObjectRestrictions);
    }

    @Override
    public Map getParameters() {
        return lookupableHelperService.getParameters();
    }

    @Override
    public String getPrimaryKeyFieldLabels() {
        return lookupableHelperService.getPrimaryKeyFieldLabels();
    }

    @Override
    public List<String> getReadOnlyFieldsList() {
        return lookupableHelperService.getReadOnlyFieldsList();
    }

    @Override
    public List getReturnKeys() {
        return lookupableHelperService.getReturnKeys();
    }

    @Override
    public String getReturnLocation() {
        return lookupableHelperService.getReturnLocation();
    }

    @Override
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        return lookupableHelperService.getReturnUrl(businessObject, lookupForm, returnKeys, businessObjectRestrictions);
    }

    @Override
    public HtmlData getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        return lookupableHelperService.getReturnUrl(businessObject, fieldConversions, lookupImpl, returnKeys, businessObjectRestrictions);
    }

    @Override
    public List<Row> getRows() {
        return lookupableHelperService.getRows();
    }

    /**
     * Gets search results and passes to access security service to apply access restrictions
     *
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    @Override
    public List getSearchResults(Map<String, String> fieldValues) {
        List results = lookupableHelperService.getSearchResults(fieldValues);

        int resultSizeBeforeRestrictions = results.size();
        if (glInquiry) {
            accessSecurityService.applySecurityRestrictionsForGLInquiry(results, GlobalVariables.getUserSession().getPerson());
        }
        if (laborInquiry) {
            accessSecurityService.applySecurityRestrictionsForLaborInquiry(results, GlobalVariables.getUserSession().getPerson());
        }


        accessSecurityService.compareListSizeAndAddMessageIfChanged(resultSizeBeforeRestrictions, results, SecKeyConstants.MESSAGE_BALANCE_INQUIRY_RESULTS_RESTRICTED);

        return results;
    }

    /**
     * Gets search results and passes to access security service to apply access restrictions
     *
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#getSearchResultsUnbounded(java.util.Map)
     */
    @Override
    public List getSearchResultsUnbounded(Map<String, String> fieldValues) {
        List results = lookupableHelperService.getSearchResultsUnbounded(fieldValues);

        int resultSizeBeforeRestrictions = results.size();
        if (glInquiry) {
            accessSecurityService.applySecurityRestrictionsForGLInquiry(results, GlobalVariables.getUserSession().getPerson());
        }
        if (laborInquiry) {
            accessSecurityService.applySecurityRestrictionsForLaborInquiry(results, GlobalVariables.getUserSession().getPerson());
        }

        accessSecurityService.compareListSizeAndAddMessageIfChanged(resultSizeBeforeRestrictions, results, SecKeyConstants.MESSAGE_BALANCE_INQUIRY_RESULTS_RESTRICTED);

        return results;
    }

    @Override
    public String getSupplementalMenuBar() {
        return lookupableHelperService.getSupplementalMenuBar();
    }

    @Override
    public String getTitle() {
        return lookupableHelperService.getTitle();
    }

    @Override
    public boolean isResultReturnable(BusinessObject object) {
        return lookupableHelperService.isResultReturnable(object);
    }

    @Override
    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        return lookupableHelperService.isSearchUsingOnlyPrimaryKeyValues();
    }

    @Override
    public void performClear(LookupForm lookupForm) {
        lookupableHelperService.performClear(lookupForm);
    }

    @Override
    public boolean performCustomAction(boolean ignoreErrors) {
        return lookupableHelperService.performCustomAction(ignoreErrors);
    }

    /**
     * Need to duplicate the logic of performLookup so that getSearchResults will be called on this class and not the nested lookup helper service
     *
     * @see org.kuali.rice.kns.lookup.LookupableHelperService#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));
        Collection displayList;


        preprocessDateFields(lookupFormFields);

        Map fieldsForLookup = new HashMap(lookupForm.getFieldsForLookup());
        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }

        HashMap<String, Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        List returnKeys = getReturnKeys();
        List pkNames = businessObjectMetaDataService.listPrimaryKeyFieldNames(getBusinessObjectClass());
        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();
            if (element instanceof PersistableBusinessObject) {
                lookupForm.setLookupObjectId(((PersistableBusinessObject) element).getObjectId());
            }

            BusinessObjectRestrictions businessObjectRestrictions = businessObjectAuthorizationService.getLookupResultRestrictions(element, user);

            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);

            String actionUrls = getActionUrls(element, pkNames, businessObjectRestrictions);
            // Fix for JIRA - KFSMI-2417
            if ("".equals(actionUrls)) {
                actionUrls = ACTION_URLS_EMPTY;
            }

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {

                Column col = (Column) iterator.next();
                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KRADConstants.EMPTY_STRING;
                Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());

                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(col.getPropertyName());
                if (propClass == null) {
                    try {
                        propClass = ObjectUtils.getPropertyType(element, col.getPropertyName(), persistenceStructureService);
                        propertyTypes.put(col.getPropertyName(), propClass);
                    }
                    catch (Exception e) {
                        throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
                    }
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

                    // for collection, use the list formatter if a formatter hasn't been defined yet
                    if (prop instanceof Collection && formatter == null) {
                        formatter = new CollectionFormatter();
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

                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);

                col.setPropertyValue(propValue);

                if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));

                }
            }

            ResultRow row = new ResultRow(columns, returnUrl.constructCompleteHtmlTag(), actionUrls);
            row.setRowId(returnUrl.getName());
            row.setReturnUrlHtmlData(returnUrl);
            // because of concerns of the BO being cached in session on the ResultRow,
            // let's only attach it when needed (currently in the case of export)
            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }

            if(element instanceof SegmentedBusinessObject) {
                for (String propertyName : ((SegmentedBusinessObject) element).getSegmentedPropertyNames()) {
                    columns.add(setupResultsColumn(element, propertyName, businessObjectRestrictions));
                }
            }

            if (element instanceof PersistableBusinessObject) {
                row.setObjectId((((PersistableBusinessObject) element).getObjectId()));
            }


            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }

        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
    }

    /**
     * @param element
     * @param attributeName
     * @return Column
     */
    protected Column setupResultsColumn(BusinessObject element, String attributeName, BusinessObjectRestrictions businessObjectRestrictions) {
        Column col = new Column();

        col.setPropertyName(attributeName);

        String columnTitle = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), attributeName);
        if (StringUtils.isBlank(columnTitle)) {
            columnTitle = getDataDictionaryService().getCollectionLabel(getBusinessObjectClass(), attributeName);
        }
        col.setColumnTitle(columnTitle);
        col.setMaxLength(getDataDictionaryService().getAttributeMaxLength(getBusinessObjectClass(), attributeName));

        Class formatterClass = getDataDictionaryService().getAttributeFormatter(getBusinessObjectClass(), attributeName);
        Formatter formatter = null;
        if (formatterClass != null) {
            try {
                formatter = (Formatter) formatterClass.newInstance();
                col.setFormatter(formatter);
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
            }
        }

        // pick off result column from result list, do formatting
        String propValue = KFSConstants.EMPTY_STRING;
        Object prop = ObjectUtils.getPropertyValue(element, attributeName);

        // set comparator and formatter based on property type
        Class propClass = null;
        try {
            PropertyDescriptor propDescriptor = PropertyUtils.getPropertyDescriptor(element, col.getPropertyName());
            if (propDescriptor != null) {
                propClass = propDescriptor.getPropertyType();
            }
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

        propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
        col.setPropertyValue(propValue);


        if (StringUtils.isNotBlank(propValue)) {
            col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
        }
        return col;
    }

    /**
     * changes from/to dates into the range operators the lookupable dao expects ("..",">" etc) this method modifies the passed in map and returns a list containing only the
     * modified fields
     *
     * @param lookupFormFields
     */
    protected Map<String, String> preprocessDateFields(Map lookupFormFields) {
        Map<String, String> fieldsToUpdate = new HashMap<String, String>();
        Set<String> fieldsForLookup = lookupFormFields.keySet();
        for (String propName : fieldsForLookup) {
            if (propName.startsWith(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
                String fromDateValue = (String) lookupFormFields.get(propName);
                String dateFieldName = StringUtils.remove(propName, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX);
                String dateValue = (String) lookupFormFields.get(dateFieldName);
                String newPropValue = dateValue;// maybe clean above with ObjectUtils.clean(propertyValue)
                if (StringUtils.isNotEmpty(fromDateValue) && StringUtils.isNotEmpty(dateValue)) {
                    newPropValue = fromDateValue + ".." + dateValue;
                }
                else if (StringUtils.isNotEmpty(fromDateValue) && StringUtils.isEmpty(dateValue)) {
                    newPropValue = ">=" + fromDateValue;
                }
                else if (StringUtils.isNotEmpty(dateValue) && StringUtils.isEmpty(fromDateValue)) {
                    newPropValue = "<=" + dateValue;
                } // could optionally continue on else here

                fieldsToUpdate.put(dateFieldName, newPropValue);
            }
        }
        // update lookup values from found date values to update
        Set<String> keysToUpdate = fieldsToUpdate.keySet();
        for (String updateKey : keysToUpdate) {
            lookupFormFields.put(updateKey, fieldsToUpdate.get(updateKey));
        }
        return fieldsToUpdate;
    }

    protected String maskValueIfNecessary(Class businessObjectClass, String propertyName, String propertyValue, BusinessObjectRestrictions businessObjectRestrictions) {
        String maskedPropertyValue = propertyValue;
        if (businessObjectRestrictions != null) {
            FieldRestriction fieldRestriction = businessObjectRestrictions.getFieldRestriction(propertyName);
            if (fieldRestriction != null && (fieldRestriction.isMasked() || fieldRestriction.isPartiallyMasked())) {
                maskedPropertyValue = fieldRestriction.getMaskFormatter().maskValue(propertyValue);
            }
        }
        return maskedPropertyValue;
    }

    @Override
    public void setBackLocation(String backLocation) {
        lookupableHelperService.setBackLocation(backLocation);
    }

    @Override
    public void setBusinessObjectClass(Class businessObjectClass) {
        lookupableHelperService.setBusinessObjectClass(businessObjectClass);
    }

    @Override
    public void setDocFormKey(String docFormKey) {
        lookupableHelperService.setDocFormKey(docFormKey);
    }

    @Override
    public void setDocNum(String docNum) {
        lookupableHelperService.setDocNum(docNum);
    }

    @Override
    public void setFieldConversions(Map fieldConversions) {
        lookupableHelperService.setFieldConversions(fieldConversions);
    }

    @Override
    public void setParameters(Map parameters) {
        lookupableHelperService.setParameters(parameters);
    }

    @Override
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList) {
        lookupableHelperService.setReadOnlyFieldsList(readOnlyFieldsList);
    }

    @Override
    public boolean shouldDisplayHeaderNonMaintActions() {
        return lookupableHelperService.shouldDisplayHeaderNonMaintActions();
    }

    @Override
    public boolean shouldDisplayLookupCriteria() {
        return lookupableHelperService.shouldDisplayLookupCriteria();
    }

    @Override
    public void validateSearchParameters(Map fieldValues) {
        lookupableHelperService.validateSearchParameters(fieldValues);
    }

    /**
     * Sets the accessSecurityService attribute value.
     *
     * @param accessSecurityService The accessSecurityService to set.
     */
    public void setAccessSecurityService(AccessSecurityService accessSecurityService) {
        this.accessSecurityService = accessSecurityService;
    }

    /**
     * Sets the lookupableHelperService attribute value.
     *
     * @param lookupableHelperService The lookupableHelperService to set.
     */
    public void setLookupableHelperService(LookupableHelperService lookupableHelperService) {
        this.lookupableHelperService = lookupableHelperService;
    }

    /**
     * Sets the businessObjectMetaDataService attribute value.
     *
     * @param businessObjectMetaDataService The businessObjectMetaDataService to set.
     */
    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
        this.businessObjectMetaDataService = businessObjectMetaDataService;
    }

    /**
     * Sets the businessObjectAuthorizationService attribute value.
     *
     * @param businessObjectAuthorizationService The businessObjectAuthorizationService to set.
     */
    public void setBusinessObjectAuthorizationService(BusinessObjectAuthorizationService businessObjectAuthorizationService) {
        this.businessObjectAuthorizationService = businessObjectAuthorizationService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     *
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Sets the glInquiry attribute value.
     *
     * @param glInquiry The glInquiry to set.
     */
    public void setGlInquiry(boolean glInquiry) {
        this.glInquiry = glInquiry;
    }

    /**
     * Sets the laborInquiry attribute value.
     *
     * @param laborInquiry The laborInquiry to set.
     */
    public void setLaborInquiry(boolean laborInquiry) {
        this.laborInquiry = laborInquiry;
    }

    //@Override
    @Override
    public void applyConditionalLogicForFieldDisplay() {
        // TODO Auto-generated method stub
    }

}
