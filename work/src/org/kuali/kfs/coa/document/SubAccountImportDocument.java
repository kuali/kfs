/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.coa.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.SubAccountImportDetail;
import org.kuali.kfs.coa.document.service.SubAccountImportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants.SubAccountImport;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.document.MassImportDocument;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

/**
 * This class is the global business object for Sub Account Import Global Maintenance Document
 */
public class SubAccountImportDocument extends FinancialSystemTransactionalDocumentBase implements MassImportDocument {
    private static final Logger Log = Logger.getLogger(SubAccountImportDocument.class);

    protected Integer nextItemLineNumber;

    // Mass upload sub-account
    private List<SubAccountImportDetail> subAccountImportDetails;

    public SubAccountImportDocument() {
        super();
        this.nextItemLineNumber = new Integer(1);
        setSubAccountImportDetails(new ArrayList<SubAccountImportDetail>());
    }


    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();

        // Workflow Status of PROCESSED --> Kuali Doc Status of Verified
        if (workflowDocument.isProcessed()) {
            SpringContext.getBean(SubAccountImportService.class).saveSubAccounts(subAccountImportDetails);
        }
    }


    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getSubAccountImportDetails());
        return managedLists;
    }

    /**
     * Gets the subAccountImportDetails attribute.
     *
     * @return Returns the subAccountImportDetails.
     */
    public List<SubAccountImportDetail> getSubAccountImportDetails() {
        return subAccountImportDetails;
    }


    /**
     * Sets the subAccountImportDetails attribute value.
     *
     * @param subAccountImportGlobalDetails The subAccountImportDetails to set.
     */
    public void setSubAccountImportDetails(List<SubAccountImportDetail> subAccountImportDetails) {
        this.subAccountImportDetails = subAccountImportDetails;
    }


    /**
     * Retrieve a particular line at a given index in the list of details.
     *
     * @param index
     * @return Check
     */
    public SubAccountImportDetail getSubAccountImportDetail(int index) {
        while (this.subAccountImportDetails.size() <= index) {
            subAccountImportDetails.add(new SubAccountImportDetail());
        }
        return subAccountImportDetails.get(index);
    }


    @Override
    public Class getImportLineClass() {
        return SubAccountImportDetail.class;
    }

    @Override
    public List getImportDetailCollection() {
        return subAccountImportDetails;
    }

    /**
     * @see org.kuali.kfs.coa.document.MassImportDocument#customizeImportedLines(java.util.List)
     */
    @Override
    public void customizeImportedLines(List<MassImportLineBase> importedLines) {
        for (Iterator iterator = importedLines.iterator(); iterator.hasNext();) {
            SubAccountImportDetail detailLine = (SubAccountImportDetail) iterator.next();
            detailLine.setSubAccountTypeCode(detailLine.getDefaultSubAccountTypeCode());
            detailLine.setActive(true);
        }
    }

    /**
     * @see org.kuali.kfs.coa.document.MassImportDocument#getErrorPathPrefix()
     */
    @Override
    public String getErrorPathPrefix() {
        return KFSConstants.SubAccountImportConstants.IMPORT_LINE_ERROR_PREFIX;
    }

    /**
     * @see org.kuali.kfs.coa.document.MassImportDocument#getErrorPathPrefix()
     */
    @Override
    public String getFullErrorPathPrefix() {
        return KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.SubAccountImport.SUB_ACCOUNT_IMPORT_DETAILS;
    }

    /**
     * Gets the nextItemLineNumber attribute.
     *
     * @return Returns the nextItemLineNumber.
     */
    @Override
    public Integer getNextItemLineNumber() {
        return nextItemLineNumber;
    }

    /**
     * Sets the nextItemLineNumber attribute value.
     *
     * @param nextItemLineNumber The nextItemLineNumber to set.
     */
    @Override
    public void setNextItemLineNumber(Integer nextItemLineNumber) {
        this.nextItemLineNumber = nextItemLineNumber;
    }


    @Override
    public String[] getOrderedFieldList() {
      return new String[] { SubAccountImport.CHART_OF_ACCOUNTS_CODE, SubAccountImport.ACCOUNT_NUMBER, SubAccountImport.SUB_ACCOUNT_NUMBER, SubAccountImport.SUB_ACCOUNT_NAME, SubAccountImport.FINANCIAL_REPORT_CHART_CODE, SubAccountImport.FIN_REPORT_ORGANIZATION_CODE, SubAccountImport.FINANCIAL_REPORTING_CODE };
    }


}
