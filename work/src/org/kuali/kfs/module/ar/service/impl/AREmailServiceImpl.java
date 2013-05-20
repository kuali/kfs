/*
\ * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.ContractsGrantsInvoiceEmailReportsBatchStep;
import org.kuali.kfs.module.ar.businessobject.InvoiceAgencyAddressDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.service.AREmailService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.kew.mail.Mailer;
import org.kuali.rice.kew.mail.service.impl.DefaultEmailService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.core.web.format.CurrencyFormatter;


/**
 * Defines methods for sending AR emails.
 */
public class AREmailServiceImpl extends DefaultEmailService implements AREmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AREmailServiceImpl.class);

    protected MailService mailService;
    private ConfigurationService kualiConfigurationService;
    protected ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;
    private Mailer mailer;


    /**
     * This method is used to send emails to the agency
     * 
     * @param invoices
     */
    public void sendInvoicesViaEmail(List<ContractsGrantsInvoiceDocument> invoices) throws AddressException, MessagingException {
        LOG.debug("sendInvoicesViaEmail() starting.");
        mailer = this.createMailer();
        Session session = mailer.getCurrentSession();
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            List<InvoiceAgencyAddressDetail> agencyAddresses = invoice.getAgencyAddressDetails();
            for (InvoiceAgencyAddressDetail agencyAddress : agencyAddresses) {
                if (ArConstants.InvoiceIndicator.EMAIL.equals(agencyAddress.getPreferredInvoiceIndicatorCode())) {
                    Note note = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Note.class, agencyAddress.getNoteId());
                    if (ObjectUtils.isNotNull(note)) {
                        MimeMessage message = new MimeMessage(session);

                        // From Address
                        String sender = KNSServiceLocator.getParameterService().getParameterValueAsString(ContractsGrantsInvoiceEmailReportsBatchStep.class, ArConstants.CG_INVOICE_FROM_EMAIL_ADDRESS);
                        message.setFrom(new InternetAddress(sender));
                        // To Address
                        Map<String, Object> primaryKeys = new HashMap<String, Object>();
                        ContractsAndGrantsAgencyAddress address; // = agencyAddress.getAgencyAddress();
                        primaryKeys.put(KFSPropertyConstants.AGENCY_NUMBER, agencyAddress.getAgencyNumber());
                        primaryKeys.put("agencyAddressIdentifier", agencyAddress.getAgencyAddressIdentifier());
                        address = (ContractsAndGrantsAgencyAddress) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObject(ContractsAndGrantsAgencyAddress.class, primaryKeys);
                        String recipients = address.getAgencyInvoiceEmailAddress();
                        if (StringUtils.isNotEmpty(recipients)) {
                            InternetAddress[] recipientAddress = { new InternetAddress(recipients) };
                            message.addRecipients(Message.RecipientType.TO, recipientAddress);
                        }
                        else {
                            LOG.warn("No recipients indicated.");
                        }

                        // The Subject
                        String subject = KNSServiceLocator.getParameterService().getParameterValueAsString(ContractsGrantsInvoiceEmailReportsBatchStep.class, ArConstants.CG_INVOICE_EMAIL_SUBJECT);
                        String bodyText = KNSServiceLocator.getParameterService().getParameterValueAsString(ContractsGrantsInvoiceEmailReportsBatchStep.class, ArConstants.CG_INVOICE_EMAIL_BODY);
                        Map<String, String> map = new HashMap<String, String>();
                        this.getEmailParameterList(map, invoice, address);
                        subject = replaceValuesInString(subject, map);
                        bodyText = replaceValuesInString(bodyText, map);
                        message.setSubject(subject);
                        if (StringUtils.isEmpty(subject)) {
                            LOG.warn("Empty subject being sent.");
                        }

                        // Now the message body.
                        // create and fill the first message part
                        MimeBodyPart body = new MimeBodyPart();
                        body.setText(bodyText);

                        // create and fill the second message part
                        MimeBodyPart attachment = new MimeBodyPart();
                        // Use setText(text, charset), to show it off !
                        // create the Multipart and its parts to it
                        Multipart multipart = new MimeMultipart();
                        multipart.addBodyPart(body);
                        try {
                            ByteArrayDataSource ds = new ByteArrayDataSource(note.getAttachment().getAttachmentContents(), "application/pdf");
                            attachment.setDataHandler(new DataHandler(ds));
                            attachment.setFileName(note.getAttachment().getAttachmentFileName());
                            multipart.addBodyPart(attachment);
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        // add the Multipart to the message
                        message.setContent(multipart);

                        // Finally, send the message!
                        Transport.send(message);
                    }
                }
            }
            invoice.setMarkedForProcessing(ArConstants.INV_RPT_PRCS_SENT);
            SpringContext.getBean(DocumentService.class).updateDocument(invoice);
        }
    }

    private void getEmailParameterList(Map<String, String> primaryKeys, ContractsGrantsInvoiceDocument invoice, ContractsAndGrantsAgencyAddress agencyAddress) {
        String[] orgCode = invoice.getAward().getAwardPrimaryFundManager().getFundManager().getPrimaryDepartmentCode().split("-");
        Map<String, Object> key = new HashMap<String, Object>();
        key.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode[0].trim());
        key.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, orgCode[1].trim());
        Organization org = (Organization) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(Organization.class, key);
        primaryKeys.put("#grantNumber", returnProperStringValue(invoice.getAward().getProposal().getGrantNumber()));
        primaryKeys.put("#proposalNumber", returnProperStringValue(invoice.getProposalNumber()));
        primaryKeys.put("#invoiceNumber", returnProperStringValue(invoice.getDocumentNumber()));
        primaryKeys.put("#agencyName", returnProperStringValue(agencyAddress.getAgency().getFullName()));
        primaryKeys.put("#addressName", returnProperStringValue(agencyAddress.getAgencyAddressName()));
        primaryKeys.put("#name", returnProperStringValue(invoice.getAward().getAwardPrimaryFundManager().getFundManager().getName()));
        primaryKeys.put("#title", returnProperStringValue(invoice.getAward().getAwardPrimaryFundManager().getAwardFundManagerProjectTitle()));
        // primaryKeys.put("#school", invoice.getAward().getAwardPrimaryFundManager().getFundManager().get);
        if (ObjectUtils.isNotNull(org))
            primaryKeys.put("#department", returnProperStringValue(org.getOrganizationName()));
        primaryKeys.put("#phone", returnProperStringValue(invoice.getAward().getAwardPrimaryFundManager().getFundManager().getPhoneNumber()));
        primaryKeys.put("#email", returnProperStringValue(invoice.getAward().getAwardPrimaryFundManager().getFundManager().getEmailAddress()));
    }

    private String returnProperStringValue(Object string) {
        if (ObjectUtils.isNotNull(string)) {
            if (string instanceof KualiDecimal) {
                String amount = (new CurrencyFormatter()).format(string).toString();
                return "$" + (StringUtils.isEmpty(amount) ? "0.00" : amount);
            }
            return string.toString();
        }
        return "";
    }

    private static String replaceValuesInString(String template, Map<String, String> replacementList) {
        StringBuilder buffOriginal = new StringBuilder();
        StringBuilder buffNormalized = new StringBuilder();

        String[] keys = template.split("[ \\t]+");

        // Scan for each word
        for (String key : keys) {
            String value = replacementList.get(key);
            if (ObjectUtils.isNotNull(value))
                buffOriginal.append(value + " ");
            else
                buffOriginal.append(key + " ");
        }
        return buffOriginal.toString();
    }

    /**
     * Sets the mailService attribute value.
     * 
     * @param mailService The mailService to set.
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
