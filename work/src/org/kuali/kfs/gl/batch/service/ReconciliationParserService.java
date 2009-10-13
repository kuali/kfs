/*
 * Copyright 2007 The Kuali Foundation
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
