package wiki.lever.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import wiki.lever.entity.SysLog;
import wiki.lever.integration.DataSourceTestExecutionListener;
import wiki.lever.repository.SysLogRepository;

import java.util.List;

/**
 * 2022/9/12 22:52:12
 *
 * @author yue
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners(
        listeners = DataSourceTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
class ControllerTest {

    @Autowired
    private SysLogRepository sysLogRepository;

    @Test
    void userTest() {
        List<SysLog> all = sysLogRepository.findAll();
        System.out.println(all);
    }
}
