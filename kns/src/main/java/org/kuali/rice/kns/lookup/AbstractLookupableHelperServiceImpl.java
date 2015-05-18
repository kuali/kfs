/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.cache.CopiedObject;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.document.authorization.FieldRestriction;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.datadictionary.mask.MaskFormatter;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ExternalizableBusinessObjectUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

import java.security.GeneralSecurityException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This class declares many of the common spring injected properties, the get/set-ers for them,
 * and some common util methods that require the injected services
 */
public abstract class AbstractLookupableHelperServiceImpl implements LookupableHelperService {

    protected static final String TITLE_RETURN_URL_PREPENDTEXT_PROPERTY = "title.return.url.value.prependtext";
    protected static final String TITLE_ACTION_URL_PREPENDTEXT_PROPERTY = "title.action.url.value.prependtext";
    protected static final String ACTION_URLS_CHILDREN_SEPARATOR = "&nbsp;|&nbsp;";
    protected static final String ACTION_URLS_CHILDREN_STARTER = "&nbsp;[";
    protected static final String ACTION_URLS_CHILDREN_END = "]";
    protected static final String ACTION_URLS_SEPARATOR = "&nbsp;&nbsp;";
    protected static final String ACTION_URLS_EMPTY = "&nbsp;";

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractLookupableHelperServiceImpl.class);

    protected Class businessObjectClass;
    protected Map<String, String[]> parameters;
    protected BusinessObjectDictionaryService businessObjectDictionaryService;
    protected BusinessObjectMetaDataService businessObjectMetaDataService;
    protected DataDictionaryService dataDictionaryService;
    protected PersistenceStructureService persistenceStructureService;
    protected EncryptionService encryptionService;
    protected List<String> readOnlyFieldsList;
    protected String backLocation;
    protected String docFormKey;
    protected Map fieldConversions;
    protected LookupService lookupService;
    protected List<Row> rows;
    protected String referencesToRefresh;
    protected SequenceAccessorService sequenceAccessorService;
    protected BusinessObjectService businessObjectService;
    protected LookupResultsService lookupResultsService;
    protected String docNum;
    protected ConfigurationService configurationService;
    protected ParameterService parameterService;
    protected BusinessObjectAuthorizationService businessObjectAuthorizationService;

    /**
     * @return the docNum
     */
    public String getDocNum() {
        return this.docNum;
    }

    /**
     * @param docNum the docNum to set
     */
    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public AbstractLookupableHelperServiceImpl() {
        rows = null;
    }

    /**
     * This implementation always returns false.
     *
     * @see LookupableHelperService#checkForAdditionalFields(Map)
     */
    public boolean checkForAdditionalFields(Map<String, String> fieldValues) {
        return false;
    }

    /**
     * @see LookupableHelperService#getBusinessObjectClass()
     */
    public Class getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * @see LookupableHelperService#setBusinessObjectClass(Class)
     */
    public void setBusinessObjectClass(Class businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
        setRows();
    }

    /**
     * @see LookupableHelperService#getParameters()
     */
    public Map<String, String[]> getParameters() {
        return parameters;
    }

    /**
     * @see LookupableHelperService#setParameters(Map)
     */
    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the dataDictionaryService attribute.
     *
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService != null ? dataDictionaryService : KRADServiceLocatorWeb.getDataDictionaryService();
    }

    /**
     * Sets the dataDictionaryService attribute value.
     *
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the businessObjectDictionaryService attribute.
     *
     * @return Returns the businessObjectDictionaryService.
     */
    public BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return businessObjectDictionaryService != null ? businessObjectDictionaryService : KNSServiceLocator
                .getBusinessObjectDictionaryService();
    }

    /**
     * Sets the businessObjectDictionaryService attribute value.
     *
     * @param businessObjectDictionaryService
     *         The businessObjectDictionaryService to set.
     */
    public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
        this.businessObjectDictionaryService = businessObjectDictionaryService;
    }

    /**
     * Gets the businessObjectMetaDataService attribute.
     *
     * @return Returns the businessObjectMetaDataService.
     */
    public BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        return businessObjectMetaDataService != null ? businessObjectMetaDataService : KNSServiceLocator
                .getBusinessObjectMetaDataService();
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
     * Gets the persistenceStructureService attribute.
     *
     * @return Returns the persistenceStructureService.
     */
    protected PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService != null ? persistenceStructureService : KRADServiceLocator
                .getPersistenceStructureService();
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
     * Gets the encryptionService attribute.
     *
     * @return Returns the encryptionService.
     */
    protected EncryptionService getEncryptionService() {
        return encryptionService != null ? encryptionService : CoreApiServiceLocator.getEncryptionService();
    }

    /**
     * Sets the encryptionService attribute value.
     *
     * @param encryptionService The encryptionService to set.
     */
    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    protected MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

    public MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        if (maintenanceDocumentDictionaryService == null) {
            maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
        }
        return maintenanceDocumentDictionaryService;
    }


    public BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
        if (businessObjectAuthorizationService == null) {
            businessObjectAuthorizationService = KNSServiceLocator.getBusinessObjectAuthorizationService();
        }
        return businessObjectAuthorizationService;
    }

    protected Inquirable kualiInquirable;

    public Inquirable getKualiInquirable() {
        if (kualiInquirable == null) {
            kualiInquirable = KNSServiceLocator.getKualiInquirable();
        }
        return kualiInquirable;
    }

    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }

    public void setKualiInquirable(Inquirable kualiInquirable) {
        this.kualiInquirable = kualiInquirable;
    }


    public ConfigurationService getKualiConfigurationService() {
        if (configurationService == null) {
            configurationService = KRADServiceLocator.getKualiConfigurationService();
        }
        return configurationService;
    }

    public void setParameterService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }


    public ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Determines if underlying lookup bo has associated maintenance document that allows new or copy maintenance actions.
     *
     * @return true if bo has maint doc that allows new or copy actions
     */
    public boolean allowsMaintenanceNewOrCopyAction() {
        boolean allowsNewOrCopy = false;

        String maintDocTypeName = getMaintenanceDocumentTypeName();
        Class boClass = this.getBusinessObjectClass();

        if (StringUtils.isNotBlank(maintDocTypeName)) {
            allowsNewOrCopy = getBusinessObjectAuthorizationService().canCreate(boClass, GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
        }
        return allowsNewOrCopy;
    }

    protected boolean allowsMaintenanceEditAction(BusinessObject businessObject) {
        boolean allowsEdit = false;

        String maintDocTypeName = getMaintenanceDocumentTypeName();

        if (StringUtils.isNotBlank(maintDocTypeName)) {
            allowsEdit = getBusinessObjectAuthorizationService().canMaintain(businessObject, GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
        }
        return allowsEdit;
    }


    /**
     * Build a maintenance url.
     *
     * @param bo           - business object representing the record for maint.
     * @param methodToCall - maintenance action
     * @return
     */
    final public String getMaintenanceUrl(BusinessObject businessObject, HtmlData htmlData, List pkNames, BusinessObjectRestrictions businessObjectRestrictions) {
        htmlData.setTitle(getActionUrlTitleText(businessObject, htmlData.getDisplayText(), pkNames, businessObjectRestrictions));
        return htmlData.constructCompleteHtmlTag();
    }

    /**
     * This method is called by performLookup method to generate action urls.
     * It calls the method getCustomActionUrls to get html data, calls getMaintenanceUrl to get the actual html tag,
     * and returns a formatted/concatenated string of action urls.
     *
     * @see LookupableHelperService#getActionUrls(BusinessObject)
     */
    final public String getActionUrls(BusinessObject businessObject, List pkNames, BusinessObjectRestrictions businessObjectRestrictions) {
        StringBuffer actions = new StringBuffer();
        List<HtmlData> htmlDataList = getCustomActionUrls(businessObject, pkNames);
        for (HtmlData htmlData : htmlDataList) {
            actions.append(getMaintenanceUrl(businessObject, htmlData, pkNames, businessObjectRestrictions));
            if (htmlData.getChildUrlDataList() != null) {
                if (htmlData.getChildUrlDataList().size() > 0) {
                    actions.append(ACTION_URLS_CHILDREN_STARTER);
                    for (HtmlData childURLData : htmlData.getChildUrlDataList()) {
                        actions.append(getMaintenanceUrl(businessObject, childURLData, pkNames, businessObjectRestrictions));
                        actions.append(ACTION_URLS_CHILDREN_SEPARATOR);
                    }
                    if (actions.toString().endsWith(ACTION_URLS_CHILDREN_SEPARATOR))
                        actions.delete(actions.length() - ACTION_URLS_CHILDREN_SEPARATOR.length(), actions.length());
                    actions.append(ACTION_URLS_CHILDREN_END);
                }
            }
            actions.append(ACTION_URLS_SEPARATOR);
        }
        if (actions.toString().endsWith(ACTION_URLS_SEPARATOR))
            actions.delete(actions.length() - ACTION_URLS_SEPARATOR.length(), actions.length());
        return actions.toString();
    }

    /**
     * Child classes should override this method if they want to return some other action urls.
     *
     * @returns This default implementation returns links to edit and copy maintenance action for
     * the current maintenance record if the business object class has an associated maintenance document.
     * Also checks value of allowsNewOrCopy in maintenance document xml before rendering the copy link.
     * @see LookupableHelperService#getCustomActionUrls(BusinessObject, List, List pkNames)
     */
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (allowsMaintenanceEditAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceNewOrCopyAction()) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceDeleteAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_DELETE_METHOD_TO_CALL, pkNames));
        }
        return htmlDataList;
    }

    /**
     * This method ...
     * for KULRice 3070
     *
     * @return
     */
    protected boolean allowsMaintenanceDeleteAction(BusinessObject businessObject) {

        boolean allowsMaintain = false;
        boolean allowsDelete = false;

        String maintDocTypeName = getMaintenanceDocumentTypeName();

        if (StringUtils.isNotBlank(maintDocTypeName)) {
            allowsMaintain = getBusinessObjectAuthorizationService().canMaintain(businessObject, GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
        }

        allowsDelete = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getAllowsRecordDeletion(businessObjectClass);

        return allowsDelete && allowsMaintain;
    }

    /**
     * This method constructs an AnchorHtmlData.
     * This method can be overriden by child classes if they want to construct the html data in a different way.
     * Foe example, if they want different type of html tag, like input/image.
     *
     * @param businessObject
     * @param methodToCall
     * @param displayText
     * @param pkNames
     * @return
     */
    protected HtmlData.AnchorHtmlData getUrlData(BusinessObject businessObject, String methodToCall, String displayText, List pkNames) {

        String href = getActionUrlHref(businessObject, methodToCall, pkNames);
        //String title = StringUtils.isBlank(href)?"":getActionUrlTitleText(businessObject, displayText, pkNames);
        HtmlData.AnchorHtmlData anchorHtmlData = new HtmlData.AnchorHtmlData(href, methodToCall, displayText);
        return anchorHtmlData;
    }

    /**
     * This method calls its overloaded method with displayText as methodToCall
     *
     * @param businessObject
     * @param methodToCall
     * @param pkNames
     * @return
     */
    protected HtmlData.AnchorHtmlData getUrlData(BusinessObject businessObject, String methodToCall, List pkNames) {
        return getUrlData(businessObject, methodToCall, methodToCall, pkNames);
    }

    /**
     * A utility method that returns an empty list of action urls.
     *
     * @return
     */
    protected List<HtmlData> getEmptyActionUrls() {
        return new ArrayList<HtmlData>();
    }

    protected HtmlData getEmptyAnchorHtmlData() {
        return new HtmlData.AnchorHtmlData();
    }

    /**
     * This method generates and returns href for the given parameters.
     * This method can be overridden by child classes if they have to generate href differently.
     * For example, refer to IntendedIncumbentLookupableHelperServiceImpl
     *
     * @param businessObject
     * @param methodToCall
     * @param pkNames
     * @return
     */
    protected String getActionUrlHref(BusinessObject businessObject, String methodToCall, List pkNames) {
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        // TODO: why is this not using the businessObject parmeter's class?
        parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObject.getClass().getName());
        parameters.putAll(getParametersFromPrimaryKey(businessObject, pkNames));
        if (StringUtils.isNotBlank(getReturnLocation())) {
            parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());
        }
        return UrlFactory.parameterizeUrl(KRADConstants.MAINTENANCE_ACTION, parameters);
    }

    protected Properties getParametersFromPrimaryKey(BusinessObject businessObject, List pkNames) {
        Properties parameters = new Properties();
        for (Iterator iter = pkNames.iterator(); iter.hasNext();) {
            String fieldNm = (String) iter.next();

            // If we cannot find the attribute in the data dictionary, then we cannot determine whether it should be encrypted
            if (getDataDictionaryService().getAttributeDefinition(businessObjectClass.getName(), fieldNm) == null) {
                String errorMessage = "The field " + fieldNm + " could not be found in the data dictionary for class "
                        + businessObjectClass.getName() + ", and thus it could not be determined whether it is a secure field.";

                if (ConfigContext.getCurrentContextConfig().getBooleanProperty(KNSConstants.EXCEPTION_ON_MISSING_FIELD_CONVERSION_ATTRIBUTE, false)) {
                    throw new RuntimeException(errorMessage);
                } else {
                    LOG.error(errorMessage);
                    continue;
                }
            }

            Object fieldVal = ObjectUtils.getPropertyValue(businessObject, fieldNm);
            if (fieldVal == null) {
                fieldVal = KRADConstants.EMPTY_STRING;
            }
            if (fieldVal instanceof Date) {
                String formattedString = "";
                if (Formatter.findFormatter(fieldVal.getClass()) != null) {
                    Formatter formatter = Formatter.getFormatter(fieldVal.getClass());
                    formattedString = (String) formatter.format(fieldVal);
                    fieldVal = formattedString;
                }
            }

            // secure values are not passed in urls
            if (getBusinessObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(businessObjectClass, fieldNm)) {
                LOG.warn("field name " + fieldNm + " is a secure value and not included in pk parameter results");
                continue;
            }

            parameters.put(fieldNm, fieldVal.toString());
        }
        return parameters;
    }

    /**
     * This method generates and returns title text for action urls.
     * Child classes can override this if they want to generate the title text differently.
     * For example, refer to BatchJobStatusLookupableHelperServiceImpl
     *
     * @param businessObject
     * @param displayText
     * @param pkNames
     * @return
     */
    protected String getActionUrlTitleText(BusinessObject businessObject, String displayText, List pkNames, BusinessObjectRestrictions businessObjectRestrictions) {
        String prependTitleText = displayText + " "
                + getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(getBusinessObjectClass().getName()).getObjectLabel()
                + " "
                + this.getKualiConfigurationService().getPropertyValueAsString(TITLE_ACTION_URL_PREPENDTEXT_PROPERTY);
        return HtmlData.getTitleText(prependTitleText, businessObject, pkNames, businessObjectRestrictions);
    }

    /**
     * Returns the maintenance document type associated with the business object class or null if one does not
     * exist.
     *
     * @return String representing the maintenance document type name
     */
    protected String getMaintenanceDocumentTypeName() {
        MaintenanceDocumentDictionaryService dd = getMaintenanceDocumentDictionaryService();
        String maintDocTypeName = dd.getDocumentTypeName(getBusinessObjectClass());
        return maintDocTypeName;
    }

    /**
     * Gets the readOnlyFieldsList attribute.
     *
     * @return Returns the readOnlyFieldsList.
     */
    public List<String> getReadOnlyFieldsList() {
        return readOnlyFieldsList;
    }


    /**
     * Sets the readOnlyFieldsList attribute value.
     *
     * @param readOnlyFieldsList The readOnlyFieldsList to set.
     */
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList) {
        this.readOnlyFieldsList = readOnlyFieldsList;
    }

    protected HashMap<String, Boolean> noLookupResultFieldInquiryCache = new HashMap<String, Boolean>();
    protected HashMap<Class, Class> inquirableClassCache = new HashMap<Class, Class>();
    protected HashMap<String, Boolean> forceLookupResultFieldInquiryCache = new HashMap<String, Boolean>();

    /**
     * Returns the inquiry url for a field if one exist.
     *
     * @param bo           the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        HtmlData inquiryUrl = new HtmlData.AnchorHtmlData();

        String cacheKey = bo.getClass().getName() + "." + propertyName;
        Boolean noLookupResultFieldInquiry = noLookupResultFieldInquiryCache.get(cacheKey);
        if (noLookupResultFieldInquiry == null) {
            noLookupResultFieldInquiry = getBusinessObjectDictionaryService().noLookupResultFieldInquiry(bo.getClass(), propertyName);
            if (noLookupResultFieldInquiry == null) {
                noLookupResultFieldInquiry = Boolean.TRUE;
            }
            noLookupResultFieldInquiryCache.put(cacheKey, noLookupResultFieldInquiry);
        }
        if (!noLookupResultFieldInquiry) {

            Class<Inquirable> inquirableClass = inquirableClassCache.get(bo.getClass());
            if (!inquirableClassCache.containsKey(bo.getClass())) {
                inquirableClass = getBusinessObjectDictionaryService().getInquirableClass(bo.getClass());
                inquirableClassCache.put(bo.getClass(), inquirableClass);
            }
            Inquirable inq = null;
            try {
                if (inquirableClass != null) {
                    inq = inquirableClass.newInstance();
                } else {
                    inq = getKualiInquirable();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Default Inquirable Class: " + inq.getClass());
                    }
                }
                Boolean forceLookupResultFieldInquiry = forceLookupResultFieldInquiryCache.get(cacheKey);
                if (forceLookupResultFieldInquiry == null) {
                    forceLookupResultFieldInquiry = getBusinessObjectDictionaryService().forceLookupResultFieldInquiry(bo.getClass(), propertyName);
                    if (forceLookupResultFieldInquiry == null) {
                        forceLookupResultFieldInquiry = Boolean.FALSE;
                    }
                    forceLookupResultFieldInquiryCache.put(cacheKey, forceLookupResultFieldInquiry);
                }
                inquiryUrl = inq.getInquiryUrl(bo, propertyName, forceLookupResultFieldInquiry);
            } catch (Exception ex) {
                LOG.error("unable to create inquirable to get inquiry URL", ex);
            }
        }

        return inquiryUrl;
    }

    protected CopiedObject<ArrayList<Column>> resultColumns = null;

    /**
     * Constructs the list of columns for the search results. All properties for the column objects come from the DataDictionary.
     */
    public List<Column> getColumns() {
        if (resultColumns == null) {
            ArrayList<Column> columns = new ArrayList<Column>();
            for (String attributeName : getBusinessObjectDictionaryService().getLookupResultFieldNames(getBusinessObjectClass())) {
                Column column = new Column();
                column.setPropertyName(attributeName);
                String columnTitle = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), attributeName);
                Boolean useShortLabel = getBusinessObjectDictionaryService().getLookupResultFieldUseShortLabel(businessObjectClass, attributeName);
                if (useShortLabel != null && useShortLabel) {
                    columnTitle = getDataDictionaryService().getAttributeShortLabel(getBusinessObjectClass(), attributeName);
                }
                if (StringUtils.isBlank(columnTitle)) {
                    columnTitle = getDataDictionaryService().getCollectionLabel(getBusinessObjectClass(), attributeName);
                }
                column.setColumnTitle(columnTitle);
                column.setMaxLength(getColumnMaxLength(attributeName));

                if (!businessObjectClass.isInterface()) {
                    try {
                        column.setFormatter(ObjectUtils.getFormatterWithDataDictionary(getBusinessObjectClass()
                                .newInstance(), attributeName));
                    } catch (InstantiationException e) {
                        LOG.info("Unable to get new instance of business object class: " + businessObjectClass.getName(), e);
                        // just swallow exception and leave formatter blank
                    } catch (IllegalAccessException e) {
                        LOG.info("Unable to get new instance of business object class: " + businessObjectClass.getName(), e);
                        // just swallow exception and leave formatter blank
                    }
                }

                String alternateDisplayPropertyName = getBusinessObjectDictionaryService()
                        .getLookupFieldAlternateDisplayAttributeName(getBusinessObjectClass(), attributeName);
                if (StringUtils.isNotBlank(alternateDisplayPropertyName)) {
                    column.setAlternateDisplayPropertyName(alternateDisplayPropertyName);
                }

                String additionalDisplayPropertyName = getBusinessObjectDictionaryService()
                        .getLookupFieldAdditionalDisplayAttributeName(getBusinessObjectClass(), attributeName);
                if (StringUtils.isNotBlank(additionalDisplayPropertyName)) {
                    column.setAdditionalDisplayPropertyName(additionalDisplayPropertyName);
                } else {
                    boolean translateCodes = getBusinessObjectDictionaryService().tranlateCodesInLookup(getBusinessObjectClass());
                    if (translateCodes) {
                        FieldUtils.setAdditionalDisplayPropertyForCodes(getBusinessObjectClass(), attributeName, column);
                    }
                }

                column.setTotal(getBusinessObjectDictionaryService().getLookupResultFieldTotal(getBusinessObjectClass(), attributeName));

                columns.add(column);
            }
            resultColumns = ObjectUtils.deepCopyForCaching(columns);
            return columns;
        }
        return resultColumns.getContent();
    }

    protected static Integer RESULTS_DEFAULT_MAX_COLUMN_LENGTH = null;

    protected int getColumnMaxLength(String attributeName) {
        Integer fieldDefinedMaxLength = getBusinessObjectDictionaryService().getLookupResultFieldMaxLength(getBusinessObjectClass(), attributeName);
        if (fieldDefinedMaxLength == null) {
            if (RESULTS_DEFAULT_MAX_COLUMN_LENGTH == null) {
                try {
                    RESULTS_DEFAULT_MAX_COLUMN_LENGTH = Integer.valueOf(getParameterService().getParameterValueAsString(
                            KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE, KRADConstants.RESULTS_DEFAULT_MAX_COLUMN_LENGTH));
                } catch (NumberFormatException ex) {
                    LOG.error("Lookup field max length parameter not found and unable to parse default set in system parameters (RESULTS_DEFAULT_MAX_COLUMN_LENGTH).");
                }
            }
            return RESULTS_DEFAULT_MAX_COLUMN_LENGTH.intValue();
        }
        return fieldDefinedMaxLength.intValue();
    }

    /**
     * @return Returns the backLocation.
     */
    public String getBackLocation() {
        return backLocation;
    }

    /**
     * @param backLocation The backLocation to set.
     */
    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }

    /**
     * @see LookupableHelperService#getReturnLocation()
     */
    public String getReturnLocation() {
        return backLocation;
    }

    /**
     * This method is for lookupable implementations
     *
     * @see LookupableHelperService#getReturnUrl(BusinessObject, Map, String, List)
     */
    final public HtmlData getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        String href = getReturnHref(businessObject, fieldConversions, lookupImpl, returnKeys);
        String returnUrlAnchorLabel =
                this.getKualiConfigurationService().getPropertyValueAsString(TITLE_RETURN_URL_PREPENDTEXT_PROPERTY);
        HtmlData.AnchorHtmlData anchor = new HtmlData.AnchorHtmlData(href, HtmlData.getTitleText(returnUrlAnchorLabel, businessObject, returnKeys, businessObjectRestrictions));
        anchor.setDisplayText(returnUrlAnchorLabel);
        return anchor;
    }

    /**
     * This method is for lookupable implementations
     *
     * @param businessObject
     * @param fieldConversions
     * @param lookupImpl
     * @param returnKeys
     * @return
     */
    final protected String getReturnHref(BusinessObject businessObject, Map fieldConversions, String lookupImpl, List returnKeys) {
        if (StringUtils.isNotBlank(backLocation)) {
            return UrlFactory.parameterizeUrl(backLocation, getParameters(
                    businessObject, fieldConversions, lookupImpl, returnKeys));
        }
        return "";
    }

    /**
     * @see LookupableHelperService#getReturnUrl(org.kuali.core.bo.BusinessObject, Map, String)
     */
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        Properties parameters = getParameters(
                businessObject, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName(), returnKeys);
        if (StringUtils.isEmpty(lookupForm.getHtmlDataType()) || HtmlData.ANCHOR_HTML_DATA_TYPE.equals(lookupForm.getHtmlDataType()))
            return getReturnAnchorHtmlData(businessObject, parameters, lookupForm, returnKeys, businessObjectRestrictions);
        else
            return getReturnInputHtmlData(businessObject, parameters, lookupForm, returnKeys, businessObjectRestrictions);
    }

    protected HtmlData getReturnInputHtmlData(BusinessObject businessObject, Properties parameters, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        String returnUrlAnchorLabel =
                this.getKualiConfigurationService().getPropertyValueAsString(TITLE_RETURN_URL_PREPENDTEXT_PROPERTY);
        String name = KRADConstants.MULTIPLE_VALUE_LOOKUP_SELECTED_OBJ_ID_PARAM_PREFIX + lookupForm.getLookupObjectId();
        HtmlData.InputHtmlData input = new HtmlData.InputHtmlData(name, HtmlData.InputHtmlData.CHECKBOX_INPUT_TYPE);
        input.setTitle(HtmlData.getTitleText(returnUrlAnchorLabel, businessObject, returnKeys, businessObjectRestrictions));
        if (((MultipleValueLookupForm) lookupForm).getCompositeObjectIdMap() == null ||
                ((MultipleValueLookupForm) lookupForm).getCompositeObjectIdMap().get(
                        ((PersistableBusinessObject) businessObject).getObjectId()) == null) {
            input.setChecked("");
        } else {
            input.setChecked(HtmlData.InputHtmlData.CHECKBOX_CHECKED_VALUE);
        }
        input.setValue(HtmlData.InputHtmlData.CHECKBOX_CHECKED_VALUE);
        return input;
    }

    protected HtmlData getReturnAnchorHtmlData(BusinessObject businessObject, Properties parameters, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        String returnUrlAnchorLabel =
                this.getKualiConfigurationService().getPropertyValueAsString(TITLE_RETURN_URL_PREPENDTEXT_PROPERTY);
        HtmlData.AnchorHtmlData anchor = new HtmlData.AnchorHtmlData(
                getReturnHref(parameters, lookupForm, returnKeys),
                HtmlData.getTitleText(returnUrlAnchorLabel, businessObject, returnKeys, businessObjectRestrictions));
        anchor.setDisplayText(returnUrlAnchorLabel);
        return anchor;
    }

    protected String getReturnHref(Properties parameters, LookupForm lookupForm, List returnKeys) {
        if (StringUtils.isNotBlank(backLocation)) {
            String href = UrlFactory.parameterizeUrl(backLocation, parameters);
            return addToReturnHref(href, lookupForm);
        }
        return "";
    }

    protected String addToReturnHref(String href, LookupForm lookupForm) {
        String lookupAnchor = "";
        if (StringUtils.isNotEmpty(lookupForm.getAnchor())) {
            lookupAnchor = lookupForm.getAnchor();
        }
        href += "&anchor=" + lookupAnchor + "&docNum=" + (StringUtils.isEmpty(getDocNum()) ? "" : getDocNum());
        return href;
    }

    protected Properties getParameters(BusinessObject bo, Map<String, String> fieldConversions, String lookupImpl, List returnKeys) {
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);
        if (getDocFormKey() != null) {
            parameters.put(KRADConstants.DOC_FORM_KEY, getDocFormKey());
        }
        if (lookupImpl != null) {
            parameters.put(KRADConstants.REFRESH_CALLER, lookupImpl);
        }
        if (getDocNum() != null) {
            parameters.put(KRADConstants.DOC_NUM, getDocNum());
        }

        if (getReferencesToRefresh() != null) {
            parameters.put(KRADConstants.REFERENCES_TO_REFRESH, getReferencesToRefresh());
        }

        Iterator returnKeysIt = getReturnKeys().iterator();
        while (returnKeysIt.hasNext()) {
            String fieldNm = (String) returnKeysIt.next();

            // If we cannot find the attribute in the data dictionary, then we cannot determine whether it should be encrypted
            if (getDataDictionaryService().getAttributeDefinition(businessObjectClass.getName(), fieldNm) == null) {
                String errorMessage = "The field " + fieldNm + " could not be found in the data dictionary for class "
                        + businessObjectClass.getName() + ", and thus it could not be determined whether it is a secure field.";

                if (ConfigContext.getCurrentContextConfig().getBooleanProperty(KNSConstants.EXCEPTION_ON_MISSING_FIELD_CONVERSION_ATTRIBUTE, false)) {
                    throw new RuntimeException(errorMessage);
                } else {
                    LOG.error(errorMessage);
                    continue;
                }
            }

            Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
            if (fieldVal == null) {
                fieldVal = KRADConstants.EMPTY_STRING;
            }

            if (getBusinessObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(businessObjectClass, fieldNm)) {
                LOG.warn("field name " + fieldNm + " is a secure value and not included in parameter results");
                continue;
            }

            //need to format date in url
            if (fieldVal instanceof Date) {
                DateFormatter dateFormatter = new DateFormatter();
                fieldVal = dateFormatter.format(fieldVal);
            }

            if (fieldConversions.containsKey(fieldNm)) {
                fieldNm = (String) fieldConversions.get(fieldNm);
            }

            parameters.put(fieldNm, fieldVal.toString());
        }

        return parameters;
    }

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    public List<String> getReturnKeys() {
        List<String> returnKeys;
        if (fieldConversions != null && !fieldConversions.isEmpty()) {
            returnKeys = new ArrayList<String>(fieldConversions.keySet());
        } else {
            returnKeys = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        }

        return returnKeys;
    }

    /**
     * Gets the docFormKey attribute.
     *
     * @return Returns the docFormKey.
     */
    public String getDocFormKey() {
        return docFormKey;
    }

    /**
     * Sets the docFormKey attribute value.
     *
     * @param docFormKey The docFormKey to set.
     */
    public void setDocFormKey(String docFormKey) {
        this.docFormKey = docFormKey;
    }

    /**
     * @see LookupableHelperService#setFieldConversions(Map)
     */
    public void setFieldConversions(Map fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    /**
     * Gets the lookupService attribute.
     *
     * @return Returns the lookupService.
     */
    protected LookupService getLookupService() {
        return lookupService != null ? lookupService : KRADServiceLocatorWeb.getLookupService();
    }

    /**
     * Sets the lookupService attribute value.
     *
     * @param lookupService The lookupService to set.
     */
    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    /**
     * Uses the DD to determine which is the default sort order.
     *
     * @return property names that will be used to sort on by default
     */
    public List<String> getDefaultSortColumns() {
        return getBusinessObjectDictionaryService().getLookupDefaultSortFieldNames(getBusinessObjectClass());
    }

    /**
     * Checks that any required search fields have value.
     *
     * @see LookupableHelperService#validateSearchParameters(Map)
     */
    public void validateSearchParameters(Map<String, String> fieldValues) {
        List<String> lookupFieldAttributeList = null;
        if (getBusinessObjectMetaDataService().isLookupable(getBusinessObjectClass())) {
            lookupFieldAttributeList = getBusinessObjectMetaDataService().getLookupableFieldNames(getBusinessObjectClass());
        }
        if (lookupFieldAttributeList == null) {
            throw new RuntimeException("Lookup not defined for business object " + getBusinessObjectClass());
        }
        for (Iterator iter = lookupFieldAttributeList.iterator(); iter.hasNext();) {
            String attributeName = (String) iter.next();
            if (fieldValues.containsKey(attributeName)) {
                // get label of attribute for message
                String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), attributeName);

                String attributeValue = (String) fieldValues.get(attributeName);

                // check for required if field does not have value
                if (StringUtils.isBlank(attributeValue)) {
                    if ((getBusinessObjectDictionaryService().getLookupAttributeRequired(getBusinessObjectClass(), attributeName)).booleanValue()) {
                        GlobalVariables.getMessageMap().putError(attributeName, RiceKeyConstants.ERROR_REQUIRED, attributeLabel);
                    }
                }
                validateSearchParameterWildcardAndOperators(attributeName, attributeValue);
            }
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    protected void validateSearchParameterWildcardAndOperators(String attributeName, String attributeValue) {
        if (StringUtils.isBlank(attributeValue))
            return;

        // make sure a wildcard/operator is in the value
        boolean found = false;
        for (SearchOperator op : SearchOperator.QUERY_CHARACTERS) {
            String queryCharacter = op.op();

            if (attributeValue.contains(queryCharacter)) {
                found = true;
            }
        }
        if (!found)
            return;

        String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), attributeName);
        if (getBusinessObjectDictionaryService().isLookupFieldTreatWildcardsAndOperatorsAsLiteral(businessObjectClass, attributeName)) {
            BusinessObject example = null;
            try {
                example = (BusinessObject) businessObjectClass.newInstance();
            } catch (Exception e) {
                LOG.error("Exception caught instantiating " + businessObjectClass.getName(), e);
                throw new RuntimeException("Cannot instantiate " + businessObjectClass.getName(), e);
            }

            Class propertyType = ObjectUtils.getPropertyType(example, attributeName, getPersistenceStructureService());
            if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {
                GlobalVariables.getMessageMap().putError(attributeName, RiceKeyConstants.ERROR_WILDCARDS_AND_OPERATORS_NOT_ALLOWED_ON_FIELD, attributeLabel);
            }
            if (TypeUtils.isStringClass(propertyType)) {
                GlobalVariables.getMessageMap().putInfo(attributeName, RiceKeyConstants.INFO_WILDCARDS_AND_OPERATORS_TREATED_LITERALLY, attributeLabel);
            }
        } else {
            if (getBusinessObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(businessObjectClass, attributeName)) {
                if (!attributeValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
                    // encrypted values usually come from the DB, so we don't need to filter for wildcards

                    // wildcards are not allowed on restricted fields, because they are typically encrypted, and wildcard searches cannot be performed without
                    // decrypting every row, which is currently not supported by KNS

                    GlobalVariables.getMessageMap().putError(attributeName, RiceKeyConstants.ERROR_SECURE_FIELD, attributeLabel);
                }
            }
        }
    }

    /**
     * Constructs the list of rows for the search fields. All properties for the field objects come
     * from the DataDictionary. To be called by setBusinessObject
     */
    protected void setRows() {
        List<String> lookupFieldAttributeList = null;
        if (getBusinessObjectMetaDataService().isLookupable(getBusinessObjectClass())) {
            lookupFieldAttributeList = getBusinessObjectMetaDataService().getLookupableFieldNames(
                    getBusinessObjectClass());
        }
        if (lookupFieldAttributeList == null) {
            throw new RuntimeException("Lookup not defined for business object " + getBusinessObjectClass());
        }

        // construct field object for each search attribute
        List fields = new ArrayList();
        try {
            fields = FieldUtils.createAndPopulateFieldsForLookup(lookupFieldAttributeList, getReadOnlyFieldsList(),
                    getBusinessObjectClass());
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }

        int numCols = getBusinessObjectDictionaryService().getLookupNumberOfColumns(this.getBusinessObjectClass());

        this.rows = FieldUtils.wrapFields(fields, numCols);
    }

    public List<Row> getRows() {
        return rows;
    }

    public abstract List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues);

    /**
     * This implementation of this method throws an UnsupportedOperationException, since not every implementation
     * may actually want to use this operation.  Subclasses desiring other behaviors
     * will need to override this.
     *
     * @see LookupableHelperService#getSearchResultsUnbounded(Map)
     */
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        throw new UnsupportedOperationException("Lookupable helper services do not always support getSearchResultsUnbounded");
    }

    /**
     * Performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param resultTable
     * @param bounded
     * @return
     */
    public Collection<? extends BusinessObject> performLookup(LookupForm lookupForm, Collection<ResultRow> resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupFormFields.get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupFormFields.get(KRADConstants.DOC_FORM_KEY));
        Collection<? extends BusinessObject> displayList;

        LookupUtils.preProcessRangeFields(lookupFormFields);

        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupFormFields);
        } else {
            displayList = getSearchResultsUnbounded(lookupFormFields);
        }

        boolean hasReturnableRow = false;

        List<String> returnKeys = getReturnKeys();
        List<String> pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action
        // urls
        for (BusinessObject element : displayList) {
            BusinessObject baseElement = element;
            //if ebo, then use base BO to get lookupId and find restrictions
            //we don't need to do this anymore as the BO is required to implement the EBO interface as of this time
            //if this needs reimplemented in the future, one should consider what happens/needs to happen
            //with the base BO fields (OBJ ID in particular) as they are all null/empty on new instantiation
            //which will fail if we try to depend on any values within it.
            //KULRICE-7223
//            if (ExternalizableBusinessObjectUtils.isExternalizableBusinessObject(element.getClass())) {
//                try {
//                    baseElement = (BusinessObject)this.getBusinessObjectClass().newInstance();
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }

            final String lookupId = KNSServiceLocator.getLookupResultsService().getLookupId(baseElement);
            if (lookupId != null) {
                lookupForm.setLookupObjectId(lookupId);
            }

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService()
                    .getLookupResultRestrictions(element, user);

            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);
            String actionUrls = getActionUrls(element, pkNames, businessObjectRestrictions);
            // Fix for JIRA - KFSMI-2417
            if ("".equals(actionUrls)) {
                actionUrls = ACTION_URLS_EMPTY;
            }

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                Column col = (Column) iterator.next();

                String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                Class propClass = getPropertyClass(element, col.getPropertyName());

                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                String propValueBeforePotientalMasking = propValue;
                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue,
                        businessObjectRestrictions);
                col.setPropertyValue(propValue);

                // if property value is masked, don't display additional or alternate properties, or allow totals
                if (StringUtils.equals(propValueBeforePotientalMasking, propValue)) {
                    if (StringUtils.isNotBlank(col.getAlternateDisplayPropertyName())) {
                        String alternatePropertyValue = ObjectUtils.getFormattedPropertyValue(element, col
                                .getAlternateDisplayPropertyName(), null);
                        col.setPropertyValue(alternatePropertyValue);
                    }

                    if (StringUtils.isNotBlank(col.getAdditionalDisplayPropertyName())) {
                        String additionalPropertyValue = ObjectUtils.getFormattedPropertyValue(element, col
                                .getAdditionalDisplayPropertyName(), null);
                        col.setPropertyValue(col.getPropertyValue() + " *-* " + additionalPropertyValue);
                    }
                } else {
                    col.setTotal(false);
                }

                if (col.isTotal()) {
                    Object unformattedPropValue = ObjectUtils.getPropertyValue(element, col.getPropertyName());
                    col.setUnformattedPropertyValue(unformattedPropValue);
                }

                if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
                }
            }

            ResultRow row = new ResultRow(columns, returnUrl.constructCompleteHtmlTag(), actionUrls);
            row.setRowId(returnUrl.getName());
            row.setReturnUrlHtmlData(returnUrl);

            // because of concerns of the BO being cached in session on the
            // ResultRow,
            // let's only attach it when needed (currently in the case of
            // export)
            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }

            if (lookupId != null) {
                row.setObjectId(lookupId);
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
     * Gets the Class for the property in the given BusinessObject instance, if
     * property is not accessible then runtime exception is thrown
     *
     * @param element      BusinessObject instance that contains property
     * @param propertyName Name of property in BusinessObject to get class for
     * @return Type for property as Class
     */
    protected Class getPropertyClass(BusinessObject element, String propertyName) {
        Class propClass = null;

        try {
            propClass = ObjectUtils.getPropertyType(element, propertyName, getPersistenceStructureService());

        } catch (Exception e) {
            throw new RuntimeException("Cannot access PropertyType for property " + "'" + propertyName + "' "
                    + " on an instance of '" + element.getClass().getName() + "'.", e);
        }

        return propClass;
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


    protected void setReferencesToRefresh(String referencesToRefresh) {
        this.referencesToRefresh = referencesToRefresh;
    }

    public String getReferencesToRefresh() {
        return referencesToRefresh;
    }

    protected SequenceAccessorService getSequenceAccessorService() {
        return sequenceAccessorService != null ? sequenceAccessorService : KRADServiceLocator
                .getSequenceAccessorService();
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService != null ? businessObjectService : KRADServiceLocator.getBusinessObjectService();
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected LookupResultsService getLookupResultsService() {
        return lookupResultsService != null ? lookupResultsService : KNSServiceLocator.getLookupResultsService();
    }

    public void setLookupResultsService(LookupResultsService lookupResultsService) {
        this.lookupResultsService = lookupResultsService;
    }

    /**
     * @return false always, subclasses should override to do something smarter
     * @see LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     */
    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        // by default, this implementation returns false, as lookups may not necessarily support this
        return false;
    }

    /**
     * Returns "N/A"
     *
     * @return "N/A"
     * @see LookupableHelperService#getPrimaryKeyFieldLabels()
     */
    public String getPrimaryKeyFieldLabels() {
        return KRADConstants.NOT_AVAILABLE_STRING;
    }

    /**
     * @see LookupableHelperService#isResultReturnable(org.kuali.core.bo.BusinessObject)
     */
    public boolean isResultReturnable(BusinessObject object) {
        return true;
    }

    /**
     * This method does the logic for the clear action.
     *
     * @see LookupableHelperService#performClear()
     */
    public void performClear(LookupForm lookupForm) {
        for (Iterator iter = this.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (field.isSecure()) {
                    field.setSecure(false);
                    field.setDisplayMaskValue(null);
                    field.setEncryptedValue(null);
                }

                if (!field.getFieldType().equals(Field.RADIO)) {
                    field.setPropertyValue(field.getDefaultValue());
                    if (field.getFieldType().equals(Field.MULTISELECT)) {
                        field.setPropertyValues(null);
                    }
                }
            }
        }
    }

    /**
     * @see LookupableHelperService#shouldDisplayHeaderNonMaintActions()
     */
    public boolean shouldDisplayHeaderNonMaintActions() {
        return true;
    }

    /**
     * @see LookupableHelperService#shouldDisplayLookupCriteria()
     */
    public boolean shouldDisplayLookupCriteria() {
        return true;
    }

    /**
     * @see LookupableHelperService#getSupplementalMenuBar()
     */
    public String getSupplementalMenuBar() {
        return new String();
    }

    /**
     * @see LookupableHelperService#getTitle()
     */
    public String getTitle() {
        return getBusinessObjectDictionaryService().getLookupTitle(getBusinessObjectClass());
    }

    /**
     * @see LookupableHelperService#performCustomAction(boolean)
     */
    public boolean performCustomAction(boolean ignoreErrors) {
        return false;
    }

    /**
     * @see Lookupable#getExtraField()
     */
    public Field getExtraField() {
        return null;
    }

    public boolean allowsNewOrCopyAction(String documentTypeName) {
        throw new UnsupportedOperationException("Function not supported.");
    }

    /**
     * Functional requirements state that users are able to perform searches using criteria values that they are not allowed to view.
     *
     * @see LookupableHelperService#applyFieldAuthorizationsFromNestedLookups(org.kuali.rice.krad.web.ui.Field)
     */
    public void applyFieldAuthorizationsFromNestedLookups(Field field) {
        BusinessObjectAuthorizationService boAuthzService = this.getBusinessObjectAuthorizationService();
        if (!Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType())) {
            if (field.getPropertyValue() != null && field.getPropertyValue().endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
                if (boAuthzService.attributeValueNeedsToBeEncryptedOnFormsAndLinks(businessObjectClass, field.getPropertyName())) {
                    AttributeSecurity attributeSecurity = getDataDictionaryService().getAttributeSecurity(businessObjectClass.getName(), field.getPropertyName());
                    Person user = GlobalVariables.getUserSession().getPerson();
                    String decryptedValue = "";
                    try {
                        String cipherText = StringUtils.removeEnd(field.getPropertyValue(), EncryptionService.ENCRYPTION_POST_PREFIX);
                        if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                            decryptedValue = getEncryptionService().decrypt(cipherText);
                        }
                    } catch (GeneralSecurityException e) {
                        throw new RuntimeException("Error decrypting value for business object " + businessObjectClass + " attribute " + field.getPropertyName(), e);
                    }
                    if (attributeSecurity.isMask() && !boAuthzService.canFullyUnmaskField(user,
                            businessObjectClass, field.getPropertyName(), null)) {
                        MaskFormatter maskFormatter = attributeSecurity.getMaskFormatter();
                        field.setEncryptedValue(field.getPropertyValue());
                        field.setDisplayMaskValue(maskFormatter.maskValue(decryptedValue));
                        field.setSecure(true);
                    } else if (attributeSecurity.isPartialMask() && !boAuthzService.canPartiallyUnmaskField(user,
                            businessObjectClass, field.getPropertyName(), null)) {
                        MaskFormatter maskFormatter = attributeSecurity.getPartialMaskFormatter();
                        field.setEncryptedValue(field.getPropertyValue());
                        field.setDisplayMaskValue(maskFormatter.maskValue(decryptedValue));
                        field.setSecure(true);
                    } else {
                        field.setPropertyValue(org.kuali.rice.krad.lookup.LookupUtils
                                .forceUppercase(businessObjectClass, field.getPropertyName(), decryptedValue));
                    }
                } else {
                    throw new RuntimeException("Field " + field.getPersonNameAttributeName() + " was encrypted on " + businessObjectClass.getName() +
                            " lookup was encrypted when it should not have been encrypted according to the data dictionary.");
                }
            }
        } else {
            if (boAuthzService.attributeValueNeedsToBeEncryptedOnFormsAndLinks(businessObjectClass, field.getPropertyName())) {
                LOG.error("Cannot handle multiple value field types that have field authorizations, please implement custom lookupable helper service");
                throw new RuntimeException("Cannot handle multiple value field types that have field authorizations.");
            }
        }
    }

    /**
     * Calls methods that can be overridden by child lookupables to implement conditional logic for setting
     * read-only, required, and hidden attributes. Called in the last part of the lookup lifecycle so the
     * fields values that will be sent will be correctly reflected in the rows (like after a clear).
     *
     * @see #getConditionallyReadOnlyPropertyNames()
     * @see #getConditionallyRequiredPropertyNames()
     * @see #getConditionallyHiddenPropertyNames()
     * @see LookupableHelperService#applyConditionalLogicForFieldDisplay()
     */
    public void applyConditionalLogicForFieldDisplay() {
        Set<String> readOnlyFields = getConditionallyReadOnlyPropertyNames();
        Set<String> requiredFields = getConditionallyRequiredPropertyNames();
        Set<String> hiddenFields = getConditionallyHiddenPropertyNames();

        for (Iterator iter = this.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();

                if (readOnlyFields != null && readOnlyFields.contains(field.getPropertyName())) {
                    field.setReadOnly(true);
                }

                if (requiredFields != null && requiredFields.contains(field.getPropertyName())) {
                    field.setFieldRequired(true);
                }

                if (hiddenFields != null && hiddenFields.contains(field.getPropertyName())) {
                    field.setFieldType(Field.HIDDEN);
                }
            }
        }
    }

    /**
     * @return Set of property names that should be set as read only based on the current search
     *         contents, note request parms containing search field values can be retrieved with
     *         {@link #getParameters()}
     */
    public Set<String> getConditionallyReadOnlyPropertyNames() {
        return new HashSet<String>();
    }

    /**
     * @return Set of property names that should be set as required based on the current search
     *         contents, note request parms containing search field values can be retrieved with
     *         {@link #getParameters()}
     */
    public Set<String> getConditionallyRequiredPropertyNames() {
        return new HashSet<String>();
    }

    /**
     * @return Set of property names that should be set as hidden based on the current search
     *         contents, note request parms containing search field values can be retrieved with
     *         {@link #getParameters()}
     */
    public Set<String> getConditionallyHiddenPropertyNames() {
        return new HashSet<String>();
    }

    /**
     * Helper method to get the value for a property out of the row-field graph. If property is
     * multi-value then the values will be joined by a semi-colon.
     *
     * @param propertyName - name of property to retrieve value for
     * @return current property value as a String
     */
    protected String getCurrentSearchFieldValue(String propertyName) {
        String currentValue = null;

        boolean fieldFound = false;
        for (Iterator iter = this.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();

                if (StringUtils.equalsIgnoreCase(propertyName, field.getPropertyName())) {
                    if (Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType())) {
                        currentValue = StringUtils.join(field.getPropertyValues(), ";");
                    } else {
                        currentValue = field.getPropertyValue();
                    }
                    fieldFound = true;
                }

                if (fieldFound) {
                    break;
                }
            }

            if (fieldFound) {
                break;
            }
        }

        return currentValue;
    }
}
