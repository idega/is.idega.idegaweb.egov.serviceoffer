/*
 * $Id: ServiceOfferApplication.java,v 1.8 2005/10/16 17:53:04 eiki Exp $
 * Created on Oct 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.presentation;

import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import java.rmi.RemoteException;
import javax.ejb.FinderException;
import se.idega.idegaweb.commune.school.presentation.inputhandler.SchoolGroupHandler;
import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolSeason;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.GenericButton;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.TimeInput;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

/**
 * An application for sending a service offer(description), that may have a price, to a citizen or a group of citizens
 * that then have to approve it.
 * 
 *  Last modified: $Date: 2005/10/16 17:53:04 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.8 $
 */
public class ServiceOfferApplication extends ServiceOfferBlock implements ServiceOfferConstants{
	
	private static final String PARAMETER_ACTION = "prm_servoff_action";
	
	public static final String PARAMETER_SERVICE_PAYMENT_TYPE = "SERVICE_PAYMENT_TYPE";
	public static final String PARAMETER_SERVICE_CHOICE_OPTIONAL = "SERVICE_CHOICE_OPTIONAL";
	public static final String PARAMETER_SERVICE_DEADLINE = "SERVICE_DEADLINE";
	public static final String PARAMETER_SERVICE_DATE = "SERVICE_DATE";
	public static final String PARAMETER_SERVICE_TIME = "SERVICE_TIME";
	public static final String PARAMETER_SERVICE_PRICE = "SERVICE_PRICE";
	public static final String PARAMETER_SERVICE_LOCATION = "SERVICE_LOCATION";
	public static final String PARAMETER_SERVICE_NAME = "SERVICE_NAME";
	public static final String PARAMETER_SERVICE_TEXT = "SERVICE_TEXT";
	public static final String PARAMETER_SERVICE_RECIPIENTS_SCHOOL_TYPE = "SERVICE_RECIP_SCH_TYPE";
	public static final String PARAMETER_SERVICE_RECIPIENTS_SCHOOL = "SERVICE_RECIP_SCH";
	public static final String PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS = "SERVICE_RECIP_SCH_CLASS";
	
	private static final int ACTION_PHASE_ONE = 1;
	private static final int ACTION_PHASE_TWO = 2;
	private static final int ACTION_OVERVIEW = 3;
	private static final int ACTION_SAVE = 4;

