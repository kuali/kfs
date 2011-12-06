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
package org.kuali.kfs.sys.document.dataaccess;

import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.dao.DocumentHeaderDao;

/**
 * This class...
 */
public interface FinancialSystemDocumentHeaderDao extends DocumentHeaderDao {

    /**
     * @param id
     * @return documentHeader of the document which corrects the document with the given documentId
     */
    public DocumentHeader getCorrectingDocumentHeader(String documentId);

}
