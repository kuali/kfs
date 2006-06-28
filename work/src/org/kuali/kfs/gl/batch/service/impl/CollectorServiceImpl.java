/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl;

import java.io.*;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.*;
import org.bouncycastle.asn1.x509.qualified.MonetaryValue;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.*;
import org.kuali.module.gl.collector.xml.*;
import org.kuali.module.gl.collector.xml.impl.*;
import org.kuali.module.gl.service.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.*;

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
        CollectorFileParser collectorFileParser = (CollectorFileParser)beanFactory.getBean("glCollectorFileParser");
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
        }catch(FileReadException fre) {
            //Do something here.
        }catch(FileNotFoundException fnfe) {
            //Do something here.
        }
    }
    private void sendEmail(CollectorFileHandler collectorFileHandler) {
        LOG.debug("sendNoteEmails() starting");
        MailMessage message = new MailMessage();
        message.setFromAddress("doNotReply@KUALI.SYSTEM");
        message.setSubject("Collector Input Summary");

        String body = createMessageBody(collectorFileHandler);

        message.setMessage(body);
        
        String email = collectorFileHandler.getHeader().getWorkgroupName();
        message.addToAddress(email);

        try {
            mailService.sendMessage(message);
        } catch (InvalidAddressException e) {
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
        } else {
            Iterator iter = errorMessages.iterator();
            while (iter.hasNext()) {
                String currentMessage = (String)iter.next();
                body.append(currentMessage + "\n");
            }
            body.append("\n----- THE RECORDS WERE NOT SAVED TO THE DATABASE -----");
        }
        return body.toString();
    }
    private CollectorFileHandlerImpl doCollectorFileParse(CollectorFileParser collectorFileParser, String fileName) {
        CollectorFileHandlerImpl collectorFileHandler = new CollectorFileHandlerImpl
                (originEntryService, interDepartmentalBillingService, originEntryGroupService, dateTimeService);
        collectorFileParser.setFileHandler(collectorFileHandler);
        try {
            InputStream inputStream2 = new FileInputStream(fileName);
            collectorFileParser.parse(inputStream2);
            return collectorFileHandler;
        }catch(FileReadException fre) {
            return null;
        }catch(FileNotFoundException fnfe) {
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
        return kualiConfigurationService.getPropertyString("collector.staging.directory");
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
