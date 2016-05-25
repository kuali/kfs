package edu.arizona.kfs.fp.document;

/**
 * This is the business object that represents the UA modifications for the GeneralErrorCorrectionDocument. This is a transactional document that
 * will eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines:
 * from and to. From lines are the source lines, to lines are the target lines.
 *
 * @author Adam Kost <kosta@email.arizona.edu> with some code adapted from UCI
 */

public class GeneralErrorCorrectionDocument extends org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument {

    private static final long serialVersionUID = 3559591546723165167L;

}
