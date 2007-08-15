package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
abstract class AbstractRelatedView extends PersistableBusinessObjectBase {

	private Integer accountsPayablePurchasingDocumentLinkIdentifier;
    private Integer purapDocumentIdentifier;
    private String documentNumber;

    private List<Note> notes;
    
	/**
	 * Gets the accountsPayablePurchasingDocumentLinkIdentifier attribute.
	 *
	 * @return Returns the accountsPayablePurchasingDocumentLinkIdentifier
	 *
	 */
	public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
		return accountsPayablePurchasingDocumentLinkIdentifier;
	}

	/**
	 * Sets the accountsPayablePurchasingDocumentLinkIdentifier attribute.
	 *
	 * @param accountsPayablePurchasingDocumentLinkIdentifier The accountsPayablePurchasingDocumentLinkIdentifier to set.
	 *
	 */
	public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
		this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
	}

	public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    /**
	 * Gets the documentNumber attribute.
	 *
	 * @return Returns the documentNumber
	 *
	 */
	public String getDocumentNumber() {
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 *
	 * @param documentNumber The documentNumber to set.
	 *
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();
        if (this.accountsPayablePurchasingDocumentLinkIdentifier != null) {
            m.put("accountsPayablePurchasingDocumentLinkIdentifier", this.accountsPayablePurchasingDocumentLinkIdentifier.toString());
        }
        return m;
    }
    
    public List<Note> getNotes() {
        if (notes == null) {
            notes = new TypedArrayList(Note.class);
            List<Note> tmpNotes = SpringContext.getBean(NoteService.class, "noteService").getByRemoteObjectId(this.getObjectId());
            for (Note note : tmpNotes) {
                notes.add(note);
            }
        }
        return notes;
    }
    
    public String getUrl() {
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + getDocumentNumber() + "&command=displayDocSearchView";
    }

 }
