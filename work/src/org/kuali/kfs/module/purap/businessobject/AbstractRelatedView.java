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
 * Base class for Related View Business Objects.
 */
abstract class AbstractRelatedView extends PersistableBusinessObjectBase {

	private Integer accountsPayablePurchasingDocumentLinkIdentifier;
    private Integer purapDocumentIdentifier;
    private String documentNumber;

    private List<Note> notes;
    
	public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
		return accountsPayablePurchasingDocumentLinkIdentifier;
	}

	public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
		this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
	}

	public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

    public List<Note> getNotes() {
        if (notes == null) {
            notes = new TypedArrayList(Note.class);
            List<Note> tmpNotes = SpringContext.getBean(NoteService.class).getByRemoteObjectId(this.getObjectId());
            for (Note note : tmpNotes) {
                notes.add(note);
            }
        }
        return notes;
    }
    
    public String getUrl() {
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + getDocumentNumber() + "&command=displayDocSearchView";
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
 }
