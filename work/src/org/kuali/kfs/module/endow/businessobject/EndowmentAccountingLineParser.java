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

import org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument;

public interface EndowmentAccountingLineParser {

    /**
     * @return <code>SourceEndowmentAccountingLine</code> attribute format
     */
    public String[] getSourceEndowmentAccountingLineFormat();

    /**
     * @return <code>TargetEndowmentAccountingLine</code> attribute format
     */
    public String[] getTargetEndowmentAccountingLineFormat();

    /**
     * @param accountingLineClass
     * @return String representation of the <code>String[]</code> attribute format with each attribute separated by a comma.
     */
    public String getExpectedEndowmentAccountingLineFormatAsString(Class<? extends EndowmentAccountingLine> accountingLineClass);

    /**
     * Parses a comma delimited string into an <code>SourceEndowmentAccountingLine</code> by populating the attributes found in the
     * getSourceAccountingLineFormat()
     * 
     * @param transactionalDocument
     * @param sourceAccountingLineString
     * @return SourceEndowmentAccountingLine
     */
    public SourceEndowmentAccountingLine parseSourceEndowmentAccountingLine(EndowmentAccountingLinesDocument transactionalDocument, String sourceAccountingLineString);

    /**
     * Parses a comma delimited string into an <code>TargetEndowmentAccountingLine</code> by populating the attributes found in the
     * getTargetAccountingLineFormat()
     * 
     * @param transactionalDocument
     * @param targetAccountingLineString
     * @return TargetEndowmentAccountingLine
     */
    public TargetEndowmentAccountingLine parseTargetEndowmentAccountingLine(EndowmentAccountingLinesDocument transactionalDocument, String targetAccountingLineString);

    /**
     * Generates a list of <code>SourceEndowmentAccountingLine</code> from the inputStream
     * 
     * @param stream
     * @param document
     * @return List containing <code>SourceEndowmentAccountingLine</code>s
     */
    public List<SourceEndowmentAccountingLine> importSourceEndowmentAccountingLines(String fileName, InputStream stream, EndowmentAccountingLinesDocument document);

    /**
     * Generates a list of <code>TargetEndowmentAccountingLine</code> from the inputStream
     * 
     * @param stream
     * @param document
     * @return List containing <code>TargetEndowmentAccountingLine</code>s
     */
    public List<TargetEndowmentAccountingLine> importTargetEndowmentAccountingLines(String fileName, InputStream stream, EndowmentAccountingLinesDocument document);

}
