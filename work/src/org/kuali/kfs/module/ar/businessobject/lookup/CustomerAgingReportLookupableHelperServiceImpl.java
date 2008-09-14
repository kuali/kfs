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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArConstants.CustomerAgingReportFields;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.mask.Mask;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
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

import sun.util.calendar.CalendarDate;



public class CustomerAgingReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerAgingReportLookupableHelperServiceImpl.class);
    private EncryptionService encryptionService;
    private DataDictionaryService dataDictionaryService;
    private Map fieldConversions;
    private CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
    private CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);   
    private BusinessObjectService businessObjectService;

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
        
        String reportOption = (String) fieldValues.get(ArConstants.CustomerAgingReportFields.REPORT_OPTION);
        String accountNumber = (String) fieldValues.get(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME);
        String chartCode = (String) fieldValues.get(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME);
        String orgCode = (String) fieldValues.get(KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME);
        Collection<CustomerInvoiceDetail> invoiceDetails = null;
        Collection<CustomerInvoiceDocument> invoices = null;

        if (reportOption.equalsIgnoreCase("PROCESSING ORGANIZATION") && chartCode.length()!=0 && orgCode.length()!=0) {
            invoices = customerInvoiceDocumentService.getCustomerInvoiceDocumentsByProcessingChartAndOrg(chartCode, orgCode);
            int invoisessize = invoices.size();
            for (CustomerInvoiceDocument ci : invoices) {
                invoiceDetails.addAll(customerInvoiceDocumentService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(ci));
                LOG.info("\t\t****** PROCESSING ORGANIZATION\t\t"+invoiceDetails.toString());
            }
        }
        if (reportOption.equalsIgnoreCase("BILLING ORGANIZATION") && chartCode.length()!=0 && orgCode.length()!=0) {
            invoices = customerInvoiceDocumentService.getCustomerInvoiceDocumentsByBillingChartAndOrg(chartCode, orgCode);
            for (CustomerInvoiceDocument ci : invoices) {
                invoiceDetails.addAll(customerInvoiceDocumentService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(ci));
            }   
        }
        if (reportOption.equalsIgnoreCase("ACCOUNT") && accountNumber.length()!=0) {
            invoiceDetails = getCustomerInvoiceDetailsByAccountNumber(accountNumber);
        }
        
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
      
        Date today = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        Date reportRunDate;
        try {
            reportRunDate = dateFormat.parse((String) fieldValues.get(ArConstants.CustomerAgingReportFields.REPORT_RUN_DATE));
        }
        catch (ParseException e) {
            reportRunDate=today;
            // MJM Auto-generated catch block
            e.printStackTrace();
        }
        Date cutoffdate30 = DateUtils.addDays(reportRunDate, -30);
        Date cutoffdate60 = DateUtils.addDays(reportRunDate, -60);
        Date cutoffdate90 = DateUtils.addDays(reportRunDate, -90);
        Date cutoffdate120 = DateUtils.addDays(reportRunDate, -120);
        Date cutoffdate365 = DateUtils.addDays(reportRunDate, -365);

        LOG.info("\t\t********** REPORT DATE\t\t"+reportRunDate.toString());
        LOG.info("\t\t***********************  cutoffdate 30:\t\t"+cutoffdate30.toString());
        LOG.info("\t\t***********************  cutoffdate 60:\t\t"+cutoffdate60.toString());
        LOG.info("\t\t***********************  cutoffdate 90:\t\t"+cutoffdate90.toString());
        LOG.info("\t\t***********************  cutoffdate 120:\t\t"+cutoffdate120.toString());
        LOG.info("\t\t***********************  cutoffdate 365:\t\t"+cutoffdate365.toString());

        // List invoices = (List) customerInvoiceDocumentService.getAllCustomerInvoiceDocuments();
        //JUSTIN SAYS NOT WORKING SO DON'T USE: Collection<CustomerInvoiceDocument> invoices = customerInvoiceDocumentService.getAllCustomerInvoiceDocuments();
        CustomerAgingReportDetail testcustomer1 = new CustomerAgingReportDetail();

        Map<String, Object> knownCustomers = new HashMap<String, Object>(invoiceDetails.size());
        // EXAMPLE: fieldNamesValuesForParameter.put("parameterNamespaceCode",CustomerInvoiceDocumentBatchStep.
        // RUN_INDICATOR_PARAMETER_NAMESPACE_CODE);

        KualiDecimal totalbalance = new KualiDecimal(0.00);
        CustomerAgingReportDetail custDetail = null;
        String previousCustomerNumber = "";

        // iterate over all invoices consolidating balances for each customer
        for (CustomerInvoiceDetail cid : invoiceDetails) {

            
          // THIS METHOD DOESN'T WORK
            //CustomerInvoiceDocument custdocobj = cid.getCustomerInvoiceDocument();
            String invoiceDocumentNumber = cid.getDocumentNumber();
            CustomerInvoiceDocument custInvoice = customerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
            Date approvalDate;
            if (custInvoice.getCustomerPurchaseOrderDate()!=null) {
                approvalDate=custInvoice.getCustomerPurchaseOrderDate();  // using customer purchase order date to test with for backdating
            }else {
                approvalDate=custInvoice.getBillingDate(); // use this if above isn't set since this is never null
                // I think should be using billingDate because use can't find "approved date" that vivek mentioned was in ar header
            }
            
         if(custInvoice!=null && customerInvoiceDetailService.getOpenAmount(cid).isNonZero()) {   
            
            Customer customerobj = custInvoice.getCustomer();                        
            String customerNumber = customerobj.getCustomerNumber();    // tested and works
            String customerName = customerobj.getCustomerName();  // tested and works
            
            
if (knownCustomers.containsKey(customerNumber)) { 
    custDetail = (CustomerAgingReportDetail) knownCustomers.get(customerNumber);
    LOG.info("\n\t\tcustomer:\t\t" + custDetail.getCustomerNumber() + "\tfound");
} else {
    custDetail = new CustomerAgingReportDetail();
    custDetail.setCustomerName(customerName);
    custDetail.setCustomerNumber(customerNumber);
    knownCustomers.put(customerNumber, custDetail);
    LOG.info("\n\t\tcustomer:\t\t" + custDetail.getCustomerNumber() + "\tADDED");
}
LOG.info("\t\t APPROVAL DATE: \t\t" + approvalDate.toString() + "\t");
LOG.info("\t\t REPORT DATE: \t\t" + reportRunDate.toString() + "\t");
            if (approvalDate.before(reportRunDate) && approvalDate.after(cutoffdate30)) {                                
                custDetail.setUnpaidBalance0to30(cid.getAmount().add(custDetail.getUnpaidBalance0to30())); 
                LOG.info("\t\t 0to30 =\t\t" + custDetail.getCustomerNumber() + "\t" + custDetail.getUnpaidBalance0to30());
            }
            if (approvalDate.before(cutoffdate30) && approvalDate.after(cutoffdate60)) {               
                custDetail.setUnpaidBalance31to60(cid.getAmount().add(custDetail.getUnpaidBalance31to60()));
                LOG.info("\t\t31to60 =\t\t" + custDetail.getCustomerNumber() + "\t" + custDetail.getUnpaidBalance31to60());
            }
            if (approvalDate.before(cutoffdate60) && approvalDate.after(cutoffdate90)) {
                custDetail.setUnpaidBalance61to90(cid.getAmount().add(custDetail.getUnpaidBalance61to90()));   
                LOG.info("\t\t61to90 =\t\t" + custDetail.getCustomerNumber() + "\t" + custDetail.getUnpaidBalance61to90());
            }
            if (approvalDate.before(cutoffdate90) && approvalDate.after(cutoffdate120)) {
                custDetail.setUnpaidBalance91toSYSPR(cid.getAmount().add(custDetail.getUnpaidBalance91toSYSPR()));   
                LOG.info("\t\t91to120 =\t\t" + custDetail.getCustomerNumber() + "\t" + custDetail.getUnpaidBalance91toSYSPR());
            }
//            if (approvalDate.before(cutoffdate120) && approvalDate.after(cutoffdate365)) {
//                custDetail = (CustomerAgingReportDetail) knownCustomers.get(customerNumber);                
//                custDetail.setUnpaidBalance0to30(cid.getAmount().add(custDetail.getUnpaidBalance0to30()));                
//            }
            if (approvalDate.before(cutoffdate120)) {
                custDetail.setUnpaidBalanceSYSPRplus1orMore(cid.getAmount().add(custDetail.getUnpaidBalanceSYSPRplus1orMore()));
                LOG.info("\t\t120+ =\t\t" + custDetail.getCustomerNumber() + "\t" + custDetail.getUnpaidBalanceSYSPRplus1orMore());
            }            
            
}        

        } // end for loop
       
     LOG.info("\n\n\n\n");   
     LOG.info("\t\tCustomer=\t\t0-30\t\t31-60\t\t61-90\t\t91-120\t\t120+\t");        
     for (Object obj : knownCustomers.values().toArray()) {
        CustomerAgingReportDetail cdetail = (CustomerAgingReportDetail)obj;        
        LOG.info("\t\t"+cdetail.getCustomerNumber()+"\t\t"+cdetail.getUnpaidBalance0to30()+"\t\t"+cdetail.getUnpaidBalance31to60()+"\t\t"+cdetail.getUnpaidBalance61to90()+"\t\t"+cdetail.getUnpaidBalance91toSYSPR()+"\t\t"+cdetail.getUnpaidBalanceSYSPRplus1orMore());       
     } 
        
        
        
        // create some fake entries to test with
        CustomerAgingReportDetail matt = new CustomerAgingReportDetail();
        matt.setCustomerName("Matt");
        matt.setCustomerNumber("DEV12345");
        matt.setUnpaidBalance0to30(KualiDecimal.ZERO);
        matt.setUnpaidBalance31to60(KualiDecimal.ZERO);
        matt.setUnpaidBalance61to90(KualiDecimal.ZERO);
        matt.setUnpaidBalance91toSYSPR(KualiDecimal.ZERO);
        matt.setUnpaidBalanceSYSPRplus1orMore(KualiDecimal.ZERO);

        CustomerAgingReportDetail justin = new CustomerAgingReportDetail();
        justin.setCustomerName("Justin");
        justin.setCustomerNumber("LED12346");
        justin.setUnpaidBalance0to30(KualiDecimal.ZERO);
        justin.setUnpaidBalance31to60(KualiDecimal.ZERO);
        justin.setUnpaidBalance61to90(KualiDecimal.ZERO);
        justin.setUnpaidBalance91toSYSPR(KualiDecimal.ZERO);
        justin.setUnpaidBalanceSYSPRplus1orMore(KualiDecimal.ZERO);

        CustomerAgingReportDetail patty = new CustomerAgingReportDetail();
        patty.setCustomerName("Patty");
        patty.setCustomerNumber("MGR12347");
        patty.setUnpaidBalance0to30(KualiDecimal.ZERO);
        patty.setUnpaidBalance31to60(KualiDecimal.ZERO);
        patty.setUnpaidBalance61to90(KualiDecimal.ZERO);
        patty.setUnpaidBalance91toSYSPR(KualiDecimal.ZERO);
        patty.setUnpaidBalanceSYSPRplus1orMore(KualiDecimal.ZERO);

        // List results = accountBalanceService.findAccountBalanceByConsolidation(universityFiscalYear, chartOfAccountsCode,
        // accountNumber, subAccountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode);
        // List<CustomerAgingReportDetail> results = new ArrayList<CustomerAgingReportDetail>(3);
        List results = new ArrayList();
        results.add(matt);
        results.add(justin);
        results.add(patty);
        for (Object detail : knownCustomers.values()) {
            results.add(detail);
        }


        LOG.info("\t\t sending results back... \n\n\n");
        return new CollectionIncomplete(results, new Long(results.size()));
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
 
