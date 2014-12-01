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
package org.kuali.kfs.fp.document.service;

/**
 * Utility methods used by the transfer of funds document.
 */
public interface TransferOfFundsService {
    /**
     * This method determines whether an object sub-type code is a mandatory transfer or not.
     * 
     * @param objectSubTypeCode
     * @return True if it is a manadatory transfer, false otherwise.
     */
    public abstract boolean isMandatoryTransfersSubType(String objectSubTypeCode);
    
    /**
     * This method determines whether an object sub-type code is a non-mandatory transfer or not.
     * 
     * @param objectSubTypeCode
     * @return True if it is a non-mandatory transfer, false otherwise.
     */
    public abstract boolean isNonMandatoryTransfersSubType(String objectSubTypeCode);
}
