package edu.arizona.kfs.gl.businessobject;

import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class GlobalTransactionEdit extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String originCode;
    private String fundGroupCode;
    private String subFundGroupCode;
    private boolean active;
    private List<GlobalTransactionEditDetail> globalTransactionEditDetails;
    private OriginationCode origin;
    private FundGroup fundGroup;
    private SubFundGroup subFundGroup;


    public GlobalTransactionEdit() {
        super();
        globalTransactionEditDetails = new ArrayList<GlobalTransactionEditDetail>();
    }


    public String getOriginCode() {
        return originCode;
    }

    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    public String getFundGroupCode() {
        return fundGroupCode;
    }

    public void setFundGroupCode(String fundGroupCode) {
        this.fundGroupCode = fundGroupCode;
    }

    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }

    public List<GlobalTransactionEditDetail> getGlobalTransactionEditDetails() {
        return globalTransactionEditDetails;
    }

    public void setGlobalTransactionEditDetails(List<GlobalTransactionEditDetail> globalTransactionEditDetails) {
        this.globalTransactionEditDetails = globalTransactionEditDetails;
    }

    public OriginationCode getOrigin() {
        return origin;
    }

    public void setOrigin(OriginationCode origin) {
        this.origin = origin;
    }

    public FundGroup getFundGroup() {
        return fundGroup;
    }

    public void setFundGroup(FundGroup fundGroup) {
        this.fundGroup = fundGroup;
    }

    public SubFundGroup getSubFundGroup() {
        return subFundGroup;
    }

    public void setSubFundGroup(SubFundGroup subFundGroup) {
        this.subFundGroup = subFundGroup;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        List<Collection<PersistableBusinessObject>> managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(new ArrayList<PersistableBusinessObject>(getGlobalTransactionEditDetails()));
        return managedLists;
    }

}
