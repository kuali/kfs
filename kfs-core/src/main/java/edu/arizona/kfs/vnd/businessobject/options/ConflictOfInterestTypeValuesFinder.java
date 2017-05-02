package edu.arizona.kfs.vnd.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

import edu.arizona.kfs.vnd.VendorParameterConstants;

public class ConflictOfInterestTypeValuesFinder extends KeyValuesBase {

	public List getKeyValues() {
		KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue("", ""));

        Collection<String> conflictOfInterestValues = SpringContext.getBean(ParameterService.class).getParameterValuesAsString(VendorDetail.class, VendorParameterConstants.CONFLICT_OF_INTEREST_VALUES);
        
        for(String value : conflictOfInterestValues) {
        	String [] values = value.split("=");
        	if(values.length == 2) {
        		labels.add(new ConcreteKeyValue(values[0], values[1]));
        	}
        }

        return labels;
	}
}
