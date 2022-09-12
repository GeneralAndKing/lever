package wiki.lever.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import wiki.lever.modal.annotation.Log;
import wiki.lever.repository.SysUserRepository;

/**
 * 2022/08/31 17:13:12
 *
 * @author yue.li
 */
@RestController
@RequiredArgsConstructor
public class HelloController {

    private final SysUserRepository sysUserRepository;

    @GetMapping("/hello")
    @Log(operateModule = "业务", operateType = "测试", operateName = "你好")
    public HttpEntity<?> hello(String param1, int param2) {
        return ResponseEntity.ok(sysUserRepository.findAll());
    }

    @PostMapping("/word")
    @Log(operateModule = "业务", operateType = "测试", operateName = "世界")
    public HttpEntity<?> word() {
        return ResponseEntity.ok(sysUserRepository.findAll());
    }

}
