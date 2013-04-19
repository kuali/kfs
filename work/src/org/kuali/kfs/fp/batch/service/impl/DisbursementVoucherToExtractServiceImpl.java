/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.fp.batch.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.dataaccess.DisbursementVoucherDao;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.document.PaymentSource;

/**
 * Service to retrieve disbursement vouchers for extraction
 */
public class DisbursementVoucherToExtractServiceImpl implements PaymentSourceToExtractService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherToExtractServiceImpl.class);

    private DisbursementVoucherDao disbursementVoucherDao;

    /**
     * Retrieve disbursement vouchers for extraction
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#retrievePaymentSourcesByCampus(boolean)
     */
    @Override
    public Map<String, List<? extends PaymentSource>> retrievePaymentSourcesByCampus(boolean immediatesOnly) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrievePaymentSourcesByCampus() started");
        }

        if (immediatesOnly) {
            throw new UnsupportedOperationException("DisbursementVoucher PDP does immediates extraction through normal document processing; immediates for DisbursementVoucher should not be run through batch.");
        }

        Map<String, List<? extends PaymentSource>> documentsByCampus = new HashMap<String, List<? extends PaymentSource>>();

        Collection<DisbursementVoucherDocument> docs = disbursementVoucherDao.getDocumentsByHeaderStatus(DisbursementVoucherConstants.DocumentStatusCodes.APPROVED, false);
        for (DisbursementVoucherDocument element : docs) {
            String dvdCampusCode = element.getCampusCode();
            if (StringUtils.isNotBlank(dvdCampusCode)) {
                if (documentsByCampus.containsKey(dvdCampusCode)) {
                    List<DisbursementVoucherDocument> documents = (List<DisbursementVoucherDocument>)documentsByCampus.get(dvdCampusCode);
                    documents.add(element);
                }
                else {
                    List<DisbursementVoucherDocument> documents = new ArrayList<DisbursementVoucherDocument>();
                    documents.add(element);
                    documentsByCampus.put(dvdCampusCode, documents);
                }
            }
        }

        return documentsByCampus;
    }

    /**
     * This method sets the disbursementVoucherDao instance.
     *
     * @param disbursementVoucherDao The DisbursementVoucherDao to be set.
     */
    public void setDisbursementVoucherDao(DisbursementVoucherDao disbursementVoucherDao) {
        this.disbursementVoucherDao = disbursementVoucherDao;
    }

}
