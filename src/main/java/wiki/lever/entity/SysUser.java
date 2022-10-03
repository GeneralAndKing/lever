package wiki.lever.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import wiki.lever.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 2022/9/2 21:55:24
 *
 * @author yue
 */
@Getter
@Setter
@Entity
@Accessors(chain = true)
@ToString(callSuper = true)
@Table(name = "sys_user")
@Where(clause = "deleted = false")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE sys_user SET deleted=true WHERE id=?")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class SysUser extends BaseEntity<SysUser> implements UserDetails {

    /**
     * User login name, it can be modify.
     */
    private String username;

    /**
     * User phone.
     */
    private String phone;

    /**
     * User email.
     */
    private String email;

    /**
     * User password.
     */
    private String password;

    /**
     * User account is expired.
     */
    private LocalDateTime expiredTime = LocalDateTime.now().plusYears(1);

    /**
     * User account is locked.
     */
    private Boolean locked = Boolean.FALSE;

    /**
     * Associated roles
     */
    @ToString.Exclude
    @ManyToMany(targetEntity = SysRole.class, cascade = CascadeType.ALL)
    @JoinTable(name = "sys_user_role",
            joinColumns = {@JoinColumn(name = "sys_user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "sys_role_id", referencedColumnName = "id")}
    )
    private Set<SysRole> roles = new HashSet<>();

    /**
     * Current permissions. It will be wrapped in {@link wiki.lever.config.security.authentication.UserTokenInfo}.
     *
     * @see wiki.lever.service.AuthenticationService#buildToken(SysUser) set this field
     * @see wiki.lever.config.security.authorization.PermissionAuthorizationManager get this field
     */
    private transient Map<HttpMethod, List<String>> permissions = Collections.emptyMap();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .flatMap(role -> role.convertAuthorities().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        if (Objects.isNull(expiredTime)) {
            return true;
        }
        if (Objects.equals(expiredTime, LocalDateTime.MAX)) {
            return true;
        }
        return LocalDateTime.now().isBefore(expiredTime);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    /**
     * Depending on the token expiration time.
     *
     * @return Always true.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getEnabled();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        SysUser sysUser = (SysUser) o;
        return getId() != null && Objects.equals(getId(), sysUser.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Collect user role names.
     *
     * @return roles set
     */
    public Set<String> getRoleNames() {
        return roles.stream()
                .flatMap(role -> role.convertAuthorities().stream())
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

}
