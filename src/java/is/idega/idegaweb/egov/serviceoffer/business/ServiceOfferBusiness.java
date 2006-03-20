/*
 * $Id: ServiceOfferBusiness.java,v 1.6 2006/03/20 08:09:34 laddi Exp $
 * Created on Mar 20, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.business;

import is.idega.idegaweb.egov.serviceoffer.data.ServiceOffer;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoice;
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;

import java.rmi.RemoteException;
import java.util.Collection;

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
 * <p>
 * TODO laddi Describe Type ServiceOfferBusiness
 * </p>
 *  Last modified: $Date: 2006/03/20 08:09:34 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.6 $
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
	public ServiceOfferChoice createServiceOfferChoiceAndSendMessage(ServiceOffer offer, User custodian, User user, User performer, boolean isOptional) throws IDOCreateException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#sendMessageToParents
	 */
	public void sendMessageToParents(ServiceOfferChoice application, ServiceOffer offer, String subject, String body) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getManagingSchoolForUser
	 */
	public School getManagingSchoolForUser(User user) throws RemoteException, FinderException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#storeServiceOffer
	 */
	public void storeServiceOffer(String name, String paymentType, String choiceOptional, String deadline, String date, String time, String price, String location, String text, String[] schoolType, String[] school, String[] schoolClass, User performer) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOffer
	 */
	public ServiceOffer getServiceOffer(Integer caseID) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOffer
	 */
	public ServiceOffer getServiceOffer(int caseID) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOfferChoice
	 */
	public ServiceOfferChoice getServiceOfferChoice(int caseID) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOfferChoice
	 */
	public ServiceOfferChoice getServiceOfferChoice(Integer caseID) throws FinderException, java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#setServiceChoiceAsViewed
	 */
	public void setServiceChoiceAsViewed(ServiceOfferChoice choice) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#changeServiceOfferChoiceStatus
	 */
	public void changeServiceOfferChoiceStatus(ServiceOfferChoice choice, boolean accepts, User performer) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOfferChoices
	 */
	public Collection getServiceOfferChoices(ServiceOffer offer) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOffers
	 */
	public Collection getServiceOffers(User owner) throws java.rmi.RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#storePaymentInfo
	 */
	public void storePaymentInfo(ServiceOffer offer, String[] offerChoices) throws java.rmi.RemoteException;

}
