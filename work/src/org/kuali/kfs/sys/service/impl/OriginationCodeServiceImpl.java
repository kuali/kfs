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
package org.kuali.kfs.service.impl;

import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.kfs.dao.OriginationCodeDao;
import org.kuali.kfs.service.OriginationCodeService;
import org.springframework.transaction.annotation.Transactional;

@NonTransactional
public class OriginationCodeServiceImpl implements OriginationCodeService {
    private OriginationCodeDao originationCodeDao;

    public OriginationCodeServiceImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.service.OriginationCodeService#getByPrimaryKey(java.lang.String)
     */
    public OriginationCode getByPrimaryKey(String code) {
        return originationCodeDao.findByCode(code);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.service.OriginationCodeService#setOriginationCodeDao(org.kuali.core.dao.OriginationCodeDao)
     */
    public void setOriginationCodeDao(OriginationCodeDao dao) {
        this.originationCodeDao = dao;
    }
}
