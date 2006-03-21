/*
 * $Id: ServiceOfferBlock.java,v 1.5 2006/03/21 09:00:58 laddi Exp $
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
import java.text.NumberFormat;

import se.idega.idegaweb.commune.business.CommuneUserBusiness;
import se.idega.idegaweb.commune.presentation.CommuneBlock;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Label;
import com.idega.util.IWTimestamp;

/**
 * A base presentationclass for ServiceOffer applications and lists...
 * 
 *  Last modified: $Date: 2006/03/21 09:00:58 $ by $Author: laddi $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.5 $
 */
public abstract class ServiceOfferBlock extends CommuneBlock {

	private ServiceOfferBusiness business;
	private CommuneUserBusiness uBusiness;

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
		addServiceOffer(iwc, layer, offer, true);
	}
	
	protected void addServiceOffer(IWContext iwc, Layer layer, ServiceOffer offer, boolean showName) {
		Layer formItem;
		Label label;
		Layer span;
		
		if (showName) {
			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(localize("service.offer.application.name_of_service_offer" ,"Name of service offer")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(offer.getServiceName()));
			formItem.add(label);
			formItem.add(span);
			layer.add(formItem);
		}
		
		double thePrice = offer.getServicePrice();
		if(thePrice>0){
			NumberFormat format = NumberFormat.getCurrencyInstance(iwc.getCurrentLocale());
			
			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(localize("service.offer.application.price_of_service_offer" ,"Price of service offer")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(format.format(thePrice)));
			formItem.add(label);
			formItem.add(span);
			layer.add(formItem);
		}
		
		Timestamp timestamp = offer.getServiceDate();
		if(timestamp!=null){
			IWTimestamp theTimestamp = new IWTimestamp(timestamp);

			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(localize("service.offer.application.service_offer_date" ,"Date of service")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(theTimestamp.getLocaleDate(iwc.getCurrentLocale())));
			formItem.add(label);
			formItem.add(span);
			layer.add(formItem);

			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(localize("service.offer.application.service_offer_time" ,"Time of service offer")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(theTimestamp.getLocaleTime(iwc.getCurrentLocale())));
			formItem.add(label);
			formItem.add(span);
			layer.add(formItem);
		}
		
		Timestamp stamper = offer.getServiceDate();
		if(stamper!=null){
			IWTimestamp theTimestamp = new IWTimestamp(stamper);

			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(localize("service.offer.application.dealine_for_choice" ,"Choice deadline")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(theTimestamp.getLocaleTime(iwc.getCurrentLocale())));
			formItem.add(label);
			formItem.add(span);
			layer.add(formItem);
		}
		
		String locationText = offer.getServiceLocation();
		if(locationText!=null){
			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(localize("service.offer.application.location" ,"Location")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(locationText));
			formItem.add(label);
			formItem.add(span);
			layer.add(formItem);
		}
		
		if(thePrice>0){
			String paymentTypeLocalizationKey = ServiceOfferConstants.PAYMENT_TYPE_CASH.equals(offer.getServicePaymentType()) ? "service.offer.application.payment.type.cash" : "service.offer.application.payment.type.invoice";

			formItem = new Layer();
			formItem.setStyleClass("formItem");
			label = new Label();
			label.add(new Text(localize("service.offer.application.payment_type" ,"Payment option")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(localize(paymentTypeLocalizationKey,paymentTypeLocalizationKey)));
			formItem.add(label);
			formItem.add(span);
			layer.add(formItem);
		}
		
		String description = offer.getServiceText();
		if(description!=null){
			formItem = new Layer();
			formItem.setStyleClass("formItem");
			formItem.setStyleClass("informationItem");
			label = new Label();
			label.add(new Text(localize("service.offer.application.description" ,"Description of service")));
			span = new Layer(Layer.SPAN);
			span.add(new Text(description));
			formItem.add(label);
			formItem.add(span);
			layer.add(formItem);
		}
	}
}