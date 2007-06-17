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
 *
 */
@Transactional
public class ReferenceServiceImpl implements ReferenceService {

  private ReferenceDao referenceDao;

  public ReferenceServiceImpl() {
    super();
  }

  public Code getCode(String type, String key) {
    return referenceDao.getCode(type,key);
  }

  public Map getallMap(String type) {
    return referenceDao.getAllMap(type);
  }

  public List getAll(String type) {
    return referenceDao.getAll(type);
  }

  public Code addCode(String type, String code, String description, PdpUser u) {
    return referenceDao.addCode(type,code,description,u);
  }

  public void updateCode(String code, String description, String type, PdpUser u) {
    referenceDao.updateCode(code,description,type,u);
  }
  
  public void updateCode(Code item,PdpUser u) {
    referenceDao.updateCode(item,u);
  }

  public void deleteCode(Code item) {
    referenceDao.deleteCode(item);
  }

  public void setReferenceDao(ReferenceDao r) {
    this.referenceDao = r;
  }
}
