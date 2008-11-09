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
package org.kuali.kfs.sys.document;

import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentHeaderDao;
import org.kuali.kfs.sys.document.workflow.FinancialSystemPropertySerializabilityEvaluator;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.datadictionary.WorkflowAttributes;
import org.kuali.rice.kns.datadictionary.WorkflowProperties;
import org.kuali.rice.kns.datadictionary.WorkflowProperty;
import org.kuali.rice.kns.datadictionary.WorkflowPropertyGroup;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.documentserializer.AlwaysFalsePropertySerializabilityEvaluator;
import org.kuali.rice.kns.util.documentserializer.AlwaysTruePropertySerializibilityEvaluator;
import org.kuali.rice.kns.util.documentserializer.PropertySerializabilityEvaluator;

/**
 * This class is used by the system to use financial specific objects and data for maintenance documents
 */
public class FinancialSystemMaintenanceDocument extends MaintenanceDocumentBase implements GenericRoutingInfo {
    private static final Logger LOG = Logger.getLogger(FinancialSystemMaintenanceDocument.class);

    protected FinancialSystemDocumentHeader documentHeader;
    protected Set<RoutingData> routingInfo;

    /**
     * Constructs a FinancialSystemMaintenanceDocument.java.
     */
    public FinancialSystemMaintenanceDocument() {
        super();
    }

    /**
     * Constructs a FinancialSystemMaintenanceDocument.java.
     * @param documentTypeName
     */
    public FinancialSystemMaintenanceDocument(String documentTypeName) {
        super(documentTypeName);
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#getDocumentHeader()
     */
    @Override
    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#setDocumentHeader(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void setDocumentHeader(DocumentHeader documentHeader) {
        if ((documentHeader != null) && (!FinancialSystemDocumentHeader.class.isAssignableFrom(documentHeader.getClass()))) {
            throw new IllegalArgumentException("document header of class '" + documentHeader.getClass() + "' is not assignable from financial document header class '" + FinancialSystemDocumentHeader.class + "'");
        }
        this.documentHeader = (FinancialSystemDocumentHeader) documentHeader;
    }

    /**
     * If the document has a total amount, call method on document to get the total and set in doc header.
     * 
     * @see org.kuali.rice.kns.document.Document#prepareForSave()
     */
//    @Override
//    public void prepareForSave() {
//        if (this instanceof AmountTotaling) {
//            getDocumentHeader().setFinancialDocumentTotalAmount(((AmountTotaling) this).getTotalDollarAmount());
//        }
//    }

    /**
     * This is the default implementation which ensures that document note attachment references are loaded.
     * 
     * @see org.kuali.rice.kns.document.Document#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        // set correctedByDocumentId manually, since OJB doesn't maintain that relationship
        try {
            DocumentHeader correctingDocumentHeader = SpringContext.getBean(FinancialSystemDocumentHeaderDao.class).getCorrectingDocumentHeader(getDocumentHeader().getWorkflowDocument().getRouteHeaderId().toString());
            if (correctingDocumentHeader != null) {
                getDocumentHeader().setCorrectedByDocumentId(correctingDocumentHeader.getDocumentNumber());
            }
        } catch (WorkflowException e) {
            LOG.error("Received WorkflowException trying to get route header id from workflow document");
            throw new WorkflowRuntimeException(e);
        }
        // set the ad hoc route recipients too, since OJB doesn't maintain that relationship
        // TODO - see KULNRVSYS-1054

        super.processAfterRetrieve();
    }

    /**
     * This is the default implementation which checks for a different workflow statuses, and updates the Kuali status accordingly.
     * 
     * @see org.kuali.rice.kns.document.Document#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
        }
        else if (getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.ENROUTE);
        }
        if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.DISAPPROVED);
        }
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }
        LOG.info("Status is: " + getDocumentHeader().getFinancialDocumentStatusCode());
        if (getDocumentHeader().getWorkflowDocument().stateIsCanceled() || getDocumentHeader().getWorkflowDocument().stateIsDisapproved() || getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
            getDocumentHeader().setDocumentFinalDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        }
        super.handleRouteStatusChange();
    }
    
    /**
     * An override of populateDocumentForRouting, which makes sure that processCustomPopulateDocumentForRouting
     * is called in a way that allows workflow properties to populate correctly.  It will also automatically
     * call populateRoutingInfo if the document implement GenericRoutingInfo.
     * @see org.kuali.rice.kns.document.DocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        if (this instanceof GenericRoutingInfo) {
            ((GenericRoutingInfo)this).populateRoutingInfo();
        }
        processCustomPopulateDocumentForRouting();
        super.populateDocumentForRouting();
    }

    /**
     * A hook to allow population of workflow properties for routing
     */
    protected void processCustomPopulateDocumentForRouting() {}

    
    public Set<RoutingData> getRoutingInfo() {
        return routingInfo;
    }

    public void populateRoutingInfo() {
        Maintainable maintainable = getNewMaintainableObject();
        if (maintainable instanceof GenericRoutingInfo) {
          ((GenericRoutingInfo)maintainable).populateRoutingInfo();
          Set<RoutingData> routingInfo =  ((GenericRoutingInfo)maintainable).getRoutingInfo();
          this.setRoutingInfo(routingInfo);
        }
    }

    public void setRoutingInfo(Set<RoutingData> routingInfo) {
        this.routingInfo = routingInfo;
    }
    
    @Override
    protected PropertySerializabilityEvaluator createPropertySerializabilityEvaluator(WorkflowProperties workflowProperties, WorkflowAttributes workflowAttributes) {
        if (workflowAttributes != null) {
            return new AlwaysFalsePropertySerializabilityEvaluator();
        }
        if (workflowProperties == null) {
            if (getNewMaintainableObject() instanceof GenericRoutingInfo) {
                FinancialSystemPropertySerializabilityEvaluator evaluator = new FinancialSystemPropertySerializabilityEvaluator();
                evaluator.addPropertyPath("routingInfo.routingTypes");
                evaluator.addPropertyPath("routingInfo.routingSet");
                evaluator.initializeEvaluator(this);
                return evaluator;
            }
            return new AlwaysTruePropertySerializibilityEvaluator();
        }
        else {
            
            FinancialSystemPropertySerializabilityEvaluator evaluator = new FinancialSystemPropertySerializabilityEvaluator();
            if (getNewMaintainableObject() instanceof GenericRoutingInfo) {
                evaluator.addPropertyPath("routingInfo.routingTypes");
                evaluator.addPropertyPath("routingInfo.routingSet");
            }
            evaluator.initializeEvaluator(this);
            return evaluator;
        } 
    }
}
