/*
 * Created on Jan 19, 2005
 *
 */
package org.kuali.module.pdp.service.impl;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author local-jsissom
 *
 */
@Transactional
public class EnvironmentServiceImpl implements org.kuali.module.pdp.service.EnvironmentService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EnvironmentServiceImpl.class);

  private String environment;
  
  public void setEnvironment(String e) {
    environment = e;
  }

  public EnvironmentServiceImpl() {
    super();
  }

  /* (non-Javadoc)
   * @see edu.iu.uis.pdp.service.EnvironmentService#getEnvironment()
   */
  public String getEnvironment() {
    return environment;
  }
}
