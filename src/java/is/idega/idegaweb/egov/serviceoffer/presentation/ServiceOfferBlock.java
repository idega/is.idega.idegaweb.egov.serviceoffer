/*
 * $Id: ServiceOfferBlock.java,v 1.1 2005/10/02 23:42:29 eiki Exp $
 * Created on Oct 2, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.presentation;

import is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusiness;
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import java.rmi.RemoteException;
import se.idega.idegaweb.commune.business.CommuneUserBusiness;
import se.idega.idegaweb.commune.presentation.CommuneBlock;
import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolClass;
import com.idega.block.school.data.SchoolClassMember;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.Phone;
import com.idega.core.location.data.Address;
import com.idega.core.location.data.PostalCode;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Heading1;
import com.idega.presentation.text.Text;
import com.idega.user.business.NoEmailFoundException;
import com.idega.user.business.NoPhoneFoundException;
import com.idega.user.data.User;
import com.idega.util.PersonalIDFormatter;

/**
 * A base presentationclass for ServiceOffer applications and lists...
 * 
 *  Last modified: $Date: 2005/10/02 23:42:29 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public abstract class ServiceOfferBlock extends CommuneBlock implements ServiceOfferConstants {

	private ServiceOfferBusiness business;
	private CommuneUserBusiness uBusiness;
	protected static final String STYLENAME_SERVICE_OFFER_FORM = "serviceOfferForm";
	protected final static String STYLENAME_LIST_TABLE = "listTable";
	protected final static String STYLENAME_LIST_TABLE_ODD_ROW = "listTable_oddRow";
	protected final static String STYLENAME_LIST_TABLE_EVEN_ROW = "listTable_evenRow";

	public void main(IWContext iwc) throws Exception {
		setBundle(getBundle(iwc));
		setResourceBundle(getResourceBundle(iwc));
		business = getBusiness(iwc);
		uBusiness = getUserBusiness(iwc);
		present(iwc);
	}

	protected ServiceOfferBusiness getBusiness() {
		return business;
	}

	protected CommuneUserBusiness getUserBusiness() {
		return uBusiness;
	}

	private ServiceOfferBusiness getBusiness(IWApplicationContext iwac) {
		try {
			return (ServiceOfferBusiness) IBOLookup.getServiceInstance(iwac, ServiceOfferBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	protected CommuneUserBusiness getUserBusiness(IWApplicationContext iwac) {
		try {
			return super.getUserBusiness(iwac);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	public String getBundleIdentifier() {
		return ServiceOfferConstants.IW_BUNDLE_IDENTIFIER;
	}

	public abstract void present(IWContext iwc);

	protected Layer getPersonInfo(IWContext iwc, SchoolClassMember member) throws RemoteException {
		User user = member.getStudent();
		SchoolClass group = member.getSchoolClass();
		School school = group.getSchool();
		return getPersonInfo(iwc, user, school, group);
	}

	protected Layer getPersonInfo(IWContext iwc, User user, School school, SchoolClass group) throws RemoteException {
		Layer layer = new Layer();
		layer.setID("personInfo");
		Address address = getUserBusiness().getUsersMainAddress(user);
		PostalCode postal = null;
		if (address != null) {
			postal = address.getPostalCode();
		}
		Phone phone = null;
		try {
			phone = getUserBusiness().getUsersHomePhone(user);
		}
		catch (NoPhoneFoundException npfe) {
			phone = null;
		}
		Email email = null;
		try {
			email = getUserBusiness().getUsersMainEmail(user);
		}
		catch (NoEmailFoundException nefe) {
			email = null;
		}
		Layer formElement = new Layer(Layer.DIV);
		formElement.setStyleClass("personInfoItem");
		Heading1 heading = new Heading1(localize("name", "Name"));
		Text text = new Text(user.getName());
		formElement.add(heading);
		formElement.add(text);
		layer.add(formElement);
		formElement = new Layer(Layer.DIV);
		formElement.setStyleClass("personInfoItem");
		heading = new Heading1(localize("personal_id", "Personal ID"));
		text = new Text(PersonalIDFormatter.format(user.getPersonalID(), iwc.getCurrentLocale()));
		formElement.add(heading);
		formElement.add(text);
		layer.add(formElement);
		formElement = new Layer(Layer.DIV);
		formElement.setStyleClass("personInfoItem");
		heading = new Heading1(localize("home_phone", "Home phone"));
		if (phone != null && phone.getNumber() != null) {
			text = new Text(phone.getNumber());
		}
		else {
			text = new Text("-");
		}
		formElement.add(heading);
		formElement.add(text);
		layer.add(formElement);
		formElement = new Layer(Layer.DIV);
		formElement.setStyleClass("personInfoItem");
		heading = new Heading1(localize("address", "Address"));
		if (address != null) {
			text = new Text(address.getStreetAddress());
		}
		else {
			text = new Text("-");
		}
		formElement.add(heading);
		formElement.add(text);
		layer.add(formElement);
		formElement = new Layer(Layer.DIV);
		formElement.setStyleClass("personInfoItem");
		heading = new Heading1(localize("zip_code", "Postal code"));
		if (postal != null) {
			text = new Text(postal.getPostalCode());
		}
		else {
			text = new Text("-");
		}
		formElement.add(heading);
		formElement.add(text);
		layer.add(formElement);
		formElement = new Layer(Layer.DIV);
		formElement.setStyleClass("personInfoItem");
		heading = new Heading1(localize("zip_city", "City"));
		if (postal != null) {
			text = new Text(postal.getName());
		}
		else {
			text = new Text("-");
		}
		formElement.add(heading);
		formElement.add(text);
		layer.add(formElement);
		if (school != null && group != null) {
			formElement = new Layer(Layer.DIV);
			formElement.setStyleClass("personInfoItem");
			heading = new Heading1(localize("school", "School"));
			text = new Text(school.getSchoolName());
			formElement.add(heading);
			formElement.add(text);
			layer.add(formElement);
			formElement = new Layer(Layer.DIV);
			formElement.setStyleClass("personInfoItem");
			heading = new Heading1(localize("group", "Group"));
			text = new Text(group.getSchoolClassName());
			formElement.add(heading);
			formElement.add(text);
			layer.add(formElement);
		}
		formElement = new Layer(Layer.DIV);
		formElement.setStyleClass("personInfoItem");
		heading = new Heading1(localize("email", "E-mail"));
		if (email != null && email.getEmailAddress() != null) {
			text = new Text(email.getEmailAddress());
		}
		else {
			text = new Text("-");
		}
		formElement.add(heading);
		formElement.add(text);
		layer.add(formElement);
		Layer clear = new Layer(Layer.DIV);
		clear.setStyleClass("Clear");
		layer.add(clear);
		return layer;
	}
}