/*
 * Created on Sep 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.kuali.module.pdp.service;

import java.util.List;

import org.kuali.module.pdp.bo.DisbursementNumberRange;


/**
 * @author delyea
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface DisbursementNumberRangeService {
  
  public List getAll();
  public DisbursementNumberRange get(Integer id);
  public void save(DisbursementNumberRange dnr);

}
