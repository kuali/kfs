/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.batch;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.CFDAClose;
import org.kuali.kfs.module.cg.service.CloseService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.MailService;

/**
 * @see CloseService#close()
 */
public class CloseBatchStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CloseBatchStep.class);

    private CloseService closeService;
  
    /**
     * See the class description.
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);

        String messageSubject = StringUtils.EMPTY;
        String messageBody = StringUtils.EMPTY;

        try {
            closeService.close();

            messageSubject = kualiConfigurationService.getPropertyString(CGKeyConstants.SUBJECT_CLOSE_JOB_SUCCEEDED);
            messageBody = kualiConfigurationService.getPropertyString(CGKeyConstants.MESSAGE_CLOSE_JOB_SUCCEEDED);
        }
        catch (Exception e) {
            messageSubject = kualiConfigurationService.getPropertyString(CGKeyConstants.SUBJECT_CLOSE_JOB_FAILED);

            String messageProperty = kualiConfigurationService.getPropertyString(CGKeyConstants.ERROR_CLOSE_JOB_FAILED);
            messageBody = MessageFormat.format(messageProperty, e.getMessage(), e.getCause().getMessage());

            LOG.error(messageBody, e);
        }

        // add a note onto the close document
        closeService.addDocumentNoteAfterClosing(messageBody);

        return true;
    }


    /**
     * Sets the {@link CloseService}. For use by Spring.
     * 
     * @param closeService The value to be used to assign to the local attribute <code>closeService</code>.
     */
    public void setCloseService(org.kuali.kfs.module.cg.service.CloseService closeService) {
        this.closeService = closeService;
    }

}