	private User user;
	private School school;
	private SchoolSeason season;

	
	public void present(IWContext iwc) {
		try {
			try {
				season = getBusiness().getOngoingSeason();
			}
			catch (FinderException fe) {
				try {
					season = getBusiness().getNextSeason();
				}
				catch (FinderException fex) {
					add(getErrorText(localize("no_season_found", "No season found.")));
					return;
				}
			}
			
			switch (parseAction(iwc)) {
				case ACTION_PHASE_ONE:
					showPhaseOne(iwc);
					break;
					
				case ACTION_PHASE_TWO:
					showPhaseTwo(iwc);
					break;

				case ACTION_OVERVIEW:
					showOverview(iwc);
					break;

				case ACTION_SAVE:
					if(iwc.isParameterSet(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS)){
						save(iwc);
					}
					else{
						showOverview(iwc);
					}
					break;
			}
			
			add(getHelpButton("service_offer_application"));
			
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}
	
	/**
	 * Shows the form to create an Service Offer
	 * @param iwc
	 * @throws RemoteException
	 */
	private void showPhaseOne(IWContext iwc) throws RemoteException {
		Form form = createForm(iwc, ACTION_PHASE_ONE);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		
		layer.add(new Heading1(localize("service.offer.application.create_a_service_offer", "Create a new service offer step 1 of 3")));
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(localize("service.offer.application.text", "Here you can define a service offer and in the next step you can define the recipients.")));
		layer.add(paragraph);
		
		Layer formElementName = new Layer();
		formElementName.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		TextInput name = new TextInput(PARAMETER_SERVICE_NAME);
		name.keepStatusOnAction();
		name.setAsNotEmpty(localize("service.offer.application.cannot_be_empty","The name of service offer must be filled in"));
		Label nameLabel = new Label(localize("service.offer.application.name_of_service_offer" ,"Name of service offer"), name);
		formElementName.add(nameLabel);
		formElementName.add(name);
		layer.add(formElementName);
		
		Layer formElementPrice = new Layer();
		formElementPrice.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		TextInput price = new TextInput(PARAMETER_SERVICE_PRICE);
		price.keepStatusOnAction();
		price.setAsDouble(localize("service.offer.application.price_must_be_number","Price must be a number"),2);
		Label priceLabel = new Label(localize("service.offer.application.price_of_service_offer" ,"Price of service offer"), price);
		formElementPrice.add(priceLabel);
		formElementPrice.add(price);
		layer.add(formElementPrice);
		
		Layer formElementChoice = new Layer();
		formElementChoice.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		RadioButton choiceYes = new RadioButton(PARAMETER_SERVICE_CHOICE_OPTIONAL, "Y");//is optional
		RadioButton choiceNo = new RadioButton(PARAMETER_SERVICE_CHOICE_OPTIONAL, "N");//is not optional
		choiceYes.setSelected(true);
		choiceYes.keepStatusOnAction();
		choiceNo.keepStatusOnAction();
		Label choiceYesLabel = new Label(localize("Yes" ,"Yes"), choiceYes);
		choiceYesLabel.setStyleClass("labelRadioButton");
		Label choiceNoLabel = new Label(localize("No" ,"No"), choiceNo);
		choiceNoLabel.setStyleClass("labelRadioButton");
		Text choiceText = new Text(localize("service.offer.application.choice_optional" ,"Optional service"));
		choiceText.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		formElementChoice.add(choiceText);
		formElementChoice.add(choiceYesLabel);
		formElementChoice.add(choiceYes);
		formElementChoice.add(choiceNoLabel);
		formElementChoice.add(choiceNo);
		layer.add(formElementChoice);
		
		int thisYear = (new IWTimestamp()).getYear();
		
		Layer formElementDate = new Layer();
		formElementDate.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		//DatePicker date = new DatePicker(PARAMETER_SERVICE_DATE);
		DateInput date = new DateInput(PARAMETER_SERVICE_DATE);
		date.setYearRange(thisYear, thisYear+1);
		date.keepStatusOnAction();
		Label dateLabel = new Label(localize("service.offer.application.service_offer_date" ,"Date of service"),date);
		formElementDate.add(dateLabel);
		formElementDate.add(date);
		layer.add(formElementDate);
		
		Layer formElementTime = new Layer();
		formElementTime.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		TimeInput time = new TimeInput(PARAMETER_SERVICE_TIME);
		time.keepStatusOnAction();
		Label timeLabel = new Label(localize("service.offer.application.service_offer_time" ,"Time of service offer"), time);
		formElementTime.add(timeLabel);
		formElementTime.add(time);
		layer.add(formElementTime);
		
		Layer formElementDeadline = new Layer();
		formElementDeadline.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		//date picker is missing keepstatus method
//		DatePicker deadline = new DatePicker(PARAMETER_SERVICE_DEADLINE);
//		Text deadlineLabel = new Text(localize("service.offer.application.dealine_for_choice" ,"Choice deadline"));
		DateInput deadline = new DateInput(PARAMETER_SERVICE_DEADLINE);
		deadline.keepStatusOnAction();
		deadline.setYearRange(thisYear, thisYear+1);
		Label deadlineLabel = new Label(localize("service.offer.application.deadline_for_choice" ,"Choice deadline"),deadline);
		deadlineLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		formElementDeadline.add(deadlineLabel);
		formElementDeadline.add(deadline);
		layer.add(formElementDeadline);
		
		Layer formElementLocation = new Layer();
		formElementLocation.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		TextInput location = new TextInput(PARAMETER_SERVICE_LOCATION);
		location.keepStatusOnAction();
		Label locationLabel = new Label(localize("service.offer.application.location" ,"Location"), location);
		formElementLocation.add(locationLabel);
		formElementLocation.add(location);
		layer.add(formElementLocation);
		
		
		Layer formElementPaymentType = new Layer();
		formElementPaymentType.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		RadioButton cash = new RadioButton(PARAMETER_SERVICE_PAYMENT_TYPE, PAYMENT_TYPE_CASH);
		RadioButton invoice = new RadioButton(PARAMETER_SERVICE_PAYMENT_TYPE, PAYMENT_TYPE_INVOICE);
		cash.setSelected(true);
		cash.keepStatusOnAction(true);
		invoice.keepStatusOnAction(true);
		Label cashLabel = new Label(localize("service.offer.application.payment.type.cash" ,"Cash"), cash);
		cashLabel.setStyleClass("labelRadioButton");
		Label invoiceLabel = new Label(localize("service.offer.application.payment.type.invoice" ,"Invoice"), invoice);
		invoiceLabel.setStyleClass("labelRadioButton");
		Text paymentTypeText = new Text(localize("service.offer.application.payment_type" ,"Payment option"));
		paymentTypeText.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		formElementPaymentType.add(paymentTypeText);
		formElementPaymentType.add(cashLabel);
		formElementPaymentType.add(cash);
		formElementPaymentType.add(invoiceLabel);
		formElementPaymentType.add(invoice);
		layer.add(formElementPaymentType);
		
		Layer formElementText = new Layer();
		formElementText.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		TextArea text = new TextArea(PARAMETER_SERVICE_TEXT);
		
		text.keepStatusOnAction();
		text.setMaximumCharacters(4000);
		Label textLabel = new Label(localize("service.offer.application.description" ,"Description of service"), text);
		formElementText.add(textLabel);
		formElementText.add(text);
		layer.add(formElementText);	
		
		layer.add(new CSSSpacer());
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonDiv");
		layer.add(buttonLayer);
		
		SubmitButton next = new SubmitButton(localize("next", "Next"));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_TWO));
		buttonLayer.add(next);

		add(form);
	}
	
	private void showPhaseTwo(IWContext iwc) throws RemoteException {
		Form form = createForm(iwc, ACTION_PHASE_TWO);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		
		layer.add(new Heading1(localize("service.offer.application.create_a_service_offer2", "Create a new service offer step 2 of 3")));
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(localize("service.offer.application.select_recipients", "Please select the recipients of the service offer.")));
		layer.add(paragraph);
		
		Layer formElementRecipient = new Layer();
		formElementRecipient.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		//TODO create a proper chooser
