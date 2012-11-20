/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch.service;

import java.io.Reader;
import java.util.List;

import org.kuali.kfs.module.tem.batch.businessobject.PerDiemForLoad;

public interface PerDiemFileParsingService {

    /**
     * build per diems from the given csv file
     * 
     * @param fileName the given csv file
     * @param deliminator the field deliminator of the flat file
     * @param fieldsToPopulate the name list of fields to be populated from the data in file
     * @return a list of per diems built from the given csv file
     */
    public List<PerDiemForLoad> buildPerDiemsFromFlatFile(String fileName, String deliminator, List<String> fieldsToPopulate);

    /**
     * build per diems from the given csv file
     * 
     * @param reader the given reader
     * @param deliminator the field deliminator of the flat file
     * @param fieldsToPopulate the name list of fields to be populated from the data in file
     * @return a list of per diems built from the given csv file
     */
    public List<PerDiemForLoad> buildPerDiemsFromFlatFile(Reader reader, String deliminator, List<String> fieldsToPopulate);
}
