package demo;

import static org.junit.Assert.assertNotNull;

import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class EngineTest {
  @Rule
  public ActivitiRule activitiRule = new ActivitiRule();

  @Test
  public void initializeEngine() {
    assertNotNull(activitiRule);
    assertNotNull(activitiRule.getIdentityService());
  }
}
