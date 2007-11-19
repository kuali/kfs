/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.dao.ojb;

import java.sql.Date;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.dao.CorrectionDocumentDao;
import org.kuali.module.gl.document.CorrectionDocument;

/**
 * The OJB implementation of CorrectionDocumentDao
 */
public class CorrectionDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements CorrectionDocumentDao {

    /**
     * Queries the database to get a Collection of GLCP documents finalized on the given date
     * 
     * @param date the finalization date of GLCP documents to find
     * @return a Collection of GLCP documents
     */
    public Collection<CorrectionDocument> getCorrectionDocumentsFinalizedOn(Date documentFinalDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSPropertyConstants.DOCUMENT_FINAL_DATE, documentFinalDate);
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(CorrectionDocument.class, criteria));
    }
}
