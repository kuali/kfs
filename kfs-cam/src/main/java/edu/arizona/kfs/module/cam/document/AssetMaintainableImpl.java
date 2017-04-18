package edu.arizona.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.cam.businessobject.AssetExtension;
import edu.arizona.kfs.sys.KFSConstants;

@SuppressWarnings("deprecation")
public class AssetMaintainableImpl extends org.kuali.kfs.module.cam.document.AssetMaintainableImpl {

    private static final long serialVersionUID = 5710219294064590806L;

    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        List<Note> notes = new ArrayList<Note>();
        if (getBusinessObject().getObjectId() != null) {
            NoteService noteService = KRADServiceLocator.getNoteService();
            notes = noteService.getByRemoteObjectId(getBusinessObject().getObjectId());
        }

        setAssetCreateAndUpdateNote(notes, KFSConstants.CreateAndUpdateNotePrefixes.CHANGE);
        document.setNotes(notes);

        super.processAfterEdit(document, parameters);
    }

    @Override
    public void setGenerateDefaultValues(String docTypeName) {
        super.setGenerateDefaultValues(docTypeName);

        if (getBusinessObject().getObjectId() != null) {
            NoteService noteService = KRADServiceLocator.getNoteService();
            List<Note> notes = noteService.getByRemoteObjectId(getBusinessObject().getObjectId());

            if (notes.isEmpty()) {
                Note newBONote = generateNewBoNote(KFSConstants.CreateAndUpdateNotePrefixes.ADD);
                notes.add(newBONote);
            }
        }
    }

    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.setupNewFromExisting(document, parameters);

        List<Note> notes = new ArrayList<Note>();

        if (getBusinessObject().getObjectId() != null) {
            NoteService noteService = KRADServiceLocator.getNoteService();
            notes = noteService.getByRemoteObjectId(getBusinessObject().getObjectId());
        }

        setAssetCreateAndUpdateNote(notes, KFSConstants.CreateAndUpdateNotePrefixes.ADD);

        document.setNotes(notes);
    }

    /**
     * Checks whether the previous note was an "Add" with the same document number as this one
     *
     * @param prefix
     *            String to determine if it is a note "Add" or a note "Change"
     */
    private void setAssetCreateAndUpdateNote(List<Note> notes, String prefix) {
        boolean shouldAddNote = true;

        if (prefix.equals(KFSConstants.CreateAndUpdateNotePrefixes.CHANGE) && (!notes.isEmpty())) {
            // Check whether the previous note was an "Add" with the same document number as this one
            Note previousNote = notes.get(notes.size() - 1);
            if(searchNotesForDocument(notes)&&(previousNote.getNoteText().contains(getDocumentNumber()))) {
                shouldAddNote = false;
            }
        }
        if (shouldAddNote) {
            Note newBONote = generateNewBoNote(prefix);
            notes.add(newBONote);
        }
    }
    
    private boolean searchNotesForDocument(List<Note> notes) {
        for (Note note: notes) {
            if (note.getNoteText().contains(getDocumentNumber())) {
                return true;
            }
        }
        return false;
    }
    
    protected Note generateNewBoNote(String prefix) {
        Note newBoNote = new Note();
        newBoNote.setNoteText(prefix + " vendor document ID " + getDocumentNumber());
        newBoNote.setNotePostedTimestampToCurrent();
        newBoNote = SpringContext.getBean(NoteService.class).createNote(newBoNote, getBusinessObject(), GlobalVariables.getUserSession().getPrincipalId());

        return newBoNote;
    }
    
    @Override
    public void saveDataObject() {
        // Save extension object first for Asset edit since Asset extension referenced collection can't be deleted though overriding BO buildListOfDeletionAwareLists, 
    	// i.e. save Asset BO cannot delete BO reference list in depth.
        if(KRADConstants.MAINTENANCE_EDIT_ACTION.equalsIgnoreCase(getMaintenanceAction())) {
            if (ObjectUtils.isNotNull(this.getBusinessObject().getExtension()) ) {
                AssetExtension assetExt = (AssetExtension)this.getBusinessObject().getExtension();
                getBusinessObjectService().linkAndSave(assetExt);
            }
        }

    	super.saveDataObject();
    }
}
