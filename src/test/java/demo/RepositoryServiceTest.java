package demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import fixture.ActivitiEngineFixture;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RepositoryServiceTest {
  private ActivitiEngineFixture activitiEngineFixture;
  private RepositoryService repositoryService;

  @Before
  public void setup() {
    activitiEngineFixture = new ActivitiEngineFixture();
    repositoryService  = activitiEngineFixture.getTestProcessEngine().getRepositoryService();
  }

  @After
  public void tearDown() {
    activitiEngineFixture.clearDatabase();
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
  public void createDeployment() {
    // Initially no deployments
    long numberOfProcessDefinitions = repositoryService
            .createProcessDefinitionQuery()
            .count();
    assertEquals(0, numberOfProcessDefinitions);

    // deploy book order process
    // confirm book order process deployed successfully
    String deploymentId =  repositoryService.createDeployment()
            .addClasspathResource("processes/bookorder.bpmn20.xml")
            .deploy()
            .getId();

    Deployment deployment  = repositoryService
            .createDeploymentQuery()
            .deploymentId(deploymentId)
            .singleResult();

    Assert.assertNotNull(deployment);
    Assert.assertEquals(deploymentId, deployment.getId());

    numberOfProcessDefinitions = repositoryService
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
