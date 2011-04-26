/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.document.web.struts;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.sys.DynamicCollectionComparator.SortOrder;
import org.kuali.rice.kns.lookup.HtmlData;

/**
 * Action form for Effort Certification Document.
 */
public class CertificationReportForm extends EffortCertificationForm {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CertificationReportForm.class);

    protected String sortOrder = SortOrder.ASC.name();

    /**
     * Gets the sortOrder attribute.
     * 
     * @return Returns the sortOrder.
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the sortOrder attribute value.
     * 
     * @param sortOrder The sortOrder to set.
     */
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Gets the reportPeriodBeginDate attribute.
     * 
     * @return Returns the reportPeriodBeginDate.
     */
    public Date getReportPeriodBeginDate() {
        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) this.getDocument();
        effortCertificationDocument.refreshReferenceObject(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_DEFINITION);
        AccountingPeriod beginPeriod = effortCertificationDocument.getEffortCertificationReportDefinition().getReportBeginPeriod();

        return getUniversityFiscalPeriodBeginDate(beginPeriod);
    }

    /**
     * Gets the reportPeriodEndDate attribute.
     * 
     * @return Returns the reportPeriodEndDate.
     */
    public Date getReportPeriodEndDate() {
        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) this.getDocument();
        effortCertificationDocument.refreshReferenceObject(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_DEFINITION);
        AccountingPeriod endPeriod = effortCertificationDocument.getEffortCertificationReportDefinition().getReportEndPeriod();

        return endPeriod.getUniversityFiscalPeriodEndDate();
    }

    /**
     * Gets the universityFiscalPeriodBeginDate attribute. The begin date is the first date of the period month.
     * 
     * @return Returns the universityFiscalPeriodBeginDate.
     */
    protected Date getUniversityFiscalPeriodBeginDate(AccountingPeriod accountingPeriod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(accountingPeriod.getUniversityFiscalPeriodEndDate());
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return new Date(calendar.getTime().getTime());
    }

    /**
     * Gets the summarizedDetailLines attribute.
     * 
     * @return Returns the summarizedDetailLines.
     */
    public List<EffortCertificationDetail> getSummarizedDetailLines() {
        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) this.getDocument();
        return effortCertificationDocument.getSummarizedDetailLines();
    }
    
    /**
     * Gets the inquiryUrl attribute.
     * 
     * @return Returns the inquiryUrl for the detail lines in the document.
     */
    public List<Map<String, HtmlData>> getSummarizedDetailLineFieldInquiryUrl() {
        LOG.debug("getSummarizedDetailLineFieldInquiryUrl() start");

        return this.getDetailLineFieldInquiryUrl(this.getSummarizedDetailLines());
    }

    /**
     * Gets the fieldInfo attribute.
     * 
     * @return Returns the fieldInfo.
     */
    public List<Map<String, String>> getSummarizedDetailLineFieldInfo() {
        LOG.debug("getSummarizedDetailLineFieldInfo() start");

        return this.getFieldInfo(this.getSummarizedDetailLines());
    }
}
