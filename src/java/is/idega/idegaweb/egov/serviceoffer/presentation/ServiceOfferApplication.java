/*
 * $Id: ServiceOfferApplication.java,v 1.20 2006/05/31 11:11:28 laddi Exp $
 * Created on Oct 2, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.presentation;

import is.idega.idegaweb.egov.application.presentation.ApplicationForm;
import is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusiness;
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;

import java.rmi.RemoteException;
import java.sql.Date;
import java.text.NumberFormat;

import javax.ejb.FinderException;

import se.idega.idegaweb.commune.school.business.SchoolCommuneSession;
import se.idega.idegaweb.commune.school.presentation.inputhandler.SchoolGroupHandler;

import com.idega.block.school.business.SchoolBusiness;
import com.idega.block.school.data.SchoolClass;
import com.idega.block.school.data.SchoolSeason;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.builder.data.ICPage;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.TimeInput;
import com.idega.user.data.User;
import com.idega.util.IWTimestamp;

/**
 * An application for sending a service offer(description), that may have a
 * price, to a citizen or a group of citizens that then have to approve it.
 * 
 * Last modified: $Date: 2006/05/31 11:11:28 $ by $Author: laddi $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.20 $
 */
public class ServiceOfferApplication extends ApplicationForm {

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
	
	private IWResourceBundle iwrb;

	protected String getCaseCode() {
		return ServiceOfferConstants.CASE_CODE_KEY_SERVICE_OFFER;
	}

	public String getBundleIdentifier() {
		return ServiceOfferConstants.IW_BUNDLE_IDENTIFIER;
	}

