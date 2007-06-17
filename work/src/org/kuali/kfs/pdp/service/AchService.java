/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.module.pdp.service;

/**
 * @author jsissom
 *
 */
public interface AchService {
  public AchInformation getAchInformation(String idType,String payeeId,String departmentCode);
}
