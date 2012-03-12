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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailLineOverride;
import org.kuali.kfs.module.ec.businessobject.inquiry.EffortLedgerBalanceInquirableImpl;
import org.kuali.kfs.module.ec.businessobject.inquiry.EffortPositionDataDetailsInquirableImpl;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.util.PayrollAmountHolder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action form for Effort Certification Document.
 */
public class EffortCertificationForm extends FinancialSystemTransactionalDocumentFormBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationForm.class);

    protected EffortCertificationDetail newDetailLine;

    /**
     * Constructs a EffortCertificationForm.java.
     */
    public EffortCertificationForm() {
        super();

        this.setNewDetailLine(this.createNewDetailLine());
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return EffortConstants.EffortDocumentTypes.EFFORT_CERTIFICATION_DOCUMENT;
    }
    
    /**
     * initialize a new detail line
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
        return (EffortCertificationDocument) this.getDocument();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
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
    public Map<String, DataObjectRelationship> getRelationshipMetadata() {
        LOG.debug("getRelationshipMetadata() start");

        PersistenceStructureService persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);

        Map<String, DataObjectRelationship> relationshipMetadata = new HashMap<String, DataObjectRelationship>();
        for (String attributeName : this.getInquirableFieldNames()) {
            Map<String, Class<? extends BusinessObject>> primitiveReference = LookupUtils.getPrimitiveReference(newDetailLine, attributeName);

            if (primitiveReference != null && !primitiveReference.isEmpty()) {
                DataObjectRelationship primitiveRelationship = this.getPrimitiveDataObjectRelationship(persistenceStructureService.getRelationshipMetadata(newDetailLine.getClass(), attributeName));
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
    public List<Map<String, HtmlData>> getDetailLineFieldInquiryUrl() {
        LOG.debug("getDetailLineFieldInquiryUrl() start");

        return this.getDetailLineFieldInquiryUrl(this.getDetailLines());
    }

    /**
     * Gets the fieldInfo attribute.
     * 
     * @return Returns the fieldInfo.
     */
    public List<Map<String, String>> getFieldInfo() {
        LOG.debug("getFieldInfo() start");

        return this.getFieldInfo(this.getDetailLines());
    }

    /**
     * pick up the primitive relationship for an attribute from a set of relationships. Generally, the primitive relationship is
     * that has the minimum number of primary keys.
     * 
     * @param relationshipMetadata the relationship metadata that contains the primitive relationship
     * @return the primitive relationship for an attribute from a set of relationships.
     */
    protected DataObjectRelationship getPrimitiveDataObjectRelationship(Map<String, DataObjectRelationship> relationshipMetadata) {
        int minCountOfKeys = Integer.MAX_VALUE;
        DataObjectRelationship primitiveRelationship = null;

        for (String attribute : relationshipMetadata.keySet()) {
            DataObjectRelationship currentRelationship = relationshipMetadata.get(attribute);

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
    protected List<Map<String, HtmlData>> getDetailLineFieldInquiryUrl(List<EffortCertificationDetail> detailLines) {
        LOG.debug("getDetailLineFieldInquiryUrl(List<EffortCertificationDetail>) start");

        Map<String, String> noninquirableFieldValues = this.getNoninquirableFieldValues();
        Inquirable inquirable = this.getInquirable();

        List<Map<String, HtmlData>> inquiryURL = new ArrayList<Map<String, HtmlData>>();
        for (EffortCertificationDetail detailLine : detailLines) {
            detailLine.refreshNonUpdateableReferences();
            
            Map<String, HtmlData> inquiryURLForAttribute = new HashMap<String, HtmlData>();            
            for (String attributeName : this.getInquirableFieldNames()) {                
                // exclude the non inquirable field values
                Object attributeValue = ObjectUtils.getPropertyValue(detailLine, attributeName);
                String noninquirableFieldValue = noninquirableFieldValues.get(attributeName);
                if(noninquirableFieldValue!=null && noninquirableFieldValue.equals(attributeValue)) {
                    continue;
                }

                HtmlData inquiryHref;
                if (this.getCustomizedInquirableFieldNames().contains(attributeName)) {
                    inquiryHref = this.getCustomizedInquiryUrl(detailLine, attributeName);
                }
                else {
                    inquiryHref = inquirable.getInquiryUrl(detailLine, attributeName, false);
                }

                inquiryURLForAttribute.put(attributeName, inquiryHref);
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
    protected HtmlData getCustomizedInquiryUrl(EffortCertificationDetail detailLine, String attributeName) {
        if (StringUtils.equals(attributeName, KFSPropertyConstants.POSITION_NUMBER)) {
            AnchorHtmlData inquiryHref = (AnchorHtmlData) getEffortPositionDataDetailsInquirableImpl().getInquiryUrl(detailLine, attributeName);
            inquiryHref.setHref(this.getCompleteURL(inquiryHref.getHref()));
            return inquiryHref;
        }
        
        AnchorHtmlData inquiryHref = (AnchorHtmlData) getInquirable().getInquiryUrl(detailLine, attributeName, false);
        inquiryHref.setHref(this.getCompleteURL(inquiryHref.getHref()));

        return inquiryHref;
    }
    
    /**
     * Gets the inquirableFieldNames attribute.
     * 
     * @return Returns the inquirableFieldNames.
     */
    public Map<String, String> getNoninquirableFieldValues() {
        Map<String, String> inquirableFieldNames = new HashMap<String, String>();
        inquirableFieldNames.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, KFSConstants.getDashSubAccountNumber());
        inquirableFieldNames.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSConstants.getDashFinancialObjectCode());
        inquirableFieldNames.put(KFSPropertyConstants.POSITION_NUMBER, EffortConstants.DASH_POSITION_NUMBER);

        inquirableFieldNames.put(EffortPropertyConstants.SOURCE_CHART_OF_ACCOUNTS_CODE, EffortConstants.DASH_CHART_OF_ACCOUNTS_CODE);
        inquirableFieldNames.put(EffortPropertyConstants.SOURCE_ACCOUNT_NUMBER, EffortConstants.DASH_ACCOUNT_NUMBER);
        inquirableFieldNames.put(EffortPropertyConstants.COST_SHARE_SOURCE_SUB_ACCOUNT_NUMBER, KFSConstants.getDashSubAccountNumber());

        return inquirableFieldNames;
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
        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_PAYROLL_AMOUNT);

        return inquirableFieldNames;
    }

    /**
     * get the inquirable field names that need to be handled specially
     * 
     * @return the inquirable field names that need to be handled specially
     */
    public List<String> getCustomizedInquirableFieldNames() {
        List<String> inquirableFieldNames = new ArrayList<String>();
        
        inquirableFieldNames.add(KFSPropertyConstants.POSITION_NUMBER);
        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_ORIGINAL_PAYROLL_AMOUNT);
        inquirableFieldNames.add(EffortPropertyConstants.EFFORT_CERTIFICATION_PAYROLL_AMOUNT);

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
    protected Map<String, String> getFieldInfo(EffortCertificationDetail detailLine) {
        LOG.info("getFieldInfo(List<EffortCertificationDetail>) start");

        //Map<String, String> fieldInfo = new HashMap<String, String>();
        EffortCertificationDocument document = (EffortCertificationDocument) this.getDocument();
        KualiDecimal totalOriginalPayrollAmount = document.getTotalOriginalPayrollAmount();

        
            detailLine.refreshNonUpdateableReferences();

            Map<String, String> fieldInfoForAttribute = new HashMap<String, String>();
            
            fieldInfoForAttribute.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, ObjectUtils.isNotNull(detailLine.getChartOfAccounts())? detailLine.getChartOfAccounts().getFinChartOfAccountDescription(): "");

            String accountInfo = buildAccountInfo(detailLine.getAccount());
            fieldInfoForAttribute.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountInfo);

            SubAccount subAccount = detailLine.getSubAccount();
            if (ObjectUtils.isNotNull(subAccount)) {
                fieldInfoForAttribute.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccount.getSubAccountName());
            }

            ObjectCode objectCode = detailLine.getFinancialObject();
            if (ObjectUtils.isNotNull(objectCode)) {
                fieldInfoForAttribute.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCodeName());
            }

            Account sourceAccount = detailLine.getSourceAccount();
            if (ObjectUtils.isNotNull(sourceAccount)) {
                fieldInfoForAttribute.put(EffortPropertyConstants.SOURCE_ACCOUNT_NUMBER, sourceAccount.getAccountName());
            }

            Chart sourceChart = detailLine.getSourceChartOfAccounts();
            if (ObjectUtils.isNotNull(sourceChart)) {
                fieldInfoForAttribute.put(EffortPropertyConstants.SOURCE_CHART_OF_ACCOUNTS_CODE, sourceChart.getFinChartOfAccountDescription());
            }

            KualiDecimal originalPayrollAmount = detailLine.getEffortCertificationOriginalPayrollAmount();
            String actualOriginalPercent = PayrollAmountHolder.recalculateEffortPercentAsString(totalOriginalPayrollAmount, originalPayrollAmount);
            fieldInfoForAttribute.put(EffortPropertyConstants.EFFORT_CERTIFICATION_CALCULATED_OVERALL_PERCENT, actualOriginalPercent);

            //fieldInfo.add(fieldInfoForAttribute);
        

        return fieldInfoForAttribute;
    }
    
    /**
     * Gets the fieldInfo attribute.
     * 
     * @return Returns the fieldInfo.
     */
    public Map<String, String> getDetailLineFieldInfo() {
        LOG.info("getSummarizedDetailLineFieldInfo() start");

        return this.getFieldInfo(this.getNewDetailLine());
    }
    
    /**
     * Gets the fieldInfo attribute.
     * 
     * @return Returns the fieldInfo.
     */
    protected List<Map<String, String>> getFieldInfo(List<EffortCertificationDetail> detailLines) {
        LOG.debug("getFieldInfo(List<EffortCertificationDetail>) start");

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
            if (ObjectUtils.isNotNull(subAccount)) {
                fieldInfoForAttribute.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccount.getSubAccountName());
            }

            ObjectCode objectCode = detailLine.getFinancialObject();
            if (ObjectUtils.isNotNull(objectCode)) {
                fieldInfoForAttribute.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCodeName());
            }

            Account sourceAccount = detailLine.getSourceAccount();
            if (ObjectUtils.isNotNull(sourceAccount)) {
                fieldInfoForAttribute.put(EffortPropertyConstants.SOURCE_ACCOUNT_NUMBER, sourceAccount.getAccountName());
            }

            Chart sourceChart = detailLine.getSourceChartOfAccounts();
            if (ObjectUtils.isNotNull(sourceChart)) {
                fieldInfoForAttribute.put(EffortPropertyConstants.SOURCE_CHART_OF_ACCOUNTS_CODE, sourceChart.getFinChartOfAccountDescription());
            }

            KualiDecimal originalPayrollAmount = detailLine.getEffortCertificationOriginalPayrollAmount();
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
    protected Inquirable getInquirable() {
        return new EffortLedgerBalanceInquirableImpl();
    }

    /**
     * get the EffortPositionDataDetailsInquirableImpl implmentation
     * 
     * @return the EffortPositionDataDetailsInquirableImpl implmentation
     */
    protected EffortPositionDataDetailsInquirableImpl getEffortPositionDataDetailsInquirableImpl() {
        return new EffortPositionDataDetailsInquirableImpl();
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
        if (ObjectUtils.isNull(account)) {
            return KFSConstants.EMPTY_STRING;
        }

        String projectDirectorName = KFSConstants.EMPTY_STRING;

        try {
            ContractsAndGrantsModuleService contractsAndGrantsModuleService = SpringContext.getBean(ContractsAndGrantsModuleService.class);
            Person projectDirector = contractsAndGrantsModuleService.getProjectDirectorForAccount(account);

            projectDirectorName = projectDirector != null ? MessageFormat.format("  ({0})", projectDirector.getName()) : KFSConstants.EMPTY_STRING;
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
