package is.idega.idegaweb.egov.serviceoffer.business;

import java.rmi.RemoteException;

import com.idega.business.IBOSession;

public interface ServiceOfferSession extends IBOSession {

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferSessionBean#getSchoolID
	 */
	public int getSchoolID() throws RemoteException, RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferSessionBean#getSchoolSeasonID
	 */
	public int getSchoolSeasonID() throws RemoteException;

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferSessionBean#getSchoolGroupIDs
	 */
	public String[] getSchoolGroupIDs() throws RemoteException;

}