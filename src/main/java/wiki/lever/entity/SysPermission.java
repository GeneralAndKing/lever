package wiki.lever.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
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
import wiki.lever.entity.serialize.HttpMethodSerialize;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author yue
 */
@Getter
@Setter
@Entity
@Accessors(chain = true)
@ToString(callSuper = true)
@Table(name = "sys_permission")
@Where(clause = "deleted = false")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE sys_permission SET deleted=true WHERE id=?")
public class SysPermission extends BaseEntity<SysPermission> {

    /**
     * Url path.
     */
    private String path;

    /**
     * Request method.
     */
    @Column(length = 55)
    @JsonSerialize(using = HttpMethodSerialize.class)
    private HttpMethod method;

    /**
     * Should security.
     */
    private Boolean security;

    /**
     * Associated roles.
     */
    @ToString.Exclude
    @JsonBackReference
    @ManyToMany(mappedBy = "permissions")
    private Set<SysRole> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        SysPermission that = (SysPermission) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}