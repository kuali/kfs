/*
 * Created on Sep 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.module.pdp.service.impl;

import java.util.List;

import org.kuali.module.pdp.bo.DisbursementNumberRange;
import org.kuali.module.pdp.dao.DisbursementNumberRangeDao;
import org.kuali.module.pdp.service.DisbursementNumberRangeService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author delyea
 *
 */
@Transactional
public class DisbursementNumberRangeServiceImpl implements DisbursementNumberRangeService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementNumberRangeServiceImpl.class);
  
  private DisbursementNumberRangeDao disbursementNumberRangeDao;

  public void setDisbursementNumberRangeDao(DisbursementNumberRangeDao d) {
    disbursementNumberRangeDao = d;
  }

  public List getAll() {
    return disbursementNumberRangeDao.getAll();
  }

  public DisbursementNumberRange get(Integer id) {
    return disbursementNumberRangeDao.get(id);
  }

  public void save(DisbursementNumberRange dnr) {
    disbursementNumberRangeDao.save(dnr);
  }

}
