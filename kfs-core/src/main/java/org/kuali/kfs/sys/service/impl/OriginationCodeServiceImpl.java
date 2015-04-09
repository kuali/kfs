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
