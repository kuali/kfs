/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.pdp.service.impl;

import java.util.Iterator;

import org.kuali.core.service.MailService;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.service.AchEmailService;
import org.kuali.module.pdp.service.EnvironmentService;
import org.kuali.module.pdp.service.PaymentDetailService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AchEmailServiceImpl implements AchEmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchEmailServiceImpl.class);

    private PaymentDetailService paymentDetailService;
    private EnvironmentService environmentService;
    private MailService mailService;
    

    /**
     * @see org.kuali.module.pdp.service.AchEmailService#sendAchEmails()
     */
    public void sendAchEmails() {
        LOG.debug("sendAchEmails() started");

        Iterator iter = paymentDetailService.getAchPaymentsWithUnsentEmail();
        while ( iter.hasNext() ) {
            PaymentDetail pd = (PaymentDetail)iter.next();
            PaymentGroup pg = pd.getPaymentGroup();
            Batch batch = pg.getBatch();
            CustomerProfile cust = batch.getCustomerProfile();

            if ( cust.getAdviceCreate().booleanValue() ) {
                if ( environmentService.isProduction() ) {
                    // Send it to the real receipients
                } else {
                    // Send it to ourselves for test purposes
                    
                }
            }
//                if ($ENV{'testing'} eq 'y') {
//                    print "sending email to pdphelp\@yahoo.com for disb # $disb_nbr\n";
//                    $mailer->open( {'To'      => 'pdphelp@yahoo.com',
//                                    'From'    => $adv_rtrn_email_addr,
//                                    'Subject' => "TESTING!:$adv_subj_ln_txt"})
//                        || &terminate("Trouble Sending advice email for disb - $disb_nbr");
//                } else {
//                    print "sending email to $adv_email_addr for disb # $disb_nbr\n";
//                    $mailer->open( {'To'      => $adv_email_addr,
//                                    'From'    => $adv_rtrn_email_addr,
//                                    'Subject' => $adv_subj_ln_txt})
//                        || &terminate("Trouble Sending advice email for disb - $disb_nbr");
//                }
//            } else {
//                print "bouncing email to $adv_rtrn_email_addr for disb # $disb_nbr\n";
//                $mailer->open( {'To'      => $adv_rtrn_email_addr,
//                                'From'    => 'fms_operations@indiana.edu',
//                                'Subject' => 'Unable to send advice email'})
//                    || &terminate("Trouble Sending error advice email for disb - $disb_nbr");
//                print $mailer "Could not email this advice because no email ";
//                print $mailer "address was found.\n\n\n";
//            }
        }
    }

    public void setPaymentDetailService(PaymentDetailService pds) {
        paymentDetailService = pds;
    }
}
