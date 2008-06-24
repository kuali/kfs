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
package org.kuali.kfs.pdp.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.kuali.kfs.pdp.businessobject.AchBank;
import org.kuali.kfs.pdp.dataaccess.AchBankDao;
import org.kuali.kfs.pdp.service.AchBankService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AchBankServiceImpl implements AchBankService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchBankServiceImpl.class);

    private AchBankDao achBankDao;

    /**
     * @see org.kuali.kfs.pdp.service.AchBankService#save(org.kuali.kfs.pdp.businessobject.AchBank)
     */
    public void save(AchBank ab) {
        LOG.debug("save() started");

        achBankDao.save(ab);
    }

    /**
     * @see org.kuali.kfs.pdp.service.AchBankService#reloadTable(java.lang.String)
     */
    public boolean reloadTable(String filename) {
        LOG.debug("reloadTable() started");

        BufferedReader inputStream = null;

        try {
            inputStream = new BufferedReader(new FileReader(filename));

            String str = null;
            while ((str = inputStream.readLine()) != null) {
                String bankRoutingNumber = getField(str, 1, 9);

                AchBank tableBank = achBankDao.getBank(bankRoutingNumber);
                AchBank ab = new AchBank(str);
                if ( tableBank != null ) {
                    ab.setVersionNumber(tableBank.getVersionNumber());
                }
                achBankDao.save(ab);
            }
        }
        catch (FileNotFoundException fnfe) {
            LOG.error("reloadTable() File Not Found: " + filename, fnfe);
            return false;
        }
        catch (IOException ie) {
            LOG.error("reloadTable() Problem reading file:  " + filename, ie);
            return false;
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ie) {
                    // Not much we can do now
                }
            }
        }
        return true;
    }

    private String getField(String data, int startChar, int length) {
        return data.substring(startChar - 1, startChar + length - 1).trim();
    }

    public void setAchBankDao(AchBankDao abd) {
        achBankDao = abd;
    }
}
