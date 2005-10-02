/*
 * $Id: ServiceOffer.java,v 1.1 2005/10/02 23:42:29 eiki Exp $
 * Created on Oct 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.data;

import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import java.sql.Timestamp;
import java.util.Collection;
import com.idega.block.process.data.Case;
import com.idega.block.school.data.SchoolClass;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOEntity;
import com.idega.user.data.Group;


/**
 * 
 *  Last modified: $Date: 2005/10/02 23:42:29 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public interface ServiceOffer extends IDOEntity, Case, ServiceOfferConstants {

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getCaseCodeKey
	 */
	public String getCaseCodeKey();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getCaseCodeDescription
	 */
	public String getCaseCodeDescription();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getServiceName
	 */
	public String getServiceName();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#setServiceName
	 */
	public void setServiceName(String name);

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getServiceText
	 */
	public String getServiceText();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#setServiceText
	 */
	public void setServiceText(String text);

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getServiceLocation
	 */
	public String getServiceLocation();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#setServiceLocation
	 */
	public void setServiceLocation(String location);

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getServicePaymentType
	 */
	public String getServicePaymentType();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#setServicePaymentType
	 */
	public void setServicePaymentType(String paymentType);

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getServiceDate
	 */
	public Timestamp getServiceDate();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#setServiceDate
	 */
	public void setServiceDate(Timestamp date);

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getServiceDeadline
	 */
	public Timestamp getServiceDeadline();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#setServiceDeadline
	 */
	public void setServiceDeadline(Timestamp deadline);

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getServicePrice
	 */
	public double getServicePrice();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#setServicePrice
	 */
	public void setServicePrice(double price);

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#isServiceChoiceOptional
	 */
	public boolean isServiceChoiceOptional();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#setServiceChoiceAsOptional
	 */
	public void setServiceChoiceAsOptional();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#setServiceChoiceAsMandatory
	 */
	public void setServiceChoiceAsMandatory();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getGroups
	 */
	public Collection getGroups();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#getSchoolClasses
	 */
	public Collection getSchoolClasses();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#addGroup
	 */
	public void addGroup(Group group) throws IDOAddRelationshipException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferBMPBean#addSchoolClass
	 */
	public void addSchoolClass(SchoolClass schoolClass) throws IDOAddRelationshipException;
}
