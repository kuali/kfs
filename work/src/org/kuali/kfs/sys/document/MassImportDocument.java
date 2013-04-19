/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.document;

import java.util.List;

import org.kuali.kfs.sys.businessobject.MassImportLineBase;

/**
 * This class is the common interface mass upload Documents
 */
public interface MassImportDocument extends FinancialSystemTransactionalDocument {
    /**
     * Gets the import collection attribute.
     *
     * @return Returns the subAccountImportDetails.
     */
    public List getImportDetailCollection();

    /**
     * Get the imported line clasee
     *
     * @return
     */
    public Class getImportLineClass();

    /**
     * Returns the list of fields in the order expected in the file
     *
     * @return
     */
    public String[] getOrderedFieldList();

    /**
     * Set additional customized property values
     *
     * @param importedLines
     */
    public void customizeImportedLines(List<MassImportLineBase> importedLines);

    /**
     * Get the error path prefix which should append to document
     *
     * @return
     */
    public String getErrorPathPrefix();

    /**
     * Get the fullerror path prefix which should append to document
     *
     * @return
     */
    public String getFullErrorPathPrefix();

    public Integer getNextItemLineNumber();

    public void setNextItemLineNumber(Integer nextItemLineNumber);

}
