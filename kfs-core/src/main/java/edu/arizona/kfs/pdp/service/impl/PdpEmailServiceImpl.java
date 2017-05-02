package edu.arizona.kfs.pdp.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.batch.LoadPaymentsStep;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.service.AchBankService;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.MessageMap;

import edu.arizona.kfs.pdp.service.PdpEmailService;

public class PdpEmailServiceImpl extends org.kuali.kfs.pdp.service.impl.PdpEmailServiceImpl implements PdpEmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpEmailServiceImpl.class);
   
    protected CustomerProfileService customerProfileService;
    protected ConfigurationService kualiConfigurationService;
    protected MailService mailService;
    protected ParameterService parameterService;
    protected DataDictionaryService dataDictionaryService;
    protected AchBankService achBankService;
    
    public void sendPrepaidChecksLoadEmail(PaymentFileLoad prepaidChecksFile, List<String> warnings, String fileName) {    	
        LOG.debug("sendPrepaidChecksLoadEmail() starting");

        // check email configuration
        if (!super.isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();

        String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if(StringUtils.isEmpty(returnAddress)) {
            returnAddress = mailService.getBatchMailingList();
        }
        message.setFromAddress(returnAddress);
        message.setSubject("Prepaid Checks File Load Success Notification - " + fileName);

        List<String> ccAddresses = new ArrayList<String>( parameterService.getParameterValuesAsString(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC) );
        message.getCcAddresses().addAll(ccAddresses);
        message.getBccAddresses().addAll(ccAddresses);
        
        CustomerProfile customer = customerProfileService.get(prepaidChecksFile.getChart(), prepaidChecksFile.getUnit(), prepaidChecksFile.getSubUnit());
        String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));

        message.getToAddresses().addAll(toAddressList);
       
        StringBuilder body = new StringBuilder();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        super.addPaymentFieldsToBody(body, prepaidChecksFile.getBatchId().intValue(), prepaidChecksFile.getChart(), prepaidChecksFile.getUnit(), prepaidChecksFile.getSubUnit(), prepaidChecksFile.getCreationDate(), prepaidChecksFile.getPaymentCount(), prepaidChecksFile.getPaymentTotalAmount());

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_WARNING_MESSAGES) + "\n");
        for (String warning : warnings) {
            body.append(warning + "\n\n");
        }
        
        message.setMessage(body.toString());

        // KFSMI-6475 - if not a production instance, replace the recipients with the testers list
        super.alterMessageWhenNonProductionInstance(message, null);

        try {
            mailService.sendMessage(message);
        }
        catch (Exception e) {
        	 LOG.error("sendPrepaidChecksLoadEmail() Invalid email address. Message not sent", e);
             throw new RuntimeException("sendPrepaidChecksLoadEmail() Invalid email address. Message not sent " + e.getMessage(), e);             
        }        

    }

    public void sendPrepaidChecksErrorEmail(PaymentFileLoad prepaidChecksFile, MessageMap errors, String fileName) {    
        LOG.debug("sendPrepaidChecksErrorEmail() starting");

        // check email configuration
        if (!super.isPaymentEmailEnabled()) {
            return;
        }

        MailMessage message = new MailMessage();

        String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if(StringUtils.isEmpty(returnAddress)) {
            returnAddress = mailService.getBatchMailingList();
        }
        message.setFromAddress(returnAddress);
        message.setSubject("Prepaid Checks File Load ERROR Notification - " + fileName);

        StringBuilder body = new StringBuilder();
        List<String> ccAddresses = new ArrayList<String>( parameterService.getParameterValuesAsString(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC) );

        if (prepaidChecksFile == null) {
            if (ccAddresses.isEmpty()) {
                LOG.warn("sendPrepaidChecksErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                return;
            }

            message.getToAddresses().addAll(ccAddresses);

            body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_BAD_FILE_PARSE) + "\n\n");
        }
        else {       	
            CustomerProfile customer = customerProfileService.get(prepaidChecksFile.getChart(), prepaidChecksFile.getUnit(), prepaidChecksFile.getSubUnit());
            if (customer == null) {
                LOG.error("sendPrepaidChecksErrorEmail() Invalid Customer.  Sending email to CC addresses");

                if (ccAddresses.isEmpty()) {
                    LOG.error("sendPrepaidChecksErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                    return;
                }

                message.getToAddresses().addAll(ccAddresses);

                body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_INVALID_CUSTOMER) + "\n\n");
            }
            else {           	
                String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
                List<String> toAddressList = Arrays.asList(toAddresses.split(","));

                message.getToAddresses().addAll(toAddressList);
                message.getCcAddresses().addAll(ccAddresses);
                //TODO: for some reason the mail service does not work unless the bcc list has addresss. This is a temporary workaround
                message.getBccAddresses().addAll(ccAddresses);
            }
        }
              
        if (prepaidChecksFile != null) {
        	body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_NOT_LOADED) + "\n\n");
            addPaymentFieldsToBody(body, null, prepaidChecksFile.getChart(), prepaidChecksFile.getUnit(), prepaidChecksFile.getSubUnit(), prepaidChecksFile.getCreationDate(), prepaidChecksFile.getPaymentCount(), prepaidChecksFile.getPaymentTotalAmount());
        }

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_ERROR_MESSAGES) + "\n");
        List<ErrorMessage> errorMessages = errors.getMessages(KFSConstants.GLOBAL_ERRORS);
        if (errorMessages != null) {
            for (ErrorMessage errorMessage : errorMessages) {
                body.append(getMessage(errorMessage.getErrorKey(), (Object[]) errorMessage.getMessageParameters()) + "\n\n");
            }

            message.setMessage(body.toString());

            // KFSMI-6475 - if not a production instance, replace the recipients with the testers list
            super.alterMessageWhenNonProductionInstance(message, null);

            try {
                mailService.sendMessage(message);
            }
            catch (Exception e) {
            	LOG.error("sendPrepaidChecksErrorEmail() Invalid email address.  Message not sent", e);
                throw new RuntimeException("sendPrepaidChecksLoadEmail() Invalid email address. Message not sent " + e.getMessage(), e);                
            }
        }
               
    }
         
    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
    	  this.customerProfileService = customerProfileService;
          super.setCustomerProfileService(customerProfileService);
    }
  
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
        super.setConfigurationService(kualiConfigurationService);
    }
    
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
        super.setMailService(mailService);
    }
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
        super.setParameterService(parameterService);        
    }
    
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
        super.setDataDictionaryService(dataDictionaryService);
    }
   
    public void setAchBankService(AchBankService achBankService) {
        this.achBankService = achBankService;
        super.setAchBankService(achBankService);
    }
    
}
