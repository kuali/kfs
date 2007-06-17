/*
 * Created on Jul 12, 2004
 *
 */
package org.kuali.module.pdp.service;

import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.exception.PaymentLoadException;

/**
 * @author jsissom
 *
 */
public interface PaymentFileService {
    public LoadPaymentStatus loadPayments(String filename,PdpUser user) throws PaymentLoadException;
}
