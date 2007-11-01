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
 * Created on Jul 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.module.pdp.service.impl;

import java.util.List;

import org.kuali.module.pdp.bo.Bank;
import org.kuali.module.pdp.dao.BankDao;
import org.kuali.module.pdp.service.BankService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author delyea
 */
@Transactional
public class BankServiceImpl implements BankService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankServiceImpl.class);

    private BankDao bankDao;

    public void setBankDao(BankDao c) {
        bankDao = c;
    }

    public List getAll() {
        return bankDao.getAll();
    }

    public Bank get(Integer bankId) {
        return bankDao.get(bankId);
    }

    public List getAllBanksForDisbursementType(String type) {
        return bankDao.getAllBanksForDisbursementType(type);
    }

    public void save(Bank b) {
        bankDao.save(b);
    }

}
