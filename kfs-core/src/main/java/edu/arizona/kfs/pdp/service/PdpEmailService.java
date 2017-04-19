package edu.arizona.kfs.pdp.service;

import java.util.List;

import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.rice.krad.util.MessageMap;

public interface PdpEmailService extends org.kuali.kfs.pdp.service.PdpEmailService {

    /**
     * Sends email for a Prepaid payment load has failed. Errors encountered will be printed out in message
     *
     * @param prepaidChecksFile parsed payment file object (might not be populated completely due to errors)
     * @param errors <code>MessageMap</code> containing <code>ErrorMessage</code> entries
     * @param fileName Prepaid Checks file name
     */
    public void sendPrepaidChecksErrorEmail(PaymentFileLoad prepaidChecksFile, MessageMap errors, String fileName);

    /**
     * Sends email for a successful payment load. Warnings encountered will be printed out in message
     *
     * @param prepaidChecksFile parsed payment file object
     * @param warnings <code>List</code> of <code>String</code> messages
     * @param fileName Prepaid Checks file name
     */
    public void sendPrepaidChecksLoadEmail(PaymentFileLoad prepaidChecksFile, List<String> warnings, String fileName);
    
}
