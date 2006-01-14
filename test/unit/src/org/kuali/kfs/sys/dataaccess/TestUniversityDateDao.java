/*
 * Created on Oct 28, 2005
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.test.KualiTestBaseWithSpring;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author jsissom
 *
 */
public class TestUniversityDateDao extends KualiTestBaseWithSpring {

  public void testGetByPrimaryKey() throws Exception {
    BeanFactory factory = SpringServiceLocator.getBeanFactory();
    assertNotNull("Factory shouldn't be null",factory);

    UniversityDateDao dao = (UniversityDateDao)factory.getBean("universityDateDao");
    assertNotNull("Dao shouldn't be null",dao);

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Date missing = sdf.parse("01/01/1901");
    UniversityDate notexist = dao.getByPrimaryKey(new java.sql.Date(missing.getTime()));
    assertNull("01/01/1901 shouldn't exist in table",notexist);

    Date notMissing = sdf.parse("08/14/1993");
    UniversityDate exist = dao.getByPrimaryKey(new java.sql.Date(notMissing.getTime()));
    assertNotNull("08/14/1993 should exist in table",exist);
  }
}
