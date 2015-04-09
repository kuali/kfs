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
package org.kuali.kfs.gl.batch.service;

import java.io.IOException;
import java.io.Reader;

import org.kuali.kfs.gl.batch.service.impl.ReconciliationBlock;

/**
 * This class parses a reconciliation file
 */
public interface ReconciliationParserService {
    /**
     * Parses a reconciliation file
     * 
     * @param reader a source of data from which to build a reconciliation
     * @param tableId defined within the reconciliation file; defines which block to parse
     * @return parsed reconciliation data
     * @throws IOException thrown if the file cannot be written for any reason
     */
    public ReconciliationBlock parseReconciliationBlock(Reader reader, String tableId) throws IOException;
}
