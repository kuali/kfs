/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.effort.web.struts.form;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.effort.document.EffortCertificationDocument;

/**
 * Action form for Effort Certification Document.
 */
public class CertificationReportForm extends EffortCertificationForm {
    
    /**
     * Gets the report start date formatted for display
     * 
     * @return
     */
    public String getFormattedStartDate() {
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) getDocument();
        effortDocument.refreshReferenceObject("effortCertificationReportDefinition");
        AccountingPeriodService accountingPeriodService = (AccountingPeriodService)SpringContext.getBean(AccountingPeriodService.class);
        Date startDate = accountingPeriodService.getByPeriod( effortDocument.getEffortCertificationReportDefinition().getEffortCertificationReportBeginPeriodCode(), effortDocument.getEffortCertificationReportDefinition().getEffortCertificationReportBeginFiscalYear()).getUniversityFiscalPeriodEndDate();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        startDate.setDate(1);
        
        return formatter.format(startDate);
    }
    
    /**
     * Gets the report end date formatted for display
     * 
     * @return
     */
    public String getFormattedEndDate() {
        EffortCertificationDocument effortDocument = (EffortCertificationDocument) getDocument();
        effortDocument.refreshReferenceObject("effortCertificationReportDefinition");
        AccountingPeriodService accountingPeriodService = (AccountingPeriodService)SpringContext.getBean(AccountingPeriodService.class);
        Date endDate = accountingPeriodService.getByPeriod( effortDocument.getEffortCertificationReportDefinition().getEffortCertificationReportEndPeriodCode(), effortDocument.getEffortCertificationReportDefinition().getEffortCertificationReportEndFiscalYear()).getUniversityFiscalPeriodEndDate();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
               
        return formatter.format(endDate);
    }
    
}
