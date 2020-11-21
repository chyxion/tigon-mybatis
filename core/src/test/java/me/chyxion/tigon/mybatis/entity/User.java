package me.chyxion.tigon.mybatis.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import lombok.ToString;
import java.io.Serializable;
import me.chyxion.tigon.mybatis.Table;
import me.chyxion.tigon.mybatis.NotUpdate;

/**
 * @author Donghuang
 * @date Nov 15, 2020 22:21:01
 */
@Getter
@Setter
@ToString
@Table("tb_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    @NotUpdate
    private String account;
    private String mobile;
    private String name;
    private Gender gender;
    private String password;
    private Date birthDate;
    private String city;
    private String avatar;

    private Boolean active;
    private String remark;
    private String createdBy;
    private Date createdAt;
    private String updatedBy;
    private Date updatedAt;

    public enum Gender {
        MALE,
        FEMALE
    }
}
