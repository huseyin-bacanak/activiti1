package demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import demo.fixture.ActivitiEngineFixture;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
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
    // clear tables between tests
    CommandExecutor commandExecutor = ((ProcessEngineImpl)activitiEngineFixture.getTestProcessEngine())
            .getProcessEngineConfiguration()
            .getCommandExecutor();

    CommandConfig config = new CommandConfig().transactionNotSupported();
    commandExecutor.execute(config, new Command<Object>() {
      public Object execute(CommandContext commandContext) {
        DbSqlSession session = commandContext.getSession(DbSqlSession.class);
        session.dbSchemaDrop();
        return null;
      }
    });
  }

  @Test
   public void repositoryServiceStartupTest() {
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
  public void createDeploymentTest() {
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
  public void deleteDeploymentTest() {
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

    numberOfProcessDefinitions = repositoryService
            .createProcessDefinitionQuery()
            .count();
    assertEquals(0, numberOfProcessDefinitions);
  }
}
