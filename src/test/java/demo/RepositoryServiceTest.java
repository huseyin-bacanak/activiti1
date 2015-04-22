package demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import demo.fixture.ActivitiEngineFixture;
import org.activiti.engine.RepositoryService;
import org.junit.Before;
import org.junit.Test;

public class RepositoryServiceTest {
  private ActivitiEngineFixture activitiEngineFixture;
  private RepositoryService repositoryService;

  @Before
  public void init() {
    activitiEngineFixture = new ActivitiEngineFixture();
    repositoryService  = activitiEngineFixture.getTestProcessEngine().getRepositoryService();
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
  public void createDeploymentTest(){

  }
}
