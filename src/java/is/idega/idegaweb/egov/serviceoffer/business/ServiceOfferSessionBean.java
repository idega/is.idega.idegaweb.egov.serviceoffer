package is.idega.idegaweb.egov.serviceoffer.business;

import java.rmi.RemoteException;

import javax.ejb.FinderException;

import com.idega.block.school.business.SchoolUserBusiness;
import com.idega.block.school.data.School;
import com.idega.business.IBOLookup;
import com.idega.business.IBOSessionBean;
import com.idega.user.data.User;

/**
 * @author Laddi
 * 
 * To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates. To enable and disable the creation of
 * type comments go to Window>Preferences>Java>Code Generation.
 */
public class ServiceOfferSessionBean extends IBOSessionBean implements ServiceOfferSession {

	protected int _schoolID = -1;
	protected int _schoolSeasonID = -1;
	private int _userID = -1;
	private String[] _schoolGroupIDs = null;

	public ServiceOfferBusiness getServiceOfferBusiness() throws RemoteException {
		return (ServiceOfferBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), ServiceOfferBusiness.class);
	}

	public SchoolUserBusiness getSchoolUserBusiness() throws RemoteException {
		return (SchoolUserBusiness) IBOLookup.getServiceInstance(getIWApplicationContext(), SchoolUserBusiness.class);
	}

	/**
	 * Returns the schoolID.
	 * 
	 * @return int
	 */
	public int getSchoolID() throws RemoteException {
		if (getUserContext().isLoggedOn()) {
			User user = getUserContext().getCurrentUser();
			int userID = ((Integer) user.getPrimaryKey()).intValue();

			if (this._userID == userID) {
				if (this._schoolID != -1) {
					return this._schoolID;
				}
				else {
					return getSchoolIDFromUser(user);
				}
			}
			else {
				this._userID = userID;
				this._schoolSeasonID = getServiceOfferBusiness().getCurrentSchoolSeasonID();
				return getSchoolIDFromUser(user);
			}
		}
		else {
			return -1;
		}
	}

	private int getSchoolIDFromUser(User user) throws RemoteException {
		this._schoolID = -1;
		if (user != null) {
			try {
				School school = getSchoolUserBusiness().getFirstManagingSchoolForUser(user);
				if (school != null) {
					this._schoolID = ((Integer) school.getPrimaryKey()).intValue();
				}
			}
			catch (FinderException fe) {
				this._schoolID = -1;
			}
		}
		return this._schoolID;
	}

	/**
	 * Returns the schoolSeasonID.
	 * 
	 * @return int
	 */
	public int getSchoolSeasonID() {
		return this._schoolSeasonID;
	}

	/**
	 * @return Returns the schoolGroupIDs.
	 */
	public String[] getSchoolGroupIDs() {
		return this._schoolGroupIDs;
	}
}