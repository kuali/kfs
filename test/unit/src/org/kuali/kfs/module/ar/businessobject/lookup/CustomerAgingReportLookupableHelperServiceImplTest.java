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

import org.kuali.kfs.module.ar.batch.CustomerInvoiceDocumentBatchStep;
import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
import org.kuali.kfs.module.ar.web.struts.CustomerAgingReportForm;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import org.kuali.rice.kns.service.BusinessObjectService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class tests the CustomerAgingReport lookup and totals calculations
 */
@ConfigureContext(session = khuntley)
public class CustomerAgingReportLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerAgingReportLookupableHelperServiceImplTest.class);

    private CustomerInvoiceDocumentBatchStep customerInvoiceDocumentBatchStep;
    private CustomerAgingReportLookupableHelperServiceImpl customerAgingReportLookupableHelperServiceImpl;
    private CustomerAgingReportForm customerAgingReportForm;
    private Map fieldValues;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        customerInvoiceDocumentBatchStep = SpringContext.getBean(CustomerInvoiceDocumentBatchStep.class);
        // customerAgingReportLookupableHelperServiceImpl =
        // SpringContext.getBean(CustomerAgingReportLookupableHelperServiceImpl.class);
        customerAgingReportLookupableHelperServiceImpl = new CustomerAgingReportLookupableHelperServiceImpl();
        customerAgingReportLookupableHelperServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        fieldValues = new LinkedHashMap();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//        fieldValues.put("reportOption", "PROCESSING ORGANIZATION");
//        fieldValues.put("chartOfAccountsCode", "UA");
//        fieldValues.put("organizationCode", "VPIT");
        fieldValues.put("backLocation", null);
        fieldValues.put("accountNumber", "1111111");
        fieldValues.put("reportRunDate", dateFormat.format(new Date()));
        fieldValues.put("reportOption", "Account");
        fieldValues.put("chartOfAccountsCode", "");
        fieldValues.put("organizationCode", "");
        fieldValues.put("docFormKey", null);


    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.CustomerAgingReportLookupableHelperServiceImpl#getSearchResults(java.util.Map)}
     * .
     */
    public void testGetSearchResultsMap() {
        Collection<?> displayList;
        // create set of customer invoices
//        customerInvoiceDocumentBatchStep.createCustomerInvoiceDocumentForFunctionalTesting("EAT17609", new Date(), 1, new KualiDecimal(4), new BigDecimal(50.00), "1031400", "BL");
//        customerInvoiceDocumentBatchStep.createCustomerInvoiceDocumentForFunctionalTesting("EAT17609", new Date(), 1, new KualiDecimal(1), new BigDecimal(25.00), "1031400", "BL");
//        customerInvoiceDocumentBatchStep.createCustomerInvoiceDocumentForFunctionalTesting("HIL22195", new Date(), 2, new KualiDecimal(5), new BigDecimal(1), "2224601", "BA");  // $10 entries
//        customerInvoiceDocumentBatchStep.createCustomerInvoiceDocumentForFunctionalTesting("IBM2655", new Date(), 2, new KualiDecimal(5), new BigDecimal(2), "2224601", "BA");  // $20 entries
//        customerInvoiceDocumentBatchStep.createCustomerInvoiceDocumentForFunctionalTesting("JAS19572", new Date(), 2, new KualiDecimal(5), new BigDecimal(3), "2224601", "BA");  // $30 entries

        // run search
        assertNotNull("search results not null", displayList = customerAgingReportLookupableHelperServiceImpl.getSearchResults(fieldValues));
        LOG.info("\n\n\n\n\n\n\t\t\t\t displaylist---size--: " + displayList.size());
        LOG.info("\n\n\n\n\n\n\t\t\t\t displaylist---array--: " + displayList.toArray().toString());

        for (Object object : displayList) {
            LOG.info("\n\n\t\t\t\t --- " + object.toString());
        }

        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            CustomerAgingReportDetail detail = (CustomerAgingReportDetail) iter.next();

            LOG.info("\n\n\t\t\t\t 0to30etcetc: " + detail.getUnpaidBalance0to30().toString() + detail.getUnpaidBalance31to60().toString() + detail.getUnpaidBalance61to90().toString() + detail.getUnpaidBalance91toSYSPR().toString() + detail.getUnpaidBalanceSYSPRplus1orMore() + detail.getAccountNumber() + detail.getChartOfAccountsCode() + detail.getCustomerName() + detail.getCustomerNumber() + detail.getReportOption() + detail.getOrganizationCode());

            //  List<Column> columns = customerAgingReportLookupableHelperServiceImpl.getColumns();
//            for (Iterator iterator = ((List) detail).iterator(); iterator.hasNext();) {
//
//                LOG.info("\n\n\t\t\t\t interator.next"+ iterator.next().toString());
//
////                Object prop = ObjectUtils.getPropertyValue(detail, col.getPropertyName());
////                LOG.info("\n\n\t\t\t\t testGetSearchResultsMap: " + col.getPropertyName() +": "+prop.toString());
//            }
        }
        for (Object displaylistentry : displayList) {
            //          LOG.info("\n\n\t\t\t\t 0to30 OBJECT UTILS: " + ObjectUtils.getPropertyValue(displaylistentry, "getUnpaidBalance0to30"));
        }


    }

    /**
     * Test method for
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.CustomerAgingReportLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)}
     * .
     */
    public void testPerformLookupLookupFormCollectionBoolean() {

        customerAgingReportForm = new CustomerAgingReportForm();
        customerAgingReportForm.setFieldsForLookup(fieldValues);

        Collection resultTable = new ArrayList<String>();
        assertNotNull("lookup list not null", customerAgingReportLookupableHelperServiceImpl.performLookup(customerAgingReportForm, resultTable, true));
    }

}