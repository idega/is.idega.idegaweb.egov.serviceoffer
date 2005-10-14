/*
 * $Id: ServiceOfferBusiness.java,v 1.3 2005/10/14 21:54:53 eiki Exp $
 * Created on Oct 12, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.business;

import is.idega.idegaweb.egov.serviceoffer.data.ServiceOffer;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoice;
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import java.rmi.RemoteException;
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
 *  Last modified: $Date: 2005/10/14 21:54:53 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface ServiceOfferBusiness extends IBOService, CaseBusiness, ServiceOfferConstants {

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
	public ServiceOfferChoice createServiceOfferChoiceAndSendMessage(ServiceOffer offer, User custodian, User user,
			User performer, boolean isOptional) throws IDOCreateException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#sendMessageToParents
	 */
	public void sendMessageToParents(ServiceOfferChoice application, ServiceOffer offer, String subject, String body)
			throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getManagingSchoolForUser
	 */
	public School getManagingSchoolForUser(User user) throws RemoteException, FinderException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#storeServiceOffer
	 */
	public void storeServiceOffer(String name, String paymentType, String choiceOptional, String deadline, String date,
			String time, String price, String location, String text, String[] schoolType, String[] school,
			String[] schoolClass, User performer) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOffer
	 */
	public ServiceOffer getServiceOffer(int caseID) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOfferChoice
	 */
	public ServiceOfferChoice getServiceOfferChoice(int caseID) throws FinderException, java.rmi.RemoteException;
}
