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
/*
 * Created on Aug 12, 2004
 */
package org.kuali.kfs.pdp.document.service;

import org.kuali.kfs.pdp.businessobject.SecurityRecord;
import org.kuali.kfs.pdp.exception.PdpException;
import org.kuali.rice.kns.bo.user.UniversalUser;


/**
 * @author HSTAPLET
 */
public interface PaymentMaintenanceService {
    public void cancelPendingPayment(Integer paymentGroupId, Integer paymentDetailId, String note, UniversalUser u, SecurityRecord sr) throws PdpException;

    public void holdPendingPayment(Integer paymentGroupId, String note, UniversalUser user) throws PdpException;

    public void removeHoldPendingPayment(Integer paymentGroupId, String note, UniversalUser user, SecurityRecord sr) throws PdpException;

    public void cancelDisbursement(Integer paymentGroupId, Integer paymentDetailId, String note, UniversalUser user) throws PdpException;

    public void cancelReissueDisbursement(Integer paymentGroupId, String changeText, UniversalUser user) throws PdpException;

    public void changeImmediateFlag(Integer paymentGroupId, String changeText, UniversalUser user) throws PdpException;
}
