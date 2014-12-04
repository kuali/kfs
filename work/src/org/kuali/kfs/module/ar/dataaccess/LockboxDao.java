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
package org.kuali.kfs.module.ar.dataaccess;

import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.Lockbox;

public interface LockboxDao {

    /**
     * Returns all Lockboxes
     *
     * @return Collection of Lockbox BOs
     */
    public Collection<Lockbox> getAllLockboxes();

    /**
     *
     * Returns the highest (numerically) value for the Lockbox
     * invoiceSequenceNumber.
     *
     * @return The max Lockbox.invoiceSequenceNumber
     */
    public Long getMaxLockboxSequenceNumber();

}
