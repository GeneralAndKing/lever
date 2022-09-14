package wiki.lever.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import wiki.lever.entity.SysLog;
import wiki.lever.repository.SysLogRepository;
import wiki.lever.service.SysLogService;

/**
 * 2022/9/14 11:57
 *
 * @author yue
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLogServiceImpl implements SysLogService {

    private final SysLogRepository sysLogRepository;


    @Async
    @Override
    public void save(SysLog sysLog) {
        sysLogRepository.save(sysLog);
    }

}
