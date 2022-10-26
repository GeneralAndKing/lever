package wiki.lever.integration.controller.test;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import wiki.lever.integration.controller.test.param.InformationRecordParam;

/**
 * 2022/10/15 23:23:15
 *
 * @author yue
 */
@RestController
@Profile("information")
public class InformationTestController {

    public static final String SIMPLE_REQUEST_FIELD = "/test/information/simpleRequestField";
    public static final String RECORD_REQUEST_FIELD = "/test/information/simpleRequestField/1";
    public static final String PATH_VARIABLE_REQUEST = "/test/information/pathVariableRequest/{param}/{number}";

    @GetMapping(SIMPLE_REQUEST_FIELD)
    public HttpEntity<?> simpleRequestField(String param1, Integer param2) {
        return ResponseEntity.ok(param1 + param2);
    }

    @GetMapping(RECORD_REQUEST_FIELD)
    public HttpEntity<?> recordRequestField(InformationRecordParam param) {
        return ResponseEntity.ok(param);
    }


    @GetMapping(PATH_VARIABLE_REQUEST)
    public HttpEntity<?> pathVariableRequest(@PathVariable String param, @PathVariable Integer number) {
        return ResponseEntity.ok(param + number);
    }

}
