/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.module.pdp.service.impl;

import java.util.List;

import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.BatchSearch;
import org.kuali.module.pdp.dao.BatchSearchDao;
import org.kuali.module.pdp.service.BatchSearchService;
import org.kuali.module.pdp.utilities.GeneralUtilities;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author delyea
 */
@Transactional
public class BatchSearchServiceImpl implements BatchSearchService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchSearchServiceImpl.class);

    private ParameterService parameterService;
    private BatchSearchDao batchSearchDao;

    public void setBatchSearchDao(BatchSearchDao b) {
        batchSearchDao = b;
    }

    public List getAllBatchesForSearchCriteria(BatchSearch bs) {
        return batchSearchDao.getAllBatchesForSearchCriteria(bs, getMaxSearchTotal());
    }

    public List getAllSingleBatchPayments(Integer id) {
        return batchSearchDao.getAllSingleBatchPayments(id);
    }

    private int getMaxSearchTotal() {
        return GeneralUtilities.getParameterInteger(parameterService, ParameterConstants.PRE_DISBURSEMENT_LOOKUP.class, PdpConstants.ApplicationParameterKeys.SEARCH_RESULTS_TOTAL);
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
