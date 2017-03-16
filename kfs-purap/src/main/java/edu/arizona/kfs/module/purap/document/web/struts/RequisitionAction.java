package edu.arizona.kfs.module.purap.document.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase;
import org.kuali.kfs.module.purap.document.web.struts.RequisitionForm;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.context.SpringContext;

import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.module.purap.PurapPropertyConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.sys.businessobject.BuildingExtension;

public class RequisitionAction extends org.kuali.kfs.module.purap.document.web.struts.RequisitionAction {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionDocument.class);
    protected ParameterEvaluatorService parameterEvaluatorService;
    
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.refresh(mapping, form, request, response);
        RequisitionForm rqForm = (RequisitionForm) form;
        RequisitionDocument document = (RequisitionDocument) rqForm.getDocument();
        document.refreshReferenceObject(PurapPropertyConstants.BUILDING_OBJ);
        document.setOrganizationAutomaticPurchaseOrderLimit(SpringContext.getBean(PurapService.class).getApoLimit(document.getVendorContractGeneratedIdentifier(), document.getChartOfAccountsCode(), document.getOrganizationCode()));
        updateRouteCode(document);
        return forward;
    }
    
    @Override
    public ActionForward useOtherDeliveryBuilding(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.useOtherDeliveryBuilding(mapping, form, request, response);
        PurchasingFormBase baseForm = (PurchasingFormBase) form;
        RequisitionDocument document = (RequisitionDocument) baseForm.getDocument();
        document.setRouteCode("");
        document.setAddressToVendorIndicator(false);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Updated to detect when the delivery campus code changes and to apply the 
     * proper default delivery type using the system parameter.
     * 
     * It will not be set if the value is unchanged or the prior value is blank.  
     * The reason for this is that we don't want to override the calculations
     * which may have been made by other code creating the requisition.
     *
     * Also - this mod should only take effect for B2B orders.  Non-B2B orders
     * do not have as restricted delivery options.
     */
    protected void updateRouteCode(RequisitionDocument document) {
        String routeCodeValue = null;
        if ( StringUtils.equals(PurapConstants.RequisitionSources.B2B, document.getRequisitionSourceCode())) {
            BusinessObjectService bos = SpringContext.getBean(BusinessObjectService.class);
            
            if(StringUtils.isNotBlank(document.getDeliveryBuildingCode())) {
                Map<String, String> primaryKeys = new HashMap<String, String>();
                primaryKeys.put(KFSPropertyConstants.BUILDING_CODE, document.getDeliveryBuildingCode());
                List<Building> buildings = (List<Building>) bos.findMatching(Building.class, primaryKeys);
                Building building =  (Building) buildings.get(0);
                BuildingExtension buildingExt = (BuildingExtension) building.getExtension();
                routeCodeValue = buildingExt.getRouteCode();
            }
            
            document.setRouteCode(routeCodeValue);

            if (StringUtils.isBlank(routeCodeValue)) {
                document.setAddressToVendorIndicator(false); //default to final delivery address
            }else{
                try {
                    ParameterEvaluator param;
                    param = getParameterEvaluatorService().getParameterEvaluator(PurchaseOrderDocument.class, PurapConstants.B2B_DIRECT_SHIP_ROUTE_CODES_PARM, routeCodeValue.toUpperCase());

                    if (param.getValue().contains(routeCodeValue)) {
                        document.setAddressToVendorIndicator(true); //default to receiving address
                    } else {
                        document.setAddressToVendorIndicator(false); //default to final delivery address
                    }
                } catch ( Exception ex ) {
                    //The address to vendor indicator is a boolean and will default to false if not set. Because of this, it's acceptable to swallow the error
                    LOG.error( "Exception while determining how to default delivery address.", ex );
                }
            }
        }
    }
    
    public ParameterEvaluatorService getParameterEvaluatorService() {
        if(parameterEvaluatorService == null) {
            parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        }
        return parameterEvaluatorService;
    }
}
