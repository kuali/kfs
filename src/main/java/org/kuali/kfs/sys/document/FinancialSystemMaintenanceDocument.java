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
package org.kuali.kfs.sys.document;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentHeaderDao;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is used by the system to use financial specific objects and data for maintenance documents
 */
public class FinancialSystemMaintenanceDocument extends MaintenanceDocumentBase implements FinancialSystemDocument {
    private static final Logger LOG = Logger.getLogger(FinancialSystemMaintenanceDocument.class);

    private transient Map<String,Boolean> canEditCache;

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
     * @see org.kuali.rice.krad.document.DocumentBase#setDocumentHeader(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void setDocumentHeader(DocumentHeader documentHeader) {
        if ((documentHeader != null) && (!FinancialSystemDocumentHeader.class.isAssignableFrom(documentHeader.getClass()))) {
            throw new IllegalArgumentException("document header of class '" + documentHeader.getClass() + "' is not assignable from financial document header class '" + FinancialSystemDocumentHeader.class + "'");
        }
        this.documentHeader = documentHeader;
    }

    /**
     * This is the default implementation which ensures that document note attachment references are loaded.
     *
     * @see org.kuali.rice.krad.document.Document#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        // set correctedByDocumentId manually, since OJB doesn't maintain that relationship
        try {
            DocumentHeader correctingDocumentHeader = SpringContext.getBean(FinancialSystemDocumentHeaderDao.class).getCorrectingDocumentHeader(getDocumentHeader().getDocumentNumber());
            if (ObjectUtils.isNotNull(correctingDocumentHeader) && !correctingDocumentHeader.getWorkflowDocument().isCanceled() && !correctingDocumentHeader.getWorkflowDocument().isDisapproved()) {
                getFinancialSystemDocumentHeader().setCorrectedByDocumentId(correctingDocumentHeader.getDocumentNumber());
            }
        } catch (RuntimeException e) {
            LOG.error("Received WorkflowException trying to get route header id from workflow document", e);
            throw new WorkflowRuntimeException(e);
        }
        // set the ad hoc route recipients too, since OJB doesn't maintain that relationship
        // TODO - see KULNRVSYS-1054

        super.processAfterRetrieve();
    }

    /**
     * This is the default implementation which checks for a different workflow statuses, and updates the Kuali status accordingly.
     *
     * @see org.kuali.rice.krad.document.Document#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        getFinancialSystemDocumentHeader().setWorkflowDocumentStatusCode(statusChangeEvent.getNewRouteStatus());

        if (getDocumentHeader().getWorkflowDocument().isCanceled()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
        }
        else if (getDocumentHeader().getWorkflowDocument().isEnroute()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.ENROUTE);
        }
        if (getDocumentHeader().getWorkflowDocument().isDisapproved()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.DISAPPROVED);
        }
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Status is: " + getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
        }

        super.doRouteStatusChange(statusChangeEvent);
    }

    @Override
    public boolean answerSplitNodeQuestion(String nodeName) {
        if (getNewMaintainableObject() == null) {
            throw new UnsupportedOperationException("Cannot access Maintainable class to answer split node question");
        }
        if (getNewMaintainableObject() instanceof FinancialSystemMaintainable) {
            return ((FinancialSystemMaintainable)getNewMaintainableObject()).answerSplitNodeQuestion(nodeName);
        } else if (getNewMaintainableObject() instanceof FinancialSystemGlobalMaintainable) {
            return ((FinancialSystemGlobalMaintainable)getNewMaintainableObject()).answerSplitNodeQuestion(nodeName);
        } else {
            throw new UnsupportedOperationException("Maintainable for "+getNewMaintainableObject().getBoClass().getName()+" does not extend org.kuali.kfs.sys.document.FinancialSystemMaintainable nor org.kuali.kfs.sys.document.FinancialSystemGlobalMaintainable and therefore cannot answer split node question");
        }
    }

    /**
     * This method is used for routing and simply returns the initiator's Chart code.
     * @return The Chart code of the document initiator
     */
    public String getInitiatorChartOfAccountsCode() {
        String[] chartOrg = getInitiatorPrimaryDepartmentCode();
        return chartOrg[0];
    }

    /**
     * This method is used for routing and simply returns the initiator's Organization code.
     * @return The Organization code of the document initiator
     */
    public String getInitiatorOrganizationCode() {
        String[] chartOrg = getInitiatorPrimaryDepartmentCode();
        return chartOrg[1];
    }

    /**
     *
     * This method is a utility method that returns a String array containing the document initiator's
     * ChartCode in the first index and the OrganizationCode in the second.
     * @return a String array.
     */
    protected String[] getInitiatorPrimaryDepartmentCode() {

        String netID = documentHeader.getWorkflowDocument().getInitiatorPrincipalId();
        Person person =  KimApiServiceLocator.getPersonService().getPerson(netID);

        String deptCode = person.getPrimaryDepartmentCode();
        String[] chartOrg = deptCode.split("-");
        return chartOrg;

    }

    @Override
    public FinancialSystemDocumentHeader getFinancialSystemDocumentHeader() {
        return (FinancialSystemDocumentHeader)documentHeader;
    }

	@Override
    public void prepareForSave() {
        if (StringUtils.isBlank(getFinancialSystemDocumentHeader().getInitiatorPrincipalId())) {
            getFinancialSystemDocumentHeader().setInitiatorPrincipalId(getFinancialSystemDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        }
        if (StringUtils.isBlank(getFinancialSystemDocumentHeader().getWorkflowDocumentTypeName())) {
            getFinancialSystemDocumentHeader().setWorkflowDocumentTypeName(getFinancialSystemDocumentHeader().getWorkflowDocument().getDocumentTypeName());
        }
        if (ObjectUtils.isNull(getFinancialSystemDocumentHeader().getWorkflowCreateDate())) {
            getFinancialSystemDocumentHeader().setWorkflowCreateDate(new java.sql.Timestamp(getFinancialSystemDocumentHeader().getWorkflowDocument().getDateCreated().getMillis()));
        }
        // we're preparing to save here.  If the save fails, the transaction should roll back - so the fact that the doc header is in saved mode shouldn't
        // cause problems.  And since org.kuali.rice.krad.service.impl.PostProcessorServiceImpl#doRouteStatusChange will NOT save the document when the
        // DocStatus is saved, let's simply pre-anticipate that
        final String statusCode = getFinancialSystemDocumentHeader().getWorkflowDocument().getStatus().equals(DocumentStatus.INITIATED) ? DocumentStatus.SAVED.getCode() : getFinancialSystemDocumentHeader().getWorkflowDocument().getStatus().getCode();
        getFinancialSystemDocumentHeader().setWorkflowDocumentStatusCode(statusCode);
        super.prepareForSave();
    }
}

