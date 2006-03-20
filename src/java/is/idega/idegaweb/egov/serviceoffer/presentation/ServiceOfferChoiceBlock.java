/*
 * $Id: ServiceOfferChoiceBlock.java,v 1.8 2006/03/20 09:44:16 laddi Exp $
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
import javax.ejb.FinderException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.user.data.User;

/**
 * A block for viewing/accepting/declining a service offer choice
 * 
 *  Last modified: $Date: 2006/03/20 09:44:16 $ by $Author: laddi $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.8 $
 */
public class ServiceOfferChoiceBlock extends ServiceOfferBlock implements ServiceOfferConstants{
	
	private static final String PARAMETER_ACTION = "prm_servofc_action";
	
	public static final String PARAMETER_SERVICE_OFFER_CHOICE = "SERVOFF_CH";
	public static final String PARAMETER_SERVICE_OFFER_CHOICE_ACCEPT = "SERVICE_CHOICE_ACCEPT";
	public static final String PARAMETER_SERVICE_OFFER_CHOICE_DECLINE = "SERVICE_CHOICE_DECLINE";
	public static final String PARAMETER_SERVICE_OFFER_CHOICE_COMMENT = "SERVICE_COMMENT";
	
	private static final int ACTION_PHASE_ONE = 1;
	private static final int ACTION_ACCEPT = 2;
	private static final int ACTION_DECLINE = 3;
	private static final int ACTION_OVERVIEW = 4;
	private static final int ACTION_SAVE = 5;
	private static final int ACTION_VIEW_CLOSED = 6;
	
	
	public void present(IWContext iwc) {
		String selectedCaseId = null;
		try {		
			selectedCaseId = iwc.getParameter(getBusiness().getSelectedCaseParameter());
			if(selectedCaseId==null || "".equals(selectedCaseId)) {
				add(localize("service.offer.choice.no_case_selected", "NO CASE SELECTED"));
			}
			else{
				ServiceOfferChoice choice = getBusiness().getServiceOfferChoice(Integer.parseInt(selectedCaseId));
				ServiceOffer offer = getBusiness().getServiceOffer(((Integer)choice.getParentCase().getPrimaryKey()).intValue());
			
				boolean isOver = !choice.getStatus().equals(getBusiness().getCaseStatusOpenString());
				switch (parseAction(iwc)) {
					case ACTION_PHASE_ONE:
						if(!isOver){
							showPhaseOne(iwc,choice,offer);
						}
						else{
							showClosedChoice(iwc,choice,offer);
						}
						break;
					case ACTION_ACCEPT:
						showOverview(iwc,choice,offer,true);
						break;
					case ACTION_DECLINE:
						showOverview(iwc,choice,offer,false);
						break;
					case ACTION_SAVE:
						save(iwc,choice,offer);
						break;
				}
			}
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			add(localize("service.offer.choice.case_id_not_a_number", "Case id was not a number : ")+selectedCaseId);
		}
		catch (FinderException e) {
			e.printStackTrace();
			add(localize("service.offer.choice.case_not_found", "The case was not found : ")+selectedCaseId);
		}
	}
	
	/**
	 * Shows the form to answer a service offer choice 
	 * @param iwc
	 * @param offer 
	 * @param choice 
	 * @throws RemoteException
	 */
	private void showPhaseOne(IWContext iwc, ServiceOfferChoice choice, ServiceOffer offer) throws RemoteException {
		Form form = createForm(iwc, ACTION_PHASE_ONE);
		
		//mark as viewed
		getBusiness().setServiceChoiceAsViewed(choice);
		
		boolean isOptional = offer.isServiceChoiceOptional();
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		if(isOptional){
			layer.add(new Heading1(localize("service.offer.choice.make_a_service_choice", "Make a service offer choice 1 of 2")));
		}
		else{
			layer.add(new Heading1(localize("service.offer.choice.service_offer_mandatory", "Mandatory Service Offer")));	
		}
		
		User user = choice.getUser();
		Paragraph forUserParagraph = new Paragraph();
		forUserParagraph.add(new Text(localize("service.offer.choice.service_offer_for","A service offer for ")+user.getName()+", "+user.getPersonalID()));
		layer.add(forUserParagraph);
		
		Paragraph paragraph = new Paragraph();
		if(isOptional){
			paragraph.add(new Text(localize("service.offer.choice.text", "Please review the following service offer and respond to it by clicking on one of the buttons (Decline/Accept) at the bottom of the page")));
		}
		else{
			paragraph.add(new Text(localize("service.offer.choice.text_mandatory", "The following service offer is mandatory and is only for your viewing.")));
		}
		layer.add(paragraph);
		
		addServiceOffer(iwc, layer,offer);
		
		layer.add(new CSSSpacer());
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonDiv");
		layer.add(buttonLayer);
		
		if(isOptional){
			SubmitButton accept = new SubmitButton(localize("accept", "Accept"));
			accept.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_ACCEPT));
			
