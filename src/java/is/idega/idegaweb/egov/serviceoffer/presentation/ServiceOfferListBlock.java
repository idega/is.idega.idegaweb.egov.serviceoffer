/*
 * $Id: ServiceOfferListBlock.java,v 1.2 2005/10/17 03:33:57 eiki Exp $
 * Created on Oct 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.presentation;

import is.idega.idegaweb.egov.serviceoffer.data.ServiceOffer;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoice;
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.core.contact.data.Phone;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableColumn;
import com.idega.presentation.TableColumnGroup;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.user.data.User;
import com.idega.util.PersonalIDFormatter;
import com.idega.util.text.Name;

/**
 * A block for viewing and editing a list of service offers
 * 
 *  Last modified: $Date: 2005/10/17 03:33:57 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class ServiceOfferListBlock extends ServiceOfferBlock implements ServiceOfferConstants{
	
	private static final String PARAMETER_ACTION = "prm_servoflb_action";
	
	private static final int ACTION_PHASE_ONE = 1;
	private static final int ACTION_LIST= 2;
	private static final int ACTION_SAVE = 3;
	
	private User user;
	
	public void present(IWContext iwc) {
		String selectedCaseId = null;
		try {		
			ServiceOffer offer = null;
			selectedCaseId = iwc.getParameter(getBusiness().getSelectedCaseParameter());
			if(selectedCaseId!=null && !"".equals(selectedCaseId)) {
				offer = (ServiceOffer) getBusiness().getServiceOffer(Integer.parseInt(selectedCaseId));
			}
			
				switch (parseAction(iwc)) {
					case ACTION_PHASE_ONE:
						if(offer==null){
							showPhaseOne(iwc);
						}
						else{
							showChoiceList(iwc, offer);
						}
						break;
					case ACTION_LIST:
						showChoiceList(iwc,offer);
						break;
					case ACTION_SAVE:
						save(iwc,offer);
						break;
				}
				
				add(getHelpButton("service_offer_list"));
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			add(localize("service.offer.list.case_id_not_a_number", "Case id was not a number : ")+selectedCaseId);
		}
		catch (FinderException e) {
			e.printStackTrace();
			add(localize("service.offer.list.case_not_found", "The case was not found : ")+selectedCaseId);
		}
	}
	
	/**
	 * Shows the form to select a service offer
	 * @param iwc
	 * @param offer 
	 * @param choice 
	 * @throws RemoteException
	 */
	private void showPhaseOne(IWContext iwc) throws RemoteException {
		Form form = createForm(iwc, ACTION_PHASE_ONE);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		layer.add(new Heading1(localize("service.offer.list.service_offer_view", "Service offer overview")));
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(localize("service.offer.list.select_a_service_offer", "Please select a service offer to view its details and click next.")));
		layer.add(paragraph);
		
		Layer formElementOffer = new Layer();
		formElementOffer.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		
		DropdownMenu offers = new DropdownMenu(getBusiness().getSelectedCaseParameter());
		//offers.setStyleClass(	STYLE_CLASS_SELECTION_BOX);
		offers.addMenuElements(getBusiness().getServiceOffers(iwc.getCurrentUser()));
		
		formElementOffer.add(offers);
		layer.add(formElementOffer);
		layer.add(new CSSSpacer());
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonDiv");
		layer.add(buttonLayer);
		
		SubmitButton next = new SubmitButton(localize("next", "Next"));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_LIST));
		
		buttonLayer.add(next);
		
		add(form);
	}

	private void showChoiceList(IWContext iwc, ServiceOffer offer) throws RemoteException {
		Form form = createForm(iwc, ACTION_LIST);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		
		layer.add(new Heading1(localize("service.offer.list.service_offer_view", "Service offer overview")));
		
		Paragraph paragraph = new Paragraph();
		
		paragraph.add(new Text(localize("service.offer.list.service_offer_list", "Below you will find details about the selected service offer and its participants.")));	
		
		layer.add(paragraph);
		
		addServiceOffer(iwc, layer, offer);
		
		layer.add(new CSSSpacer());
		addServiceOfferChoiceList(iwc,layer,offer);
		
		layer.add(new CSSSpacer());
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonDiv");
		layer.add(buttonLayer);
		
//		SubmitButton save = new SubmitButton(localize("save", "Save"));
//		save.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));
//		
		SubmitButton previous = new SubmitButton(localize("previous", "Previous"));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_ONE));
		
		buttonLayer.add(previous);
		
		ICPage homePage = null;
		try {
			homePage = getUserBusiness(iwc).getHomePageForUser(iwc.getCurrentUser());
		}
		catch (FinderException e) {
			//no homepage for user??
			e.printStackTrace();
		}
		
		if (homePage!= null) {
			GenericButton home = new GenericButton(localize("my_page", "My page"));
			home.setPageToOpen(homePage);
			buttonLayer.add(home);
		}
		
	//TODO make able to delete/cancel a service offer
		//buttonLayer.add(save);
		
		add(form);
	}
	
	private void addServiceOfferChoiceList(IWContext iwc, Layer layer, ServiceOffer offer) throws RemoteException {

		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setStyleClass(STYLENAME_LIST_TABLE);
		
		TableColumnGroup columnGroup = table.createColumnGroup();
		TableColumn column = columnGroup.createColumn();
		column.setSpan(2);
		column = columnGroup.createColumn();
		column.setSpan(7);
		column.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_CENTER);
		
		Collection choices = getBusiness().getServiceOfferChoices(offer);

		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		row.createHeaderCell().add(new Text(localize("service.offer.choice.name", "Child's name")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.personal_id", "Child's ssn")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.custodian_name", "Custodian's name")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.custodian_personal_id", "Custodian's ssn")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.phone_number", "Phone number")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.status", "Status")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.payment_status", "Payment")));
		row.createHeaderCell().add(new Text(localize("service.offer.choice.viewed", "Viewed")));
		
		group = table.createBodyRowGroup();
		int iRow = 1;
		
		int totalAgreed = 0;
		int totalDenied = 0;
		int totalPaid = 0;
		int totalViewed = 0;
		int total = choices.size();
		
		String agreedStatusCode = getBusiness().getCaseStatusGranted().getStatus();
		String deniedStatusCode = getBusiness().getCaseStatusDenied().getStatus();
		
		Iterator iter = choices.iterator();
		
		while (iter.hasNext()) {
			row = group.createRow();
			ServiceOfferChoice choice = (ServiceOfferChoice) iter.next();
			User user = choice.getUser();
			Name name = new Name(user.getFirstName(), user.getMiddleName(), user.getLastName());
			
			User owner = choice.getOwner();
			Name ownerName = new Name(owner.getFirstName(), owner.getMiddleName(), owner.getLastName());
			
			String status = choice.getStatus();
			if(agreedStatusCode.equals(status)){
				totalAgreed++;
			}
			else if(deniedStatusCode.equals(status)){
				totalDenied++;
			}
			boolean paid = choice.hasBeenPaidFor();
			if(paid){
				totalPaid++;
			}
			boolean viewed = choice.hasBeenViewed();
			if(viewed){
				totalViewed++;
			}
			
			try {
				row.createCell().add(new Text(name.getName(iwc.getCurrentLocale())));
				row.createCell().add(new Text(user.getPersonalID() != null ? PersonalIDFormatter.format(user.getPersonalID(), iwc.getCurrentLocale()) : "-"));
				
				row.createCell().add(new Text(ownerName.getName(iwc.getCurrentLocale())));
				row.createCell().add(new Text(owner.getPersonalID() != null ? PersonalIDFormatter.format(owner.getPersonalID(), iwc.getCurrentLocale()) : "-"));
				
				Collection userPhones = owner.getPhones();
				TableCell2 phoneCell = row.createCell();
				//int foneCount = 1;
				for (Iterator phones = userPhones.iterator(); phones.hasNext();) {
					Phone phone = (Phone) phones.next();
//					if(foneCount>1){
//						phoneCell.add(new Text(" / "));
//					}
					String number = phone.getNumber();
					if(number!=null && !"".equals(number)){
						phoneCell.add(new Text(phone.getNumber()));
					//foneCount++;
						break;
					}
				}
				
				row.createCell().add(new Text(localize(status,status)));
				
				//row.createCell().add(new Text( (paid)?localize("service.offer.choice.paid_for","Paid"):localize("service.offer.choice.un_paid","Not paid") ));
				row.createCell().add(new Text( (paid)?"X":"-"));
				row.createCell().add(new Text( (viewed)?"X":"-"));
				
				if (iRow % 2 == 0) {
					row.setStyleClass(STYLENAME_LIST_TABLE_EVEN_ROW);
				}
				else {
					row.setStyleClass(STYLENAME_LIST_TABLE_ODD_ROW);
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			
			if (iter.hasNext()) {
				iRow++;
			}
		}
		
		group = table.createFooterRowGroup();
		row = group.createRow();
		TableCell2 cell = row.createCell();
		cell.setColumnSpan(5);
		cell.add(new Text(localize("service.offer.choice.total", "Total")));
		row.createCell().add(new Text(String.valueOf(totalAgreed)+"/"+String.valueOf(totalDenied)+"/"+total));
		row.createCell().add(new Text(String.valueOf(totalPaid)+"/"+total));
		row.createCell().add(new Text(String.valueOf(totalViewed)+"/"+total));

		layer.add(table);
		
	}

	private void save(IWContext iwc, ServiceOffer offer) throws RemoteException {
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		add(layer);
		
		layer.add(new Heading1(localize("service.offer.list.saved", "Service offer choice finished")));
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(localize("service.offer.list.saved.text", "Thank you, your service choice has been saved and sent to the manager handling the service offer.")));
		layer.add(paragraph);
		
		ICPage homePage = null;
		try {
			homePage = getUserBusiness(iwc).getHomePageForUser(iwc.getCurrentUser());
		}
		catch (FinderException e) {
			//no homepage for user??
			e.printStackTrace();
		}
		
		if (homePage!= null) {
			Layer buttonLayer = new Layer(Layer.DIV);
			buttonLayer.setStyleClass("buttonDiv");
			layer.add(buttonLayer);
			
			GenericButton home = new GenericButton(localize("my_page", "My page"));
			home.setPageToOpen(homePage);
			buttonLayer.add(home);
		}
		
	}
	
	private Form createForm(IWContext iwc, int actionPhase) throws RemoteException {
		Form form = new Form();
		form.setID("serviceOfferForm");
		form.addParameter(PARAMETER_ACTION, actionPhase);
		
		//form.maintainParameter(getBusiness().getSelectedCaseParameter());
		
		
		return form;
	}
	
	private int parseAction(IWContext iwc) {
		int action = ACTION_PHASE_ONE;
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		
		return action;
	}
	
	
}