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
package org.kuali.kfs.sys.document;

/**
 * A set of methods that KFS Documents which plan to handle ElectronicPaymentClaim claims should implement
 */
public interface ElectronicPaymentClaiming {
    /**
     * A method which is called when a document is disapproved or canceled, which removes the claim
     * the document had on any ElectronicPaymentClaim records.
     */
    public abstract void declaimElectronicPaymentClaims();
}