//    /**
//     * @return a List of the CustomerInvoiceDetails associated with a given Processing Chart and Org
//     */
//    @SuppressWarnings("unchecked")
//    public Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentByProcessingChartOrg(String chartCode, String orgCode) {
//        // MJM NOT WORKING YET
//        Map args = new HashMap();
//        args.put("chartOfAccountsCode", chartCode);
//        return businessObjectService.findMatching(CustomerInvoiceDetail.class, args);
//    }      
//
//    /**
//     * @return a List of the CustomerInvoiceDetails associated with a given Billing Chart and Org
//     */
//    @SuppressWarnings("unchecked")
//    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsByBillingChartOrg(String chartCode, String orgCode) {
//        // MJM THIS NO WORKY YET
//        Map args = new HashMap();
//        args.put("chartOfAccountsCode", chartCode);
//        return businessObjectService.findMatching(CustomerInvoiceDetail.class, args);
//    }   
//    
    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    @Override
    public List getReturnKeys() {
        List returnKeys;
        // if (fieldConversions != null && !fieldConversions.isEmpty()) {
        returnKeys = new ArrayList(fieldConversions.keySet());
        LOG.info("\n\n\t\t THIS OVERRIDE IS WORKING (GETRETURNKEYS)... \n\n\n");
        // }
        // else {
        // returnKeys = getPersistenceStructureService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        // }
        //
        return returnKeys;

    }

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    @Override
    protected Properties getParameters(BusinessObject bo, Map fieldConversions, String lookupImpl, List pkNames) {
        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.RETURN_METHOD_TO_CALL);
        parameters.put(KNSConstants.DOC_FORM_KEY, getDocFormKey());
        parameters.put(KNSConstants.REFRESH_CALLER, lookupImpl);
        if (getReferencesToRefresh() != null) {
            parameters.put(KNSConstants.REFERENCES_TO_REFRESH, getReferencesToRefresh());
        }

        Iterator returnKeys = getReturnKeys().iterator();
        while (returnKeys.hasNext()) {
            String fieldNm = (String) returnKeys.next();

            Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
            if (fieldVal == null) {
                fieldVal = KNSConstants.EMPTY_STRING;
            }

            // Encrypt value if it is a secure field
            String displayWorkgroup = dataDictionaryService.getAttributeDisplayWorkgroup(bo.getClass(), fieldNm);

            if (fieldConversions.containsKey(fieldNm)) {
                fieldNm = (String) fieldConversions.get(fieldNm);
            }

            if (StringUtils.isNotBlank(displayWorkgroup) && !GlobalVariables.getUserSession().getUniversalUser().isMember(displayWorkgroup)) {
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
        LOG.info("\n\n\t\t THIS OVERRIDE IS WORKING (performLookup)... \n\n\n");
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

        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            // String returnUrl = getReturnUrl(element, lookupForm.getFieldConversions(),
            // lookupForm.getLookupableImplServiceName());
            // String actionUrls = getActionUrls(element);
            String returnUrl = "www.bigfrickenRETURNurl";
            String actionUrls = "www.someACTIONurl";

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {

                Column col = (Column) iterator.next();
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
                    }
                    else {
                        propValue = prop.toString();
                    }
                }

                // comparator
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                // check security on field and do masking if necessary
                boolean viewAuthorized = getAuthorizationService().isAuthorizedToViewAttribute(GlobalVariables.getUserSession().getUniversalUser(), element.getClass().getName(), col.getPropertyName());
                if (!viewAuthorized) {
                    Mask displayMask = getDataDictionaryService().getAttributeDisplayMask(element.getClass().getName(), col.getPropertyName());
                    propValue = displayMask.maskValue(propValue);
                }
                col.setPropertyValue(propValue);


                if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
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

        return displayList;
    }

    /**
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
