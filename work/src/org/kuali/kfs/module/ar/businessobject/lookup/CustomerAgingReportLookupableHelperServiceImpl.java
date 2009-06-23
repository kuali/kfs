/*
 * Copyright 2008 The Kuali Foundation.
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.CustomerInvoiceDocumentBatchStep;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.web.struts.CustomerAgingReportForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.format.BooleanFormatter;
import org.kuali.rice.kns.web.format.CollectionFormatter;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;



public class CustomerAgingReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerAgingReportLookupableHelperServiceImpl.class);

    private DataDictionaryService dataDictionaryService;
    private DateTimeService dateTimeService;

    private Map fieldConversions;

    private CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
    private CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);   
    private BusinessObjectService businessObjectService;

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

    private Date reportRunDate;
    private String reportOption;
    private String accountNumber;
    private String chartCode;
    private String orgCode;
    private String nbrDaysForLastBucket = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerAgingReportDetail.class, "CUSTOMER_INVOICE_AGE");     // ArConstants.CUSTOMER_INVOICE_AGE); // default is 120
    private String cutoffdate91toSYSPRlabel = "91-"+nbrDaysForLastBucket+" days"; 
    private String cutoffdateSYSPRplus1orMorelabel = Integer.toString((Integer.parseInt(nbrDaysForLastBucket))+1)+"+ days";


    /**
     * Get the search results that meet the input search criteria.
     *
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     */
    @Override
    public List getSearchResults(Map fieldValues) {

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        reportOption = (String) fieldValues.get(ArPropertyConstants.CustomerAgingReportFields.REPORT_OPTION);
        accountNumber = (String) fieldValues.get(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME);
        chartCode = (String) fieldValues.get(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME);
        orgCode = (String) fieldValues.get(KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME);

        Collection<CustomerInvoiceDetail> invoiceDetails = new ArrayList<CustomerInvoiceDetail>(); // default max is 10?

        Collection<CustomerInvoiceDocument> invoices;
        total0to30 = KualiDecimal.ZERO;
        total31to60 = KualiDecimal.ZERO;
        total61to90 = KualiDecimal.ZERO;
        total91toSYSPR = KualiDecimal.ZERO;
        totalSYSPRplus1orMore = KualiDecimal.ZERO;

        if (reportOption.equalsIgnoreCase(ArConstants.CustomerAgingReportFields.PROCESSING_ORG) && StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
            invoices = customerInvoiceDocumentService.getCustomerInvoiceDocumentsByProcessingChartAndOrg(chartCode, orgCode);
            for (CustomerInvoiceDocument ci : invoices) {
                invoiceDetails.addAll(customerInvoiceDocumentService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(ci));
//                LOG.info("\t\t****** PROCESSING ORGANIZATION\t\t"+invoiceDetails.toString()+chartCode+"\t"+orgCode);
//                for (CustomerInvoiceDetail cidetail : customerInvoiceDocumentService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(ci)) {
//                    LOG.info("\t** invoicedetail30\t"+cidetail.getAmount());
//                }

            }
        }
        if (reportOption.equalsIgnoreCase(ArConstants.CustomerAgingReportFields.BILLING_ORG) && StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
            invoices = customerInvoiceDocumentService.getCustomerInvoiceDocumentsByBillingChartAndOrg(chartCode, orgCode);
            for (CustomerInvoiceDocument ci : invoices) {
                invoiceDetails.addAll(customerInvoiceDocumentService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(ci));
            }
        }
        if (reportOption.equalsIgnoreCase(ArConstants.CustomerAgingReportFields.ACCT) && accountNumber.length() != 0) {
            invoiceDetails = getCustomerInvoiceDetailsByAccountNumber(accountNumber);
        }

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        Date today = getDateTimeService().getCurrentDate();
        try {
            reportRunDate = dateFormat.parse((String) fieldValues.get(ArPropertyConstants.CustomerAgingReportFields.REPORT_RUN_DATE));
        }
        catch (ParseException e) {
            reportRunDate = today;
            e.printStackTrace();
        }
        Date cutoffdate30 = DateUtils.addDays(reportRunDate, -30);
        Date cutoffdate60 = DateUtils.addDays(reportRunDate, -60);
        Date cutoffdate90 = DateUtils.addDays(reportRunDate, -90);
        //Date cutoffdate120 = DateUtils.addDays(reportRunDate, -120);
        Date cutoffdate120 = DateUtils.addDays(reportRunDate, -1 * Integer.parseInt(nbrDaysForLastBucket));

        //        LOG.info("\t\t********** REPORT DATE\t\t"+reportRunDate.toString());
        //        LOG.info("\t\t***********************  cutoffdate 30:\t\t"+cutoffdate30.toString());
        //        LOG.info("\t\t***********************  cutoffdate 60:\t\t"+cutoffdate60.toString());
        //        LOG.info("\t\t***********************  cutoffdate 90:\t\t"+cutoffdate90.toString());
        //        LOG.info("\t\t***********************  cutoffdate 120:\t\t"+cutoffdate120.toString());

        Map<String, Object> knownCustomers = new HashMap<String, Object>(invoiceDetails.size());

        CustomerAgingReportDetail custDetail;
        int detailnum = 0;
        // iterate over all invoices consolidating balances for each customer
        for (CustomerInvoiceDetail cid : invoiceDetails) {
            String invoiceDocumentNumber = cid.getDocumentNumber();
            CustomerInvoiceDocument custInvoice = customerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
            Date approvalDate;
            detailnum++;
            approvalDate = custInvoice.getBillingDate();
            if (ObjectUtils.isNull(approvalDate))
                continue;

            if (custInvoice != null && cid.getAmountOpen().isNonZero()) {//customerInvoiceDetailService.getAmountOpen(cid).isNonZero()) {

                Customer customerobj = custInvoice.getCustomer();
                String customerNumber = customerobj.getCustomerNumber();    // tested and works
                String customerName = customerobj.getCustomerName();  // tested and works


                if (knownCustomers.containsKey(customerNumber)) {
                    custDetail = (CustomerAgingReportDetail) knownCustomers.get(customerNumber);
                    //    LOG.info("\n\t\tcustomer:\t\t" + custDetail.getCustomerNumber() + "\tfound");
                } else {
                    custDetail = new CustomerAgingReportDetail();
                    custDetail.setCustomerName(customerName);
                    custDetail.setCustomerNumber(customerNumber);
                    knownCustomers.put(customerNumber, custDetail);
                    //    LOG.info("\n\t\tcustomer:\t\t" + custDetail.getCustomerNumber() + "\tADDED");
                }
                //LOG.info("\t\t APPROVAL DATE: \t\t" + approvalDate.toString() + "\t");
                //LOG.info("\t\t REPORT DATE: \t\t" + reportRunDate.toString() + "\t");
                if (!approvalDate.after(reportRunDate) && !approvalDate.before(cutoffdate30)) {
                    custDetail.setUnpaidBalance0to30(cid.getAmountOpen().add(custDetail.getUnpaidBalance0to30()));
                    //                total0to30 = total0to30.add(custDetail.getUnpaidBalance0to30());
                    total0to30 = total0to30.add(cid.getAmountOpen());
                    //LOG.info("\t\t 0to30 =\t\t" + custDetail.getCustomerNumber() + "\t" + custDetail.getUnpaidBalance0to30());
                    //LOG.info("\t\t 0to30 total =\t\t" + total0to30.toString());
                }
                if (approvalDate.before(cutoffdate30) && !approvalDate.before(cutoffdate60)) {
                    custDetail.setUnpaidBalance31to60(cid.getAmountOpen().add(custDetail.getUnpaidBalance31to60()));
                    total31to60 = total31to60.add(cid.getAmountOpen());
                    //LOG.info("\t\t31to60 =\t\t" + custDetail.getCustomerNumber() + "\t" + custDetail.getUnpaidBalance31to60());
                }
                if (approvalDate.before(cutoffdate60) && !approvalDate.before(cutoffdate90)) {
                    custDetail.setUnpaidBalance61to90(cid.getAmountOpen().add(custDetail.getUnpaidBalance61to90()));
                    total61to90 = total61to90.add(cid.getAmountOpen());
                    //LOG.info("\t\t61to90 =\t\t" + custDetail.getCustomerNumber() + "\t" + custDetail.getUnpaidBalance61to90());
                }
                if (approvalDate.before(cutoffdate90) && !approvalDate.before(cutoffdate120)) {
                    custDetail.setUnpaidBalance91toSYSPR(cid.getAmountOpen().add(custDetail.getUnpaidBalance91toSYSPR()));
                    total91toSYSPR = total91toSYSPR.add(cid.getAmountOpen());
                    //LOG.info("\t\t91to120 =\t\t" + custDetail.getCustomerNumber() + "\t" + custDetail.getUnpaidBalance91toSYSPR());
                }
                if (approvalDate.before(cutoffdate120)) {
                    custDetail.setUnpaidBalanceSYSPRplus1orMore(cid.getAmountOpen().add(custDetail.getUnpaidBalanceSYSPRplus1orMore()));
                    totalSYSPRplus1orMore = totalSYSPRplus1orMore.add(cid.getAmountOpen());
                    //LOG.info("\t\t120+ =\t\t" + custDetail.getCustomerNumber() + "\t" + custDetail.getUnpaidBalanceSYSPRplus1orMore());
                }

            }

        } // end for loop



        List results = new ArrayList();
        for (Object detail : knownCustomers.values()) {
            results.add(detail);
        }


        return new CollectionIncomplete(results, (long) results.size());
    }

    /**
     * @return a List of the CustomerInvoiceDetails associated with a given Account Number
     */
    @SuppressWarnings("unchecked")
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsByAccountNumber(String accountNumber) {
        Map args = new HashMap();
        args.put("accountNumber", accountNumber);
        return businessObjectService.findMatching(CustomerInvoiceDetail.class, args);
    }      

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    @Override
    public List getReturnKeys() {
        List returnKeys;
        returnKeys = new ArrayList(fieldConversions.keySet());
        return returnKeys;
    }

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected Properties getParameters(BusinessObject bo, Map fieldConversions, String lookupImpl, List pkNames) {
        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KNSConstants.DOC_FORM_KEY, getDocFormKey());
        parameters.put(KNSConstants.REFRESH_CALLER, lookupImpl);
        if (getReferencesToRefresh() != null) {
            parameters.put(KNSConstants.REFERENCES_TO_REFRESH, getReferencesToRefresh());
        }

        for (Object o : getReturnKeys()) {
            String fieldNm = (String) o;

            Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
            if (fieldVal == null) {
                fieldVal = KNSConstants.EMPTY_STRING;
            }

            // Encrypt value if it is a secure field
            if (fieldConversions.containsKey(fieldNm)) {
                fieldNm = (String) fieldConversions.get(fieldNm);
            }

            if (SpringContext.getBean(BusinessObjectAuthorizationService.class).attributeValueNeedsToBeEncryptedOnFormsAndLinks(bo.getClass(), fieldNm)) {
                // try {
                // fieldVal = encryptionService.encrypt(fieldVal);
                // }
                // catch (GeneralSecurityException e) {
                // LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
                // throw new RuntimeException(e);
                // }
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
        // MJM get resultTable populated here

        HashMap<String, Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        Person user = GlobalVariables.getUserSession().getPerson();
        
        try {
            // iterate through result list and wrap rows with return url and action urls
            for (Object aDisplayList : displayList) {
                BusinessObject element = (BusinessObject) aDisplayList;

                BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);
                // String returnUrl = getReturnUrl(element, lookupForm.getFieldConversions(),
                // lookupForm.getLookupableImplServiceName());
                // String actionUrls = getActionUrls(element);
                String returnUrl = "www.bigfrickenRETURNurl";
                String actionUrls = "www.someACTIONurl";


                if (ObjectUtils.isNotNull(getColumns())) {
                    List<Column> columns = getColumns();
                    populateCutoffdateLabels();
                    for (Object column : columns) {

                        Column col = (Column) column;
                        Formatter formatter = col.getFormatter();

                        // pick off result column from result list, do formatting
                        String propValue = KNSConstants.EMPTY_STRING;
                        Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());

                        // set comparator and formatter based on property type
                        Class propClass = propertyTypes.get(col.getPropertyName());
                        // if ( propClass == null ) {
                        // try {
                        // propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
                        // propertyTypes.put( col.getPropertyName(), propClass );
                        // } catch (Exception e) {
                        // throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " +
                        // " on an instance of '" + element.getClass().getName() + "'.", e);
                        // }
                        // }

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
                            } else {
                                propValue = prop.toString();
                            }
                        }

                        // comparator
                        col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                        col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                        propValue = super.maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                        col.setPropertyValue(propValue);

                        // add correct label for sysparam
                        if (StringUtils.equals("unpaidBalance91toSYSPR", col.getPropertyName()))
                            col.setColumnTitle(cutoffdate91toSYSPRlabel);
                        if (StringUtils.equals("unpaidBalanceSYSPRplus1orMore", col.getPropertyName()))
                            col.setColumnTitle(cutoffdateSYSPRplus1orMorelabel);

                        if (StringUtils.isNotBlank(propValue)) {
                            // do not add link to the values in column "Customer Name"
                            if (StringUtils.equals(customerNameLabel, col.getColumnTitle()))
                                col.setPropertyURL("");
                            else
                                col.setPropertyURL(getCustomerOpenItemReportUrl(element, col.getColumnTitle()));
                        }

                    }

                    ResultRow row = new ResultRow(columns, returnUrl, actionUrls);
                    if (element instanceof PersistableBusinessObject) {
                        row.setObjectId(((PersistableBusinessObject) element).getObjectId());
                    }

                    boolean rowReturnable = isResultReturnable(element);
                    row.setRowReturnable(rowReturnable);
                    if (rowReturnable) {
                        hasReturnableRow = true;
                    }
                    resultTable.add(row);
                }


                lookupForm.setHasReturnableRow(hasReturnableRow);
            }
        }
        catch (Exception e) {
            // do nothing, try block needed to make CustomerAgingReportLookupableHelperServiceImpl
           // e.printStackTrace();
        }
    

        if (displayList.size() != 0) {
            ((CustomerAgingReportForm) lookupForm).setTotal0to30(total0to30.toString());
            ((CustomerAgingReportForm) lookupForm).setTotal31to60(total31to60.toString());
            ((CustomerAgingReportForm) lookupForm).setTotal61to90(total61to90.toString());
            ((CustomerAgingReportForm) lookupForm).setTotal91toSYSPR(total91toSYSPR.toString());
            ((CustomerAgingReportForm) lookupForm).setTotalSYSPRplus1orMore(totalSYSPRplus1orMore.toString());
        }

        return displayList;
    }

    private void populateCutoffdateLabels() {
        customerNameLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CustomerAgingReportDetail.class.getName()).getAttributeDefinition(KFSConstants.CustomerAgingReport.CUSTOMER_NAME).getLabel();
        customerNumberLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CustomerAgingReportDetail.class.getName()).getAttributeDefinition(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER).getLabel();
        cutoffdate30Label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CustomerAgingReportDetail.class.getName()).getAttributeDefinition(KFSConstants.CustomerAgingReport.UNPAID_BALANCE_0_TO_30).getLabel();
        cutoffdate60Label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CustomerAgingReportDetail.class.getName()).getAttributeDefinition(KFSConstants.CustomerAgingReport.UNPAID_BALANCE_31_TO_60).getLabel();
        cutoffdate90Label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CustomerAgingReportDetail.class.getName()).getAttributeDefinition(KFSConstants.CustomerAgingReport.UNPAID_BALANCE_61_TO_90).getLabel();
    }

    private String getCustomerOpenItemReportUrl(BusinessObject bo, String columnTitle) {

        CustomerAgingReportDetail detail = (CustomerAgingReportDetail) bo;
        String href = "arCustomerOpenItemReportLookup.do" + "?businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerOpenItemReportDetail" + "&returnLocation=&lookupableImplementaionServiceName=arCustomerOpenItemReportLookupable" + "&methodToCall=search&reportName=" + KFSConstants.CustomerOpenItemReport.OPEN_ITEM_REPORT_NAME + "&docFormKey=88888888&customerNumber=" +
        // Customer Name, Customer Number
        detail.getCustomerNumber() + "&customerName=" + detail.getCustomerName();
        // Report Option
        href += "&reportOption=" + reportOption;
        if (reportOption.equals(ArConstants.CustomerAgingReportFields.ACCT))
            // Account Number
            href += "&accountNumber=" + accountNumber;
        else
            // Chart Code, Organization Code
            href += "&chartCode=" + chartCode + "&orgCode=" + orgCode;

        // Report Run Date
        DateFormatter dateFormatter = new DateFormatter();
        href += "&reportRunDate=" + dateFormatter.format(reportRunDate).toString();

        if (StringUtils.equals(columnTitle, customerNumberLabel)) {
            href += "&columnTitle=" + KFSConstants.CustomerOpenItemReport.ALL_DAYS;
        }
        else {
            if (StringUtils.equals(columnTitle, cutoffdate30Label))
                href += "&startDate=" + dateFormatter.format(DateUtils.addDays(reportRunDate, -30)).toString() + "&endDate=" + dateFormatter.format(reportRunDate).toString();
            else if (StringUtils.equals(columnTitle, cutoffdate60Label))
                href += "&startDate=" + dateFormatter.format(DateUtils.addDays(reportRunDate, -60)).toString() + "&endDate=" + dateFormatter.format(DateUtils.addDays(reportRunDate, -31)).toString();
            else if (StringUtils.equals(columnTitle, cutoffdate90Label))
                href += "&startDate=" + dateFormatter.format(DateUtils.addDays(reportRunDate, -90)).toString() + "&endDate=" + dateFormatter.format(DateUtils.addDays(reportRunDate, -61)).toString();
            else if (StringUtils.equals(columnTitle, cutoffdate91toSYSPRlabel))
                href += "&startDate=" + dateFormatter.format(DateUtils.addDays(reportRunDate, -120)).toString() + "&endDate=" + dateFormatter.format(DateUtils.addDays(reportRunDate, -91)).toString();
            else if (StringUtils.equals(columnTitle, cutoffdateSYSPRplus1orMorelabel)) {
                href += "&startDate=" + "&endDate=" + dateFormatter.format(DateUtils.addDays(reportRunDate, -121)).toString();
                columnTitle = Integer.toString((Integer.parseInt(nbrDaysForLastBucket))+1)+" days and older";
            }
            href += "&columnTitle=" + columnTitle;
        }

        return href;
    }

    /**
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * 
     * This method...
     * @return
     */
    public DateTimeService getDateTimeService() {
        if(dateTimeService==null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }
    
    /**
     * 
     * This method...
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    /**
     * Gets the total0to30 attribute. 
     * @return Returns the total0to30.
     */
    public KualiDecimal getTotal0to30() {
        return total0to30;
    }

    /**
     * Sets the total0to30 attribute value.
     * @param total0to30 The total0to30 to set.
     */
    public void setTotal0to30(KualiDecimal total0to30) {
        this.total0to30 = total0to30;
    }

    /**
     * Gets the total31to60 attribute. 
     * @return Returns the total31to60.
     */
    public KualiDecimal getTotal31to60() {
        return total31to60;
    }

    /**
     * Sets the total31to60 attribute value.
     * @param total31to60 The total31to60 to set.
     */
    public void setTotal31to60(KualiDecimal total31to60) {
        this.total31to60 = total31to60;
    }

    /**
     * Gets the total61to90 attribute. 
     * @return Returns the total61to90.
     */
    public KualiDecimal getTotal61to90() {
        return total61to90;
    }

    /**
     * Sets the total61to90 attribute value.
     * @param total61to90 The total61to90 to set.
     */
    public void setTotal61to90(KualiDecimal total61to90) {
        this.total61to90 = total61to90;
    }

    /**
     * Gets the total91toSYSPR attribute. 
     * @return Returns the total91toSYSPR.
     */
    public KualiDecimal getTotal91toSYSPR() {
        return total91toSYSPR;
    }

    /**
     * Sets the total91toSYSPR attribute value.
     * @param total91toSYSPR The total91toSYSPR to set.
     */
    public void setTotal91toSYSPR(KualiDecimal total91toSYSPR) {
        this.total91toSYSPR = total91toSYSPR;
    }

    /**
     * Gets the totalSYSPRplus1orMore attribute. 
     * @return Returns the totalSYSPRplus1orMore.
     */
    public KualiDecimal getTotalSYSPRplus1orMore() {
        return totalSYSPRplus1orMore;
    }

    /**
     * Sets the totalSYSPRplus1orMore attribute value.
     * @param totalSYSPRplus1orMore The totalSYSPRplus1orMore to set.
     */
    public void setTotalSYSPRplus1orMore(KualiDecimal totalSYSPRplus1orMore) {
        this.totalSYSPRplus1orMore = totalSYSPRplus1orMore;
    }


}

