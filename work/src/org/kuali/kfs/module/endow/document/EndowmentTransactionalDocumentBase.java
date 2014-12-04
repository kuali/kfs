/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.document;

import java.sql.Date;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSourceType;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSubType;
import org.kuali.kfs.module.endow.businessobject.PendingTransactionDocumentEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;


public abstract class EndowmentTransactionalDocumentBase extends FinancialSystemTransactionalDocumentBase implements EndowmentTransactionalDocument {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EndowmentTransactionalDocumentBase.class);

    protected static final String CHECK_IF_ROUTE_SPLIT = "CheckIfNoRoute";
    protected String transactionSubTypeCode;
    protected String transactionSourceTypeCode;
    protected boolean transactionPosted;

    protected EndowmentTransactionSubType transactionSubType;
    protected EndowmentTransactionSourceType transactionSourceType;

    private static transient DateTimeService dateTimeService;

    protected boolean noRouteIndicator;

    /**
     * Constructs a EndowmentTransactionalDocumentBase.java.
     */
    public EndowmentTransactionalDocumentBase() {
        super();
        this.transactionPosted = false;

        //set noRouteIndicator = false by default to make sure when a user initiates
        //the eDoc manually through UI, it goes through the routing path defined in
        //the workflow xml file.
        this.noRouteIndicator= false;
        this.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.MANUAL);
        initializeSourceTypeObj();
    }

    /**
     * Constructs used by creating a recurring or automated eDoc
     */
    public EndowmentTransactionalDocumentBase(String transactionSourceTypeCode) {
        super();
        this.transactionPosted = false;
        this.setTransactionSourceTypeCode(transactionSourceTypeCode);
        initializeSourceTypeObj();
    }

    /**
     * This method fills source type code for UI on Initial request.
     */
    protected void initializeSourceTypeObj() {
        EndowmentTransactionSourceType endowmentTransactionSourceType = getBusinessObjectService().findBySinglePrimaryKey(EndowmentTransactionSourceType.class, this.getTransactionSourceTypeCode());
        this.setTransactionSourceType(endowmentTransactionSourceType);
    }

    /**
     * This method fills sub type code for UI on Initial request.
     */
    protected void initializeSubType() {
        EndowmentTransactionSubType endowmentTransactionSubType = getBusinessObjectService().findBySinglePrimaryKey(EndowmentTransactionSubType.class, this.getTransactionSubTypeCode());
        setTransactionSubType(endowmentTransactionSubType);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#getTransactionSubTypeCode()
     */
    @Override
    public String getTransactionSubTypeCode() {
        return transactionSubTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#setTransactionSubTypeCode(java.lang.String)
     */
    @Override
    public void setTransactionSubTypeCode(String transactionSubTypeCode) {
        this.transactionSubTypeCode = transactionSubTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#getTransactionSourceTypeCode()
     */
    @Override
    public String getTransactionSourceTypeCode() {
        return transactionSourceTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#setTransactionSourceTypeCode(java.lang.String)
     */
    @Override
    public void setTransactionSourceTypeCode(String transactionSourceTypeCode) {
        this.transactionSourceTypeCode = transactionSourceTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#isTransactionPosted()
     */
    @Override
    public boolean isTransactionPosted() {
        return transactionPosted;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#setTransactionPosted(boolean)
     */
    @Override
    public void setTransactionPosted(boolean transactionPosted) {
        this.transactionPosted = transactionPosted;
    }

    /**
     * Gets the transactionSubType.
     *
     * @return transactionSubType
     */
    public EndowmentTransactionSubType getTransactionSubType() {
        return transactionSubType;
    }

    /**
     * Sets the transactionSubType.
     *
     * @param transactionSubType
     */
    public void setTransactionSubType(EndowmentTransactionSubType transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    /**
     * Gets the transactionSourceType.
     *
     * @return transactionSourceType
     */
    public EndowmentTransactionSourceType getTransactionSourceType() {
        return transactionSourceType;
    }

    /**
     * Sets the transactionSourceType.
     *
     * @param transactionSourceType
     */
    public void setTransactionSourceType(EndowmentTransactionSourceType transactionSourceType) {
        this.transactionSourceType = transactionSourceType;
    }

    /**
     * Gets the noRouteIndicator.
     *
     * @return noRouteIndicator
     */
    public boolean getNoRouteIndicator(){
        return noRouteIndicator;
    }

    /**
     * Sets the noRouteIndicator.
     *
     * @param noRouteIndicator
     */
    @Override
    public void setNoRouteIndicator(boolean noRouteIndicator){
        this.noRouteIndicator = noRouteIndicator;
    }

    /**
     * @see org.kuali.kfs.sys.document.Correctable#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException, IllegalStateException {
        super.toErrorCorrection();

        // Reset All the Version numbers to 1
        try {
            ObjectUtils.setObjectPropertyDeep(this, KRADPropertyConstants.VERSION_NUMBER, versionNumber.getClass(), 0L);
        }
        catch (Exception e) {
            LOG.error("Unable to set version number property in copied document " + e.getMessage(), e);
            throw new RuntimeException("Unable to set version number property in copied document " + e.getMessage(), e);
        }
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#isErrorCorrectedDocument()
     */
    @Override
    public boolean isErrorCorrectedDocument() {
        if (StringUtils.isEmpty(getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber()))
            return false;
        else
            return true;
    }

    /**
     * When document is processed or in the final status, create an PendingTransactionDocumentEntry object
     * and persist documentId, documentType and the approved date to END_PENDING_TRAN_DOC_T.
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent){
        super.doRouteStatusChange(statusChangeEvent);

        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {

            String documentId = getDocumentHeader().getDocumentNumber();
            String documentType = getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
            Date approvedDate =  getDateTimeService().getCurrentSqlDate();

            //persist documentId, documentType and the approved date to END_PENDING_TRAN_DOC_T
            PendingTransactionDocumentEntry entry = new PendingTransactionDocumentEntry();
            entry.setDocumentNumber(documentId);
            entry.setDocumentType(documentType);
            entry.setApprovedDate(approvedDate);

            getBusinessObjectService().save(entry);
        }
    }

    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(CHECK_IF_ROUTE_SPLIT))
            return this.noRouteIndicator;
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \""+nodeName+"\"");
    }

    protected DateTimeService getDateTimeService() {
        if ( dateTimeService == null ) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

//    protected BusinessObjectService getBusinessObjectService() {
//        if ( businessObjectService == null ) {
//            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
//        }
//        return businessObjectService;
//    }
//
}
