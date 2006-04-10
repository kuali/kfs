/*
 * Created on Apr 10, 2006
 *
 */
package org.kuali.module.gl.service;

import org.kuali.module.gl.bo.Encumbrance;

public interface EncumbranceService {
  /**
   * Save an Encumbrance entry
   * 
   * @param enc
   */
  public void save(Encumbrance enc);

  /**
   * Purge an entire fiscal year for a single chart.
   * 
   * @param chartOfAccountscode
   * @param year
   */
  public void purgeYearByChart(String chartOfAccountsCode,int year);
}
