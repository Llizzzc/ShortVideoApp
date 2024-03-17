package com.tiktok.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 返回给前端的用户对象.
 *
 * @author lzc
 * @version 1.0
 */
@Data
public class UsersVO implements Serializable {
    private String id;
    private String mobile;
    private String nickname;
    private String tiktokNum;
    private String face;
    private Integer sex;
    private Date birthday;
    private String country;
    private String province;
    private String city;
    private String district;
    private String description;
    private String bgImg;
    private Integer canTiktokNumBeUpdated;
    private Date createdTime;
    private Date updatedTime;
    private String userToken;
    private Integer myFollowsCounts;
    private Integer myFansCounts;
    private Integer totalLikeMeCounts;
}