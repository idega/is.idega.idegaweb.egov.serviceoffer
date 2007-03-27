package is.idega.idegaweb.egov.serviceoffer.business;


import com.idega.block.process.business.CaseBusiness;
import com.idega.block.school.data.School;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOffer;
import is.idega.block.family.business.FamilyLogic;
import is.idega.idegaweb.egov.accounting.business.CitizenBusiness;
import com.idega.user.data.User;
import com.idega.block.school.data.SchoolClassMember;
import com.idega.data.IDOCreateException;
import java.rmi.RemoteException;
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import java.util.Collection;
import is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoice;
import javax.ejb.FinderException;
import com.idega.business.IBOService;
import com.idega.block.school.business.SchoolUserBusiness;
import com.idega.block.school.data.SchoolSeason;
import com.idega.block.school.business.SchoolBusiness;

public interface ServiceOfferBusiness extends IBOService, CaseBusiness, ServiceOfferConstants {

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getSchoolBusiness
	 */
	public SchoolBusiness getSchoolBusiness() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getSchoolUserBusiness
	 */
	public SchoolUserBusiness getSchoolUserBusiness() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getUserBusiness
	 */
	public CitizenBusiness getUserBusiness() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getMemberFamilyLogic
	 */
	public FamilyLogic getMemberFamilyLogic() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getOngoingSeason
	 */
	public SchoolSeason getOngoingSeason() throws FinderException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getNextSeason
	 */
	public SchoolSeason getNextSeason() throws FinderException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getSchoolPlacing
	 */
	public SchoolClassMember getSchoolPlacing(User user, SchoolSeason season) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#createServiceOfferChoiceAndSendMessage
	 */
	public ServiceOfferChoice createServiceOfferChoiceAndSendMessage(ServiceOffer offer, User custodian, User user, User performer, boolean isOptional) throws IDOCreateException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#sendMessageToParents
	 */
	public void sendMessageToParents(ServiceOfferChoice application, ServiceOffer offer, String subject, String body) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getManagingSchoolForUser
	 */
	public School getManagingSchoolForUser(User user) throws RemoteException, FinderException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#storeServiceOffer
	 */
	public void storeServiceOffer(String name, String paymentType, String choiceOptional, String deadline, String date, String time, String price, String location, String text, String[] schoolType, String[] school, String[] schoolClass, User performer) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOffer
	 */
	public ServiceOffer getServiceOffer(Integer caseID) throws FinderException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOffer
	 */
	public ServiceOffer getServiceOffer(int caseID) throws FinderException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOfferChoice
	 */
	public ServiceOfferChoice getServiceOfferChoice(int caseID) throws FinderException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOfferChoice
	 */
	public ServiceOfferChoice getServiceOfferChoice(Integer caseID) throws FinderException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#setServiceChoiceAsViewed
	 */
	public void setServiceChoiceAsViewed(ServiceOfferChoice choice) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#changeServiceOfferChoiceStatus
	 */
	public void changeServiceOfferChoiceStatus(ServiceOfferChoice choice, boolean accepts, User performer) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOfferChoices
	 */
	public Collection getServiceOfferChoices(ServiceOffer offer) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getServiceOffers
	 */
	public Collection getServiceOffers(User owner) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#storePaymentInfo
	 */
	public void storePaymentInfo(ServiceOffer offer, String[] offerChoices) throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusinessBean#getCurrentSchoolSeasonID
	 */
	public int getCurrentSchoolSeasonID() throws RemoteException, RemoteException;
}