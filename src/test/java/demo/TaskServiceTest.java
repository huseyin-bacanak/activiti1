package demo;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskServiceTest {
  @Rule
  public ActivitiRule activitiRule  = new ActivitiRule();

  private static final String TEST_USER_ID = "donald.trump";
  private static final String TEST_GROUP_ID = "sales";
  private static final String TEST_GROUP_NAME = "Sales Dept.";

  @Test
  @Deployment(resources = {"processes/bookorder.bpmn20.xml"})
  public void queryTask() {
    startProcessInstance();
    TaskService taskService = activitiRule.getTaskService();
    List<Task> taskList = taskService.createTaskQuery()
            .processDefinitionKey("bookorder")
            .includeProcessVariables()
            .orderByTaskCreateTime()
            .desc()
            .list();

    assertEquals(1, taskList.size());

    Task task = taskList.get(0);
    assertEquals("Complete order", task.getName());
    assertEquals("usertask1", task.getTaskDefinitionKey());
    assertEquals("book order user task", task.getDescription());

    Map<String,Object> processVariables  = task.getProcessVariables();
    assertNotNull(processVariables);
    assertEquals(1, processVariables.size());
    assertEquals("123456", processVariables.get("isbn"));
  }

  @Test
  @Deployment(resources = {"processes/bookorder.bpmn20.xml"})
  public void claimTask() {
    TaskService taskService = activitiRule.getTaskService();
    createTestUser();

    // initially the user has no claimable task
    List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(TEST_USER_ID).list();
    assertEquals(0, taskList.size());

    // we create a test process instance, which have a user task
    // claimable by test group
    startProcessInstance();

    // test user still no assigned tasks
    List<Task> myTaskList = taskService.createTaskQuery().taskAssignee(TEST_USER_ID).list();
    assertEquals(0, myTaskList.size());

    // But a task is available to test user, since it is a member of test group
    taskList = taskService.createTaskQuery().taskCandidateUser(TEST_USER_ID).list();
    assertEquals(1, taskList.size());

    // claim the task
    Task task = taskList.get(0);
    taskService.claim(task.getId(), TEST_USER_ID);
    assertEquals("Complete order", task.getName());

    // test user now has one task
    assertEquals(1, taskService.createTaskQuery().taskAssignee(TEST_USER_ID).list().size());

    // no unassigned task remains
    assertEquals(0, taskService.createTaskQuery().taskUnassigned().list().size());
  }

  private void startProcessInstance() {
    RuntimeService runtimeService = activitiRule.getRuntimeService();
    Map<String, Object> variableMap  = new HashMap<>();
    variableMap.put("isbn", "123456");
    runtimeService.startProcessInstanceByKey("bookorder", variableMap);
  }

  private void createTestUser() {
    IdentityService identityService = activitiRule.getIdentityService();
    // create user
    User newUser = identityService.newUser(TEST_USER_ID);
    identityService.saveUser(newUser);

    // create group
    Group newGroup = identityService.newGroup(TEST_GROUP_ID);
    newGroup.setName(TEST_GROUP_NAME);
    identityService.saveGroup(newGroup);

    // create membership
    identityService.createMembership(TEST_USER_ID, TEST_GROUP_ID);
  }

}
