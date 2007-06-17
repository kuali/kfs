/*
 * Created on Jun 30, 2004
 *
 */
package org.kuali.module.pdp.service;

import java.util.List;
import java.util.Map;

import org.kuali.module.pdp.bo.Code;
import org.kuali.module.pdp.bo.PdpUser;


/**
 * @author jsissom
 *
 */
public interface ReferenceService {
  public Code getCode(String type,String key);
  public List getAll(String type);
  public Map getallMap(String type);

  public Code addCode(String type,String code,String description,PdpUser u);
  public void updateCode(String code, String description, String type, PdpUser u);
  public void updateCode(Code item,PdpUser u);
  public void deleteCode(Code item);
}
