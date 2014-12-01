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
package org.kuali.kfs.module.endow.util;

import java.util.List;

import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;

/**
 * Defines an abstraction for parsing serialized <code>PurApItem</code> lines.
 */
public interface LineParser {

    /**
     * Parses the items from the specified import file line by line,
     * and generates items of the specified type from the parsed data.
     * 
     * @param itemFile the input file from which items are parsed
     * @param itemClass a subclass of PurApItem, of which new items shall be generated
     * @param documentNumber the number of the docment that contains the items to be imported
     * @return a list of items of a subclass of PurApItem.
     */
    public List<EndowmentTransactionLine> importLines( FormFile lineFile, Class<? extends EndowmentTransactionLine> lineClass, String documentNumber ) throws Exception;

}

