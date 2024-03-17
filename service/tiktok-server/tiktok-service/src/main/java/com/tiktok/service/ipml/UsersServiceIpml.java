package com.tiktok.service.ipml;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiktok.bo.UpdatedUserBO;
import com.tiktok.enums.SexTypeEnum;
import com.tiktok.enums.UserInfoModifyType;
import com.tiktok.enums.YesOrNoTypeEnum;
import com.tiktok.exceptions.GraceException;
import com.tiktok.grace.result.ResponseStatusEnum;
import com.tiktok.mapper.UsersMapper;
import com.tiktok.pojo.Users;
import com.tiktok.service.UsersService;
import com.tiktok.utils.DateUtil;
import com.tiktok.utils.DesensitizationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * 针对users表的service层实现.
 *
 * @author lzc
 * @version 1.0
 */
@Service
public class UsersServiceIpml implements UsersService {
    @Resource
    private UsersMapper usersMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Users queryIsMobileExist(String mobile) {
        LambdaQueryWrapper<Users> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Users::getMobile, mobile);
        return usersMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Users queryIsAccountExist(String tiktokNum) {
        LambdaQueryWrapper<Users> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Users::getTiktokNum, tiktokNum);
        return usersMapper.selectOne(lambdaQueryWrapper);
    }

    private Users queryIsNicknameExist(String nickname) {
        LambdaQueryWrapper<Users> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Users::getNickname, nickname);
        return usersMapper.selectOne(lambdaQueryWrapper);
    }

    @Transactional
    @Override
    public Users createUser(String mobile) {
        Users user = new Users();
        user.setPassword(passwordEncoder.encode(mobile));
        user.setMobile(mobile);
        user.setNickname("tik_" + DesensitizationUtil.commonDisplay(mobile));
        user.setTiktokNum("tik_" + DesensitizationUtil.commonDisplay(mobile));
        user.setFace("");
        user.setBirthday(DateUtil.stringToDate("1999-01-01"));
        user.setSex(SexTypeEnum.secret.type);
        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setDescription("这家伙很懒，什么都没留下~");
        user.setBgImg("");
        user.setCanTiktokNumBeUpdated(YesOrNoTypeEnum.YES.type);
        usersMapper.insert(user);
        return getUser(user.getId());
    }

    @Override
    public Users getUser(String userId) {
        return usersMapper.selectById(userId);
    }

    @Transactional
    @Override
    public Users updateUserInfo(UpdatedUserBO updatedUserBO) {
        Users pendingUser = new Users();
        BeanUtils.copyProperties(updatedUserBO, pendingUser);
        int result = usersMapper.updateById(pendingUser);
        if (result != 1) {
            GraceException.display(ResponseStatusEnum.USER_UPDATE_INFO_ERROR);
        }
        return getUser(updatedUserBO.getId());
    }

    @Transactional
    @Override
    public Users updateUserInfo(UpdatedUserBO updatedUserBO, Integer type) {
        if (type.equals(UserInfoModifyType.NICKNAME.type)) {
            Users user = queryIsNicknameExist(updatedUserBO.getNickname());
            if (user != null) {
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_NICKNAME_EXIST_ERROR);
            }
        }

        if (type.equals(UserInfoModifyType.TIKTOKNUM.type)) {
            Users user = queryIsAccountExist(updatedUserBO.getTiktokNum());
            if (user != null) {
                GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_TIKTOKNUM_EXIST_ERROR);
            }
            Users tempUser = getUser(updatedUserBO.getId());
            if (tempUser.getCanTiktokNumBeUpdated().equals(YesOrNoTypeEnum.NO.type)) {
                GraceException.display(ResponseStatusEnum.USER_INFO_CANT_UPDATED_TIKTOKNUM_ERROR);
            }
            updatedUserBO.setCanTiktokNumBeUpdated(YesOrNoTypeEnum.NO.type);
        }

        if (type.equals(UserInfoModifyType.PASSWORD.type)) {
            Users user = getUser(updatedUserBO.getId());
            if (!passwordEncoder.matches(updatedUserBO.getPassword(), user.getPassword())) {
                GraceException.display(ResponseStatusEnum.USER_PASSWORD_ERROR);
            }
            // 判断新旧密码是否相同
            if (updatedUserBO.getPassword().equals(updatedUserBO.getNewPassword())) {
                GraceException.display(ResponseStatusEnum.USER_NEWPASSWORD_SAME_ERROR);
            }
            updatedUserBO.setPassword(passwordEncoder.encode(updatedUserBO.getNewPassword()));
        }
        return updateUserInfo(updatedUserBO);
    }
}