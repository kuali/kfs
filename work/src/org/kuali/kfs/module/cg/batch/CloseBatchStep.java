/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.cg.batch;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.service.MailService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.cg.service.CfdaService;
import org.kuali.module.cg.service.CfdaUpdateResults;
import org.kuali.module.cg.service.CloseService;

/**
 * This class...
 * 
 * @author Laran Evans <lc278@cornell.edu>
 * @since Apr 6, 2007 12:58:58 PM
 */
public class CloseBatchStep extends AbstractStep {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(CloseBatchStep.class);
    private static String MAIL_RECIPIENTS_GROUP_NAME = "KUALI_CGCFDA";
    
    private CloseService closeService;
    private MailService mailService;
    private KualiGroupService kualiGroupService;
    private UniversalUserService universalUserService;

    /**
     * 
     * @see org.kuali.kfs.batch.Step#execute()
     */
    public boolean execute(String jobName) {
        
        MailMessage message = new MailMessage();
        
        try {
            
            // TODO this message should come from some config file.
            StringBuilder builder = new StringBuilder();
            
            boolean failed = false;
            
            try {
                closeService.close();
            } catch(Exception e) {
                builder.append("An Exception was encountered when running the close job.\n");
                builder.append("The exception message is: ").append(e.getMessage()).append("\n");
                builder.append("The message from the exception cause is: ").append(e.getCause().getMessage()).append("\n");
                builder.append("Please see the log for more details.\n");

                LOG.error("The following exception was encountered during the close batch process.", e);
                
                failed = true;
            }
            
            if(!failed) {
                builder.append("The Close batch script ran successfully.\n");
            }

            KualiGroup workgroup = kualiGroupService.getByGroupName(MAIL_RECIPIENTS_GROUP_NAME);
            List<String> memberNetworkIds = workgroup.getGroupUsers();
            for(String id : memberNetworkIds) {
                try {
                    AuthenticationUserId authId = new AuthenticationUserId(id.toUpperCase());
                    UniversalUser user = 
                        universalUserService.getUniversalUser(authId);
                    String address = user.getPersonEmailAddress();
                    if(!StringUtils.isEmpty(address)) {
                        message.addToAddress(address);
                    }
                } catch(UserNotFoundException unfe) {
                    LOG.info("User " + id + " doesn't exist.", unfe);
                }
            }
            
            // TODO Must remember to take this out.
            message.addToAddress("lc278@cornell.edu");

            message.setMessage(builder.toString());
            mailService.sendMessage(message);

        } catch(GroupNotFoundException gnfe) {
            LOG.fatal("Couldn't find workgroup to send notification to.", gnfe);
            return true;
        } catch(InvalidAddressException iae) {
            LOG.warn("The email address for one or more of the members of the " + MAIL_RECIPIENTS_GROUP_NAME + " workgroup is invalid.", iae);
            return true;
        }
        
        return true;
    }

    /**
     * 
     * This method is a simple setter used to assign a value to local attribute.
     * @param closeService The value to be used to assign to the local attribute <code>closeService</code>.
     */
    public void setCloseService(org.kuali.module.cg.service.CloseService closeService) {
        this.closeService = closeService;
    }

    public void setKualiGroupService(KualiGroupService kualiGroupService) {
        this.kualiGroupService = kualiGroupService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }
    
}
