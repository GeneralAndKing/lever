package wiki.lever.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpMethod;
import wiki.lever.base.BaseEntity;

import java.util.Objects;


/**
 * 2022/09/12 11:38:24
 *
 * @author xy
 */
@Getter
@Setter
@Entity
@Accessors(chain = true)
@ToString(callSuper = true)
@Table(name = "sys_log")
@Where(clause = "deleted = false")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE sys_log SET deleted=true WHERE id=?")
public class SysLog extends BaseEntity<SysLog> {
    private String className;
    private String methodName;
    private HttpMethod httpMethod;
    private String paramContent;
    private String operateModule;
    private String operateType;
    private String operateName;
    private String operateIp;

    /**
     * It will null when request success.
     */
    private String result;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        SysLog sysLog = (SysLog) o;
        return getId() != null && Objects.equals(getId(), sysLog.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}