/*
 * Copyright 2008 The Kuali Foundation
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

