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
package org.kuali.kfs.sys.businessobject;

import java.io.InputStream;
import java.util.List;

import org.kuali.kfs.sys.document.AccountingDocument;

/**
 * Defines an abstraction for parsing serialized <code>AccountingLines</code>
 */
public interface AccountingLineParser {
    /**
     * @return <code>SourceAccountingLine</code> attribute format
     */
    public String[] getSourceAccountingLineFormat();

    /**
     * @return <code>TargetAccountingLine</code> attribute format
     */
    public String[] getTargetAccountingLineFormat();

    /**
     * @param accountingLineClass
     * @return String representation of the <code>String[]</code> attribute format with each attribute seperated by a comma.
     */
    public String getExpectedAccountingLineFormatAsString(Class<? extends AccountingLine> accountingLineClass);

    /**
     * parses a comma deliminated string into an <code>SourceAccountingLine</code> by populating the attributes found in the
     * getSourceAccountingLineFormat()
     * 
     * @param transactionalDocument
     * @param sourceAccountingLineString
     * @return SourceAccountingLine
     */
    public SourceAccountingLine parseSourceAccountingLine(AccountingDocument transactionalDocument, String sourceAccountingLineString);

    /**
     * parses a comma deliminated string into an <code>TargetAccountingLine</code> by populating the attributes found in the
     * getTargetAccountingLineFormat()
     * 
     * @param transactionalDocument
     * @param targetAccountingLineString
     * @return TargetAccountingLine
     */
    public TargetAccountingLine parseTargetAccountingLine(AccountingDocument transactionalDocument, String targetAccountingLineString);

    /**
     * generates a list of <code>SourceAccountingLine</code> from the inputStream
     * 
     * @param stream
     * @param document
     * @return List containing <code>SourceAccountingLine</code>s
     */
    public List importSourceAccountingLines(String fileName, InputStream stream, AccountingDocument document);

    /**
     * generates a list of <code>TargetAccountingLine</code> from the inputStream
     * 
     * @param stream
     * @param document
     * @return List containing <code>SourceAccountingLine</code>s
     */
    public List importTargetAccountingLines(String fileName, InputStream stream, AccountingDocument document);

}
