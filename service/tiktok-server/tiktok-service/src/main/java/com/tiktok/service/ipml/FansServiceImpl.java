package com.tiktok.service.ipml;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.tiktok.base.BasePropertiesInfo;
import com.tiktok.enums.MessageTypeEnum;
import com.tiktok.enums.YesOrNoTypeEnum;
import com.tiktok.mapper.FansCustomMapper;
import com.tiktok.mapper.FansMapper;
import com.tiktok.pojo.Fans;
import com.tiktok.service.FansService;
import com.tiktok.service.MsgService;
import com.tiktok.utils.PagedGridResult;
import com.tiktok.vo.FansVO;
import com.tiktok.vo.VlogerVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 针对fans表的service层实现.
 *
 * @author lzc
 * @version 1.0
 */
@Service
public class FansServiceImpl extends BasePropertiesInfo implements FansService {

    @Resource
    private FansMapper fansMapper;

    @Resource
    private FansCustomMapper fansCustomMapper;

    @Resource
    private MsgService msgService;

    @Transactional
    @Override
    public void doFollow(String myId, String vlogerId) {
        Fans fans = new Fans();
        fans.setFanId(myId);
        fans.setVlogerId(vlogerId);
        fans.setIsFanFriendOfMine(YesOrNoTypeEnum.NO.type);

        // 判断对方是否关注我, 如果关注我, 那么双方都要互为朋友关系
        Fans vlogerIsFanOfMe = queryDoVlogerFollowMe(vlogerId, myId);
        if (vlogerIsFanOfMe != null) {
            fans.setIsFanFriendOfMine(YesOrNoTypeEnum.YES.type);
            vlogerIsFanOfMe.setIsFanFriendOfMine(YesOrNoTypeEnum.YES.type);
            fansMapper.updateById(vlogerIsFanOfMe);
        }
        fansMapper.insert(fans);

        // 系统消息: 关注
        msgService.createMsg(myId, vlogerId, MessageTypeEnum.FOLLOW_YOU.type, null);
    }

    /**
     * 查询对方是否关注我.
     *
     * @param myId 当前用户id
     * @param vlogerId 视频发布者id
     * @return fan对象
     */
    private Fans queryDoVlogerFollowMe(String vlogerId, String myId) {
        LambdaQueryWrapper<Fans> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Fans::getVlogerId, myId).eq(Fans::getFanId, vlogerId);
        return fansMapper.selectOne(lambdaQueryWrapper);
    }

    @Transactional
    @Override
    public void doCancel(String myId, String vlogerId) {

        // 判断是否相互关注, 如果是, 则需要取消双方的关系
        Fans vlogerIsFanOfMe = queryDoVlogerFollowMe(vlogerId, myId);
        if (vlogerIsFanOfMe != null && vlogerIsFanOfMe.getIsFanFriendOfMine().equals(YesOrNoTypeEnum.YES.type)) {
            // 抹除对方的朋友关系
            vlogerIsFanOfMe.setIsFanFriendOfMine(YesOrNoTypeEnum.NO.type);
            fansMapper.updateById(vlogerIsFanOfMe);
        }
        // 删除自己的关注记录
        LambdaQueryWrapper<Fans> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Fans::getVlogerId, vlogerId).eq(Fans::getFanId, myId);
        fansMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public boolean queryDoIFollowVloger(String myId, String vlogerId) {
        Fans vloger = queryDoVlogerFollowMe(myId, vlogerId);
        return vloger != null;
    }

    @Override
    public PagedGridResult queryMyFollows(String vlogerId, String currentUserId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("myId", vlogerId);

        // 开始分页
        PageHelper.startPage(page, pageSize);
        List<VlogerVO> list = fansCustomMapper.queryMyFollows(map);
        for (VlogerVO vo : list) {
            if (StringUtils.isNotBlank(currentUserId) && queryDoIFollowVloger(currentUserId, vo.getVlogerId())) {
                vo.setFollow(true);
            }
            if (StringUtils.isNotBlank(currentUserId) && queryDoVlogerFollowMe(vo.getVlogerId(), currentUserId) != null) {
                vo.setFan(true);
            }
            if (StringUtils.isNotBlank(currentUserId) && vo.getVlogerId().equalsIgnoreCase(currentUserId)) {
                vo.setMe(true);
            }
        }
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult queryMyFans(String vlogerId, String currentUserId, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("myId", vlogerId);

        PageHelper.startPage(page, pageSize);
        List<FansVO> list = fansCustomMapper.queryMyFans(map);
        for (FansVO vo : list) {
            if (StringUtils.isNotBlank(currentUserId) && queryDoIFollowVloger(currentUserId, vo.getFanId())) {
                vo.setFollow(true);
            }
            if (StringUtils.isNotBlank(currentUserId) && queryDoVlogerFollowMe(vo.getFanId(), currentUserId) != null) {
                vo.setFan(true);
            }
            if (StringUtils.isNotBlank(currentUserId) && vo.getFanId().equalsIgnoreCase(currentUserId)) {
                vo.setMe(true);
            }
        }
        return setterPagedGrid(list, page);
    }
}