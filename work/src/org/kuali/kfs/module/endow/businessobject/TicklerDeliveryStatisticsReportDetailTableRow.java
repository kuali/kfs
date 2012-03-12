/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class TicklerDeliveryStatisticsReportDetailTableRow extends TransientBusinessObjectBase {

    private long ticklerDeliveryNotifications;
    private long numberOfExceptions;

    public TicklerDeliveryStatisticsReportDetailTableRow(){
        ticklerDeliveryNotifications = 0;
        numberOfExceptions = 0;
    }
    
    public long getTicklerDeliveryNotifications() {
        return ticklerDeliveryNotifications;
    }

    public void setTicklerDeliveryNotifications(long ticklerDeliveryNotifications) {
        this.ticklerDeliveryNotifications = ticklerDeliveryNotifications;
    }

    public long getNumberOfExceptions() {
        return numberOfExceptions;
    }

    public void setNumberOfExceptions(long numberOfExceptions) {
        this.numberOfExceptions = numberOfExceptions;
    }

    /**
     * method to increase the count of number of tickler delivery notifications
     */
    public void increaseTicklerDeliveryNotificationsCount() {
        this.ticklerDeliveryNotifications++;
    }

    /**
     * method to increase the count of number of exceptions
     */
    public void increaseNumberOfExceptionsCount() {
        this.numberOfExceptions++;
    }
    
    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();        
        pks.put("ticklerDeliveryNotifications",this.getTicklerDeliveryNotifications());
        pks.put("numberOfExceptions",this.getNumberOfExceptions());
        
        return pks;
    }

}
