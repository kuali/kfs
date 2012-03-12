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
package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.dataaccess.OriginationCodeDao;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OriginationCodeService;

@NonTransactional
public class OriginationCodeServiceImpl implements OriginationCodeService {
    private OriginationCodeDao originationCodeDao;

    public OriginationCodeServiceImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.krad.service.OriginationCodeService#getByPrimaryKey(java.lang.String)
     */
    public OriginationCode getByPrimaryKey(String code) {
        return originationCodeDao.findByCode(code);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.krad.service.OriginationCodeService#setOriginationCodeDao(org.kuali.rice.krad.dao.OriginationCodeDao)
     */
    public void setOriginationCodeDao(OriginationCodeDao dao) {
        this.originationCodeDao = dao;
    }
}
