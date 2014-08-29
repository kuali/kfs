/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.module.ar.report.service.CustomerAgingReportService;
import org.kuali.kfs.module.ar.web.struts.CustomerAgingReportForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.springframework.beans.factory.InitializingBean;

public class CustomerAgingReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl implements InitializingBean {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerAgingReportLookupableHelperServiceImpl.class);
    protected DateTimeService dateTimeService;
    protected CustomerAgingReportService customerAgingReportService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    private String customerNameLabel;
    private String customerNumberLabel;
    private String cutoffdate30Label;
    private String cutoffdate60Label;
    private String cutoffdate90Label;

    private KualiDecimal total0to30 = KualiDecimal.ZERO;
    private KualiDecimal total31to60 = KualiDecimal.ZERO;
    private KualiDecimal total61to90 = KualiDecimal.ZERO;
    private KualiDecimal total91toSYSPR = KualiDecimal.ZERO;
    private KualiDecimal totalSYSPRplus1orMore = KualiDecimal.ZERO;

    private KualiDecimal totalOpenInvoices = KualiDecimal.ZERO;
    private KualiDecimal totalCredits = KualiDecimal.ZERO;
    private KualiDecimal totalWriteOffs = KualiDecimal.ZERO;

    private Date reportRunDate;
    private String reportOption;
    private String accountNumber;
    private String processingOrBillingChartCode;
    private String accountChartCode;
    private String orgCode;
    private String nbrDaysForLastBucket;
    // default is 120 days
    private String cutoffdate91toSYSPRlabel;
    private String cutoffdateSYSPRplus1orMorelabel;


    /**
     * Get the search results that meet the input search criteria.
     *
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     *
     * KRAD Conversion: Lookupable performs customization of the results by adding to
     * search results from list of CustomerAgingReportDetail records.
     *
     * Fields are in data dictionary for bo CustomerAgingReportDetail.
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));

        reportOption = fieldValues.get(ArPropertyConstants.REPORT_OPTION);
        accountNumber = fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        processingOrBillingChartCode = fieldValues.get(ArPropertyConstants.CustomerAgingReportFields.PROCESSING_OR_BILLING_CHART_ACCOUNT_CODE);
        accountChartCode = fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.ACCOUNT_CHART_CODE);
        orgCode = fieldValues.get(KFSPropertyConstants.ORGANIZATION_CODE);

        total0to30 = KualiDecimal.ZERO;
        total31to60 = KualiDecimal.ZERO;
        total61to90 = KualiDecimal.ZERO;
        total91toSYSPR = KualiDecimal.ZERO;
        totalSYSPRplus1orMore = KualiDecimal.ZERO;
        totalOpenInvoices = KualiDecimal.ZERO;
        totalWriteOffs = KualiDecimal.ZERO;
        totalCredits = KualiDecimal.ZERO;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        Date today = getDateTimeService().getCurrentDate();
        try {
            reportRunDate = dateFormat.parse(fieldValues.get(ArPropertyConstants.CustomerAgingReportFields.REPORT_RUN_DATE));
        }
        catch (ParseException e) {
            reportRunDate = today;
            LOG.error("problem during CustomerAgingReportLookupableHelperServiceImpl.getSearchResults()",e);
        }
        Date cutoffdate30 = DateUtils.addDays(reportRunDate, -30);
        Date cutoffdate31 = DateUtils.addDays(reportRunDate, -31);
        Date cutoffdate60 = DateUtils.addDays(reportRunDate, -60);
        Date cutoffdate61 = DateUtils.addDays(reportRunDate, -61);
        Date cutoffdate90 = DateUtils.addDays(reportRunDate, -90);
        Date cutoffdate91 = DateUtils.addDays(reportRunDate, -91);
        Date cutoffdate120 = DateUtils.addDays(reportRunDate, -1 * Integer.parseInt(nbrDaysForLastBucket));
        Date cutoffdate121 = DateUtils.addDays(cutoffdate120, -1);

        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();
        Map<String, CustomerAgingReportDetail> knownCustomers = new HashMap<String, CustomerAgingReportDetail>();

        CustomerAgingReportDetail custDetail;
        if (reportOption.equalsIgnoreCase(ArConstants.ReportOptionFieldValues.PROCESSING_ORG) && StringUtils.isNotBlank(processingOrBillingChartCode) && StringUtils.isNotBlank(orgCode)) {
            // 30 days
            computeFor0To30DaysByProcessingChartAndOrg(new java.sql.Date(cutoffdate30.getTime()), new java.sql.Date(reportRunDate.getTime()), knownCustomers);
            // 60 days
            computeFor31To60DaysByProcessingChartAndOrg(new java.sql.Date(cutoffdate60.getTime()), new java.sql.Date(cutoffdate31.getTime()), knownCustomers);
            // 90 days
            computeFor61To90DaysByProcessingChartAndOrg(new java.sql.Date(cutoffdate90.getTime()), new java.sql.Date(cutoffdate61.getTime()), knownCustomers);
            // 120 days
            computeFor91ToSYSPRDaysByProcessingChartAndOrg(new java.sql.Date(cutoffdate120.getTime()), new java.sql.Date(cutoffdate91.getTime()), knownCustomers);
            // 120 + older
            computeForSYSPRplus1orMoreDaysByProcessingChartAndOrg(null, new java.sql.Date(cutoffdate121.getTime()), knownCustomers);
        }
        if (reportOption.equalsIgnoreCase(ArConstants.ReportOptionFieldValues.BILLING_ORG) && StringUtils.isNotBlank(processingOrBillingChartCode) && StringUtils.isNotBlank(orgCode)) {
            // 30 days
            computeFor0To30DaysByBillingChartAndOrg(new java.sql.Date(cutoffdate30.getTime()), new java.sql.Date(reportRunDate.getTime()), knownCustomers);
            // 60 days
            computeFor31To60DaysByBillingChartAndOrg(new java.sql.Date(cutoffdate60.getTime()), new java.sql.Date(cutoffdate31.getTime()), knownCustomers);
            // 90 days
            computeFor61To90DaysByBillingChartAndOrg(new java.sql.Date(cutoffdate90.getTime()), new java.sql.Date(cutoffdate61.getTime()), knownCustomers);
            // 120 days
            computeFor91ToSYSPRDaysByBillingChartAndOrg(new java.sql.Date(cutoffdate120.getTime()), new java.sql.Date(cutoffdate91.getTime()), knownCustomers);
            // 120 + older
            computeForSYSPRplus1orMoreDaysByBillingChartAndOrg(null, new java.sql.Date(cutoffdate121.getTime()), knownCustomers);
        }
        if (reportOption.equalsIgnoreCase(ArConstants.CustomerAgingReportFields.ACCT) && StringUtils.isNotBlank(accountChartCode) && StringUtils.isNotBlank(accountNumber)) {
            // 30 days
            computeFor0To30DaysByAccount(new java.sql.Date(cutoffdate30.getTime()), new java.sql.Date(reportRunDate.getTime()), knownCustomers);
            // 60 days
            computeFor31To60DaysByAccount(new java.sql.Date(cutoffdate60.getTime()), new java.sql.Date(cutoffdate31.getTime()), knownCustomers);
            // 90 days
            computeFor61To90DaysByAccount(new java.sql.Date(cutoffdate90.getTime()), new java.sql.Date(cutoffdate61.getTime()), knownCustomers);
            // 120 days
            computeFor91ToSYSPRDaysByAccount(new java.sql.Date(cutoffdate120.getTime()), new java.sql.Date(cutoffdate91.getTime()), knownCustomers);
            // 120 + older
            computeForSYSPRplus1orMoreDaysByAccount(null, new java.sql.Date(cutoffdate121.getTime()), knownCustomers);
        }

        List<CustomerAgingReportDetail> results = new ArrayList<CustomerAgingReportDetail>();
        for (CustomerAgingReportDetail detail : knownCustomers.values()) {
            // set total open invoices
            KualiDecimal amount = detail.getUnpaidBalance0to30().add(detail.getUnpaidBalance31to60()).add(detail.getUnpaidBalance61to90()).add(detail.getUnpaidBalance91toSYSPR().add(detail.getUnpaidBalanceSYSPRplus1orMore()));
            detail.setTotalOpenInvoices(amount);
            totalOpenInvoices = totalOpenInvoices.add(amount);

            // find total writeoff
            KualiDecimal writeOffAmt = getCustomerAgingReportService().findWriteOffAmountByCustomerNumber(detail.getCustomerNumber());
            if (ObjectUtils.isNotNull(writeOffAmt)) {
                totalWriteOffs = totalWriteOffs.add(writeOffAmt);
            }
            else {
                writeOffAmt = KualiDecimal.ZERO;
            }
            detail.setTotalWriteOff(writeOffAmt);
            results.add(detail);
        }

        return new CollectionIncomplete<CustomerAgingReportDetail>(results, (long) results.size());
    }

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public List getReturnKeys() {
        return new ArrayList(fieldConversions.keySet());
    }

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    @Override
    protected Properties getParameters(BusinessObject bo, Map fieldConversions, String lookupImpl, List pkNames) {
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KRADConstants.DOC_FORM_KEY, getDocFormKey());
        parameters.put(KRADConstants.REFRESH_CALLER, lookupImpl);
        if (getReferencesToRefresh() != null) {
            parameters.put(KRADConstants.REFERENCES_TO_REFRESH, getReferencesToRefresh());
        }

        for (Object o : getReturnKeys()) {
            String fieldNm = (String) o;

            Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
            if (fieldVal == null) {
                fieldVal = KRADConstants.EMPTY_STRING;
            }

            // Encrypt value if it is a secure field
            if (fieldConversions.containsKey(fieldNm)) {
                fieldNm = (String) fieldConversions.get(fieldNm);
            }

            // need to format date in url
            if (fieldVal instanceof Date) {
                DateFormatter dateFormatter = new DateFormatter();
                fieldVal = dateFormatter.format(fieldVal);
            }

            parameters.put(fieldNm, fieldVal.toString());
        }

        return parameters;
    }

    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     *
     * KRAD Conversion: Lookupable performs customization of the display results.
     *
     * No use of data dictionary.
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection displayList = getSearchResults(lookupForm.getFieldsForLookup());
        // MJM get resultTable populated here

        HashMap<String, Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls
        for (Object aDisplayList : displayList) {
            BusinessObject element = (BusinessObject) aDisplayList;

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            if (ObjectUtils.isNotNull(getColumns())) {
                List<Column> columns = getColumns();
                populateCutoffdateLabels();
                for (Object column : columns) {

                    Column col = (Column) column;
                    Formatter formatter = col.getFormatter();

                    // pick off result column from result list, do formatting
                    String propValue = KRADConstants.EMPTY_STRING;
                    Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());

                    // set comparator and formatter based on property type
                    Class propClass = propertyTypes.get(col.getPropertyName());
                    if (propClass == null) {
                        propClass = ObjectUtils.getPropertyType(element, col.getPropertyName(),    getPersistenceStructureService());
                        if (propClass != null) {
                            propertyTypes.put(col.getPropertyName(), propClass);
                        }
                    }

                    // formatters
                    if (prop != null) {
                        propValue = getContractsGrantsReportHelperService().formatByType(prop, formatter);
                    }

                    // comparator
                    col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                    col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                    propValue = super.maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                    col.setPropertyValue(propValue);

                    // add correct label for sysparam
                    if (StringUtils.equals("unpaidBalance91toSYSPR", col.getPropertyName())) {
                        col.setColumnTitle(cutoffdate91toSYSPRlabel);
                    }
                    if (StringUtils.equals("unpaidBalanceSYSPRplus1orMore", col.getPropertyName())) {
                        col.setColumnTitle(cutoffdateSYSPRplus1orMorelabel);
                    }

                    if (StringUtils.isNotBlank(propValue)) {
                        // do not add link to the values in column "Customer Name"
                        if (StringUtils.equals(customerNameLabel, col.getColumnTitle())) {
                            col.setPropertyURL("");
                        } else {
                            col.setPropertyURL(getCustomerOpenItemReportUrl(element, col.getColumnTitle()));
                        }
                    }

                }

                ResultRow row = new ResultRow(columns, KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING);
                if (element instanceof PersistableBusinessObject) {
                    row.setObjectId(((PersistableBusinessObject) element).getObjectId());
                }

                boolean isRowReturnable = isResultReturnable(element);
                row.setRowReturnable(isRowReturnable);
                if (isRowReturnable) {
                    hasReturnableRow = true;
                }
                resultTable.add(row);
            }


            lookupForm.setHasReturnableRow(hasReturnableRow);
        }


        if (displayList.size() != 0) {
            ((CustomerAgingReportForm) lookupForm).setTotal0to30(total0to30.toString());
            ((CustomerAgingReportForm) lookupForm).setTotal31to60(total31to60.toString());
            ((CustomerAgingReportForm) lookupForm).setTotal61to90(total61to90.toString());
            ((CustomerAgingReportForm) lookupForm).setTotal91toSYSPR(total91toSYSPR.toString());
            ((CustomerAgingReportForm) lookupForm).setTotalSYSPRplus1orMore(totalSYSPRplus1orMore.toString());
            ((CustomerAgingReportForm) lookupForm).setTotalOpenInvoices(totalOpenInvoices.toString());
            ((CustomerAgingReportForm) lookupForm).setTotalWriteOffs(totalWriteOffs.toString());
        }

        return displayList;
    }

    protected void populateCutoffdateLabels() {
        customerNameLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CustomerAgingReportDetail.class.getName()).getAttributeDefinition(KFSConstants.CustomerAgingReport.CUSTOMER_NAME).getLabel();
        customerNumberLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CustomerAgingReportDetail.class.getName()).getAttributeDefinition(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER).getLabel();
        cutoffdate30Label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CustomerAgingReportDetail.class.getName()).getAttributeDefinition(KFSConstants.CustomerAgingReport.UNPAID_BALANCE_0_TO_30).getLabel();
        cutoffdate60Label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CustomerAgingReportDetail.class.getName()).getAttributeDefinition(KFSConstants.CustomerAgingReport.UNPAID_BALANCE_31_TO_60).getLabel();
        cutoffdate90Label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CustomerAgingReportDetail.class.getName()).getAttributeDefinition(KFSConstants.CustomerAgingReport.UNPAID_BALANCE_61_TO_90).getLabel();
    }

    protected String getCustomerOpenItemReportUrl(BusinessObject bo, String columnTitle) {
        CustomerAgingReportDetail detail = (CustomerAgingReportDetail) bo;
        Properties parameters = new Properties();
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, CustomerOpenItemReportDetail.class.getName());
        parameters.put(KFSConstants.RETURN_LOCATION_PARAMETER, StringUtils.EMPTY);
        parameters.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, ArConstants.CUSTOMER_OPEN_ITEM_REPORT_LOOKUPABLE_IMPL);
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        parameters.put(KFSConstants.CustomerOpenItemReport.REPORT_NAME, KFSConstants.CustomerOpenItemReport.OPEN_ITEM_REPORT_NAME);
        parameters.put(KFSConstants.DOC_FORM_KEY, "88888888");

        parameters.put(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, detail.getCustomerNumber());
        parameters.put(ArPropertyConstants.CustomerFields.CUSTOMER_NAME, detail.getCustomerName());
        parameters.put(ArPropertyConstants.REPORT_OPTION, reportOption);

        if (reportOption.equals(ArConstants.CustomerAgingReportFields.ACCT)) {
            parameters.put(ArPropertyConstants.CustomerAgingReportFields.ACCOUNT_CHART_CODE, accountChartCode);
            parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        } else {
            parameters.put(ArPropertyConstants.CustomerAgingReportFields.PROCESSING_OR_BILLING_CHART_CODE, processingOrBillingChartCode);
            parameters.put(KFSConstants.CustomerOpenItemReport.ORGANIZATION_CODE, orgCode);
        }

        DateFormatter dateFormatter = new DateFormatter();
        parameters.put(ArPropertyConstants.CustomerAgingReportFields.REPORT_RUN_DATE, dateFormatter.format(reportRunDate).toString());

        if (StringUtils.equals(columnTitle, customerNumberLabel)) {
            columnTitle = KFSConstants.CustomerOpenItemReport.ALL_DAYS;
        }
        else {
            String startDate = StringUtils.EMPTY;
            String endDate = StringUtils.EMPTY;

            if (StringUtils.equals(columnTitle, cutoffdate30Label)) {
                startDate = dateFormatter.format(DateUtils.addDays(reportRunDate, -30)).toString();
                endDate = dateFormatter.format(reportRunDate).toString();
            } else if (StringUtils.equals(columnTitle, cutoffdate60Label)) {
                startDate = dateFormatter.format(DateUtils.addDays(reportRunDate, -60)).toString();
                endDate = dateFormatter.format(DateUtils.addDays(reportRunDate, -31)).toString();
            } else if (StringUtils.equals(columnTitle, cutoffdate90Label)) {
                startDate = dateFormatter.format(DateUtils.addDays(reportRunDate, -90)).toString();
                endDate = dateFormatter.format(DateUtils.addDays(reportRunDate, -61)).toString();
            } else if (StringUtils.equals(columnTitle, cutoffdate91toSYSPRlabel)) {
                startDate = dateFormatter.format(DateUtils.addDays(reportRunDate, -120)).toString();
                endDate = dateFormatter.format(DateUtils.addDays(reportRunDate, -91)).toString();
            } else if (StringUtils.equals(columnTitle, cutoffdateSYSPRplus1orMorelabel)) {
                endDate = dateFormatter.format(DateUtils.addDays(reportRunDate, -121)).toString();
                columnTitle = Integer.toString((Integer.parseInt(nbrDaysForLastBucket)) + 1) + " days and older";
            }else {
                endDate = dateFormatter.format(reportRunDate).toString();
                columnTitle = KFSConstants.CustomerOpenItemReport.ALL_DAYS;
            }
            parameters.put(KFSConstants.CustomerOpenItemReport.REPORT_BEGIN_DATE, startDate);
            parameters.put(KFSConstants.CustomerOpenItemReport.REPORT_END_DATE, endDate);
        }
        parameters.put(KFSConstants.CustomerOpenItemReport.COLUMN_TITLE, columnTitle);

        String href = UrlFactory.parameterizeUrl(ArConstants.UrlActions.CUSTOMER_OPEN_ITEM_REPORT_LOOKUP, parameters);

        return href;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the total0to30 attribute.
     *
     * @return Returns the total0to30.
     */
    public KualiDecimal getTotal0to30() {
        return total0to30;
    }

    /**
     * Sets the total0to30 attribute value.
     *
     * @param total0to30 The total0to30 to set.
     */
    public void setTotal0to30(KualiDecimal total0to30) {
        this.total0to30 = total0to30;
    }

    /**
     * Gets the total31to60 attribute.
     *
     * @return Returns the total31to60.
     */
    public KualiDecimal getTotal31to60() {
        return total31to60;
    }

    /**
     * Sets the total31to60 attribute value.
     *
     * @param total31to60 The total31to60 to set.
     */
    public void setTotal31to60(KualiDecimal total31to60) {
        this.total31to60 = total31to60;
    }

    /**
     * Gets the total61to90 attribute.
     *
     * @return Returns the total61to90.
     */
    public KualiDecimal getTotal61to90() {
        return total61to90;
    }

    /**
     * Sets the total61to90 attribute value.
     *
     * @param total61to90 The total61to90 to set.
     */
    public void setTotal61to90(KualiDecimal total61to90) {
        this.total61to90 = total61to90;
    }

    /**
     * Gets the total91toSYSPR attribute.
     *
     * @return Returns the total91toSYSPR.
     */
    public KualiDecimal getTotal91toSYSPR() {
        return total91toSYSPR;
    }

    /**
     * Sets the total91toSYSPR attribute value.
     *
     * @param total91toSYSPR The total91toSYSPR to set.
     */
    public void setTotal91toSYSPR(KualiDecimal total91toSYSPR) {
        this.total91toSYSPR = total91toSYSPR;
    }

    /**
     * Gets the totalSYSPRplus1orMore attribute.
     *
     * @return Returns the totalSYSPRplus1orMore.
     */
    public KualiDecimal getTotalSYSPRplus1orMore() {
        return totalSYSPRplus1orMore;
    }

    /**
     * Sets the totalSYSPRplus1orMore attribute value.
     *
     * @param totalSYSPRplus1orMore The totalSYSPRplus1orMore to set.
     */
    public void setTotalSYSPRplus1orMore(KualiDecimal totalSYSPRplus1orMore) {
        this.totalSYSPRplus1orMore = totalSYSPRplus1orMore;
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor0To30DaysByProcessingChartAndOrg(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance0to30(amount);
            total0to30 = total0to30.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor31To60DaysByProcessingChartAndOrg(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance31to60(amount);
            total31to60 = total31to60.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor61To90DaysByProcessingChartAndOrg(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance61to90(amount);
            total61to90 = total61to90.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor91ToSYSPRDaysByProcessingChartAndOrg(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance91toSYSPR(amount);
            total91toSYSPR = total91toSYSPR.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeForSYSPRplus1orMoreDaysByProcessingChartAndOrg(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByProcessingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalanceSYSPRplus1orMore(amount);
            totalSYSPRplus1orMore = totalSYSPRplus1orMore.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor0To30DaysByBillingChartAndOrg(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance0to30(amount);
            total0to30 = total0to30.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor31To60DaysByBillingChartAndOrg(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance31to60(amount);
            total31to60 = total31to60.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor61To90DaysByBillingChartAndOrg(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance61to90(amount);
            total61to90 = total61to90.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor91ToSYSPRDaysByBillingChartAndOrg(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance91toSYSPR(amount);
            total91toSYSPR = total91toSYSPR.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeForSYSPRplus1orMoreDaysByBillingChartAndOrg(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByBillingChartAndOrg(processingOrBillingChartCode, orgCode, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalanceSYSPRplus1orMore(amount);
            totalSYSPRplus1orMore = totalSYSPRplus1orMore.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor0To30DaysByAccount(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByAccount(accountChartCode, accountNumber, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByAccount(accountChartCode, accountNumber, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByAccount(accountChartCode, accountNumber, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance0to30(amount);
            total0to30 = total0to30.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor31To60DaysByAccount(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByAccount(accountChartCode, accountNumber, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByAccount(accountChartCode, accountNumber, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByAccount(accountChartCode, accountNumber, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance31to60(amount);
            total31to60 = total31to60.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor61To90DaysByAccount(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByAccount(accountChartCode, accountNumber, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByAccount(accountChartCode, accountNumber, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByAccount(accountChartCode, accountNumber, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance61to90(amount);
            total61to90 = total61to90.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeFor91ToSYSPRDaysByAccount(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByAccount(accountChartCode, accountNumber, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByAccount(accountChartCode, accountNumber, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByAccount(accountChartCode, accountNumber, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalance91toSYSPR(amount);
            total91toSYSPR = total91toSYSPR.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param agingReportDao
     * @param begin
     * @param end
     * @param knownCustomers
     */
    protected void computeForSYSPRplus1orMoreDaysByAccount(java.sql.Date begin, java.sql.Date end, Map<String, CustomerAgingReportDetail> knownCustomers) {
        HashMap<String, KualiDecimal> invAmountDays = getCustomerAgingReportService().findInvoiceAmountByAccount(accountChartCode, accountNumber, begin, end);
        HashMap<String, KualiDecimal> appliedAmountDays = getCustomerAgingReportService().findAppliedAmountByAccount(accountChartCode, accountNumber, begin, end);
        HashMap<String, KualiDecimal> discountAmountDays = getCustomerAgingReportService().findDiscountAmountByAccount(accountChartCode, accountNumber, begin, end);
        Set<String> customerIds = invAmountDays.keySet();
        for (String customer : customerIds) {
            CustomerAgingReportDetail agingReportDetail = pickCustomerAgingReportDetail(knownCustomers, customer);
            KualiDecimal amount = (replaceNull(invAmountDays, customer).subtract(replaceNull(discountAmountDays, customer))).subtract(replaceNull(appliedAmountDays, customer));
            agingReportDetail.setUnpaidBalanceSYSPRplus1orMore(amount);
            totalSYSPRplus1orMore = totalSYSPRplus1orMore.add(amount);
        }
    }

    /**
     *
     * This method...
     * @param knownCustomers
     * @param customer
     * @return
     */
    protected CustomerAgingReportDetail pickCustomerAgingReportDetail(Map<String, CustomerAgingReportDetail> knownCustomers, String customer) {
        CustomerAgingReportDetail agingReportDetail = null;
        if ((agingReportDetail = knownCustomers.get(customer)) == null) {
            agingReportDetail = new CustomerAgingReportDetail();
            agingReportDetail.setCustomerNumber(customer.substring(0, customer.indexOf('-')));
            agingReportDetail.setCustomerName(customer.substring(customer.indexOf('-') + 1));
            knownCustomers.put(customer, agingReportDetail);
        }
        return agingReportDetail;
    }

    /**
     *
     * This method...
     * @param amountMap
     * @param customer
     * @return
     */
    protected KualiDecimal replaceNull(HashMap<String, KualiDecimal> amountMap, String customer) {
        return amountMap.get(customer) != null ? amountMap.get(customer) : KualiDecimal.ZERO;
    }

    /**
     * Sets properties which are parameter based - this is just the easiest place to set their values
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        nbrDaysForLastBucket = getParameterService().getParameterValueAsString(CustomerAgingReportDetail.class, ArConstants.CUSTOMER_INVOICE_AGE);
        // default is 120 days
        cutoffdate91toSYSPRlabel = "91-" + nbrDaysForLastBucket + " days";
        cutoffdateSYSPRplus1orMorelabel = Integer.toString((Integer.parseInt(nbrDaysForLastBucket)) + 1) + "+ days";
    }

    public CustomerAgingReportService getCustomerAgingReportService() {
        return customerAgingReportService;
    }

    public void setCustomerAgingReportService(CustomerAgingReportService customerAgingReportService) {
        this.customerAgingReportService = customerAgingReportService;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}