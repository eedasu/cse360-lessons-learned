package guiTools;

/*******
 * <p> Title: RoleLabels Class. </p>
 *
 * <p> Description: Translates between the role names the database stores and the role names the
 * user sees.  In TP2 the Foundations "Role1" is the Contributor role (design decision D12), so
 * every screen shows "Contributor", while the database, the invitation codes, and the account
 * set-up code keep using "Role1".</p>
 *
 * <p> The stored name is kept as "Role1" on purpose:</p>
 * <ul>
 * <li> the InvitationCodes table stores the role in a VARCHAR(10) column, and "Contributor" is 11
 * 		characters long, so it would not fit; </li>
 * <li> ControllerNewAccount.doCreateUser and Database.updateUserRole compare the stored value
 * 		with "Role1", and an unknown role makes doCreateUser end the program; </li>
 * <li> invitations already sent and databases already in use stay valid. </li>
 * </ul>
 * <p> So the translation happens only at the edge of the screens: a role is turned into its
 * label just before it is shown (toLabel), and a label chosen by the user is turned back into
 * the stored name just before it is given to the database (toStoredName).</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2026 </p>
 *
 * @author Emilia Davey
 *
 * @version 1.00		2026-10-16 TP2: Role1 is shown as "Contributor" on the admin pages
 */
public class RoleLabels {

	/** The name the database and the Foundations code use for the Contributor role. */
	public static final String ROLE1_STORED_NAME = "Role1";

	/** The name every screen shows for that role (design decision D12). */
	public static final String ROLE1_LABEL = "Contributor";

	/**********
	 * Default constructor is not used; every member is static.
	 */
	public RoleLabels() {
	}

	/**********
	 * <p> Method: String toLabel(String storedName) </p>
	 *
	 * <p> Description: Returns the name to show on the screen for a stored role name.  "Role1"
	 * becomes "Contributor"; every other name ("Admin", "Role2") is shown unchanged. </p>
	 *
	 * @param storedName	the role name as the database stores it
	 * @return the role name to show the user
	 */
	public static String toLabel(String storedName) {
		return ROLE1_STORED_NAME.equals(storedName) ? ROLE1_LABEL : storedName;
	}

	/**********
	 * <p> Method: String toStoredName(String label) </p>
	 *
	 * <p> Description: Returns the stored role name for a name chosen on the screen.
	 * "Contributor" becomes "Role1"; every other name, including the "&lt;Select a role&gt;"
	 * prompt, is returned unchanged. </p>
	 *
	 * @param label	the role name as the user saw and chose it
	 * @return the role name the database expects
	 */
	public static String toStoredName(String label) {
		return ROLE1_LABEL.equals(label) ? ROLE1_STORED_NAME : label;
	}
}
