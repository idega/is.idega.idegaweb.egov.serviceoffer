/*
 * $Id: ServiceOfferChoiceBlock.java,v 1.11 2006/04/05 21:24:39 laddi Exp $
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
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.Form;
import com.idega.user.data.User;

/**
 * A block for viewing/accepting/declining a service offer choice
 * 
 *  Last modified: $Date: 2006/04/05 21:24:39 $ by $Author: laddi $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.11 $
 */
public class ServiceOfferChoiceBlock extends ServiceOfferBlock implements ServiceOfferConstants{
	
	private static final String PARAMETER_ACTION = "prm_servofc_action";
	
	public static final String PARAMETER_SERVICE_OFFER_CHOICE = "SERVOFF_CH";
	
	private static final int ACTION_PHASE_ONE = 1;
	private static final int ACTION_ACCEPT = 2;
	private static final int ACTION_DECLINE = 3;
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
						save(iwc, choice, offer, true);
						break;
						
					case ACTION_DECLINE:
						save(iwc, choice, offer, false);
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
		
		User user = choice.getUser();
		form.add(getPersonInfo(iwc, user));

		String message = "";
		if(isOptional){
			message = localize("service.offer.choice.make_a_service_choice", "Make a service offer choice 1 of 2");
		}
		else{
			message = localize("service.offer.choice.service_offer_mandatory", "Mandatory Service Offer");	
		}
		
		Heading1 heading = new Heading1(message);
		heading.setStyleClass("subHeader");
		heading.setStyleClass("topSubHeader");
		form.add(heading);
		
		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		addServiceOffer(iwc, section, offer);
		
		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		section.add(clearLayer);
		
		if(isOptional){
			message = localize("service.offer.choice.text", "Please review the following service offer and respond to it by clicking on one of the buttons (Decline/Accept) at the bottom of the page");
		}
		else{
			message = localize("service.offer.choice.text_mandatory", "The following service offer is mandatory and is only for your viewing.");
		}
		section.add(getAttentionLayer(message));
		
		Layer bottom = new Layer(Layer.DIV);
		bottom.setStyleClass("bottom");
		form.add(bottom);
		
		if(isOptional){
			Link next = getButtonLink(localize("accept", "Accept"));
			next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_ACCEPT));
			next.setToFormSubmit(form);
			bottom.add(next);

			Link back = getButtonLink(localize("decline", "Decline"));
			back.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_DECLINE));
			back.setToFormSubmit(form);
			bottom.add(back);
		}
		else{
			try {
				ICPage page = getUserBusiness(iwc).getHomePageForUser(iwc.getCurrentUser());
				Link link = getButtonLink(localize("my_page", "My page"));
				link.setStyleClass("homeButton");
				link.setPage(page);
				bottom.add(link);
			}
			catch (FinderException e) {
				e.printStackTrace();
			}
		}
		
		add(form);
	}
	
	private void showClosedChoice(IWContext iwc, ServiceOfferChoice choice, ServiceOffer offer) throws RemoteException {
			Form form = createForm(iwc, ACTION_VIEW_CLOSED);
			
			User user = choice.getUser();
			form.add(getPersonInfo(iwc, user));

			boolean accepted = choice.getStatus().equals(getBusiness().getCaseStatusGranted().getStatus());
			
			Heading1 heading = new Heading1(localize("service.offer.choice.closed_choice_overview", "Closed service offer overview"));
			heading.setStyleClass("subHeader");
			heading.setStyleClass("topSubHeader");
			form.add(heading);
			
			Layer section = new Layer(Layer.DIV);
			section.setStyleClass("formSection");
			form.add(section);
					
			addServiceOffer(iwc, section, offer);
			
			Layer clearLayer = new Layer(Layer.DIV);
			clearLayer.setStyleClass("Clear");
			section.add(clearLayer);
			
			String message = "";
			if(accepted){
				message = localize("service.offer.choice.already_accepted_text", "You have already decided to accept the service offer below and the case is therefore closed.");
			}
			else{
				message = localize("service.offer.choice.already_declined_text", "You have already decided to decline the service offer below and the case is therefore closed.");	
			}
			section.add(getAttentionLayer(message));
			
			Layer bottom = new Layer(Layer.DIV);
			bottom.setStyleClass("bottom");
			form.add(bottom);
			
			try {
				ICPage page = getUserBusiness(iwc).getHomePageForUser(iwc.getCurrentUser());
				Link link = getButtonLink(localize("my_page", "My page"));
				link.setStyleClass("homeButton");
				link.setPage(page);
				bottom.add(link);
			}
			catch (FinderException e) {
				e.printStackTrace();
			}
			
			add(form);
	}
	
	
	private void save(IWContext iwc, ServiceOfferChoice choice, ServiceOffer offer, boolean accepts) throws RemoteException {
		getBusiness().changeServiceOfferChoiceStatus(choice,accepts,iwc.getCurrentUser());

		Form form = new Form();
		
		form.add(getReceiptLayer(localize("service.offer.choice.saved", "Service offer choice finished"), localize("service.offer.choice.saved.text", "Thank you, your service choice has been saved and sent to the manager handling the service offer.")));
		
		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		form.add(clearLayer);
		
		Layer bottom = new Layer(Layer.DIV);
		bottom.setStyleClass("bottom");
		form.add(bottom);

		try {
			ICPage page = getUserBusiness(iwc).getHomePageForUser(iwc.getCurrentUser());
			Link link = getButtonLink(localize("my_page", "My page"));
			link.setStyleClass("homeButton");
			link.setPage(page);
			bottom.add(link);
		}
		catch (FinderException e) {
			//no homepage for user??
			e.printStackTrace();
		}
		
		add(form);
	}
	
	private Form createForm(IWContext iwc, int actionPhase) throws RemoteException {
		Form form = new Form();
		form.setID("serviceOfferForm");
		form.addParameter(PARAMETER_ACTION, actionPhase);
		form.maintainParameter(getBusiness().getSelectedCaseParameter());
		
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