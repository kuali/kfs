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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.rice.kim.api.identity.Person;

/**
 * This interface defines all the necessary methods to define a collector.
 */
public interface ARCollector {

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId();

    /**
     * Sets the principalId attribute.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId);

    /**
     * Gets the customerNumber attribute.
     * 
     * @return Returns the customerNumber
     */
    public String getCustomerNumber();

    /**
     * Sets the customerNumber attribute.
     * 
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber);

    /**
     * Gets the collector attribute.
     * 
     * @return Returns the collector object
     */
    public Person getCollector();

    /**
     * Sets the collector object.
     * 
     * @param collector The collector object to set.
     */
    public void setCollector(Person collector);

}
