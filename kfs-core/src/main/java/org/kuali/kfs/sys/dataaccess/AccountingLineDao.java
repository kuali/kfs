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
package org.kuali.kfs.sys.dataaccess;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.businessobject.AccountingLine;

/**
 * The data access interface for persisting AccountingLineBase objects.
 */
public interface AccountingLineDao {

    /**
     * Deletes an accounting line from the DB.
     *
     * @param line
     */
    void deleteAccountingLine(AccountingLine line);

    /**
     * Retrieves a list of accounting lines (by class type) associated with a given document.
     *
     * @param clazz
     * @param id
     * @return
     */
    public ArrayList findByDocumentHeaderId(Class clazz, String id);

    /**
     * Retrieves a list of accounting lines associated with a given document by the line type
     *
     * @param clazz the class of the accounting line to retrieve
     * @param id the document number
     * @param lineType the financial line type code of the accounting lines to retrieve
     * @return a List of matching accounting lines
     */
    public List findByDocumentHeaderIdAndLineType(Class clazz, String id, String lineType);
}
