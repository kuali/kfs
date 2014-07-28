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
import java.sql.Date;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.batch.UpcomingMilestoneNotificationStep;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.service.AREmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.mail.AttachmentMailMessage;
import org.kuali.kfs.sys.service.AttachmentMailService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.exception.InvalidAddressException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.service.impl.MailServiceImpl;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * Defines methods for sending AR emails.
 */
public class AREmailServiceImpl implements AREmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AREmailServiceImpl.class);

    protected AttachmentMailService mailService;
    protected ParameterService parameterService;
    protected DataDictionaryService dataDictionaryService;
    protected ConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected NoteService noteService;
    protected KualiModuleService kualiModuleService;

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    @NonTransactional
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * This method gets the document service
     *
     * @return the document service
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * This method sets the document service
     *
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    /**
     * This method is used to send emails to the agency
     *
     * @param invoices
     */
    @Override
    public void sendInvoicesViaEmail(Collection<ContractsGrantsInvoiceDocument> invoices) throws InvalidAddressException, MessagingException  {
        LOG.debug("sendInvoicesViaEmail() starting.");

        Properties props = getConfigProperties();

        // Get session
        Session session = Session.getInstance(props, null);
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            List<InvoiceAddressDetail> invoiceAddressDetails = invoice.getInvoiceAddressDetails();
            for (InvoiceAddressDetail invoiceAddressDetail : invoiceAddressDetails) {
                if (ArConstants.InvoiceTransmissionMethod.EMAIL.equals(invoiceAddressDetail.getInvoiceTransmissionMethodCode())) {
                    Note note = noteService.getNoteByNoteId(invoiceAddressDetail.getNoteId());

                    if (ObjectUtils.isNotNull(note)) {
                        AttachmentMailMessage message = new AttachmentMailMessage();

                        String sender = parameterService.getParameterValueAsString(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE, ArConstants.CONTRACTS_GRANTS_INVOICE_COMPONENT, ArConstants.FROM_EMAIL_ADDRESS);
                        message.setFromAddress(sender);

                        CustomerAddress customerAddress = invoiceAddressDetail.getCustomerAddress();
                        String recipients = invoiceAddressDetail.getCustomerEmailAddress();
                        if (StringUtils.isNotEmpty(recipients)) {
                            message.getToAddresses().add(recipients);
                        }
                        else {
                            LOG.warn("No recipients indicated.");
                        }

                        String subject = parameterService.getParameterValueAsString(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE, ArConstants.CONTRACTS_GRANTS_INVOICE_COMPONENT, ArConstants.EMAIL_SUBJECT);
                        Map<String, String> map = new HashMap<String, String>();
                        getEmailParameterList(map, invoice, customerAddress);
                        subject = replaceValuesInString(subject, map);
                        message.setSubject(subject);
                        if (StringUtils.isEmpty(subject)) {
                            LOG.warn("Empty subject being sent.");
                        }

                        String bodyText = parameterService.getParameterValueAsString(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE, ArConstants.CONTRACTS_GRANTS_INVOICE_COMPONENT, ArConstants.EMAIL_BODY);
                        bodyText = replaceValuesInString(bodyText, map);
                        message.setMessage(bodyText);
                        if (StringUtils.isEmpty(bodyText)) {
                            LOG.warn("Empty bodyText being sent.");
                        }

                        Attachment attachment = note.getAttachment();
                        if (ObjectUtils.isNotNull(attachment)) {
                            try {
                                message.setContent(IOUtils.toByteArray(attachment.getAttachmentContents()));
                            }
                            catch (IOException ex) {
                                LOG.error("Error setting attachment contents", ex);
                                throw new RuntimeException(ex);
                            }
                            message.setFileName(attachment.getAttachmentFileName());
                            message.setType(attachment.getAttachmentMimeTypeCode());
                        }

                        setupMailServiceForNonProductionInstance();
                        mailService.sendMessage(message);
                    }
                }
            }
            invoice.setDateEmailProcessed(new Date(new java.util.Date().getTime()));
            documentService.updateDocument(invoice);
        }
    }

    protected void getEmailParameterList(Map<String, String> primaryKeys, ContractsGrantsInvoiceDocument invoice, CustomerAddress customerAddress) {
        String[] orgCode = invoice.getAward().getAwardPrimaryFundManager().getFundManager().getPrimaryDepartmentCode().split("-");
        Map<String, Object> key = new HashMap<String, Object>();
        key.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, orgCode[0].trim());
        key.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode[1].trim());
        Organization org = businessObjectService.findByPrimaryKey(Organization.class, key);
        primaryKeys.put("#grantNumber", returnProperStringValue(invoice.getAward().getProposal().getGrantNumber()));
        primaryKeys.put("#proposalNumber", returnProperStringValue(invoice.getProposalNumber()));
        primaryKeys.put("#invoiceNumber", returnProperStringValue(invoice.getDocumentNumber()));
        primaryKeys.put("#customerName", returnProperStringValue(customerAddress.getCustomer().getCustomerName()));
        primaryKeys.put("#addressName", returnProperStringValue(customerAddress.getCustomerAddressName()));
        primaryKeys.put("#name", returnProperStringValue(invoice.getAward().getAwardPrimaryFundManager().getFundManager().getName()));
        primaryKeys.put("#title", returnProperStringValue(invoice.getAward().getAwardPrimaryFundManager().getAwardFundManagerProjectTitle()));
        if (ObjectUtils.isNotNull(org)) {
            primaryKeys.put("#department", returnProperStringValue(org.getOrganizationName()));
        }
        primaryKeys.put("#phone", returnProperStringValue(invoice.getAward().getAwardPrimaryFundManager().getFundManager().getPhoneNumber()));
        primaryKeys.put("#email", returnProperStringValue(invoice.getAward().getAwardPrimaryFundManager().getFundManager().getEmailAddress()));
    }

    protected String returnProperStringValue(Object string) {
        if (ObjectUtils.isNotNull(string)) {
            if (string instanceof KualiDecimal) {
                String amount = (new CurrencyFormatter()).format(string).toString();
                return "$" + (StringUtils.isEmpty(amount) ? "0.00" : amount);
            }
            return string.toString();
        }
        return "";
    }

    protected static String replaceValuesInString(String template, Map<String, String> replacementList) {
        StringBuilder buffOriginal = new StringBuilder();
        StringBuilder buffNormalized = new StringBuilder();

        String[] keys = template.split("[ \\t]+");

        // Scan for each word
        for (String key : keys) {
            String value = replacementList.get(key);
            if (ObjectUtils.isNotNull(value)) {
                buffOriginal.append(value + " ");
            }
            else {
                buffOriginal.append(key + " ");
            }
        }
        return buffOriginal.toString();
    }

    /**
     * Setup properties to handle mail messages in a non-production environment as appropriate.
     *
     * NOTE: We should be setting up configuration properties for these values, and once that is done
     * this method can be removed.
     */
    public void setupMailServiceForNonProductionInstance() {
        if (!ConfigContext.getCurrentContextConfig().isProductionEnvironment()) {
            ((MailServiceImpl)mailService).setRealNotificationsEnabled(false);
            ((MailServiceImpl)mailService).setNonProductionNotificationMailingList(mailService.getBatchMailingList());
        }
    }

    /**
     * Sets the mailService attribute value.
     *
     * @param mailService The mailService to set.
     */
    public void setMailService(AttachmentMailService mailService) {
        this.mailService = mailService;
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

    /**
     * Retrieves the Properties used to configure the Mailer. The property names configured in the Workflow configuration should
     * match those of Java Mail.
     *
     * @return
     */
    protected Properties getConfigProperties() {
        return ConfigContext.getCurrentContextConfig().getProperties();
    }

    /**
     * This method sends out emails for upcoming milestones.
     *
     * @see org.kuali.kfs.module.ar.service.CGEmailService#sendEmail(java.util.List, org.kuali.kfs.module.ar.businessobject.Award)
     */
    @Override
    public void sendEmail(List<Milestone> milestones, ContractsAndGrantsBillingAward award) {
        LOG.debug("sendEmail() starting");

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(ArConstants.REMINDER_EMAIL_SUBJECT));
        message.getToAddresses().add(award.getAwardPrimaryFundManager().getFundManager().getEmailAddress());
        StringBuffer body = new StringBuffer();


        String messageKey = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.MESSAGE_CG_UPCOMING_MILESTONES_EMAIL_LINE_1);
        body.append(MessageFormat.format(messageKey, new Object[] { null }));

        body.append(award.getProposalNumber() + ".\n\n");

        for (Milestone milestone : milestones) {

            String milestoneNumber = dataDictionaryService.getAttributeLabel(Milestone.class, "milestoneNumber");
            String milestoneDescription = dataDictionaryService.getAttributeLabel(Milestone.class, "milestoneDescription");
            String milestoneAmount = dataDictionaryService.getAttributeLabel(Milestone.class, "milestoneAmount");
            String milestoneExpectedCompletionDate = dataDictionaryService.getAttributeLabel(Milestone.class, "milestoneExpectedCompletionDate");
            String milestoneActualCompletionDate = dataDictionaryService.getAttributeLabel(Milestone.class, "milestoneActualCompletionDate");


            body.append(milestoneNumber + ": " + milestone.getMilestoneNumber() + " \n");
            body.append(milestoneDescription + ": " + milestone.getMilestoneDescription() + " \n");
            body.append(milestoneAmount + ": " + milestone.getMilestoneAmount() + " \n");
            body.append(milestoneExpectedCompletionDate + ": " + milestone.getMilestoneExpectedCompletionDate() + " \n");

            body.append("\n\n");
        }
        body.append("\n\n");

        messageKey = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.MESSAGE_CG_UPCOMING_MILESTONES_EMAIL_LINE_2);
        body.append(MessageFormat.format(messageKey, new Object[] { null }) + "\n\n");

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException | MessagingException ex) {
            LOG.error("Problems sending milestones e-mail", ex);
            throw new RuntimeException("Problems sending milestones e-mail", ex);
        }
    }

    /**
     * Retrieves the email subject text from system parameter then checks environment code and prepends to message if not
     * production.
     *
     * @param subjectParmaterName name of parameter giving the subject text
     * @return subject text
     */
    protected String getEmailSubject(String subjectParmaterName) {
        String subject = parameterService.getParameterValueAsString(UpcomingMilestoneNotificationStep.class, subjectParmaterName);

        String productionEnvironmentCode = kualiConfigurationService.getPropertyValueAsString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
        if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            subject = environmentCode + ": " + subject;
        }

        return subject;
    }

    /**
     * Sets the noteService attribute value.
     *
     * @param noteService The noteService to set.
     */
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
