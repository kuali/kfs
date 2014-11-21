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
package org.kuali.kfs.sys.service;

import java.util.List;

/**
 * This interface defines methods that an AccountingLine service implementation must provide.
 */
public interface AccountingLineService {
    /**
     * Retrieves a list of accounting lines for a given group (i.e. Target/Source) given the associated document id.
     *
     * @param clazz
     * @param documentHeaderId
     * @return A list of AccountingLines... to be casted to the appropriate class.
     * @throws Exception
     */
    public List getByDocumentHeaderId(Class clazz, String documentHeaderId);

    /**
     * Retrieves a list of accounting lines for a document, specified by the given document id, of a given line type
     * @param clazz the class of the AccountingLine to return
     * @param documentHeaderId the document number of the document the accounting lines are associated with
     * @param lineType the line type code of the accounting lines to retrieve
     * @return a List of matching accounting lines
     */
    public List getByDocumentHeaderIdAndLineType(Class clazz, String documentHeaderId, String lineType);

}
