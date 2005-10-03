/*
 * $Id: ServiceOfferBusiness.java,v 1.2 2005/10/03 16:49:09 eiki Exp $
 * Created on Oct 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.business;

import is.idega.idegaweb.egov.serviceoffer.data.ServiceOffer;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoice;
import java.sql.Date;
import java.util.Map;
import javax.ejb.FinderException;
import se.idega.idegaweb.commune.business.CommuneUserBusiness;
import com.idega.block.process.business.CaseBusiness;
import com.idega.block.school.business.SchoolBusiness;
import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolClassMember;
import com.idega.block.school.data.SchoolSeason;
import com.idega.business.IBOService;
import com.idega.data.IDOCreateException;
import com.idega.user.data.User;


/**
 * 
 *  Last modified: $Date: 2005/10/03 16:49:09 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public interface ServiceOfferBusiness extends IBOService, CaseBusiness {

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getSchoolBusiness
	 */
	public SchoolBusiness getSchoolBusiness() throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getCommuneUserBusiness
	 */
	public CommuneUserBusiness getCommuneUserBusiness() throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getOngoingSeason
	 */
	public SchoolSeason getOngoingSeason() throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getNextSeason
	 */
	public SchoolSeason getNextSeason() throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getSchoolPlacing
	 */
	public SchoolClassMember getSchoolPlacing(User user, SchoolSeason season) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#createServiceOfferChoiceAndSendMessage
	 */
	public ServiceOfferChoice createServiceOfferChoiceAndSendMessage(ServiceOffer offer, User user, School school,
			SchoolSeason season, String comments, Date[] months, Map monthValues, User performer)
			throws IDOCreateException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#sendMessageToParents
	 */
	public void sendMessageToParents(ServiceOfferChoice application, String subject, String body)
			throws java.rmi.RemoteException;

	/**
	 * Stores the service offer and creates the cases/processes and messages to send to the citizens.
	 * @param name
	 * @param paymentType
	 * @param choiceOptional
	 * @param deadline
	 * @param date
	 * @param time
	 * @param price
	 * @param location
	 * @param text
	 * @param schoolType
	 * @param school
	 * @param schoolClass
	 * @param user
	 */
	public void storeServiceOffer(String name, String paymentType, String choiceOptional, String deadline, String date, String time, String price, String location, String text, String schoolType, String school, String schoolClass, User user);

}
