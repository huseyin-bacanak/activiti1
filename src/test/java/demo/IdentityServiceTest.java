package demo;


import static org.junit.Assert.assertEquals;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.test.ActivitiRule;
import org.h2.tools.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class IdentityServiceTest {

  @Rule
  public final ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem-fullhistory.xml");

  private IdentityService identityService;

  private static final String TEST_USER_ID = "jon.snow";
  private static final String TEST_GROUP_ID = "NW";
  private static final String TEST_GROUP_NAME = "Night's Watch";


  /**
   * Clean users and groups before tests.
   */
  @Before
  public void setup() {
    //clear users and groups
    identityService = activitiRule.getIdentityService();
    List<User> users = identityService.createUserQuery().list();
    for (User user:users) {
      identityService.deleteUser(user.getId());
    }

    List<Group> groups = identityService.createGroupQuery().list();
    for (Group group:groups) {
      identityService.deleteGroup(group.getId());
    }

  }

  @Test
  public void createUser() {
    // initially no user is registered
    long userCount =  identityService.createUserQuery().count();
    assertEquals(0, userCount);

    // add test user
    User newUser = identityService.newUser("jon.snow");
    identityService.saveUser(newUser);

    // confirm we only have test user in the repository.
    List<User> userList = identityService.createUserQuery().list();
    assertEquals(1,userList.size());
    Assert.assertEquals(TEST_USER_ID, userList.get(0).getId());

  }

  @Test
  public void deleteUser() {
    // initially no user is registered
    long userCount =  identityService.createUserQuery().count();
    assertEquals(0, userCount);

    // add test user
    User newUser = identityService.newUser(TEST_USER_ID);
    identityService.saveUser(newUser);
    userCount =  identityService.createUserQuery().count();
    assertEquals(1, userCount);

    // delete test user
    identityService.deleteUser(TEST_USER_ID);
    userCount =  identityService.createUserQuery().count();
    assertEquals(0, userCount);
  }

  @Test
   public void addGroup() {
    long groupCount =  identityService.createGroupQuery().count();
    assertEquals(0, groupCount);

    Group newGroup = identityService.newGroup("NW");
    newGroup.setName("Night's Watch");
    identityService.saveGroup(newGroup);

    Group group  = identityService.createGroupQuery().singleResult();
    assertEquals(TEST_GROUP_ID, group.getId());
    assertEquals(TEST_GROUP_NAME, group.getName());

    groupCount =  identityService.createGroupQuery().count();
    assertEquals(1, groupCount);
  }

  @Test
  public void createMembership() {
    // create user
    User newUser = identityService.newUser(TEST_USER_ID);
    identityService.saveUser(newUser);

    // create group
    Group newGroup = identityService.newGroup(TEST_GROUP_ID);
    newGroup.setName(TEST_GROUP_NAME);
    identityService.saveGroup(newGroup);

    // create membership
    identityService.createMembership(TEST_USER_ID, TEST_GROUP_ID);

    // confirm that test group has only test user as a member
    List<User> userList = identityService
            .createUserQuery()
            .memberOfGroup(TEST_GROUP_ID)
            .list();

    assertEquals(1, userList.size());
    assertEquals(TEST_USER_ID, userList.get(0).getId());

    // confirm that test user has membership only to test group
    List<Group> groupList = identityService
            .createGroupQuery()
            .groupMember(TEST_USER_ID)
            .list();

    assertEquals(1, groupList.size());
    assertEquals(TEST_GROUP_ID, groupList.get(0).getId());
    assertEquals(TEST_GROUP_NAME, groupList.get(0).getName());
  }
}
