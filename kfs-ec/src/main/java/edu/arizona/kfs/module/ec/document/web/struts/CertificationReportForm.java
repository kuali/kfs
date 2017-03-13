package edu.arizona.kfs.module.ec.document.web.struts;

import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Action form for Effort Certification Document.
 */
public class CertificationReportForm extends org.kuali.kfs.module.ec.document.web.struts.CertificationReportForm {
	private static final String EFFORT_CERT_CALC_BUTTON_TEXT = "Calculate";
	private static final String EFFORT_CERT_CALC_REQUEST = ".calculate";
	private static final String EFFORT_CERT_CALC_BUTTON_FILE = "buttonsmall_calculate.gif";
	private ExtraButton calculateButton;
	 
	@Override
    public List<ExtraButton> getExtraButtons() {
		List<ExtraButton> buttons = super.getExtraButtons();
        buttons.add(generateCalculateButton());       
        return buttons;
    }
	
    /**
     * Generates an ExtraButton which represents the calculate button
     *
     * @return an ExtraButton representing a calculate button    
     */
    protected ExtraButton generateCalculateButton() {
        if ( calculateButton == null ) {
        	String appExternalImageURL = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
            ExtraButton button = new ExtraButton();
            button.setExtraButtonAltText(EFFORT_CERT_CALC_BUTTON_TEXT);
            button.setExtraButtonProperty(KFSConstants.DISPATCH_REQUEST_PARAMETER + EFFORT_CERT_CALC_REQUEST);
            button.setExtraButtonSource(appExternalImageURL + EFFORT_CERT_CALC_BUTTON_FILE);
            calculateButton = button;
        }
        return calculateButton;
    }
}
