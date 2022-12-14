package wiki.lever.base;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * 2022/9/2 22:02:48
 *
 * @author yue
 */
@MappedSuperclass
@SuppressWarnings({"unused", "unchecked"})
public class BaseEntity<E extends BaseEntity<E>> {

    /**
     * Entity id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Comment("Primary key")
    private Long id;

    /**
     * Entity name.
     */
    @Column
    @Comment("Data name")
    private String name;

    /**
     * Entity delete status.
     */
    @Column(nullable = false)
    @Comment("Data deleted")
    @ColumnDefault("false")
    private Boolean deleted = Boolean.FALSE;

    /**
     * Entity enable.
     */
    @Column(nullable = false)
    @Comment("Data enabled")
    @ColumnDefault("true")
    private Boolean enabled = Boolean.TRUE;

    /**
     * Create time
     */
    @CreatedDate
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL")
    private LocalDateTime createTime;

    /**
     * Create user name
     */
    @CreatedBy
    @Comment("Create user name")
    private String createUser;

    /**
     * Update time
     */
    @LastModifiedDate
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateTime;

    /**
     * Update user name
     */
    @LastModifiedBy
    @Comment("Update user name")
    private String updateUser;

    /**
     * Remark information.
     */
    private String remark;

    /**
     * Sort data.
     */
    @ColumnDefault("0")
    private Integer sort = 0;

    public Long getId() {
        return id;
    }

    public E setId(Long id) {
        this.id = id;
        return (E) this;
    }

    public String getName() {
        return name;
    }

    public E setName(String name) {
        this.name = name;
        return (E) this;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public E setDeleted(Boolean deleted) {
        this.deleted = deleted;
        return (E) this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public E setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return (E) this;
    }

    public String getCreateUser() {
        return createUser;
    }

    public E setCreateUser(String createUser) {
        this.createUser = createUser;
        return (E) this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public E setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return (E) this;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public E setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
        return (E) this;
    }

    public String getRemark() {
        return remark;
    }

    public E setRemark(String remark) {
        this.remark = remark;
        return (E) this;
    }

    public Integer getSort() {
        return sort;
    }

    public E setSort(Integer sort) {
        this.sort = sort;
        return (E) this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public E setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return (E) this;
    }
}
