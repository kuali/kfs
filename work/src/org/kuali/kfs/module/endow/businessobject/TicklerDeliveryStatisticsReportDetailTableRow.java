/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
