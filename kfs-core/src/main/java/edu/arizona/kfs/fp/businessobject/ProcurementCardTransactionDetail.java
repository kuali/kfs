package edu.arizona.kfs.fp.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class ProcurementCardTransactionDetail extends org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail {

	//Procurement Card Level Three Objects
	private ProcurementCardLevel3Add	    procurementCardLevel3Add;
	private ProcurementCardLevel3AddUser	procurementCardLevel3AddUser;
	private ProcurementCardLevel3Fuel	    procurementCardLevel3Fuel;
	private ProcurementCardLevel3Generic	procurementCardLevel3Generic;
	private ProcurementCardLevel3Lodging	procurementCardLevel3Lodging;
	private ProcurementCardLevel3Rental	    procurementCardLevel3Rental;
	private ProcurementCardLevel3Transport	procurementCardLevel3Transport;
	
	private List<ProcurementCardLevel3AddItem>	procurementCardLevel3AddItems;
	private List<ProcurementCardLevel3NonFuel>	procurementCardLevel3NonFuels;
	private List<ProcurementCardLevel3ShipSvc>	procurementCardLevel3ShipSvcs;
	private List<ProcurementCardLevel3TempSvc>	procurementCardLevel3TempSvcs;
	private List<ProcurementCardLevel3TransportLeg>	procurementCardLevel3TransportLegs;
	
	private KualiDecimal transactionEditableSalesTaxAmount;
	private boolean transactionNoReceiptIndicator;

	public ProcurementCardTransactionDetail() {
		super();
		procurementCardLevel3AddItems = new ArrayList<ProcurementCardLevel3AddItem>();
		procurementCardLevel3NonFuels = new ArrayList<ProcurementCardLevel3NonFuel>();
		procurementCardLevel3ShipSvcs = new ArrayList<ProcurementCardLevel3ShipSvc>();
		procurementCardLevel3TempSvcs = new ArrayList<ProcurementCardLevel3TempSvc>();
		procurementCardLevel3TransportLegs = new ArrayList<ProcurementCardLevel3TransportLeg>();
	}

	public ProcurementCardLevel3Add getProcurementCardLevel3Add() {
		return procurementCardLevel3Add;
	}

	public void setProcurementCardLevel3Add(ProcurementCardLevel3Add procurementCardLevel3Add) {
		this.procurementCardLevel3Add = procurementCardLevel3Add;
	}

	public ProcurementCardLevel3AddUser getProcurementCardLevel3AddUser() {
		return procurementCardLevel3AddUser;
	}

	public void setProcurementCardLevel3AddUser(ProcurementCardLevel3AddUser procurementCardLevel3AddUser) {
		this.procurementCardLevel3AddUser = procurementCardLevel3AddUser;
	}

	public ProcurementCardLevel3Fuel getProcurementCardLevel3Fuel() {
		return procurementCardLevel3Fuel;
	}

	public void setProcurementCardLevel3Fuel(ProcurementCardLevel3Fuel procurementCardLevel3Fuel) {
		this.procurementCardLevel3Fuel = procurementCardLevel3Fuel;
	}

	public ProcurementCardLevel3Generic getProcurementCardLevel3Generic() {
		return procurementCardLevel3Generic;
	}

	public void setProcurementCardLevel3Generic(ProcurementCardLevel3Generic procurementCardLevel3Generic) {
		this.procurementCardLevel3Generic = procurementCardLevel3Generic;
	}

	public ProcurementCardLevel3Lodging getProcurementCardLevel3Lodging() {
		return procurementCardLevel3Lodging;
	}

	public void setProcurementCardLevel3Lodging(ProcurementCardLevel3Lodging procurementCardLevel3Lodging) {
		this.procurementCardLevel3Lodging = procurementCardLevel3Lodging;
	}

	public ProcurementCardLevel3Rental getProcurementCardLevel3Rental() {
		return procurementCardLevel3Rental;
	}

	public void setProcurementCardLevel3Rental(ProcurementCardLevel3Rental procurementCardLevel3Rental) {
		this.procurementCardLevel3Rental = procurementCardLevel3Rental;
	}

	public ProcurementCardLevel3Transport getProcurementCardLevel3Transport() {
		return procurementCardLevel3Transport;
	}

	public void setProcurementCardLevel3Transport(ProcurementCardLevel3Transport procurementCardLevel3Transport) {
		this.procurementCardLevel3Transport = procurementCardLevel3Transport;
	}

	public List<ProcurementCardLevel3AddItem> getProcurementCardLevel3AddItems() {
		return procurementCardLevel3AddItems;
	}

	public void setProcurementCardLevel3AddItems(List<ProcurementCardLevel3AddItem> procurementCardLevel3AddItems) {
		this.procurementCardLevel3AddItems = procurementCardLevel3AddItems;
	}

	public List<ProcurementCardLevel3NonFuel> getProcurementCardLevel3NonFuels() {
		return procurementCardLevel3NonFuels;
	}

	public void setProcurementCardLevel3NonFuels(List<ProcurementCardLevel3NonFuel> procurementCardLevel3NonFuels) {
		this.procurementCardLevel3NonFuels = procurementCardLevel3NonFuels;
	}

	public List<ProcurementCardLevel3ShipSvc> getProcurementCardLevel3ShipSvcs() {
		return procurementCardLevel3ShipSvcs;
	}

	public void setProcurementCardLevel3ShipSvcs(List<ProcurementCardLevel3ShipSvc> procurementCardLevel3ShipSvcs) {
		this.procurementCardLevel3ShipSvcs = procurementCardLevel3ShipSvcs;
	}

	public List<ProcurementCardLevel3TempSvc> getProcurementCardLevel3TempSvcs() {
		return procurementCardLevel3TempSvcs;
	}

	public void setProcurementCardLevel3TempSvcs(List<ProcurementCardLevel3TempSvc> procurementCardLevel3TempSvcs) {
		this.procurementCardLevel3TempSvcs = procurementCardLevel3TempSvcs;
	}

	public List<ProcurementCardLevel3TransportLeg> getProcurementCardLevel3TransportLegs() {
		return procurementCardLevel3TransportLegs;
	}

	public void setProcurementCardLevel3TransportLegs(List<ProcurementCardLevel3TransportLeg> procurementCardLevel3TransportLegs) {
		this.procurementCardLevel3TransportLegs = procurementCardLevel3TransportLegs;
	}

	public KualiDecimal getTransactionEditableSalesTaxAmount() {
		return transactionEditableSalesTaxAmount;
	}

	public void setTransactionEditableSalesTaxAmount(KualiDecimal transactionEditableSalesTaxAmount) {
		this.transactionEditableSalesTaxAmount = transactionEditableSalesTaxAmount;
	}

	public boolean getTransactionNoReceiptIndicator() {
		return transactionNoReceiptIndicator;
	}

	public void setTransactionNoReceiptIndicator(boolean transactionNoReceiptIndicator) {
		this.transactionNoReceiptIndicator = transactionNoReceiptIndicator;
	}

	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
		m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
		m.put("financialDocumentTransactionLineNumber", this.getFinancialDocumentTransactionLineNumber().toString());
		return m;
	}
}
