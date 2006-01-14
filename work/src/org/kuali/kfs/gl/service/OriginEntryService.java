/*
 * Created on Nov 28, 2005
 *
 */
package org.kuali.module.gl.service;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;

/**
 * @author jsissom
 *
 */
public interface OriginEntryService {
  public Collection getGroupsToPost(Date postDate);
  public Collection getIcrGroupsToPost(Date postDate);
  public Collection getGroupsToScrub(Date scrubDate);

  public Iterator getEntriesByGroup(OriginEntryGroup oeg);

  public OriginEntryGroup createGroup(Date date,String sourceCode,boolean valid,boolean processed,boolean scrub);

  public void createEntry(Transaction tran,OriginEntryGroup group);
}
