/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
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
