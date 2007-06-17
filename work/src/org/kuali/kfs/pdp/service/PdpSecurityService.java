/*
 * Created on Sep 22, 2004
 *
 */
package org.kuali.module.pdp.service;

import org.kuali.module.pdp.bo.PdpUser;

/**
 * @author jsissom
 *
 */
public interface PdpSecurityService {
  public SecurityRecord getSecurityRecord(PdpUser user);
}
