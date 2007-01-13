/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.MailService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.collector.xml.CollectorFileHandler;
import org.kuali.module.gl.collector.xml.CollectorFileParser;
import org.kuali.module.gl.collector.xml.FileReadException;
import org.kuali.module.gl.collector.xml.XmlHeader;
import org.kuali.module.gl.collector.xml.XmlTrailer;
import org.kuali.module.gl.collector.xml.impl.CollectorFileHandlerImpl;
import org.kuali.module.gl.collector.xml.impl.HardEditHandler;
import org.kuali.module.gl.service.CollectorService;
import org.kuali.module.gl.service.InterDepartmentalBillingService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class CollectorServiceImpl implements CollectorService, BeanFactoryAware {
    private static Logger LOG = Logger.getLogger(CollectorServiceImpl.class);

    private InterDepartmentalBillingService interDepartmentalBillingService;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private KualiConfigurationService kualiConfigurationService;
    private MailService mailService;
    private DateTimeService dateTimeService;
    private BeanFactory beanFactory;

    public void loadCollectorFile(String fileName) {
        CollectorFileParser collectorFileParser = (CollectorFileParser) beanFactory.getBean(GLConstants.LookupableBeanKeys.COLLECTOR_FILE_PARSER);
        doHardEditParse(collectorFileParser, fileName);
        List errors = collectorFileParser.getFileHandler().getErrorMessages();
        if (errors.isEmpty())
            doCollectorFileParse(collectorFileParser, fileName);
        sendEmail(collectorFileParser.getFileHandler());
    }

    private void doHardEditParse(CollectorFileParser collectorFileParser, String fileName) {
        HardEditHandler hardEditHandler = new HardEditHandler();
        hardEditHandler.clear();
        collectorFileParser.setFileHandler(hardEditHandler);
        try {
            InputStream inputStream1 = new FileInputStream(fileName);
            collectorFileParser.parse(inputStream1);
        }
        catch (FileReadException fre) {
            // Do something here.
        }
        catch (FileNotFoundException fnfe) {
            // Do something here.
        }
    }

    private void sendEmail(CollectorFileHandler collectorFileHandler) {
        LOG.debug("sendNoteEmails() starting");
        MailMessage message = new MailMessage();

        message.setFromAddress(kualiConfigurationService.getApplicationParameterValue("Kuali.GeneralLedger.Collector", "Kuali.GeneralLedger.EmailAddress.DoNotReply"));
        message.setSubject(kualiConfigurationService.getApplicationParameterValue("Kuali.GeneralLedger.Collector", "SubjectLine"));

        String body = createMessageBody(collectorFileHandler);

        message.setMessage(body);

        String email = collectorFileHandler.getHeader().getWorkgroupName();
        message.addToAddress(email);

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    private String createMessageBody(CollectorFileHandler collectorFileHandler) {
        StringBuffer body = new StringBuffer();
        XmlHeader header = collectorFileHandler.getHeader();
        XmlTrailer trailer = collectorFileHandler.getTrailer();
        body.append("Header Information:\n\n");
        body.append("Chart: " + header.getChartOfAccountsCode() + "\n");
        body.append("Org: " + header.getOrganizationCode() + "\n");
        body.append("Contact: " + header.getContactPerson() + "\n");
        body.append("Email: " + header.getWorkgroupName() + "\n");
        body.append("File Date: " + header.getTransmissionDate() + "\n\n");
        body.append("Summary Totals:\n");
        // SUMMARY TOTALS HERE
        body.append("    Total Records: " + String.valueOf(trailer.getTotalRecords().intValue()) + "\n");
        body.append("    Total Amount: " + trailer.getTotalAmount() + "\n\n");
        body.append("Reported Errors:\n");
        // ERRORS GO HERE
        List errorMessages = collectorFileHandler.getErrorMessages();
        if (errorMessages.isEmpty()) {
            body.append("----- NO ERRORS TO REPORT -----\nFiles have been added to the system.");
        }
        else {
            Iterator iter = errorMessages.iterator();
            while (iter.hasNext()) {
                String currentMessage = (String) iter.next();
                body.append(currentMessage + "\n");
            }
            body.append("\n----- THE RECORDS WERE NOT SAVED TO THE DATABASE -----");
        }
        return body.toString();
    }

    private CollectorFileHandlerImpl doCollectorFileParse(CollectorFileParser collectorFileParser, String fileName) {
        CollectorFileHandlerImpl collectorFileHandler = new CollectorFileHandlerImpl(originEntryService, interDepartmentalBillingService, originEntryGroupService, dateTimeService);
        collectorFileParser.setFileHandler(collectorFileHandler);
        try {
            InputStream inputStream2 = new FileInputStream(fileName);
            collectorFileParser.parse(inputStream2);
            return collectorFileHandler;
        }
        catch (FileReadException fre) {
            return null;
        }
        catch (FileNotFoundException fnfe) {
            return null;
        }
    }

    public void setInterDepartmentalBillingService(InterDepartmentalBillingService interDepartmentalBillingService) {
        this.interDepartmentalBillingService = interDepartmentalBillingService;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public String getStagingDirectory() {
        return kualiConfigurationService.getPropertyString(Constants.GL_COLLECTOR_STAGING_DIRECTORY);
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public static void setLOG(Logger log) {
        LOG = log;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
}
