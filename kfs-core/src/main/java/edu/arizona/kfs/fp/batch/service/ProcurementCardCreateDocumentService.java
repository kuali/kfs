package edu.arizona.kfs.fp.batch.service;

public interface ProcurementCardCreateDocumentService extends org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService {
    
    /**
     * Puts the document back into action list of reconciler
     * @param pcardDocumentId id of the procurement card document
     * @param node document route node
     * @param prevNode previous document route node
     * @param annotation
     */
    public void requeueDocument(String pcardDocumentId, String node, String prevNode, String annotation);
}