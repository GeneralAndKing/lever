package wiki.lever.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public HttpEntity<?> hello() {
        return ResponseEntity.ok(sysUserRepository.findAll());
    }

}
