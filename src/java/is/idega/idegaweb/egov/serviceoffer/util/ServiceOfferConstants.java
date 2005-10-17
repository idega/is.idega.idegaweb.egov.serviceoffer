/*
 * $Id: ServiceOfferConstants.java,v 1.3 2005/10/17 02:27:54 eiki Exp $
 * Created on Oct 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.util;

/**
 * Contains all constants for the serviceoffer package. "Implement" this interface to get access to them.
 * 
 *  Last modified: $Date: 2005/10/17 02:27:54 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface ServiceOfferConstants {
	public static final String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.egov.serviceoffer";	
	public static final String CASE_CODE_KEY_SERVICE_OFFER = "SERVOFF";
	public static final String CASE_CODE_KEY_SERVICE_OFFER_PARENT = "SERVOFP";
	
	public static final String PAYMENT_TYPE_CASH = "CASH";
	public static final String PAYMENT_TYPE_INVOICE = "INVOICE";
	public static final String PAYMENT_TYPE_CREDIT_CARD = "CREDIT";
	
	
	public static final String STYLE_CLASS_SERVICE_DESCRIPTION = "serviceDescription";
	public static final String STYLE_CLASS_LABEL_TEXT = "labelText";
	public static final String STYLE_CLASS_FORM_TEXT = "formText";
	public static final String STYLE_CLASS_FORM_ELEMENT = "formElement";
	public static final String STYLE_CLASS_SELECTION_BOX = "selectionBox";
	
}
