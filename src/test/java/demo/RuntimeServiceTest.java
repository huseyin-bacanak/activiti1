package demo;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import fixture.ActivitiEngineFixture;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeServiceTest {
  private ActivitiEngineFixture activitiEngineFixture;
  private RuntimeService runtimeService;
  private RepositoryService repositoryService;

  private static final String TEST_PD_KEY = "bookorder";

  /**
   * Before each test, deploy the test process definition.
   */
  @Before
  public void setup() {
    activitiEngineFixture = new ActivitiEngineFixture();
    runtimeService  = activitiEngineFixture.getTestProcessEngine().getRuntimeService();
    repositoryService = activitiEngineFixture.getTestProcessEngine().getRepositoryService();
    repositoryService.createDeployment()
            .addClasspathResource("processes/bookorder.bpmn20.xml")
            .deploy();
  }

  @After
  public void tearDown() {
    activitiEngineFixture.clearDatabase();
  }

  @Test
  public void startProcessInstance() {
    // confirm repository has only test process definition
    List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().list();
    assertEquals(1, pdList.size());
    assertEquals(TEST_PD_KEY, pdList.get(0).getKey());

    Map<String, Object> variableMap  = new HashMap<>();
    variableMap.put("isbn", "12345");

    ProcessInstance processInstance = runtimeService
            .startProcessInstanceByKey("bookorder", variableMap);
    assertNotNull(processInstance.getId());
  }

  @Test
  public void queryProcessInstance() {
    // create process instance of the test process definition.
    Map<String, Object> variableMap  = new HashMap<>();
    variableMap.put("isbn", "12345");
    runtimeService.startProcessInstanceByKey("bookorder", variableMap);

    List<ProcessInstance> instanceList = runtimeService
            .createProcessInstanceQuery()
            .processDefinitionKey("bookorder")
            .list();

    // confirm only an instance of deployed test process is available
    assertEquals(1, instanceList.size());
    ProcessInstance processInstance = instanceList.get(0);
    assertEquals(TEST_PD_KEY, processInstance.getProcessDefinitionKey());
    assertEquals(false, processInstance.isSuspended());
    assertEquals(false, processInstance.isEnded());
  }

  @Test(expected = ActivitiException.class)
  public void startProcessInstanceWithoutRequiredVars() {
    // confirm repository has only test process definition
    List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().list();
    assertEquals(1, pdList.size());
    assertEquals(TEST_PD_KEY, pdList.get(0).getKey());

    ProcessInstance processInstance = runtimeService
            .startProcessInstanceByKey("bookorder");
    assertNotNull(processInstance.getId());
  }
}
