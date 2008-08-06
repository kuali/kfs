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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.coa.businessobject.defaultvalue.ValueFinderUtil;
//import org.kuali.kfs.gl.Constant;
//import org.kuali.kfs.gl.GeneralLedgerConstants;
//import org.kuali.kfs.gl.businessobject.AccountBalance;
//import org.kuali.kfs.gl.businessobject.TransientBalanceInquiryAttributes;
//import org.kuali.kfs.gl.businessobject.lookup.AccountBalanceByConsolidationLookupableHelperServiceImpl;
//import org.kuali.kfs.gl.businessobject.lookup.BusinessObjectFieldConverter;
//import org.kuali.kfs.gl.service.AccountBalanceService;
import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
import org.kuali.kfs.module.ar.businessobject.lookup.CustomerLookupableHelperServiceImpl;
import org.kuali.kfs.module.ar.util.ARUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;

public class CustomerAgingReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerAgingReportLookupableHelperServiceImpl.class);

    
    /**
     * Get the search results that meet the input search criteria.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     */
    @Override
    public List getSearchResults(Map fieldValues) {
        LOG.debug("\n\ngetSearchResults() started");
        LOG.info("Trying to log an INFO message with a some newlines \n\n\n");

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));
        
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
        
        // List results = accountBalanceService.findAccountBalanceByConsolidation(universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber, isCostShareExcluded, isConsolidated, pendingEntryCode);
        // List<CustomerAgingReportDetail> results = new ArrayList<CustomerAgingReportDetail>(3);
        List results = new ArrayList();
        results.add(matt);
        results.add(justin);
        results.add(patty);
            
     
        
        LOG.info("\t\t sending results back... \n\n\n");
        return new CollectionIncomplete(results, new Long(results.size()));
    }    

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    public List getReturnKeys() {
//        List returnKeys;
//        if (fieldConversions != null && !fieldConversions.isEmpty()) {
//            returnKeys = new ArrayList(fieldConversions.keySet());
//        }
//        else {
//            returnKeys = getPersistenceStructureService().listPrimaryKeyFieldNames(getBusinessObjectClass());
//        }
//
//        return returnKeys;
        return null;
    }
}
