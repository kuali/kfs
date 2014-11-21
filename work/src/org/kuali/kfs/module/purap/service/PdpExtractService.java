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
package org.kuali.kfs.module.purap.service;

import java.util.Date;

/**
 * Defines methods that must be implemented by a PdpExtractService implementation.
 */
public interface PdpExtractService {

    /**
     * Extract all payments marked immediate. This won't combine any payments with credit memos.
     */
    public void extractImmediatePaymentsOnly();

    /**
     * Extract all payments ready to be paid. This may combine payments with appropriate credit memos
     * 
     * @param runDate the date to assume when the process (e.g. a batch Step or Job) was started to extract payments 
     */
    public void extractPayments(Date runDate);

}
