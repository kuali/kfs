package edu.arizona.kfs.module.ec.document.web.struts;

import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Action form for Effort Certification Document.
 */
public class EffortCertificationForm extends org.kuali.kfs.module.ec.document.web.struts.EffortCertificationForm {
	private static final String EFFORT_CERT_CALC_BUTTON_TEXT = "Calculate";
	private static final String EFFORT_CERT_CALC_REQUEST = ".calculate";
	private static final String EFFORT_CERT_CALC_BUTTON_FILE = "buttonsmall_calculate.gif";
	 
	@Override
    public List<ExtraButton> getExtraButtons() {
		extraButtons.clear();
		
        String appExternalImageURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
        addExtraButton(KFSConstants.DISPATCH_REQUEST_PARAMETER + EFFORT_CERT_CALC_REQUEST, appExternalImageURL + EFFORT_CERT_CALC_BUTTON_FILE, EFFORT_CERT_CALC_BUTTON_TEXT);
       
        return extraButtons;
    }
	
	/**
	 * This method adds a new button to the extra buttons list.
	 * @param property for button
	 * @param source of the image
	 * @param altText  of the image if it doesn't show up in the UI
	 */
	protected void addExtraButton(String property, String source, String altText) {
	    
        ExtraButton newButton = new ExtraButton();
    
        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);
    
        extraButtons.add(newButton);
    }

}
