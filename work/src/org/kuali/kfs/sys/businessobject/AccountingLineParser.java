/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.bo;

import java.io.InputStream;
import java.util.List;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.kfs.document.AccountingDocument;

/**
 * Defines an abstraction for parsing serialized <code>AccountingLines</code>
 */
public interface AccountingLineParser {
    /**
     * 
     * @return <code>SourceAccountingLine</code> attribute format
     */
    public String[] getSourceAccountingLineFormat();

    /**
     * 
     * @return <code>TargetAccountingLine</code> attribute format
     */
    public String[] getTargetAccountingLineFormat();

    /**
     * 
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
