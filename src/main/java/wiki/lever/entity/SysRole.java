package wiki.lever.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import wiki.lever.base.BaseEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 2022/9/2 22:38:15
 *
 * @author yue
 */
@Getter
@Setter
@Entity
@Accessors(chain = true)
@ToString(callSuper = true)
@Table(name = "sys_role")
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE sys_role SET deleted=true WHERE id=?")
public class SysRole extends BaseEntity<SysRole> {

    /**
     * 子对象
     */
    @ToString.Exclude
    @JsonIgnoreProperties({"users", "parent", "children", "permissions"})
    @OneToMany(mappedBy = "parent")
    private Set<SysRole> children;

    /**
     * 父对象
     */
    @ManyToOne
    @ToString.Exclude
    @JsonIgnoreProperties({"users", "parent", "children", "permissions"})
    @JoinColumn(name = "parent_id")
    private SysRole parent;


    /**
     * Associated users.
     */
    @ToString.Exclude
    @JsonIgnoreProperties("roles")
    @ManyToMany(mappedBy = "roles")
    private Set<SysUser> users = new HashSet<>();


    /**
     * Associated permissions.
     */
    @ToString.Exclude
    @JsonIgnoreProperties("roles")
    @ManyToMany(targetEntity = SysPermission.class, cascade = CascadeType.ALL)
    @JoinTable(name = "sys_role_permission",
            joinColumns = {@JoinColumn(name = "sys_role_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "sys_permission_id", referencedColumnName = "id")}
    )
    private Set<SysPermission> permissions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        SysRole sysRole = (SysRole) o;
        return getId() != null && Objects.equals(getId(), sysRole.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Convert current roles to authorities with all role.
     *
     * @return Authorities list.
     */
    public Collection<? extends GrantedAuthority> convertAuthorities() {
        Set<GrantedAuthority> authorityList = new HashSet<>();
        authorityList.add(new SimpleGrantedAuthority(getName()));
        if (Objects.isNull(parent)) {
            return authorityList;
        }
        Collection<? extends GrantedAuthority> parentAuthorityList = parent.convertAuthorities();
        authorityList.addAll(parentAuthorityList);
        return authorityList;
    }
}
