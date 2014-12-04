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
