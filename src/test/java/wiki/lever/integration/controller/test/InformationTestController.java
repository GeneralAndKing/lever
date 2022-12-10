package wiki.lever.integration.controller.test;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wiki.lever.integration.controller.test.param.InformationRecordParam;
import wiki.lever.integration.controller.test.param.JsonRequest;

import java.util.List;

/**
 * 2022/10/15 23:23:15
 *
 * @author yue
 */
@RestController
@Profile("information")
public class InformationTestController {

    public static final String SIMPLE_REQUEST = "/test/information/simpleRequest";
    public static final String RECORD_REQUEST = "/test/information/recordRequest";
    public static final String PATH_VARIABLE_REQUEST = "/test/information/pathVariableRequest/{param}/{number}";
    public static final String JSON_BODY_REQUEST = "/test/information/requestRequest";

    @GetMapping(SIMPLE_REQUEST)
    public HttpEntity<String> simpleRequestField(String param1, Integer param2) {
        return ResponseEntity.ok(param1 + param2);
    }

    @GetMapping(RECORD_REQUEST)
    public HttpEntity<InformationRecordParam> recordRequestField(InformationRecordParam param) {
        return ResponseEntity.ok(param);
    }

    @GetMapping(PATH_VARIABLE_REQUEST)
    public HttpEntity<String> pathVariableRequest(@PathVariable String param, @PathVariable Integer number) {
        return ResponseEntity.ok(param + number);
    }

    @PostMapping(JSON_BODY_REQUEST)
    public HttpEntity<List<String>> jsonBodyRequest(@RequestBody JsonRequest request) {
        return ResponseEntity.ok(List.of(request.stringValue(), "1", "2", "3"));
    }

}
