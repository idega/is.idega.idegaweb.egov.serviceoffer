/*
 * $Id: ServiceOfferChoiceBlock.java,v 1.2 2005/10/15 18:22:42 eiki Exp $
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
import java.sql.Timestamp;
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
import com.idega.util.IWTimestamp;

/**
 * A block for viewing/accepting/declining a service offer choice
 * 
 *  Last modified: $Date: 2005/10/15 18:22:42 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public class ServiceOfferChoiceBlock extends ServiceOfferBlock implements ServiceOfferConstants{
	
	private static final String PARAMETER_ACTION = "prm_servofc_action";
	
	public static final String PARAMETER_SERVICE_OFFER_CHOICE_ACCEPT = "SERVICE_CHOICE_ACCEPT";
	public static final String PARAMETER_SERVICE_OFFER_CHOICE_DECLINE = "SERVICE_CHOICE_DECLINE";
	public static final String PARAMETER_SERVICE_OFFER_CHOICE_COMMENT = "SERVICE_COMMENT";
	
	private static final int ACTION_PHASE_ONE = 1;
	private static final int ACTION_ACCEPT = 2;
	private static final int ACTION_DECLINE = 3;
	private static final int ACTION_OVERVIEW = 4;
	private static final int ACTION_SAVE = 5;
	
	private User user;
	
	public void present(IWContext iwc) {
		String selectedCaseId = null;
		try {		
			selectedCaseId = iwc.getParameter(getBusiness().getSelectedCaseParameter());
			if(selectedCaseId==null || "".equals(selectedCaseId)) {
				add(localize("service.offer.choice.no_case_selected", "NO CASE SELECTED"));
			}
			else{
				ServiceOfferChoice choice = (ServiceOfferChoice) getBusiness().getServiceOfferChoice(Integer.parseInt(selectedCaseId));
				ServiceOffer offer = (ServiceOffer)  getBusiness().getServiceOffer(((Integer)choice.getParentCase().getPrimaryKey()).intValue());
			
				switch (parseAction(iwc)) {
					case ACTION_PHASE_ONE:
						showPhaseOne(iwc,choice,offer);
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
	 * Shows the form to create an Service Offer
	 * @param iwc
	 * @param offer 
	 * @param choice 
	 * @throws RemoteException
	 */
	private void showPhaseOne(IWContext iwc, ServiceOfferChoice choice, ServiceOffer offer) throws RemoteException {
		Form form = createForm(iwc, ACTION_PHASE_ONE);
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
		
		Paragraph paragraph = new Paragraph();
		if(isOptional){
			paragraph.add(new Text(localize("service.offer.choice.text", "Please review the following service offer and respond to it by clicking on one of the buttons (Decline/Accept) at the bottom of the page")));
		}
		else{
			paragraph.add(new Text(localize("service.offer.choice.text_mandatory", "The following service offer is mandatory and is only for your viewing.")));
		}
		
		layer.add(paragraph);
		
		addChoiceOffer(iwc, layer,offer,choice);
		
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
	
	private void addChoiceOffer(IWContext iwc, Layer layer, ServiceOffer offer, ServiceOfferChoice choice) {
		Layer formElementName = new Layer();
		formElementName.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		Text name = new Text(offer.getServiceName());
		name.setStyleClass(STYLE_CLASS_FORM_TEXT);
		Text nameLabel = new Text(localize("service.offer.application.name_of_service_offer" ,"Name of service offer"));
		nameLabel.setStyle(STYLE_CLASS_LABEL_TEXT);
		formElementName.add(nameLabel);
		formElementName.add(name);
		layer.add(formElementName);
		
		double thePrice = offer.getServicePrice();
		if(thePrice>0){
			Layer formElementPrice = new Layer();
			formElementPrice.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
			
			//TODO make the formatting of the double optional as a set method? Here I strip the .x off if it starts with .0
			String priceString = String.valueOf(thePrice);
			Text price;
			if(priceString.endsWith(".0")){
				price = new Text( String.valueOf((int)thePrice));	
			}
			else{
				price = new Text(Double.toString(thePrice));
			}
			
			price.setStyleClass(STYLE_CLASS_FORM_TEXT);
			Text priceLabel = new Text(localize("service.offer.application.price_of_service_offer" ,"Price of service offer"));
			priceLabel.setStyle(STYLE_CLASS_LABEL_TEXT);
			formElementPrice.add(priceLabel);
			formElementPrice.add(price);
			layer.add(formElementPrice);
			
		}
		
		Timestamp timestamp = offer.getServiceDate();
		if(timestamp!=null){
			IWTimestamp theTimestamp = new IWTimestamp(timestamp);
			String dateString = theTimestamp.getLocaleDate(iwc.getCurrentLocale());
			
			Layer formElementDate = new Layer();
			formElementDate.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
			Text date = new Text(dateString);
			date.setStyleClass(STYLE_CLASS_FORM_TEXT);
			
			Text dateLabel = new Text(localize("service.offer.application.service_offer_date" ,"Date of service"));
			dateLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
			formElementDate.add(dateLabel);
			formElementDate.add(date);
			layer.add(formElementDate);
			
			Layer formElementTime = new Layer();
			formElementTime.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
			int hour = theTimestamp.getHour();
			int minute = theTimestamp.getMinute();
			Text time = new Text( ((hour<10)?"0"+hour : ""+hour)+":"+ ((minute<10)?"0"+minute:""+minute));
			time.setStyleClass(STYLE_CLASS_FORM_TEXT);
					
			Text timeLabel = new Text(localize("service.offer.application.service_offer_time" ,"Time of service offer"));
			timeLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
			formElementTime.add(timeLabel);
			formElementTime.add(time);
			layer.add(formElementTime);
		
		}
		
		Timestamp stamper = offer.getServiceDate();
		if(stamper!=null){
			IWTimestamp theTimestamp = new IWTimestamp(stamper);
			String dateString = theTimestamp.getLocaleDate(iwc.getCurrentLocale());
			Layer formElementDeadline = new Layer();
			formElementDeadline.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
			Text deadline = new Text(dateString);
			deadline.setStyleClass(STYLE_CLASS_FORM_TEXT);
			Text deadlineLabel = new Text(localize("service.offer.application.dealine_for_choice" ,"Choice deadline"));
			deadlineLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
			formElementDeadline.add(deadlineLabel);
			formElementDeadline.add(deadline);
			layer.add(formElementDeadline);
		}
		
		String locationText = offer.getServiceLocation();
		
		if(locationText!=null){
			Layer formElementLocation = new Layer();
			formElementLocation.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
			Text location = new Text(locationText);
			location.setStyleClass(STYLE_CLASS_FORM_TEXT);
			Text locationLabel = new Text(localize("service.offer.application.location" ,"Location"));
			locationLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
			formElementLocation.add(locationLabel);
			formElementLocation.add(location);
			layer.add(formElementLocation);
		}
		
		if(thePrice>0){
			Layer formElementPaymentType = new Layer();
			formElementPaymentType.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
			String paymentTypeLocalizationKey = PAYMENT_TYPE_CASH.equals(offer.getServicePaymentType())?"service.offer.application.payment.type.cash" :"service.offer.application.payment.type.invoice";
			Text paymentType = new Text(localize(paymentTypeLocalizationKey,paymentTypeLocalizationKey));
			paymentType.setStyleClass(STYLE_CLASS_FORM_TEXT);
			Text paymentTypeText = new Text(localize("service.offer.application.payment_type" ,"Payment option"));
			paymentTypeText.setStyleClass(STYLE_CLASS_LABEL_TEXT);
			formElementPaymentType.add(paymentTypeText);
			formElementPaymentType.add(paymentType);
			layer.add(formElementPaymentType);
		}
		
		String description = offer.getServiceText();
		if(description!=null){
			Layer formElementText = new Layer();
			formElementText.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
			Text text = new Text(description);
			text.setStyleClass(STYLE_CLASS_SERVICE_DESCRIPTION);
			Text textLabel = new Text(localize("service.offer.application.description" ,"Description of service"));
			textLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
			formElementText.add(textLabel);
			formElementText.add(text);
			layer.add(formElementText);			
		}
	}

	private void showOverview(IWContext iwc, ServiceOfferChoice choice, ServiceOffer offer, boolean accepts) throws RemoteException {
		Form form = createForm(iwc, ACTION_OVERVIEW);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		
		layer.add(new Heading1(localize("service.offer.choice.make_a_service_choice2", "Make a service offer choice 2 of 2")));
		
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
	
	private void save(IWContext iwc, ServiceOfferChoice choice, ServiceOffer offer) throws RemoteException {
		
		//TODO save the result
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		add(layer);
		
		layer.add(new Heading1(localize("service.offer.choice.saved", "Service offer choice finished")));
		
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
		
		//todo maintain the choice somehow
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