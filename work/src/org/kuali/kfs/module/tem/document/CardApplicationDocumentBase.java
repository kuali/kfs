/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.document.TransactionalDocumentBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

public class CardApplicationDocumentBase extends TransactionalDocumentBase implements CardApplicationDocument {
    protected static Logger LOG = Logger.getLogger(CardApplicationDocumentBase.class);
    protected TEMProfile temProfile;
    protected Integer temProfileId;
    protected boolean userAgreement;
    private String dummyAppDocStatus;

    @Override
    public TEMProfile getTemProfile() {
        return temProfile;
    }
    @Override
    public void setTemProfile(TEMProfile temProfile) {
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

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

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

    public String getApplicationDocumentStatus() {
        String status = getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus();
        return StringUtils.defaultIfEmpty(status, TemConstants.TRAVEL_DOC_APP_DOC_STATUS_INIT);
    }

    @Override
    public void applyToBank() {
        // TODO Auto-generated method stub

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
    @Override
    public void approvedByBank() {
        // TODO Auto-generated method stub

    }

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