			SubmitButton decline = new SubmitButton(localize("decline", "Decline"));
			decline.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_DECLINE));
			
			buttonLayer.add(decline);
	
			buttonLayer.add(accept);
		}
		else{
			ICPage homePage = null;
			try {
				homePage = getUserBusiness(iwc).getHomePageForUser(getUser(iwc));
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
		}
		
		
		add(form);
	}
	

	private void showOverview(IWContext iwc, ServiceOfferChoice choice, ServiceOffer offer, boolean accepts) throws RemoteException {
		Form form = createForm(iwc, ACTION_OVERVIEW);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		
		
		if(accepts){
			form.addParameter(PARAMETER_SERVICE_OFFER_CHOICE,PARAMETER_SERVICE_OFFER_CHOICE_ACCEPT);
		}
		else{
			form.addParameter(PARAMETER_SERVICE_OFFER_CHOICE, PARAMETER_SERVICE_OFFER_CHOICE_DECLINE);
		}
		
		layer.add(new Heading1(localize("service.offer.choice.make_a_service_choice2", "Make a service offer choice 2 of 2")));
		
		Paragraph forUserParagraph = new Paragraph();
		User user = choice.getUser();
		forUserParagraph.add(new Text(localize("service.offer.choice.service_offer_for","A service offer for ")+user.getName()+", "+user.getPersonalID()));
		layer.add(forUserParagraph);
		
		Paragraph paragraph = new Paragraph();
		if(accepts){
			paragraph.add(new Text(localize("service.offer.choice.accepted_text", "You have decided to accept the service offer, to finish press save or press previous change your choice.")));
		}
		else{
			paragraph.add(new Text(localize("service.offer.choice.declined_text", "You have decided to decline the service offer, to finish press save or press previous change your choice.")));	
		}
		layer.add(paragraph);
		
		//TODO if requested add text area for comment if the user wants to decline the offer
//		Layer formElementText = new Layer();
//		formElementText.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
//		TextArea text = new TextArea(PARAMETER_SERVICE_OFFER_CHOICE_COMMENT);
//		
//		text.keepStatusOnAction();
//		text.setMaximumCharacters(4000);
//		Label textLabel = new Label(localize("service.offer.application.description" ,"Description of service"), text);
//		formElementText.add(textLabel);
//		formElementText.add(text);
//		layer.add(formElementText);	
		
		layer.add(new CSSSpacer());
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonDiv");
		layer.add(buttonLayer);
		
		SubmitButton save = new SubmitButton(localize("save", "Save"));
		save.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));
		//save.setDisabled(!canSave);
		SubmitButton previous = new SubmitButton(localize("previous", "Previous"));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_ONE));
		buttonLayer.add(previous);
		buttonLayer.add(save);
		
		add(form);
	}
	
	private void showClosedChoice(IWContext iwc, ServiceOfferChoice choice, ServiceOffer offer) throws RemoteException {
			Form form = createForm(iwc, 	ACTION_VIEW_CLOSED);
			
			boolean accepted = choice.getStatus().equals(getBusiness().getCaseStatusGranted().getStatus());
			
			Layer layer = new Layer(Layer.DIV);
			layer.setID("phasesDiv");
			form.add(layer);
					
			layer.add(new Heading1(localize("service.offer.choice.closed_choice_overview", "Closed service offer overview")));
			
			Paragraph forUserParagraph = new Paragraph();
			User user = choice.getUser();
			forUserParagraph.add(new Text(localize("service.offer.choice.service_offer_for","A service offer for ")+user.getName()+", "+user.getPersonalID()));
			layer.add(forUserParagraph);
			
			Paragraph paragraph = new Paragraph();
			if(accepted){
				paragraph.add(new Text(localize("service.offer.choice.already_accepted_text", "You have already decided to accept the service offer below and the case is therefore closed.")));
			}
			else{
				paragraph.add(new Text(localize("service.offer.choice.already_declined_text", "You have already decided to decline the service offer below and the case is therefore closed.")));	
			}
			layer.add(paragraph);
			
			addServiceOffer(iwc, layer,offer);
			
			layer.add(new CSSSpacer());
						
			ICPage homePage = null;
			try {
				homePage = getUserBusiness(iwc).getHomePageForUser(getUser(iwc));
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
			
			add(form);
	}
	
	
	private void save(IWContext iwc, ServiceOfferChoice choice, ServiceOffer offer) throws RemoteException {
		
		
		boolean accepts = PARAMETER_SERVICE_OFFER_CHOICE_ACCEPT.equals(iwc.getParameter(PARAMETER_SERVICE_OFFER_CHOICE));
		
		getBusiness().changeServiceOfferChoiceStatus(choice,accepts,iwc.getCurrentUser());
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		add(layer);
		
		layer.add(new Heading1(localize("service.offer.choice.saved", "Service offer choice finished")));
		
		
		Paragraph forUserParagraph = new Paragraph();
		User user = choice.getUser();
		forUserParagraph.add(new Text(localize("service.offer.choice.service_offer_for","A service offer for ")+user.getName()+", "+user.getPersonalID()));
		layer.add(forUserParagraph);
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(localize("service.offer.choice.saved.text", "Thank you, your service choice has been saved and sent to the manager handling the service offer.")));
		layer.add(paragraph);
		
		ICPage homePage = null;
		try {
			homePage = getUserBusiness(iwc).getHomePageForUser(getUser(iwc));
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
		
		form.maintainParameter(getBusiness().getSelectedCaseParameter());
		
		if(actionPhase!=ACTION_OVERVIEW){
			form.maintainParameter(PARAMETER_SERVICE_OFFER_CHOICE_COMMENT);
		}
		
		return form;
	}
	
	private int parseAction(IWContext iwc) {
		int action = ACTION_PHASE_ONE;
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		
		return action;
	}
	
	private User getUser(IWContext iwc) throws RemoteException {
		return iwc.getCurrentUser();
	}
	
}