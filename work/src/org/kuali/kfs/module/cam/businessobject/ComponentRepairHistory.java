package org.kuali.module.cams.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ComponentRepairHistory extends PersistableBusinessObjectBase {

	private Long capitalAssetNumber;
	private Integer componentNumber;
	private Date componentIncidentDate;
	private Date componentEstimatedRepairDate;
	private String componentProblemDescription;
	private KualiDecimal componentRepairAmount;
	private String componentRepairContactName;
	private Date componentRepairDate;
	private String componentRepairNoteText;
	private String componentRepairSolutionDescription;

    private AssetComponent component;

	/**
	 * Default constructor.
	 */
	public ComponentRepairHistory() {

	}

	/**
	 * Gets the capitalAssetNumber attribute.
	 * 
	 * @return Returns the capitalAssetNumber
	 * 
	 */
	public Long getCapitalAssetNumber() { 
		return capitalAssetNumber;
	}

	/**
	 * Sets the capitalAssetNumber attribute.
	 * 
	 * @param capitalAssetNumber The capitalAssetNumber to set.
	 * 
	 */
	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}


	/**
	 * Gets the componentNumber attribute.
	 * 
	 * @return Returns the componentNumber
	 * 
	 */
	public Integer getComponentNumber() { 
		return componentNumber;
	}

	/**
	 * Sets the componentNumber attribute.
	 * 
	 * @param componentNumber The componentNumber to set.
	 * 
	 */
	public void setComponentNumber(Integer componentNumber) {
		this.componentNumber = componentNumber;
	}


	/**
	 * Gets the componentIncidentDate attribute.
	 * 
	 * @return Returns the componentIncidentDate
	 * 
	 */
	public Date getComponentIncidentDate() { 
		return componentIncidentDate;
	}

	/**
	 * Sets the componentIncidentDate attribute.
	 * 
	 * @param componentIncidentDate The componentIncidentDate to set.
	 * 
	 */
	public void setComponentIncidentDate(Date componentIncidentDate) {
		this.componentIncidentDate = componentIncidentDate;
	}


	/**
	 * Gets the componentEstimatedRepairDate attribute.
	 * 
	 * @return Returns the componentEstimatedRepairDate
	 * 
	 */
	public Date getComponentEstimatedRepairDate() { 
		return componentEstimatedRepairDate;
	}

	/**
	 * Sets the componentEstimatedRepairDate attribute.
	 * 
	 * @param componentEstimatedRepairDate The componentEstimatedRepairDate to set.
	 * 
	 */
	public void setComponentEstimatedRepairDate(Date componentEstimatedRepairDate) {
		this.componentEstimatedRepairDate = componentEstimatedRepairDate;
	}


	/**
	 * Gets the componentProblemDescription attribute.
	 * 
	 * @return Returns the componentProblemDescription
	 * 
	 */
	public String getComponentProblemDescription() { 
		return componentProblemDescription;
	}

	/**
	 * Sets the componentProblemDescription attribute.
	 * 
	 * @param componentProblemDescription The componentProblemDescription to set.
	 * 
	 */
	public void setComponentProblemDescription(String componentProblemDescription) {
		this.componentProblemDescription = componentProblemDescription;
	}


	/**
	 * Gets the componentRepairAmount attribute.
	 * 
	 * @return Returns the componentRepairAmount
	 * 
	 */
	public KualiDecimal getComponentRepairAmount() { 
		return componentRepairAmount;
	}

	/**
	 * Sets the componentRepairAmount attribute.
	 * 
	 * @param componentRepairAmount The componentRepairAmount to set.
	 * 
	 */
	public void setComponentRepairAmount(KualiDecimal componentRepairAmount) {
		this.componentRepairAmount = componentRepairAmount;
	}


	/**
	 * Gets the componentRepairContactName attribute.
	 * 
	 * @return Returns the componentRepairContactName
	 * 
	 */
	public String getComponentRepairContactName() { 
		return componentRepairContactName;
	}

	/**
	 * Sets the componentRepairContactName attribute.
	 * 
	 * @param componentRepairContactName The componentRepairContactName to set.
	 * 
	 */
	public void setComponentRepairContactName(String componentRepairContactName) {
		this.componentRepairContactName = componentRepairContactName;
	}


	/**
	 * Gets the componentRepairDate attribute.
	 * 
	 * @return Returns the componentRepairDate
	 * 
	 */
	public Date getComponentRepairDate() { 
		return componentRepairDate;
	}

	/**
	 * Sets the componentRepairDate attribute.
	 * 
	 * @param componentRepairDate The componentRepairDate to set.
	 * 
	 */
	public void setComponentRepairDate(Date componentRepairDate) {
		this.componentRepairDate = componentRepairDate;
	}


	/**
	 * Gets the componentRepairNoteText attribute.
	 * 
	 * @return Returns the componentRepairNoteText
	 * 
	 */
	public String getComponentRepairNoteText() { 
		return componentRepairNoteText;
	}

	/**
	 * Sets the componentRepairNoteText attribute.
	 * 
	 * @param componentRepairNoteText The componentRepairNoteText to set.
	 * 
	 */
	public void setComponentRepairNoteText(String componentRepairNoteText) {
		this.componentRepairNoteText = componentRepairNoteText;
	}


	/**
	 * Gets the componentRepairSolutionDescription attribute.
	 * 
	 * @return Returns the componentRepairSolutionDescription
	 * 
	 */
	public String getComponentRepairSolutionDescription() { 
		return componentRepairSolutionDescription;
	}

	/**
	 * Sets the componentRepairSolutionDescription attribute.
	 * 
	 * @param componentRepairSolutionDescription The componentRepairSolutionDescription to set.
	 * 
	 */
	public void setComponentRepairSolutionDescription(String componentRepairSolutionDescription) {
		this.componentRepairSolutionDescription = componentRepairSolutionDescription;
	}


	/**
	 * Gets the component attribute.
	 * 
	 * @return Returns the component
	 * 
	 */
	public AssetComponent getComponent() { 
		return component;
	}

	/**
	 * Sets the component attribute.
	 * 
	 * @param component The component to set.
	 * @deprecated
	 */
	public void setComponent(AssetComponent component) {
		this.component = component;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
        if (this.componentNumber != null) {
            m.put("componentNumber", this.componentNumber.toString());
        }
        if (this.componentIncidentDate != null) {
            m.put("componentIncidentDate", this.componentIncidentDate.toString());
        }
	    return m;
    }
}