	public void present(IWContext iwc) {
		this.iwrb = getResourceBundle(iwc);
		
		try {
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
					if (iwc.isParameterSet(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS)) {
						save(iwc);
					}
					else {
						showOverview(iwc);
					}
					break;
			}
		}
		catch (RemoteException re) {
			throw new IBORuntimeException(re);
		}
	}

	/**
	 * Shows the form to create an Service Offer
	 * 
	 * @param iwc
	 * @throws RemoteException
	 */
	private void showPhaseOne(IWContext iwc) throws RemoteException {
		Form form = createForm(iwc, ACTION_PHASE_ONE);

		form.add(getPhasesHeader(this.iwrb.getLocalizedString("service.offer.application.create_a_service_offer", "Create a new service offer"), 1, 4, false));

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		
		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		Layer helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("service.offer.application.text", "Here you can define a service offer and in the next step you can define the recipients.")));
		section.add(helpLayer);
		
		TextInput name = new TextInput(PARAMETER_SERVICE_NAME);
		name.keepStatusOnAction();
		name.setAsNotEmpty(this.iwrb.getLocalizedString("service.offer.application.cannot_be_empty", "The name of service offer must be filled in"));

		TextInput price = new TextInput(PARAMETER_SERVICE_PRICE);
		price.keepStatusOnAction();
		price.setAsDouble(this.iwrb.getLocalizedString("service.offer.application.price_must_be_number", "Price must be a number"), 2);

		RadioButton choiceYes = new RadioButton(PARAMETER_SERVICE_CHOICE_OPTIONAL, "Y");
		choiceYes.setStyleClass("radiobutton");
		choiceYes.setSelected(true);
		choiceYes.keepStatusOnAction();

		RadioButton choiceNo = new RadioButton(PARAMETER_SERVICE_CHOICE_OPTIONAL, "N");
		choiceNo.setStyleClass("radiobutton");
		choiceNo.keepStatusOnAction();
		
		int thisYear = (new IWTimestamp()).getYear();
		DateInput date = new DateInput(PARAMETER_SERVICE_DATE);
		date.setStyleClass("dateInput");
		date.setYearRange(thisYear, thisYear + 1);
		date.keepStatusOnAction();

		TimeInput time = new TimeInput(PARAMETER_SERVICE_TIME);
		time.setStyleClass("timeInput");
		time.keepStatusOnAction();

		DateInput deadline = new DateInput(PARAMETER_SERVICE_DEADLINE);
		deadline.setStyleClass("dateInput");
		deadline.keepStatusOnAction();
		deadline.setYearRange(thisYear, thisYear + 1);

		TextInput location = new TextInput(PARAMETER_SERVICE_LOCATION);
		location.keepStatusOnAction();

		RadioButton cash = new RadioButton(PARAMETER_SERVICE_PAYMENT_TYPE, ServiceOfferConstants.PAYMENT_TYPE_CASH);
		cash.setStyleClass("radiobutton");
		cash.setSelected(true);
		cash.keepStatusOnAction(true);

		RadioButton invoice = new RadioButton(PARAMETER_SERVICE_PAYMENT_TYPE, ServiceOfferConstants.PAYMENT_TYPE_INVOICE);
		invoice.setStyleClass("radiobutton");
		invoice.keepStatusOnAction(true);

		Layer formItem = new Layer();
		formItem.setStyleClass("formItem");
		Label label = new Label(this.iwrb.getLocalizedString("service.offer.application.name_of_service_offer", "Name of service offer"), name);
		formItem.add(label);
		formItem.add(name);
		section.add(formItem);

		formItem = new Layer();
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("service.offer.application.price_of_service_offer", "Price of service offer"), price);
		formItem.add(label);
		formItem.add(price);
		section.add(formItem);
		
		section.add(clearLayer);

		Heading1 heading = new Heading1(this.iwrb.getLocalizedString("service.offer.application.choice_optional", "Optional service"));
		heading.setStyleClass("subHeader");
		form.add(heading);
		
		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("service.offer.application.optional_service_help", "If custodians need to approve/reject the service offer, please select the appropriate option.")));
		section.add(helpLayer);
		
		formItem = new Layer();
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("radioButtonItem");
		label = new Label(this.iwrb.getLocalizedString("Yes", "Yes"), choiceYes);
		formItem.add(choiceYes);
		formItem.add(label);
		section.add(formItem);
		
		formItem = new Layer();
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("radioButtonItem");
		label = new Label(this.iwrb.getLocalizedString("No", "No"), choiceNo);
		formItem.add(choiceNo);
		formItem.add(label);
		section.add(formItem);

		section.add(clearLayer);

		heading = new Heading1(this.iwrb.getLocalizedString("service.offer.application.choose_dates", "Choose dates of service"));
		heading.setStyleClass("subHeader");
		form.add(heading);
		
		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("service.offer.application.date_help", "Please enter the dates of the service offer.")));
		section.add(helpLayer);
		
		formItem = new Layer();
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("service.offer.application.service_offer_date", "Date of service"), date);
		formItem.add(label);
		formItem.add(date);
		section.add(formItem);

		formItem = new Layer();
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("service.offer.application.service_offer_time", "Time of service offer"), time);
		formItem.add(label);
		formItem.add(time);
		section.add(formItem);

		formItem = new Layer();
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("service.offer.application.deadline_for_choice", "Choice deadline"), deadline);
		formItem.add(label);
		formItem.add(deadline);
		section.add(formItem);

		formItem = new Layer();
		formItem.setStyleClass("formItem");
		label = new Label(this.iwrb.getLocalizedString("service.offer.application.location", "Location"), location);
		formItem.add(label);
		formItem.add(location);
		section.add(formItem);

		section.add(clearLayer);

		heading = new Heading1(this.iwrb.getLocalizedString("service.offer.application.choose_payment_type", "Payment option"));
		heading.setStyleClass("subHeader");
		form.add(heading);
		
		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("service.offer.application.payment_option_help", "Please select the appropriate payment option type.")));
		section.add(helpLayer);
		
		formItem = new Layer();
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("radioButtonItem");
		label = new Label(this.iwrb.getLocalizedString("service.offer.application.payment.type.cash", "Cash"), cash);
		formItem.add(cash);
		formItem.add(label);
		section.add(formItem);

		/*formItem = new Layer();
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("radioButtonItem");
		label = new Label(iwrb.getLocalizedString("service.offer.application.payment.type.invoice", "Invoice"), invoice);
		formItem.add(invoice);
		formItem.add(label);
		section.add(formItem);*/

		section.add(clearLayer);

		heading = new Heading1(this.iwrb.getLocalizedString("service.offer.application.description", "Description of service"));
		heading.setStyleClass("subHeader");
		form.add(heading);
		
		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("service.offer.application.description_help", "Please enter the service offer's detailed description.")));
		section.add(helpLayer);
		
		TextArea text = new TextArea(PARAMETER_SERVICE_TEXT);
		text.setStyleClass("details");
		text.keepStatusOnAction();
		text.setMaximumCharacters(4000);
		section.add(text);

		Layer bottom = new Layer(Layer.DIV);
		bottom.setStyleClass("bottom");
		form.add(bottom);

		Link next = getButtonLink(this.iwrb.getLocalizedString("next", "Next"));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_TWO));
		next.setToFormSubmit(form);
		bottom.add(next);

		add(form);
	}

	private void showPhaseTwo(IWContext iwc) throws RemoteException {
		Form form = createForm(iwc, ACTION_PHASE_TWO);

		form.add(getPhasesHeader(this.iwrb.getLocalizedString("service.offer.application.create_a_service_offer", "Create a new service offer"), 2, 4, false));

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		
		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		Layer helpLayer = new Layer(Layer.DIV);
		helpLayer.setStyleClass("helperText");
		helpLayer.add(new Text(this.iwrb.getLocalizedString("service.offer.application.select_recipients", "Please select the recipients of the service offer.")));
		section.add(helpLayer);
		
		Date placementDate = new IWTimestamp(iwc.getParameter(PARAMETER_SERVICE_DATE)).getDate();
		int schoolID = getSchoolSession(iwc).getSchoolID();
		int seasonID = -1;
		try {
			SchoolSeason placementSeason = getSchoolBusiness(iwc).getSchoolSeasonHome().findSeasonByDate(getSchoolBusiness(iwc).getCategoryElementarySchool(), placementDate);
			seasonID = new Integer(placementSeason.getPrimaryKey().toString()).intValue();
		}
		catch (FinderException fe) {
			log(fe);
			
			try {
				SchoolSeason placementSeason = getSchoolBusiness(iwc).getSchoolSeasonHome().findNextSeason(getSchoolBusiness(iwc).getCategoryElementarySchool(), placementDate);
				seasonID = new Integer(placementSeason.getPrimaryKey().toString()).intValue();
			}
			catch (FinderException fe1) {
				log(fe1);
			}
		}
		
		SchoolGroupHandler recipients = new SchoolGroupHandler(schoolID, seasonID);
		recipients.setName(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS);
		recipients.setStyleClass("selectionBox");
		recipients.keepStatusOnAction();

		Layer formItem = new Layer();
		formItem.setStyleClass("formItem");
		formItem.add(recipients);
		section.add(formItem);

		Layer bottom = new Layer(Layer.DIV);
		bottom.setStyleClass("bottom");
		form.add(bottom);

		Link next = getButtonLink(this.iwrb.getLocalizedString("next", "Next"));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_OVERVIEW));
		next.setToFormSubmit(form);
		bottom.add(next);

		Link back = getButtonLink(this.iwrb.getLocalizedString("previous", "Previous"));
		back.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_ONE));
		back.setToFormSubmit(form);
		bottom.add(back);

		add(form);
	}

	private void showOverview(IWContext iwc) throws RemoteException {
		Form form = createForm(iwc, ACTION_OVERVIEW);

		form.add(getPhasesHeader(this.iwrb.getLocalizedString("service.offer.application.create_a_service_offer", "Create a new service offer"), 3, 4, false));

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		
		Layer section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		Layer formItem = new Layer();
		formItem.setStyleClass("formItem");
		Label label = new Label();
		label.add(new Text(this.iwrb.getLocalizedString("service.offer.application.name_of_service_offer", "Name of service offer")));
		Layer span = new Layer(Layer.SPAN);
		span.add(new Text(iwc.getParameter(PARAMETER_SERVICE_NAME)));
		formItem.add(label);
		formItem.add(span);
		section.add(formItem);

		if (iwc.isParameterSet(PARAMETER_SERVICE_PRICE)) {
			NumberFormat format = NumberFormat.getCurrencyInstance(iwc.getCurrentLocale());
			float price = Float.parseFloat(iwc.getParameter(PARAMETER_SERVICE_PRICE));
			
			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(this.iwrb.getLocalizedString("service.offer.application.price_of_service_offer", "Price of service offer")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(format.format(price)));
			formItem.add(label);
			formItem.add(span);
			section.add(formItem);
		}
		
		section.add(clearLayer);

		Heading1 heading = new Heading1(this.iwrb.getLocalizedString("service.offer.application.choice_optional", "Optional service"));
		heading.setStyleClass("subHeader");
		form.add(heading);
		
		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		String localKey = "Y".equals(iwc.getParameter(PARAMETER_SERVICE_CHOICE_OPTIONAL)) ? "Yes" : "No";
		formItem = new Layer();
		formItem.setStyleClass("formItem");
		formItem.setStyleClass("informationItem");
		label = new Label();
		label.add(new Text(this.iwrb.getLocalizedString("service.offer.application.choice_optional", "Optional service")));
		span = new Layer(Layer.SPAN);
		span.add(new Text(this.iwrb.getLocalizedString(localKey, localKey)));
		formItem.add(label);
		formItem.add(span);
		section.add(formItem);

		section.add(clearLayer);

		heading = new Heading1(this.iwrb.getLocalizedString("service.offer.application.choose_dates", "Choose dates of service"));
		heading.setStyleClass("subHeader");
		form.add(heading);
		
		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		if (iwc.isParameterSet(PARAMETER_SERVICE_DATE)) {
			IWTimestamp date = new IWTimestamp(iwc.getParameter(PARAMETER_SERVICE_DATE));
			
			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(this.iwrb.getLocalizedString("service.offer.application.service_offer_date", "Date of service")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(date.getLocaleDate(iwc.getCurrentLocale(), IWTimestamp.SHORT)));
			formItem.add(label);
			formItem.add(span);
			section.add(formItem);
		}
		
		if (iwc.isParameterSet(PARAMETER_SERVICE_TIME)) {
			String time = iwc.getParameter(PARAMETER_SERVICE_TIME);
			
			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(this.iwrb.getLocalizedString("service.offer.application.service_offer_time", "Time of service offer")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(new IWTimestamp(time.substring(0, time.indexOf("."))).getLocaleTime(iwc.getCurrentLocale(), IWTimestamp.SHORT)));
			formItem.add(label);
			formItem.add(span);
			section.add(formItem);
		}

		if (iwc.isParameterSet(PARAMETER_SERVICE_DEADLINE)) {
			IWTimestamp date = new IWTimestamp(iwc.getParameter(PARAMETER_SERVICE_DEADLINE));
			
			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(this.iwrb.getLocalizedString("service.offer.application.dealine_for_choice", "Choice deadline")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(date.getLocaleDate(iwc.getCurrentLocale(), IWTimestamp.SHORT)));
			formItem.add(label);
			formItem.add(span);
			section.add(formItem);
		}
		
		if (iwc.isParameterSet(PARAMETER_SERVICE_LOCATION)) {
			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(this.iwrb.getLocalizedString("service.offer.application.location", "Location")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(iwc.getParameter(PARAMETER_SERVICE_LOCATION)));
			formItem.add(label);
			formItem.add(span);
			section.add(formItem);
		}
		
		section.add(clearLayer);

		heading = new Heading1(this.iwrb.getLocalizedString("service.offer.application.choose_payment_type", "Payment option"));
		heading.setStyleClass("subHeader");
		form.add(heading);
		
		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		String paymentTypeLocalizationKey = ServiceOfferConstants.PAYMENT_TYPE_CASH.equals(iwc.getParameter(PARAMETER_SERVICE_PAYMENT_TYPE)) ? "service.offer.application.payment.type.cash" : "service.offer.application.payment.type.invoice";

		formItem = new Layer();
		formItem.setStyleClass("formItem");
		label = new Label();
		label.add(new Text(this.iwrb.getLocalizedString("service.offer.application.payment_type", "Payment option")));
		span = new Layer(Layer.SPAN);
		span.add(new Text(this.iwrb.getLocalizedString(paymentTypeLocalizationKey, paymentTypeLocalizationKey)));
		formItem.add(label);
		formItem.add(span);
		section.add(formItem);

		if (iwc.isParameterSet(PARAMETER_SERVICE_TEXT)) {
			section.add(clearLayer);

			heading = new Heading1(this.iwrb.getLocalizedString("service.offer.application.description", "Description of service"));
			heading.setStyleClass("subHeader");
			form.add(heading);
			
			section = new Layer(Layer.DIV);
			section.setStyleClass("formSection");
			form.add(section);
			
			formItem = new Layer();
			formItem.setStyleClass("formItem");
			formItem.setStyleClass("informationItem");
			label = new Label();
			label.add(new Text(this.iwrb.getLocalizedString("service.offer.application.description", "Description of service")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(iwc.getParameter(PARAMETER_SERVICE_TEXT)));
			formItem.add(label);
			formItem.add(span);
			section.add(formItem);
		}
		
		section.add(clearLayer);

		heading = new Heading1(this.iwrb.getLocalizedString("service.offer.application.recipients", "Recipients"));
		heading.setStyleClass("subHeader");
		form.add(heading);
		
		section = new Layer(Layer.DIV);
		section.setStyleClass("formSection");
		form.add(section);
		
		Lists groupList = new Lists();
		groupList.setStyleClass("formItemList");

		String[] groups = iwc.getParameterValues(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS);
		for (int i = 0; i < groups.length; i++) {
			try {
				SchoolClass group = getSchoolBusiness(iwc).getSchoolClassHome().findByPrimaryKey(new Integer(groups[i]));
				
				ListItem item = new ListItem();
				item.add(group.getSchoolClassName());
				groupList.add(item);
			}
			catch (FinderException fe) {
				fe.printStackTrace();
			}
		}

		section.add(groupList);

		section.add(clearLayer);

		section.add(getAttentionLayer(this.iwrb.getLocalizedString("service.offer.application.overview", "Please confirm the information below is correct.")));
		
		Layer bottom = new Layer(Layer.DIV);
		bottom.setStyleClass("bottom");
		form.add(bottom);

		Link next = getButtonLink(this.iwrb.getLocalizedString("next", "Next"));
		next.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_SAVE));
		next.setToFormSubmit(form);
		bottom.add(next);

		Link back = getButtonLink(this.iwrb.getLocalizedString("previous", "Previous"));
		back.setValueOnClick(PARAMETER_ACTION, String.valueOf(ACTION_PHASE_TWO));
		back.setToFormSubmit(form);
		bottom.add(back);

		add(form);
	}

	private void save(IWContext iwc) throws RemoteException {
		getBusiness(iwc).storeServiceOffer(iwc.getParameter(PARAMETER_SERVICE_NAME), iwc.getParameter(PARAMETER_SERVICE_PAYMENT_TYPE), iwc.getParameter(PARAMETER_SERVICE_CHOICE_OPTIONAL), iwc.getParameter(PARAMETER_SERVICE_DEADLINE), iwc.getParameter(PARAMETER_SERVICE_DATE), iwc.getParameter(PARAMETER_SERVICE_TIME), iwc.getParameter(PARAMETER_SERVICE_PRICE), iwc.getParameter(PARAMETER_SERVICE_LOCATION), iwc.getParameter(PARAMETER_SERVICE_TEXT), iwc.getParameterValues(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_TYPE), iwc.getParameterValues(PARAMETER_SERVICE_RECIPIENTS_SCHOOL), iwc.getParameterValues(PARAMETER_SERVICE_RECIPIENTS_SCHOOL_CLASS), getUser(iwc));

		addPhasesReceipt(iwc, this.iwrb.getLocalizedString("service.offer.application.create_a_service_offer", "Create a new service offer"), this.iwrb.getLocalizedString("application.service.offer.application.saved", "Finished creating a service offer"), this.iwrb.getLocalizedString("service.offer.application.saved.text", "Thank you, your service offer has been saved and sent to the corresponding recipients."), 4, 4, false);

		Layer clearLayer = new Layer(Layer.DIV);
		clearLayer.setStyleClass("Clear");
		add(clearLayer);
		
		Layer bottom = new Layer(Layer.DIV);
		bottom.setStyleClass("bottom");
		add(bottom);

		try {
			ICPage page = getUserBusiness(iwc).getHomePageForUser(iwc.getCurrentUser());
			Link link = getButtonLink(this.iwrb.getLocalizedString("my_page", "My page"));
			link.setStyleClass("homeButton");
			link.setPage(page);
			bottom.add(link);
		}
		catch (FinderException fe) {
			fe.printStackTrace();
		}
	}

	private Form createForm(IWContext iwc, int actionPhase) {
		Form form = new Form();
		form.setID("serviceOfferForm");

		form.addParameter(PARAMETER_ACTION, actionPhase);

		if (actionPhase != ACTION_PHASE_ONE) {
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

		if (actionPhase != ACTION_PHASE_TWO) {
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

	private ServiceOfferBusiness getBusiness(IWApplicationContext iwac) {
		try {
			return (ServiceOfferBusiness) IBOLookup.getServiceInstance(iwac, ServiceOfferBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}
	
	private SchoolBusiness getSchoolBusiness(IWApplicationContext iwac) {
		try {
			return (SchoolBusiness) IBOLookup.getServiceInstance(iwac, SchoolBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	private SchoolCommuneSession getSchoolSession(IWUserContext iwuc) {
		try {
			return (SchoolCommuneSession) IBOLookup.getSessionInstance(iwuc, SchoolCommuneSession.class);
		}
		catch (RemoteException e) {
			throw new IBORuntimeException(e);
		}
	}
}