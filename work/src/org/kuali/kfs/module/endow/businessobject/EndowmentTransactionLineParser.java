/*
 * Copyright 2010 The Kuali Foundation.
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
     * @return <code>SourceTransactionLine</code> attribute format
     */
    public String[] getSourceTransactionLineFormat();

    /**
     * @return <code>TargetTransactionLine</code> attribute format
     */
    public String[] getTargetTransactionLineFormat();

    /**
     * @param transactionLineClass
     * @return String representation of the <code>String[]</code> attribute format with each attribute separated by a comma.
     */
    public String getExpectedTransactionLineFormatAsString(Class<? extends EndowmentTransactionLine> transactionLineClass);

    /**
     * parses a comma deliminated string into an <code>SourceAccountingLine</code> by populating the attributes found in the
     * getSourceAccountingLineFormat()
     * 
     * @param transactionalDocument
     * @param sourceTransactionLineString
     * @return EndowmentSourceTransactionLine
     */
    public EndowmentSourceTransactionLine parseSourceTransactionLine(EndowmentTransactionLinesDocument transactionalDocument, String sourceTransactionLineString);

    /**
     * parses a comma deliminated string into an <code>TargetAccountingLine</code> by populating the attributes found in the
     * getTargetAccountingLineFormat()
     * 
     * @param transactionalDocument
     * @param targetTransactionLineString
     * @return EndowmentTargetTransactionLine
     */
    public EndowmentTargetTransactionLine parseTargetAccountingLine(EndowmentTransactionLinesDocument transactionalDocument, String targetTransactionLineString);

    /**
     * generates a list of <code>SourceTransactionLine</code> from the inputStream
     * 
     * @param stream
     * @param document
     * @return List containing <code>EndowmentSourceTransactionLine</code>s
     */
    public List importEndowmentSourceTransactionLines(String fileName, InputStream stream, EndowmentTransactionLinesDocument document);

    /**
     * generates a list of <code>EndowmentTargetTransactionLine</code> from the inputStream
     * 
     * @param stream
     * @param document
     * @return List containing <code>EndowmentTargetTransactionLine</code>s
     */
    public List importEndowmentTargetTransactionLines(String fileName, InputStream stream, EndowmentTransactionLinesDocument document);

}
