package edu.arizona.kfs.fp.batch.service;

import edu.arizona.kfs.fp.document.ProcurementCardDocument; 

public interface ProcurementCardCreateDocumentService extends org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService {
    
    /**
     * Puts the document back into action list of reconciler
     * @param pcardDocument the procurement card document
     * @param node document route node
     * @param prevNode previous document route node
     * @param annotation
     */
    public void requeueDocument(ProcurementCardDocument pcardDocument, String node, String prevNode, String annotation);
}