/*
 * Created on Aug 12, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.List;

import org.kuali.module.pdp.bo.DisbursementNumberRange;


/**
 * @author jsissom
 *
 */
public interface DisbursementNumberRangeDao {

  public List getAll();
  public DisbursementNumberRange get(Integer id);
  public void save(DisbursementNumberRange dnr);

}
