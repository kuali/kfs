package org.kuali.workflow.attribute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.kuali.KualiSpringServiceLocator;
import org.kuali.workflow.KualiConstants;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.RoleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.routetemplate.RouteContext;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * @author bmcgough
 *
 * Attribute for kuali accounts based routing, which will expose
 * three different roles, Fiscal Officer, Fiscal Officer Primary Delegate,
 * and Fiscal Officer Secondary Delegate
 * 
 */
public class KualiAccountAttribute implements RoleAttribute, WorkflowAttribute {
    
    static final long serialVersionUID = 1000;
    
    private static Logger LOG = Logger.getLogger(KualiAccountAttribute.class);
    
    private static final String FIN_COA_CD_KEY = "fin_coa_cd";

    private static final String ACCOUNT_NBR_KEY = "account_nbr";
    
    private static final String FDOC_TOTAL_DOLLAR_AMOUNT_KEY = "fdoc_ttl_dlr_amt"; 

    private static final String FISCAL_OFFICER_ROLE_KEY = "FISCAL-OFFICER";

    private static final String FISCAL_OFFICER_ROLE_LABEL = "Fiscal Officer";

    private static final String FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_KEY = "FISCAL-OFFICER-PRIMARY-DELEGATE";

    private static final String FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_LABEL = "Fiscal Officer Primary Delegate";
    
    private static final String FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_KEY = "FISCAL-OFFICER-SECONDARY-DELEGATE";

    private static final String FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_LABEL = "Fiscal Officer Secondary Delegate";

    private static final String ACCOUNT_ATTRIBUTE = "KUALI_ACCOUNT_ATTRIBUTE";

    //TODO - here we need to integrate our AccountLookupable long term...
    //private static final String LOOKUPABLE_CLASS = "AccountLookupableImplService";

    private static final String ROLE_STRING_DELIMITER = "~!~!~";
        
    private String finCoaCd;

    private String accountNbr;
    
    private String totalDollarAmount;

    private boolean required;

    /**
     * No arg constructor
     *
     */
    public KualiAccountAttribute() {
    }

    /**
     * Constructor that takes chart, account, and total dollar amount
     * @param finCoaCd
     * @param accountNbr
     * @param totalDollarAmount
     */
    public KualiAccountAttribute(String finCoaCd, String accountNbr, String totalDollarAmount) {
        this.finCoaCd = finCoaCd;
        this.accountNbr = accountNbr;
        this.totalDollarAmount = totalDollarAmount;
    }

    /**
     * return the universal set of role names provided by this attribute
     */
    public List getRoleNames() {
        List roles = new ArrayList();
        roles.add(new Role(this.getClass(), FISCAL_OFFICER_ROLE_KEY, FISCAL_OFFICER_ROLE_LABEL));
        roles.add(new Role(this.getClass(), FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_KEY, FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_LABEL));
        roles.add(new Role(this.getClass(), FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_KEY, FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_LABEL));
        return roles;
    }

    /**
     * return whether or not this attribute is required
     * @return
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * simple setter
     * 
     * @param required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * simple getter for the rule extension values
     * @return
     */
    public List getRuleExtensionValues() {
        return Collections.EMPTY_LIST;
    }

    /**
     * method to validate the routing data, need to determine if this should actually
     * be implemented to throw errors or anything like that.
     * @param paramMap
     * @return
     */
    public List validateRoutingData(Map paramMap) {
        //TODO should this actually validate
//        List errors = new ArrayList();
        this.finCoaCd = (String) paramMap.get(FIN_COA_CD_KEY);
        this.accountNbr = (String) paramMap.get(ACCOUNT_NBR_KEY);
        this.totalDollarAmount = (String) paramMap.get(FDOC_TOTAL_DOLLAR_AMOUNT_KEY);
        return new ArrayList();
    }

    /**
     * method to validate the rule data, which matches the routing data in this attributes case
     * therefore, we should just be able to call into the other implementation validateRoutingData
     * @param paramMap
     * @return
     */
    public List validateRuleData(Map paramMap) {
        return validateRoutingData(paramMap);
    }

