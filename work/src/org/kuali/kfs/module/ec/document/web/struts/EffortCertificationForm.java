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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.inquiry.Inquirable;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.inquiry.EffortLedgerBalanceInquirableImpl;
import org.kuali.module.labor.web.inquirable.BaseFundsInquirableImpl;
import org.kuali.rice.KNSServiceLocator;

/**
 * Action form for Effort Certification Document.
 */
public class EffortCertificationForm extends KualiTransactionalDocumentFormBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationForm.class);

    private EffortCertificationDocument effortCertificationDocument;
    private EffortCertificationDetail newDetailLine;

    /**
     * Constructs a EffortCertificationForm.java.
     */
    public EffortCertificationForm() {
        super();

        this.setEffortCertificationDocument(new EffortCertificationDocument());
        this.setDocument(this.getEffortCertificationDocument());

        newDetailLine = new EffortCertificationDetail();
    }

    /**
     * @return new detail line
     */
    public EffortCertificationDetail getNewDetailLine() {
        return newDetailLine;
    }

    /**
     * Sets the new detail line
     * 
     * @param newDetailLine
     */
    public void setNewDetailLine(EffortCertificationDetail newDetailLine) {
        this.newDetailLine = newDetailLine;
    }

    /**
     * Gets the effortCertificationDocument attribute.
     * 
     * @return Returns the effortCertificationDocument.
     */
    public EffortCertificationDocument getEffortCertificationDocument() {
        return effortCertificationDocument;
    }

    /**
     * Sets the effortCertificationDocument attribute value.
     * 
     * @param effortCertificationDocument The effortCertificationDocument to set.
     */
    public void setEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument) {
        this.effortCertificationDocument = effortCertificationDocument;
    }

    /**
     * Gets the detailLines attribute.
     * 
     * @return Returns the detailLines.
     */
    public List<EffortCertificationDetail> getDetailLines() {
        return ((EffortCertificationDocument) this.getDocument()).getEffortCertificationDetailLines();
    }
    
    /**
     * Gets the inquiryUrl attribute.
     * 
     * @return Returns the inquiryUrl for the detail lines in the document.
     */
    public List<Map<String, String>> getDetailLineFieldInquiryUrl() {
        Inquirable inquirable = this.getInquirable();
        List<Map<String, String>> inquiryURL = new ArrayList<Map<String, String>>();

        for (EffortCertificationDetail detailLine : this.getDetailLines()) {
            detailLine.refreshNonUpdateableReferences();
            Map<String, String> inquiryURLForAttribute = new HashMap<String, String>();

            for (String attributeName : this.getInquirableFieldNames()) {
                String url = KFSConstants.EMPTY_STRING;

                if (this.getCustomizedInquirableFieldNames().contains(attributeName)) {
                    url = this.getCustomizedInquiryUrl(detailLine, attributeName);
                }
                else {
                    url = inquirable.getInquiryUrl(detailLine, attributeName, false);
                }

                inquiryURLForAttribute.put(attributeName, url);
            }

            inquiryURL.add(inquiryURLForAttribute);
        }

        return inquiryURL;
    }
    
    /**
     * get the inquiry URL for the specified attribute
     * 
     * @param detailLine the detail line containing the given attribute
     * @param attributeName the specified attribute name
     * @return the inquiry URL for the specified attribute
     */
    protected String getCustomizedInquiryUrl(EffortCertificationDetail detailLine, String attributeName) {
        String baseURL = getInquirable().getInquiryUrl(detailLine, attributeName, false);       
        String inquiryURL = this.getCompleteURL(baseURL);
                
        return inquiryURL;
    }

    /**
     * Gets the inquirableFieldNames attribute.
     * 
     * @return Returns the inquirableFieldNames.
     */
    public List<String> getInquirableFieldNames() {
        List<String> inquirableFieldNames = new ArrayList<String>();
        inquirableFieldNames.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        inquirableFieldNames.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        inquirableFieldNames.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        inquirableFieldNames.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        inquirableFieldNames.add(KFSPropertyConstants.POSITION_NUMBER);

        inquirableFieldNames.add(EffortPropertyConstants.SOURCE_CHART_OF_ACCOUNTS_CODE);
        inquirableFieldNames.add(EffortPropertyConstants.SOURCE_ACCOUNT_NUMBER);
        inquirableFieldNames.add(EffortPropertyConstants.COST_SHARE_SOURCE_SUB_ACCOUNT_NUMBER);

        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_ORIGINAL_PAYROLL_AMOUNT);
        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_CALCULATED_OVERALL_PERCENT);
        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_PAYROLL_AMOUNT);
        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_UPDATED_OVERALL_PERCENT);

        return inquirableFieldNames;
    }

    /**
     * get the inquirable field names that need to be handled specially 
     * 
     * @return the inquirable field names that need to be handled specially 
     */
    public List<String> getCustomizedInquirableFieldNames() {
        List<String> inquirableFieldNames = new ArrayList<String>();

        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_ORIGINAL_PAYROLL_AMOUNT);
        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_CALCULATED_OVERALL_PERCENT);
        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_PAYROLL_AMOUNT);
        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_UPDATED_OVERALL_PERCENT);

        return inquirableFieldNames;
    }
    
    /**
     * append the extract query string into the given base URL
     * @param baseURL the given base URL. If the parameter is blank, the base URL won't be changed
     * @return the complete URL built from the given base URL and extra query strings 
     */
    protected String getCompleteURL(String baseURL) {
        if (StringUtils.isBlank(baseURL)) {
            return baseURL;
        }

        String completeURL = baseURL;
        EffortCertificationDocument document = (EffortCertificationDocument) this.getDocument();
        Properties properties = new Properties();

        properties.put(KFSPropertyConstants.EMPLID, document.getEmplid());
        properties.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, document.getEffortCertificationReportNumber());
        properties.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, document.getUniversityFiscalYear());

        StringBuilder queryString = new StringBuilder();
        for (Object key : properties.keySet()) {
            queryString.append("&").append(key).append("=").append(properties.get(key));
        }

        return completeURL.concat(queryString.toString());
    }
    
    /**
     * get the inquirable implmentation
     * @return the inquirable implmentation
     */
    private Inquirable getInquirable() {
        return new EffortLedgerBalanceInquirableImpl();
    }
}
