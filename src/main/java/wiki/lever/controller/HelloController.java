package wiki.lever.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import wiki.lever.modal.annotation.Log;

import java.util.List;

/**
 * 2022/08/31 17:13:12
 *
 * @author yue.li
 */
@RestController
@RequiredArgsConstructor
public class HelloController {

    @GetMapping("/hello")
    @Log(operateModule = "业务", operateType = "测试", operateName = "你好")
    public HttpEntity<String> hello(String param1, int param2) {
        return ResponseEntity.ok(param1 + param2);
    }

    @PostMapping("/word")
    @Log(operateModule = "业务", operateType = "测试", operateName = "世界")
    public HttpEntity<List<String>> word() {
        return ResponseEntity.ok(List.of("World"));
    }

}
