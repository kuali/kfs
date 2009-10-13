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
package org.kuali.kfs.gl.batch.dataaccess;

import java.util.List;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;

/**
 * This class is used to support the enterprise feeder reconciliation process
 */
public interface ReconciliationDao {
    /**
     * Converts a list of DB column names to a list of java attribute names. The returned list is the same size as arrap parameter
     * 
     * @param clazz a class for the OriginEntryFull class
     * @param columnNames an array of database columns
     * @param caseInsensitive whether to do matching
     * @return for every valid index in the return value and the array, the value in the array is the db column name, and the value
     *         in the list is the java attribute name
     */
    public List<String> convertDBColumnNamesToJavaName(Class<? extends OriginEntryFull> clazz, String[] columnNames, boolean caseInsensitive);
}
