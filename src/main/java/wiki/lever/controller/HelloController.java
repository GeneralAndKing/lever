package wiki.lever.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wiki.lever.entity.SysUser;
import wiki.lever.repository.SysUserRepository;

import java.util.List;

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
    public HttpEntity<List<SysUser>> hello() {
        List<SysUser> all = sysUserRepository.findAll();
        return ResponseEntity.ok(all);
    }

}