//		SchoolGroupSelector recipients = new SchoolGroupSelector(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_TYPE,PARAMETER_SERVICE_RECIPIENTS_SCHOOL,PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS);
//		recipients.keepStatusOnAction();
//		
//		recipients.setSchoolCategory(getBusiness().getSchoolBusiness().getCategoryElementarySchool());
//		School usersSchool = null;
//		try {
//			usersSchool = getBusiness().getCommuneUserBusiness().getFirstManagingSchoolForUser(getUser(iwc));
//			recipients.setSelectedSchool(usersSchool.getPrimaryKey());
//		}
//		catch (FinderException e) {
//			e.printStackTrace();
//		}
		SchoolGroupHandler recipients = new SchoolGroupHandler();
		//Doesn't work
		//recipients.setAsNotEmpty(localize("service.offer.application.must_select_recipients","At least one recipient must be chosen"));
		
		recipients.setName(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS);
		recipients.setStyleClass("selectionBox");
		recipients.keepStatusOnAction();
		
//		Label recipientsLabel = new Label(localize("service.offer.application.recipients" ,"Recipients"), recipients);
//		formElementRecipient.add(recipientsLabel);
		formElementRecipient.add(recipients);
		layer.add(formElementRecipient);
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonDiv");
		layer.add(buttonLayer);
		
		SubmitButton previous = new SubmitButton(localize("previous", "Previous"));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_ONE));
		SubmitButton next = new SubmitButton(localize("next", "Next"));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_OVERVIEW));
		buttonLayer.add(previous);
		buttonLayer.add(next);

		add(form);
	}
	
	private void showOverview(IWContext iwc) throws RemoteException {
		Form form = createForm(iwc, ACTION_OVERVIEW);
		
		Layer layer = new Layer(Layer.DIV);
		layer.setID("phasesDiv");
		form.add(layer);
		
		layer.add(new Heading1(localize("service.offer.application.create_a_service_offer3", "Create a new service offer step 3 of 3")));
		
		Paragraph paragraph = new Paragraph();
		paragraph.add(new Text(localize("service.offer.application.overview", "Please confirm the information below is correct.")));
		layer.add(paragraph);
		
		Layer formElementName = new Layer();
		formElementName.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		Text name = new Text(iwc.getParameter(PARAMETER_SERVICE_NAME));
		name.setStyleClass(STYLE_CLASS_FORM_TEXT);
		Text nameLabel = new Text(localize("service.offer.application.name_of_service_offer" ,"Name of service offer"));
		nameLabel.setStyle(STYLE_CLASS_LABEL_TEXT);
		formElementName.add(nameLabel);
		formElementName.add(name);
		layer.add(formElementName);
		
		Layer formElementPrice = new Layer();
		formElementPrice.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		Text price = new Text(iwc.getParameter(PARAMETER_SERVICE_PRICE));
		price.setStyleClass(STYLE_CLASS_FORM_TEXT);
		Text priceLabel = new Text(localize("service.offer.application.price_of_service_offer" ,"Price of service offer"));
		priceLabel.setStyle(STYLE_CLASS_LABEL_TEXT);
		formElementPrice.add(priceLabel);
		formElementPrice.add(price);
		layer.add(formElementPrice);
		
		Layer formElementChoice = new Layer();
		formElementChoice.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		String localKey = "Y".equals(iwc.getParameter(PARAMETER_SERVICE_CHOICE_OPTIONAL))?"Yes":"No";
		Text choice = new Text(localize(localKey,localKey));
		choice.setStyleClass(STYLE_CLASS_FORM_TEXT);
		Text choiceText = new Text(localize("service.offer.application.choice_optional" ,"Optional service"));
		choiceText.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		formElementChoice.add(choiceText);
		formElementChoice.add(choice);
		layer.add(formElementChoice);
		
		Layer formElementDate = new Layer();
		formElementDate.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		DateInput date = new DateInput(PARAMETER_SERVICE_DATE);
		date.setContent(iwc.getParameter(PARAMETER_SERVICE_DATE));
		date.setDisabled(true);
		Text dateLabel = new Text(localize("service.offer.application.service_offer_date" ,"Date of service"));
		dateLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		formElementDate.add(dateLabel);
		formElementDate.add(date);
		layer.add(formElementDate);
		
		Layer formElementTime = new Layer();
		formElementTime.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		TimeInput time = new TimeInput(PARAMETER_SERVICE_TIME);
		time.setContent(iwc.getParameter(PARAMETER_SERVICE_TIME));
		time.setDisabled(true);
		
		Text timeLabel = new Text(localize("service.offer.application.service_offer_time" ,"Time of service offer"));
		timeLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		formElementTime.add(timeLabel);
		formElementTime.add(time);
		layer.add(formElementTime);
		
		Layer formElementDeadline = new Layer();
		formElementDeadline.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		DateInput deadline = new DateInput(PARAMETER_SERVICE_DEADLINE);
		deadline.setContent(iwc.getParameter(PARAMETER_SERVICE_DEADLINE));
		deadline.setDisabled(true);
		Text deadlineLabel = new Text(localize("service.offer.application.dealine_for_choice" ,"Choice deadline"));
		deadlineLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		formElementDeadline.add(deadlineLabel);
		formElementDeadline.add(deadline);
		layer.add(formElementDeadline);
		
		Layer formElementLocation = new Layer();
		formElementLocation.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		Text location = new Text(iwc.getParameter(PARAMETER_SERVICE_LOCATION));
		location.setStyleClass(STYLE_CLASS_FORM_TEXT);
		Text locationLabel = new Text(localize("service.offer.application.location" ,"Location"));
		locationLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		formElementLocation.add(locationLabel);
		formElementLocation.add(location);
		layer.add(formElementLocation);
		
		Layer formElementPaymentType = new Layer();
		formElementPaymentType.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		String paymentTypeLocalizationKey = PAYMENT_TYPE_CASH.equals(iwc.getParameter(PARAMETER_SERVICE_PAYMENT_TYPE))?"service.offer.application.payment.type.cash" :"service.offer.application.payment.type.invoice";
		Text paymentType = new Text(localize(paymentTypeLocalizationKey,paymentTypeLocalizationKey));
		paymentType.setStyleClass(STYLE_CLASS_FORM_TEXT);
		Text paymentTypeText = new Text(localize("service.offer.application.payment_type" ,"Payment option"));
		paymentTypeText.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		formElementPaymentType.add(paymentTypeText);
		formElementPaymentType.add(paymentType);
		layer.add(formElementPaymentType);

		Layer formElementText = new Layer();
		formElementText.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		Text text = new Text( iwc.getParameter(PARAMETER_SERVICE_TEXT));
		text.setStyleClass(STYLE_CLASS_SERVICE_DESCRIPTION);
		Text textLabel = new Text(localize("service.offer.application.description" ,"Description of service"));
		textLabel.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		formElementText.add(textLabel);
		formElementText.add(text);
		layer.add(formElementText);		
				
		Layer formElementRecipient = new Layer();
		formElementRecipient.setStyleClass(STYLE_CLASS_FORM_ELEMENT);
		
		Text recipientsText = new Text(localize("service.offer.application.recipients" ,"Recipients"));
		recipientsText.setStyleClass(STYLE_CLASS_LABEL_TEXT);
		
		SchoolGroupHandler recipients = new SchoolGroupHandler();
		recipients.setName(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS);
		recipients.setStyleClass("selectionBox");
		recipients.setDisabled(true);	
		recipients.setToShowOnlySelected(true);
		recipients.keepStatusOnAction();
		
		formElementRecipient.add(recipientsText);
		formElementRecipient.add(recipients);
		layer.add(formElementRecipient);
		
		layer.add(new CSSSpacer());
		
		Layer buttonLayer = new Layer(Layer.DIV);
		buttonLayer.setStyleClass("buttonDiv");
		layer.add(buttonLayer);
		
		SubmitButton save = new SubmitButton(localize("save", "Save"));
		save.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));
		//save.setDisabled(!canSave);
		SubmitButton previous = new SubmitButton(localize("previous", "Previous"));
		previous.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_TWO));
		buttonLayer.add(previous);
		buttonLayer.add(save);

		add(form);
	}
	
	private void save(IWContext iwc) throws RemoteException {
		
			getBusiness().storeServiceOffer(iwc.getParameter(PARAMETER_SERVICE_NAME),
					iwc.getParameter(PARAMETER_SERVICE_PAYMENT_TYPE),
					iwc.getParameter(PARAMETER_SERVICE_CHOICE_OPTIONAL),
					iwc.getParameter(PARAMETER_SERVICE_DEADLINE),
					iwc.getParameter(PARAMETER_SERVICE_DATE),
					iwc.getParameter(PARAMETER_SERVICE_TIME),
					iwc.getParameter(PARAMETER_SERVICE_PRICE),
					iwc.getParameter(PARAMETER_SERVICE_LOCATION),
					iwc.getParameter(PARAMETER_SERVICE_TEXT),
					iwc.getParameterValues(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_TYPE),
					iwc.getParameterValues(PARAMETER_SERVICE_RECIPIENTS_SCHOOL),
					iwc.getParameterValues(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS),
					getUser(iwc));
			
			
			Layer layer = new Layer(Layer.DIV);
			layer.setID("phasesDiv");
			add(layer);
			
			layer.add(new Heading1(localize("service.offer.application.saved", "Finished creating a service offer")));
			
			Paragraph paragraph = new Paragraph();
			paragraph.add(new Text(localize("service.offer.application.saved.text", "Thank you, your service offer has been saved and sent to the corresponding recipients.")));
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
	
	private Form createForm(IWContext iwc, int actionPhase) {
		Form form = new Form();
		form.setID("serviceOfferForm");
		
		form.addParameter(PARAMETER_ACTION, actionPhase);
		
		if(actionPhase!=ACTION_PHASE_ONE){
			form.maintainParameter(PARAMETER_SERVICE_NAME);
			form.maintainParameter(PARAMETER_SERVICE_PAYMENT_TYPE);
			form.maintainParameter(PARAMETER_SERVICE_CHOICE_OPTIONAL);
			form.maintainParameter(PARAMETER_SERVICE_DEADLINE);
			form.maintainParameter(PARAMETER_SERVICE_DATE);
			form.maintainParameter(PARAMETER_SERVICE_PRICE);
			form.maintainParameter(PARAMETER_SERVICE_LOCATION);
			form.maintainParameter(PARAMETER_SERVICE_TEXT);
			form.maintainParameter(PARAMETER_SERVICE_TIME);
		}
		
		if(actionPhase!=ACTION_PHASE_TWO){
			form.maintainParameter(PARAMETER_SERVICE_RECIPIENTS_SCHOOL);
			form.maintainParameter(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS);
			form.maintainParameter(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_TYPE);
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