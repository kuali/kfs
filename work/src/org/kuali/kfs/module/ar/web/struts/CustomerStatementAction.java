/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.web.struts.action.KualiAction;

/**
 * This class handles Actions for lookup flow
 */

public class CustomerStatementAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerStatementAction.class);

   // private static final String TOTALS_TABLE_KEY = "totalsTable";


    public CustomerStatementAction() {
        super();
    }



    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerStatementForm csForm = (CustomerStatementForm)form;
        csForm.clear();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
   public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
       return mapping.findForward(KFSConstants.MAPPING_BASIC);
   }

   public ActionForward selectOperation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
      CustomerStatementForm csForm = (CustomerStatementForm)form;
      String chartCode = csForm.getChartCode();
      String orgCode = csForm.getOrgCode();
      String customerNumber = csForm.getCustomerNumber();
      String accountNumber = csForm.getAccountNumber();
      AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
      if ( chartCode != null && orgCode != null) {
         reportService.generateStatementByBillingOrg(chartCode, orgCode); 
      } else if (customerNumber != null) {
          reportService.generateStatementByCustomer(customerNumber);
      } else if (accountNumber != null) {
          reportService.generateStatementByAccount(accountNumber);
      }
      System.out.println("statementAction");
       return mapping.findForward(KFSConstants.MAPPING_BASIC);
   }
}
