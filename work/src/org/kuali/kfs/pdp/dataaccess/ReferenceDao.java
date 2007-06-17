/*
 * Created on Jul 8, 2004
 *
 */
package org.kuali.module.pdp.dao;

import java.util.List;
import java.util.Map;

import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.module.pdp.bo.Code;
import org.kuali.module.pdp.bo.PdpUser;


/**
 * @author jsissom
 *
 */
public interface ReferenceDao {
  public Code getCode(String type,String key);
  public List getAll(String type);
  public Map getAllMap(String type);

  public Code addCode(String type,String code,String description,PdpUser u);
  public void updateCode(String code, String description, String type, PdpUser u);
  public void updateCode(Code item,PdpUser u);
  public void deleteCode(Code item);
}
