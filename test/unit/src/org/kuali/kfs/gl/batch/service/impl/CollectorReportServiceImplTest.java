/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch.service.impl;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.service.CollectorReportService;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;

@ConfigureContext
public class CollectorReportServiceImplTest extends KualiTestBase {

    /**
     * Tests sending email notification for email send failures during the collector run
     */
    public void testSendEmailSendFailureNotice() throws Exception {
        CollectorReportServiceImpl collectorReportServiceImpl = (CollectorReportServiceImpl) SpringContext.getBean(CollectorReportService.class);
        MockMailServiceImpl mockMailService = (MockMailServiceImpl) SpringContext.getService("mockMailService");
        collectorReportServiceImpl.setMailService(mockMailService);
        
        ConfigurationService configurationService = SpringContext.getBean(ConfigurationService.class);

        CollectorReportData reportData = new CollectorReportData();

        // create a success email status
        CollectorBatch batch1 = new CollectorBatch();
        batch1.setBatchSequenceNumber(1);
        batch1.setBatchName("Test Batch 1");
        batch1.setEmailAddress("foo@arizona.edu");

        reportData.addBatch(batch1);

        String notificationMessage = configurationService.getPropertyValueAsString(KFSKeyConstants.Collector.NOTIFICATION_EMAIL_SENT);
        String formattedMessage = MessageFormat.format(notificationMessage, new Object[] { batch1.getEmailAddress() });
        reportData.setEmailSendingStatusForParsedBatch(batch1, formattedMessage);

        // create some failure email status
        CollectorBatch batch2 = new CollectorBatch();
        batch2.setBatchSequenceNumber(2);
        batch2.setBatchName("Test Batch 2");
        batch2.setEmailAddress("jonny@arizona");

        reportData.addBatch(batch2);

        String errorMessage = configurationService.getPropertyValueAsString(KFSKeyConstants.Collector.EMAIL_SEND_ERROR);
        formattedMessage = MessageFormat.format(errorMessage, new Object[] { batch2.getEmailAddress() });
        reportData.setEmailSendingStatusForParsedBatch(batch2, formattedMessage);

        CollectorBatch batch3 = new CollectorBatch();
        batch3.setBatchSequenceNumber(3);
        batch3.setBatchName("Test Batch 3");
        batch3.setEmailAddress("yo");

        reportData.addBatch(batch3);

        formattedMessage = MessageFormat.format(errorMessage, new Object[] { batch3.getEmailAddress() });
        reportData.setEmailSendingStatusForParsedBatch(batch3, formattedMessage);

        collectorReportServiceImpl.sendEmailSendFailureNotice(reportData);
        
        MailMessage message = mockMailService.getMailMessage();
        assertTrue("message body does not contain failure batch id 2", StringUtils.contains(message.getMessage(), "Test Batch 2"));
        assertTrue("message body does not contain failure batch id 3", StringUtils.contains(message.getMessage(), "Test Batch 3"));
        assertTrue("message body does not contain failure email for batch 2", StringUtils.contains(message.getMessage(), "jonny@arizona"));
        assertTrue("message body does not contain failure email for batch 3", StringUtils.contains(message.getMessage(), "yo"));
        
        assertFalse("message body contains success batch id 1", StringUtils.contains(message.getMessage(), "Test Batch 1"));
        assertFalse("message body contains email success batch id 1", StringUtils.contains(message.getMessage(), "foo@arizona.edu"));
    }
}
