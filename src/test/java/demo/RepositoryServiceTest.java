package demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RepositoryServiceTest {
  /**
   * The processEngine will be initialized by default with the activiti.cfg.xml resource
   * on the classpath.
   */
  @Rule
  public ActivitiRule activitiRule = new ActivitiRule();

  private RepositoryService repositoryService;

  @Before
  public void setup() {
    repositoryService  = activitiRule.getRepositoryService();
  }

  @Test
   public void initializeRepositoryService() {
    assertNotNull(repositoryService);
  }

  @Test
  public void initiallyNoDeploymentShouldBePresent() {
    long numberOfProcessDefinitions = repositoryService
            .createProcessDefinitionQuery()
            .count();
    assertEquals(0, numberOfProcessDefinitions);
  }

  @Test
  @Deployment(resources = "processes/bookorder.bpmn20.xml")
  public void createDeployment() {
    long numberOfProcessDefinitions = repositoryService
            .createProcessDefinitionQuery()
            .count();
    assertEquals(1, numberOfProcessDefinitions);

    ProcessDefinition processDefinition = repositoryService
            .createProcessDefinitionQuery()
            .latestVersion()
            .processDefinitionKey("bookorder")
            .singleResult();

    assertNotNull(processDefinition);
    Assert.assertEquals("bookorder", processDefinition.getKey());
  }

  @Test
  public void deleteDeployment() {
    // Initially no deployments
    long numberOfProcessDefinitions = repositoryService
            .createProcessDefinitionQuery()
            .count();
    assertEquals(0, numberOfProcessDefinitions);

    // deploy book order process
    String deploymentId = repositoryService.createDeployment()
            .addClasspathResource("processes/bookorder.bpmn20.xml")
            .deploy()
            .getId();

    // we now have one deployment
    numberOfProcessDefinitions = repositoryService
            .createProcessDefinitionQuery()
            .count();
    assertEquals(1, numberOfProcessDefinitions);

    // remove deployment
    repositoryService.deleteDeployment(deploymentId);

    // again, we have zero deployment
    numberOfProcessDefinitions = repositoryService
            .createProcessDefinitionQuery()
            .count();
    assertEquals(0, numberOfProcessDefinitions);
  }
}
