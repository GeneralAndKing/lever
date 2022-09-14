package wiki.lever.service;

import wiki.lever.entity.SysLog;

/**
 * 2022/9/14 11:56
 *
 * @author yue
 */
public interface SysLogService {

    /**
     * Async insert entity.
     *
     * @param sysLog entity
     */
    void save(SysLog sysLog);

}
