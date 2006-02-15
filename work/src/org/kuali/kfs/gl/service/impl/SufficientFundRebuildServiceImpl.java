package org.kuali.module.gl.service.impl;

import java.util.Collection;

import org.kuali.module.gl.dao.SufficientFundRebuildDao;
import org.kuali.module.gl.service.SufficientFundRebuildService;

public class SufficientFundRebuildServiceImpl implements SufficientFundRebuildService {

    SufficientFundRebuildDao sufficientFundRebuildDao;   
    
    public Collection getAll() {
        return sufficientFundRebuildDao.getAll();
    }
    
    public void setSufficientFundRebuildDao(SufficientFundRebuildDao sufficientFundRebuildDao) {
        this.sufficientFundRebuildDao = sufficientFundRebuildDao;
    }
    
}
