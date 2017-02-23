package edu.arizona.kfs.module.tax.document;

import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.module.tax.businessobject.Payment;

@SuppressWarnings({ "deprecation", "unused" })
public class PaymentMaintainableImpl extends FinancialSystemMaintainable {
    private static final long serialVersionUID = 1854397259745682785L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentMaintainableImpl.class);

    @Override
    public void setGenerateDefaultValues(String docTypeName) {
        super.setGenerateDefaultValues(docTypeName);
        if (((Payment) this.getBusinessObject()).getBoNotes().isEmpty()) {
            setPaymentCreateAndUpdateNote(TaxConstants.TaxCreateAndUpdateNotePrefixes.ADD);
        }
    }

    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        setPaymentCreateAndUpdateNote(TaxConstants.TaxCreateAndUpdateNotePrefixes.CHANGE);
        super.processAfterEdit(document, parameters);
    }

    /**
     * Checks whether the previous note was an "Add" with the same document number as this one
     *
     * @param prefix
     *            String to determine if it is a note "Add" or a note "Change"
     */
    private void setPaymentCreateAndUpdateNote(String prefix) {
        boolean shouldAddNote = true;
        if (prefix.equals(TaxConstants.TaxCreateAndUpdateNotePrefixes.CHANGE)) {
            // Check whether the previous note was an "Add" with the same document number as this one
            if (!((Payment) this.getBusinessObject()).getBoNotes().isEmpty()) {
                Note previousNote = ((Payment) this.getBusinessObject()).getBoNotes().get(((Payment) this.getBusinessObject()).getBoNotes().size() - 1);
                if (previousNote.getNoteText().contains(this.getDocumentNumber())) {
                    shouldAddNote = false;
                }
            }
        }
        if (shouldAddNote) {
            Note newBONote = new Note();
            newBONote.setNoteText(prefix + " 1099 payment document ID " + this.getDocumentNumber());
            try {
                newBONote = SpringContext.getBean(NoteService.class).createNote(newBONote, this.getBusinessObject(), GlobalVariables.getUserSession().getPrincipalId());
            } catch (Exception e) {
                throw new RuntimeException("Caught Exception While Trying To Add Note to 1099 Payment", e);
            }
            ((Payment) this.getBusinessObject()).getBoNotes().add(newBONote);
        }
    }

    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.setupNewFromExisting(document, parameters);
        setPaymentCreateAndUpdateNote(TaxConstants.TaxCreateAndUpdateNotePrefixes.ADD);
    }
}