    /**
     * method to actually construct the docContent that will be appended to this documents contents
     * 
     * @return
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getAccountNbr())) {
            return "";
        }
        return "<" + ACCOUNT_ATTRIBUTE + ">" + "<" + FIN_COA_CD_KEY + ">" + getFinCoaCd() + "</" + FIN_COA_CD_KEY + ">" + "<" + ACCOUNT_NBR_KEY + ">" + getAccountNbr() + "</" + ACCOUNT_NBR_KEY + ">" + "<" + FDOC_TOTAL_DOLLAR_AMOUNT_KEY + ">" + getTotalDollarAmount() + "</" + FDOC_TOTAL_DOLLAR_AMOUNT_KEY + ">" + "</" + ACCOUNT_ATTRIBUTE + ">";
    }
    public String getAttributeLabel(){
        return "";
    }

    /**
     * return true since this is a rule attribute, and if there are no routing records returned, then there was no valid
     * mapping in the docContent for a given role.
     * 
     * @param docContent
     * @param ruleExtensions
     * @return
     */
    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
        return true;
    }

    /**
     * This method is used by the workflow report to allow the user to fill in some arbitrary values
     * for the routable contents of an example document, and then to run the report to generate a virtual route
     * log of who the document would route to, etc.
     * 
     * @return
     */
    public List getRoutingDataRows() {
        List rows = new ArrayList();
        
        List fields = new ArrayList();
        fields.add(new Field("Chart", "", Field.TEXT, false, FIN_COA_CD_KEY, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("Account", "", Field.TEXT, false, ACCOUNT_NBR_KEY, "", null, null,"accountNbr"));        
        rows.add(new Row(fields));
        
        fields = new ArrayList();
        fields.add(new Field("Total Dollar Amount", "", Field.TEXT, false, FDOC_TOTAL_DOLLAR_AMOUNT_KEY, "", null, null));
        rows.add(new Row(fields));
        return rows;
    }

    /**
     * simple getter which returns empty
     * @return
     */
    public List getRuleRows() {
        return Collections.EMPTY_LIST;
    }

    /**
     * simple getter which returns the account number
     * @return
     */
    public String getAccountNbr() {
        return accountNbr;
    }

    /**
     * simple setter that takes the account number
     * @param accountNbr
     */
    public void setAccountNbr(String accountNbr) {
        this.accountNbr = accountNbr;
    }

    /**
     * simple getter which returns the chart
     * @return
     */
    public String getFinCoaCd() {
        return finCoaCd;
    }

    /**
     * simple setter which takes the chart

     * @param finCoaCd
     */
    public void setFinCoaCd(String finCoaCd) {
        this.finCoaCd = finCoaCd;
    }
    /**
     * simple getter which returns the total dollar amount
     * @return
     */
    public String getTotalDollarAmount() {
        return totalDollarAmount;
    }

    /**
     * simple setter which takes the total dollar amount
     * @param totalDollarAmount
     */
    public void setTotalDollarAmount(String totalDollarAmount) {
        this.totalDollarAmount = totalDollarAmount;
    }
    
    /**
     * This method will build a string representation of a qualified account role
     * a qualified account role is the role, with the corresponding string values that
     * further qualify the role to apply for a given account.
     * 
     * @param roleName
     * @param chart
     * @param account
     * @param totalDollarAmount
     * @return
     */
    private String getQualifiedRoleString(String roleName, String chart, String account, String totalDollarAmount) {
        return roleName + ROLE_STRING_DELIMITER + chart + ROLE_STRING_DELIMITER + account + ROLE_STRING_DELIMITER + totalDollarAmount; 
    }
    
    /**
     * get the Role name from a qualified role String
     * @param qualifiedRole
     * @return
     */
    private String getUnqualifiedRoleFromString(String qualifiedRole) {
        return qualifiedRole.split(ROLE_STRING_DELIMITER)[0];
    }
    
    /**
     * get the chart name from a qualified role string
     * @param qualifiedRole
     * @return
     */
    private String getUnqualifiedChartFromString(String qualifiedRole) {
        return qualifiedRole.split(ROLE_STRING_DELIMITER)[1];
    }
    
    /**
     * get the account number from a qualified role string
     * @param qualifiedRole
     * @return
     */
    private String getUnqualifiedAccountFromString(String qualifiedRole) {
        return qualifiedRole.split(ROLE_STRING_DELIMITER)[2];
    }
    
    /**
     * get the total dollar amount from the qualified role string
     * @param qualifiedRole
     * @return
     */
    private String getUnqualifiedTotalDollarAmountFromString(String qualifiedRole) {
        return qualifiedRole.split(ROLE_STRING_DELIMITER)[3];
    }
    
    /**
     * This should be a mapping between the FIS document type and the Workflow document type, and
     * should give back the FIS document type name so that we can lookup delegations based on the
     * FIS name instead of the workflow name.
     * 
     * @param documentTypeName
     * @return
     */
    private String getFisDocumentTypeNameFromWorkflowDocumentTypeName(String documentTypeName) {
        //TODO need to look at the document type table for 
        return "ALL";
    }
    
    /*
     *  (non-Javadoc)
     * @see edu.iu.uis.eden.routetemplate.RoleAttribute#getQualifiedRoleNames(java.lang.String, java.lang.String)
     */
    public List getQualifiedRoleNames(String roleName, DocumentContent docContent) throws EdenUserNotFoundException {
        Set qualifiedRoleNames = new HashSet();
        if (FISCAL_OFFICER_ROLE_KEY.equals(roleName) || FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_KEY.equals(roleName) || FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_KEY.equals(roleName)) {
            Document doc = null;
            //TODO: undo this so we're not dependent on a core workflow class
            doc = XmlHelper.buildJDocument(docContent.getDocument());
            List accountElements = XmlHelper.findElements(doc.getRootElement(), ACCOUNT_ATTRIBUTE);
            for (Iterator iter = accountElements.iterator(); iter.hasNext();) {
                Element accountElement = (Element) iter.next();
                qualifiedRoleNames.add(getQualifiedRoleString(roleName, accountElement.getChild(FIN_COA_CD_KEY).getText(), accountElement.getChild(ACCOUNT_NBR_KEY).getText(), accountElement.getChild(FDOC_TOTAL_DOLLAR_AMOUNT_KEY).getText()));
            }
        }
        return new ArrayList(qualifiedRoleNames);
    }

    /**
     * determines if a given delegation record is active or not.
     * 
     * @param timestamp
     * @return
     */
    private boolean isActiveDelegationRecord(Timestamp timestamp) {
        if (timestamp == null || timestamp.before(new Date())) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * determines if a given delegation is within a dollar range
     * 
     * @param fromAmount
     * @param toAmount
     * @param totalDollarAmount
     * @return
     */
    private boolean isDelegationWithinRangeCheck(long fromAmount, long toAmount, long totalDollarAmount) {
        if (fromAmount == 0 && toAmount == 0) {
            return true;
        }
        if (fromAmount <= totalDollarAmount && toAmount >= totalDollarAmount) {
            return true;
        }
        return false;
    }
    
    /*
     *  (non-Javadoc)
     * @see edu.iu.uis.eden.routetemplate.RoleAttribute#resolveQualifiedRole(edu.iu.uis.eden.routetemplate.attribute.RouteContext, java.lang.String, java.lang.String)
     */
    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext context, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
        List members = new ArrayList();
        
        Connection conn = null;
        try {
            conn = KualiSpringServiceLocator.getDataSource().getConnection();
            String chart = getUnqualifiedChartFromString(qualifiedRole);
            String account = getUnqualifiedAccountFromString(qualifiedRole);
            String totalDollarAmount = getUnqualifiedTotalDollarAmountFromString(qualifiedRole);
            String fisDocumentType = getFisDocumentTypeNameFromWorkflowDocumentTypeName(context.getRouteHeader().getDocumentType().getName());
            
            if (FISCAL_OFFICER_ROLE_KEY.equals(roleName)) {
                String kualiSystemId = null;
                String sql = "select ACCT_FSC_OFC_UID from CA_ACCOUNT_T where FIN_COA_CD = ? and ACCOUNT_NBR = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, chart);
                ps.setString(2, account);
                
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    kualiSystemId = rs.getString("ACCT_FSC_OFC_UID");
                } else {
                    LOG.warn("No Fiscal Officer retrieved for account " + account);
                }
                members.add(new AuthenticationUserId(KualiConstants.getNetworkId(conn, kualiSystemId)));
            } else if (FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_KEY.equals(roleName)) {
                String kualiSystemId = null;
                String sql = "select ACCT_DLGT_UNVL_ID from CA_ACCT_DELEGATE_T "+
                	"where FIN_COA_CD = ? and ACCOUNT_NBR = ? and FDOC_TYP_CD = ? and ACCT_DLGT_ACTV_IND = 'Y' "+
                	"and ACCT_DLGT_START_DT <= SYSDATE and ACCT_DLGT_PRMRT_IND = 'Y' "+
                	"and ((FDOC_APRV_FROM_AMT <= ? and FDOC_APRV_TO_AMT >= ?) " +
                	"		OR FDOC_APRV_TO_AMT = 0)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, chart);
                ps.setString(2, account);
                ps.setString(3, fisDocumentType);
                ps.setString(4, totalDollarAmount);
                ps.setString(5, totalDollarAmount);
                
                ResultSet rs = ps.executeQuery();
                int counter = 0;
                while (rs.next()) {
                    counter ++;
                    kualiSystemId = rs.getString("ACCT_DLGT_UNVL_ID");
                    members.add(new AuthenticationUserId(KualiConstants.getNetworkId(conn, kualiSystemId)));
                }
                if (counter > 1) {
                    throw new RuntimeException("There was more than one primary account delegate for account "+ account);
                }
            } else if (FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_KEY.equals(roleName)) {
                String kualiSystemId = null;
                
                String sql = "select ACCT_DLGT_UNVL_ID from CA_ACCT_DELEGATE_T "+
                	"where FIN_COA_CD = ? and ACCOUNT_NBR = ? and FDOC_TYP_CD = ? and ACCT_DLGT_ACTV_IND = 'Y' "+
                	"and ACCT_DLGT_START_DT <= SYSDATE and ACCT_DLGT_PRMRT_IND = 'N' "+
                	"and ((FDOC_APRV_FROM_AMT <= ? and FDOC_APRV_TO_AMT >= ?) " +
                	"		OR FDOC_APRV_TO_AMT = 0)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, chart);
                ps.setString(2, account);
                ps.setString(3, fisDocumentType);
                ps.setString(4, totalDollarAmount);
                ps.setString(5, totalDollarAmount);
                
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    kualiSystemId = rs.getString("ACCT_DLGT_UNVL_ID");
                    
                    members.add(new AuthenticationUserId(KualiConstants.getNetworkId(conn, kualiSystemId)));
                }
            }
        } catch (Exception e) {
            LOG.error("Error getting connection",e);
            throw new RuntimeException("An Error occurred during routing of this document",e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    // Ignore
                }
                conn = null;
            }
        }
        return new ResolvedQualifiedRole(roleName, members);
    }
}
