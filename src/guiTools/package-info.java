/*******
 * <p> Helpers shared by several GUI packages (design decision D28). </p>
 * <ul>
 * <li> LessonResult: the outcome of every Contributor Model operation: success or failure, the
 * 		exact message for the user, the field the message belongs beneath, and the data. </li>
 * <li> LessonAccess: the one ownership check every Contributor page uses before viewing,
 * 		changing, adding experience to, or deleting a lesson (US-R3-V, US-R4), and the shared
 * 		&quot;Lesson not found.&quot; and locked messages. </li>
 * <li> RoleLabels: shows the stored role name Role1 as &quot;Contributor&quot; on the admin pages
 * 		(design decision D12). </li>
 * <li> GUISingleRoleDispatch: the Foundations class that opens the home page of a user with one
 * 		role; in TP2 a Role1 user is sent to the Contributor Home Page. </li>
 * </ul>
 */
package guiTools;
