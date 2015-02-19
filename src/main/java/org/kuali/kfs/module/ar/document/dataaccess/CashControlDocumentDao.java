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
package org.kuali.kfs.module.ar.document.dataaccess;

import java.util.Map;

import org.kuali.kfs.module.ar.document.CashControlDocument;

/**
 * Data Access Object for Cash Control Document.
 */
public interface CashControlDocumentDao {

    /**
     * Retrieves UNFINAL cash control document by criteria.
     *
     * @param criteria used to retrieve the cash control document
     * @return Cash Control Document
     */
    public CashControlDocument getCashControlDocument(Map fieldValues);
}
