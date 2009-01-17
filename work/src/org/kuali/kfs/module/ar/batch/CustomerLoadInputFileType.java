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
package org.kuali.kfs.module.ar.batch;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.batch.service.CustomerLoadService;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kns.service.DateTimeService;

public class CustomerLoadInputFileType extends BatchInputFileTypeBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadInputFileType.class);

    private static final String FILE_NAME_PREFIX = "customer_load";
    private static final String FILE_NAME_DELIM = "_";
    
    private DateTimeService dateTimeService;
    private CustomerLoadService customerLoadService;
    
    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.bo.Person, java.lang.Object, java.lang.String)
     */
    public String getFileName(String principalId, Object parsedFileContents, String fileUserIdentifer) {
        
        //  start with the batch-job-prefix
        StringBuilder fileName = new StringBuilder(FILE_NAME_PREFIX);
        
        //  add the logged-in user name if there is one, otherwise use a sensible default
        String userName = null;
        if (StringUtils.isBlank(principalId)) {
            userName = SpringContext.getBean(IdentityManagementService.class).getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER).getPrincipalId();
        }
        else {
            userName = principalId;
        }
        fileName.append(FILE_NAME_DELIM + userName);
        
        //  if the user specified an identifying lable, then use it
        if (StringUtils.isNotBlank(fileUserIdentifer)) {
            fileName.append(FILE_NAME_DELIM + fileUserIdentifer);
        }
        
        //  stick a timestamp on the end
        fileName.append(FILE_NAME_DELIM + dateTimeService.toString(dateTimeService.getCurrentTimestamp(), "yyyyMMdd_HHmmss"));

        //  stupid spaces, begone!
        return StringUtils.remove(fileName.toString(), " ");
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return ArConstants.CustomerLoad.CUSTOMER_LOAD_FILE_TYPE_IDENTIFIER;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    public boolean validate(Object parsedFileContents) {
        //  attempt to cast the parsedFileContents into the expected type
        List<CustomerDigesterVO> customerVOs = null;
        try {
            customerVOs = (List<CustomerDigesterVO>) parsedFileContents;
        }
        catch (Exception e) {
            LOG.error("Could not convert the passed-in parsedFileContents of type [" + parsedFileContents.getClass().toString() + 
                    "] to List<CustomerDigesterVO>.");
            throw new RuntimeException("Could not convert the passed-in parsedFileContents of type [" + parsedFileContents.getClass().toString() + 
                    "] to List<CustomerDigesterVO>.", e);
        }
        return customerLoadService.validate(customerVOs);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileTypeBase#process(java.lang.String, java.lang.Object)
     */
    public void process(String fileName, Object parsedFileContents) {
        super.process(fileName, parsedFileContents);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        return ArKeyConstants.CustomerLoad.MESSAGE_BATCH_UPLOAD_TITLE_CUSTOMER;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setCustomerLoadService(CustomerLoadService customerLoadService) {
        this.customerLoadService = customerLoadService;
    }

    public String getAuthorPrincipalId(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), FILE_NAME_DELIM);
        if (fileNameParts.length > 3) {
            return fileNameParts[2];
        }
        return null;
    }
}

