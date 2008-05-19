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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.BusinessObjectRelationship;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.inquiry.Inquirable;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationDetailLineOverride;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.inquiry.EffortLedgerBalanceInquirableImpl;
import org.kuali.module.effort.util.PayrollAmountHolder;
import org.kuali.module.integration.service.ContractsAndGrantsModuleService;

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
        this.setNewDetailLine(this.createNewDetailLine());
    }

    /**
     * initialize a new detail line
     * 
     * @return the initialized detail line
     */
    public EffortCertificationDetail createNewDetailLine() {
        EffortCertificationDetail detailLine = new EffortCertificationDetail();
        detailLine.setEffortCertificationUpdatedOverallPercent(null);
        detailLine.setEffortCertificationPayrollAmount(null);
        detailLine.setSubAccountNumber(null);

        return detailLine;
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
     * @see org.kuali.core.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        for (EffortCertificationDetail detailLine : this.getDetailLines()) {
            EffortCertificationDetailLineOverride.populateFromInput(detailLine);
        }
    }

    /**
     * Gets the detailLines attribute.
     * 
     * @return Returns the detailLines.
     */
    public List<EffortCertificationDetail> getDetailLines() {
        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) this.getDocument();
        return effortCertificationDocument.getEffortCertificationDetailLines();
    }

    /**
     * get the relationship metadata for the detail line fields
     * 
     * @return the relationship metadata for the detail line fields
     */
    public Map<String, BusinessObjectRelationship> getRelationshipMetadata() {
        LOG.info("getRelationshipMetadata() start");

        PersistenceStructureService persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);

        Map<String, BusinessObjectRelationship> relationshipMetadata = new HashMap<String, BusinessObjectRelationship>();
        for (String attributeName : this.getInquirableFieldNames()) {
            Map<String, Class<? extends BusinessObject>> primitiveReference = LookupUtils.getPrimitiveReference(newDetailLine, attributeName);

            if (primitiveReference != null && !primitiveReference.isEmpty()) {
                BusinessObjectRelationship primitiveRelationship = this.getPrimitiveBusinessObjectRelationship(persistenceStructureService.getRelationshipMetadata(newDetailLine.getClass(), attributeName));
                relationshipMetadata.put(attributeName, primitiveRelationship);
            }
        }

        return relationshipMetadata;
    }

    /**
     * Gets the inquiryUrl attribute.
     * 
     * @return Returns the inquiryUrl for the detail lines in the document.
     */
    public List<Map<String, String>> getDetailLineFieldInquiryUrl() {
        LOG.info("getDetailLineFieldInquiryUrl() start");

        return this.getDetailLineFieldInquiryUrl(this.getDetailLines());
    }

    /**
     * Gets the fieldInfo attribute.
     * 
     * @return Returns the fieldInfo.
     */
    public List<Map<String, String>> getFieldInfo() {
        LOG.info("getFieldInfo() start");

        return this.getFieldInfo(this.getDetailLines());
    }

    /**
     * pick up the primitive relationship for an attribute from a set of relationships. Generally, the primitive relationship is
     * that has the minimum number of primary keys.
     * 
     * @param relationshipMetadata the relationship metadata that contains the primitive relationship
     * @return the primitive relationship for an attribute from a set of relationships.
     */
    private BusinessObjectRelationship getPrimitiveBusinessObjectRelationship(Map<String, BusinessObjectRelationship> relationshipMetadata) {
        int minCountOfKeys = Integer.MAX_VALUE;
        BusinessObjectRelationship primitiveRelationship = null;

        for (String attribute : relationshipMetadata.keySet()) {
            BusinessObjectRelationship currentRelationship = relationshipMetadata.get(attribute);

            Map<String, String> parentToChildReferences = currentRelationship.getParentToChildReferences();
            if (parentToChildReferences.size() < minCountOfKeys) {
                minCountOfKeys = parentToChildReferences.size();
                primitiveRelationship = currentRelationship;
            }
        }
        return primitiveRelationship;
    }

    /**
     * Gets the inquiryUrl attribute.
     * 
     * @return Returns the inquiryUrl for the detail lines in the document.
     */
    protected List<Map<String, String>> getDetailLineFieldInquiryUrl(List<EffortCertificationDetail> detailLines) {
        LOG.info("getDetailLineFieldInquiryUrl(List<EffortCertificationDetail>) start");

        Inquirable inquirable = this.getInquirable();
        List<Map<String, String>> inquiryURL = new ArrayList<Map<String, String>>();

        for (EffortCertificationDetail detailLine : detailLines) {
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
     * 
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
     * Gets the fieldInfo attribute.
     * 
     * @return Returns the fieldInfo.
     */
    protected List<Map<String, String>> getFieldInfo(List<EffortCertificationDetail> detailLines) {
        LOG.info("getFieldInfo(List<EffortCertificationDetail>) start");

        List<Map<String, String>> fieldInfo = new ArrayList<Map<String, String>>();
        EffortCertificationDocument document = (EffortCertificationDocument) this.getDocument();
        KualiDecimal totalOriginalPayrollAmount = document.getTotalOriginalPayrollAmount();

        for (EffortCertificationDetail detailLine : detailLines) {
            detailLine.refreshNonUpdateableReferences();

            Map<String, String> fieldInfoForAttribute = new HashMap<String, String>();

            fieldInfoForAttribute.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, detailLine.getChartOfAccounts().getFinChartOfAccountDescription());

            String accountInfo = buildAccountInfo(detailLine.getAccount());
            fieldInfoForAttribute.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountInfo);

            SubAccount subAccount = detailLine.getSubAccount();
            if (subAccount != null) {
                fieldInfoForAttribute.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccount.getSubAccountName());
            }

            ObjectCode objectCode = detailLine.getFinancialObject();
            if (objectCode != null) {
                fieldInfoForAttribute.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCodeName());
            }

            Account sourceAccount = detailLine.getSourceAccount();
            if (sourceAccount != null) {
                fieldInfoForAttribute.put(EffortPropertyConstants.SOURCE_ACCOUNT_NUMBER, sourceAccount.getAccountName());
            }

            Chart sourceChart = detailLine.getSourceChartOfAccounts();
            if (sourceChart != null) {
                fieldInfoForAttribute.put(EffortPropertyConstants.SOURCE_CHART_OF_ACCOUNTS_CODE, sourceChart.getFinChartOfAccountDescription());
            }

            KualiDecimal originalPayrollAmount = detailLine.getEffortCertificationOriginalPayrollAmount();

            LOG.info("============> " + totalOriginalPayrollAmount + " : " + originalPayrollAmount);
            String actualOriginalPercent = PayrollAmountHolder.recalculateEffortPercentAsString(totalOriginalPayrollAmount, originalPayrollAmount);
            fieldInfoForAttribute.put(EffortPropertyConstants.EFFORT_CERTIFICATION_CALCULATED_OVERALL_PERCENT, actualOriginalPercent);

            fieldInfo.add(fieldInfoForAttribute);
        }

        return fieldInfo;
    }

    /**
     * get the inquirable implmentation
     * 
     * @return the inquirable implmentation
     */
    private Inquirable getInquirable() {
        return new EffortLedgerBalanceInquirableImpl();
    }

    /**
     * build the descriptive information of the given account. The information includes account name and project director's name if
     * any
     * 
     * @param chartOfAccountsCode the given chart of accounts code
     * @param accountNumber the given account number
     * @return the descriptive information of the given account
     */
    public static String buildAccountInfo(Account account) {
        if (account == null) {
            return KFSConstants.EMPTY_STRING;
        }

        String projectDirectorName = KFSConstants.EMPTY_STRING;

        try {
            ContractsAndGrantsModuleService contractsAndGrantsModuleService = SpringContext.getBean(ContractsAndGrantsModuleService.class);
            UniversalUser projectDirector = contractsAndGrantsModuleService.getProjectDirectorForAccount(account);
            projectDirectorName = projectDirector != null ? MessageFormat.format("  ({0})", projectDirector.getPersonName()) : KFSConstants.EMPTY_STRING;
        }
        catch (Exception e) {
            LOG.error("Cannot find a project director for the account:" + account);
        }

        return MessageFormat.format("{0}{1}", account.getAccountName(), projectDirectorName);
    }

    /**
     * load the descriptive information of the given account. This method is used by DWR.
     * 
     * @param chartOfAccountsCode the given chart of accounts code
     * @param accountNumber the given account number
     * @return the descriptive information of the given account
     */
    public static String loadAccountInfo(String chartOfAccountsCode, String accountNumber) {
        Account account = SpringContext.getBean(AccountService.class).getByPrimaryIdWithCaching(chartOfAccountsCode, accountNumber);

        return buildAccountInfo(account);
    }
}
