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
package org.kuali.kfs.module.tem.document;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

public abstract class CardApplicationDocumentBase extends FinancialSystemTransactionalDocumentBase implements CardApplicationDocument {
    protected static Logger LOG = Logger.getLogger(CardApplicationDocumentBase.class);
    protected TemProfile temProfile;
    protected Integer temProfileId;
    protected boolean userAgreement;
    private String dummyAppDocStatus;

    @Override
    public TemProfile getTemProfile() {
        return temProfile;
    }
    @Override
    public void setTemProfile(TemProfile temProfile) {
        this.temProfile = temProfile;
    }
    @Override
    public Integer getTemProfileId() {
        return temProfileId;
    }
    @Override
    public void setTemProfileId(Integer temProfileId) {
        this.temProfileId = temProfileId;
    }
    @Override
    public boolean isUserAgreement() {
        return userAgreement;
    }
    @Override
    public void setUserAgreement(boolean userAgreement) {
        this.userAgreement = userAgreement;
    }

    @Override
    public String getUserAgreementText() {
        return null;
    }

    /**
     * Gets the dummyAppDocStatus attribute.
     *
     * @return Returns the dummyAppDocStatus
     */

    public String getDummyAppDocStatus() {
        return dummyAppDocStatus;
    }
    /**
     * Sets the dummyAppDocStatus attribute.
     *
     * @param dummyAppDocStatus The dummyAppDocStatus to set.
     */
    public void setDummyAppDocStatus(String dummyAppDocStatus) {
        this.dummyAppDocStatus = dummyAppDocStatus;
    }

    protected String zeroBuffer(Long number) {
        String numberStr = number.toString();
        while (numberStr.length() < 16){
            numberStr = "0" + numberStr;
        }
        return numberStr;
    }

    protected SequenceAccessorService getSequenceAccessorService() {
        return SpringContext.getBean(SequenceAccessorService.class);
    }

    protected WorkflowDocumentService getWorkflowDocumentService() {
        return SpringContext.getBean(WorkflowDocumentService.class);
    }

    @Override
    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    @Override
    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    protected ConfigurationService getConfigurationService() {
        return SpringContext.getBean(ConfigurationService.class);
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    public DocumentDao getDocumentDao() {
        return SpringContext.getBean(DocumentDao.class);
    }

    @Override
    public String getApplicationDocumentStatus() {
        String status = getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus();
        return StringUtils.defaultIfEmpty(status, TemWorkflowConstants.RouteNodeNames.APPLICATION);
    }

    @Override
    public void sendAcknowledgement() {
        getTravelDocumentService().addAdHocRecipient(this, getTemProfile().getPrincipalId(), KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
        try {
            SpringContext.getBean(WorkflowDocumentService.class).acknowledge(this.getDocumentHeader().getWorkflowDocument(), null, new ArrayList<AdHocRouteRecipient>(getAdHocRoutePersons()));
        }
        catch (WorkflowException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

    }

    /**
     * Default: do nothing
     * @see org.kuali.kfs.module.tem.document.CardApplicationDocument#approvedByBank()
     */
    @Override
    public void approvedByBank() {}

    @Override
    public boolean saveAppDocStatus() {
        boolean saved = false;
        try {
            getWorkflowDocumentService().save(getDocumentHeader().getWorkflowDocument(), null);
            saved = true;
        }
        catch (WorkflowException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return saved;
    }
}
