package edu.arizona.kfs.rq.batch.dataaccess;

import org.kuali.rice.kew.api.document.Document;

import java.util.List;

/**
 * UAF-475: MOD-WKFLW-03 Requeue Stuck Documents Job
 *
 * @author Josh Shaloo <shaloo@email.arizona.edu>
 */
public interface StuckDocumentsDAO {
    List<Document> getStuckDocuments();
}
