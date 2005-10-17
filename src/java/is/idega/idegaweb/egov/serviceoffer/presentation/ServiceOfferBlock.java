/*
 * $Id: ServiceOfferBlock.java,v 1.2 2005/10/17 02:27:54 eiki Exp $
 * Created on Oct 2, 2005
 * 
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.presentation;

import is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusiness;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOffer;
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import java.sql.Timestamp;
import se.idega.idegaweb.commune.business.CommuneUserBusiness;
import se.idega.idegaweb.commune.presentation.CommuneBlock;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Text;
import com.idega.util.IWTimestamp;

/**
 * A base presentationclass for ServiceOffer applications and lists...
 * 
 *  Last modified: $Date: 2005/10/17 02:27:54 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
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

	protected void addServiceOffer(IWContext iwc, Layer layer, ServiceOffer offer) {
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

}