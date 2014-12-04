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

import java.io.InputStream;
import java.util.List;

import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;

// Bonnie: refer to AccountingLineParser
/**
 * Defines an abstraction for parsing serialized <code>EndowmentTransactionLine</code>
 */
public interface EndowmentTransactionLineParser {
    /**
     * @return EndowmentSourceTransactionLine attribute format
     */
    public String[] getSourceTransactionLineFormat();

    /**
     * @return EndowmentTargetTransactionLine attribute format
     */
    public String[] getTargetTransactionLineFormat();

    /**
     * @param transactionLineClass
     * @return String representation of the <code>String[]</code> attribute format with each attribute separated by a comma.
     */
    public String getExpectedTransactionLineFormatAsString(Class<? extends EndowmentTransactionLine> transactionLineClass);

    /**
     * parses a comma deliminated string into an <code>EndowmentSourceTransactionLine</code> by populating the attributes found in the
     * getSourceTransactionLineFormat()
     * 
     * @param transactionalDocument
     * @param sourceTransactionLineString
     * @return EndowmentSourceTransactionLine
     */
//    public EndowmentSourceTransactionLine parseSourceTransactionLine(EndowmentTransactionLinesDocument transactionalDocument, String sourceTransactionLineString);

    /**
     * parses a comma deliminated string into an <code>EndowmentTargetTransactionLine</code> by populating the attributes found in the
     * getTargetTransactionLineFormat()
     * 
     * @param transactionalDocument
     * @param targetTransactionLineString
     * @return EndowmentTargetTransactionLine
     */
//    public EndowmentTargetTransactionLine parseTargetTransactionLine(EndowmentTransactionLinesDocument transactionalDocument, String targetTransactionLineString);

    /**
     * generates a list of EndowmentSourceTransactionLine from the inputStream
     * 
     * @param stream
     * @param document
     * @return List containing EndowmentSourceTransactionLine
     */
    public List importEndowmentSourceTransactionLines(String fileName, InputStream stream, EndowmentTransactionLinesDocument document);

    /**
     * generates a list of EndowmentTargetTransactionLine from the inputStream
     * 
     * @param stream
     * @param document
     * @return List containing EndowmentTargetTransactionLine
     */
    public List importEndowmentTargetTransactionLines(String fileName, InputStream stream, EndowmentTransactionLinesDocument document);

}
