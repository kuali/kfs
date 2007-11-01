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
/*
 * Created on Jul 8, 2004
 *
 */
package org.kuali.module.pdp.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.module.pdp.bo.Code;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.dao.ReferenceDao;
import org.kuali.module.pdp.service.ReferenceService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author jsissom
 */
@Transactional
public class ReferenceServiceImpl implements ReferenceService {

    private ReferenceDao referenceDao;

    public ReferenceServiceImpl() {
        super();
    }

    public Code getCode(String type, String key) {
        return referenceDao.getCode(type, key);
    }

    public Map getallMap(String type) {
        return referenceDao.getAllMap(type);
    }

    public List getAll(String type) {
        return referenceDao.getAll(type);
    }

    public Code addCode(String type, String code, String description, PdpUser u) {
        return referenceDao.addCode(type, code, description, u);
    }

    public void updateCode(String code, String description, String type, PdpUser u) {
        referenceDao.updateCode(code, description, type, u);
    }

    public void updateCode(Code item, PdpUser u) {
        referenceDao.updateCode(item, u);
    }

    public void deleteCode(Code item) {
        referenceDao.deleteCode(item);
    }

    public void setReferenceDao(ReferenceDao r) {
        this.referenceDao = r;
    }
}
