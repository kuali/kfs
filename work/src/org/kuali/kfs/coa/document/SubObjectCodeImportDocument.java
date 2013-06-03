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
import org.kuali.kfs.coa.businessobject.SubObjectCodeImportDetail;
import org.kuali.kfs.coa.document.service.SubObjectCodeImportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants.SubObjectCodeImport;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.document.MassImportDocument;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

/**
 * This class is the global business object for Sub Account Import Global Maintenance Document
 */
public class SubObjectCodeImportDocument extends FinancialSystemTransactionalDocumentBase implements MassImportDocument {
    private static final Logger Log = Logger.getLogger(SubObjectCodeImportDocument.class);

    protected Integer nextItemLineNumber;

    // Mass upload sub-Object Code
    private List<SubObjectCodeImportDetail> subObjectCodeImportDetails;

    public SubObjectCodeImportDocument() {
        super();
        this.nextItemLineNumber = new Integer(1);
        setSubObjectCodeImportDetails(new ArrayList<SubObjectCodeImportDetail>());
    }


    /**
     * Saves the imported details into database when document is processed successfully
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#doRouteStatusChange(org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {

        super.doRouteStatusChange(statusChangeEvent);

        WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isProcessed()) {
            SpringContext.getBean(SubObjectCodeImportService.class).saveSubObjectCodeDetails(subObjectCodeImportDetails);
        }

    }


    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getSubObjectCodeImportDetails());
        return managedLists;
    }

    /**
     * Gets the subObjectCodeImportDetails attribute.
     *
     * @return Returns the subObjectCodeImportDetails.
     */
    public List<SubObjectCodeImportDetail> getSubObjectCodeImportDetails() {
        return subObjectCodeImportDetails;
    }

    /**
     * Sets the subObjectCodeImportDetails attribute value.
     *
     * @param subObjectCodeImportDetails The subObjectCodeImportDetails to set.
     */
    public void setSubObjectCodeImportDetails(List<SubObjectCodeImportDetail> subObjectCodeImportDetails) {
        this.subObjectCodeImportDetails = subObjectCodeImportDetails;
    }

    /**
     * Retrieve a particular line at a given index in the list of details.
     *
     * @param index
     * @return Check
     */
    public SubObjectCodeImportDetail getSubObjectCodeImportDetail(int index) {
        while (this.subObjectCodeImportDetails.size() <= index) {
            subObjectCodeImportDetails.add(new SubObjectCodeImportDetail());
        }
        return subObjectCodeImportDetails.get(index);
    }


    @Override
    public Class getImportLineClass() {
        return SubObjectCodeImportDetail.class;
    }


    @Override
    public List getImportDetailCollection() {
        return subObjectCodeImportDetails;
    }

    /**
     * @see org.kuali.kfs.coa.document.MassImportDocument#customizeImportedLines(java.util.List)
     */
    @Override
    public void customizeImportedLines(List<MassImportLineBase> importedLines) {
        for (Iterator iterator = importedLines.iterator(); iterator.hasNext();) {
            SubObjectCodeImportDetail importedLine = (SubObjectCodeImportDetail) iterator.next();
            importedLine.setActive(true);
        }
    }

    /**
     * @see org.kuali.kfs.coa.document.MassImportDocument#getErrorPathPrefix()
     */
    @Override
    public String getErrorPathPrefix() {
        return KFSConstants.SubObjectCodeImportConstants.IMPORT_LINE_ERROR_PREFIX;
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
    public String getFullErrorPathPrefix() {
        return KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.SubObjectCodeImport.SUB_OBJECT_CODE_IMPORT_DETAILS;
    }


    @Override
    public String[] getOrderedFieldList() {
        return new String[] { SubObjectCodeImport.UNIVERSITY_FISCAL_YEAR, SubObjectCodeImport.CHART_OF_ACCOUNTS_CODE, SubObjectCodeImport.ACCOUNT_NUMBER, SubObjectCodeImport.FIN_OBJECT_CODE, SubObjectCodeImport.FIN_SUB_OBJECT_CODE, SubObjectCodeImport.FIN_SUB_OBJECT_CODE_NAME, SubObjectCodeImport.FIN_SUB_OBJECT_CODE_SHORT_NAME };
    }


}
