package com.tiktok.service.ipml;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tiktok.enums.MessageTypeEnum;
import com.tiktok.mapper.MyLikedVlogMapper;
import com.tiktok.pojo.MyLikedVlog;
import com.tiktok.pojo.Vlog;
import com.tiktok.service.MsgService;
import com.tiktok.service.MyLikedVlogService;
import com.tiktok.service.VlogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 针对my_liked_vlog表的service层实现.
 *
 * @author lzc
 * @version 1.0
 */
@Service
public class MyLikedVlogServiceImpl implements MyLikedVlogService {

    @Resource
    private MyLikedVlogMapper myLikedVlogMapper;

    @Resource
    private MsgService msgService;

    @Resource
    private VlogService vlogService;

    @Transactional
    @Override
    public void userLikeVlog(String userId, String vlogId) {

        MyLikedVlog likedVlog = new MyLikedVlog();
        likedVlog.setVlogId(vlogId);
        likedVlog.setUserId(userId);
        likedVlog.setCreatedTime(new Date());
        myLikedVlogMapper.insert(likedVlog);


        // 系统消息: 点赞短视频
        Vlog vlog = vlogService.getVlog(vlogId);
        Map msgContent = new HashMap();
        msgContent.put("vlogId", vlogId);
        msgContent.put("vlogCover", vlog.getCover());
        msgService.createMsg(userId, vlog.getVlogerId(), MessageTypeEnum.LIKE_VLOG.type, msgContent);
    }

    @Transactional
    @Override
    public void userUnLikeVlog(String userId, String vlogId) {

        QueryWrapper<MyLikedVlog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("vlog_id", vlogId);
        myLikedVlogMapper.delete(queryWrapper);
    }
}