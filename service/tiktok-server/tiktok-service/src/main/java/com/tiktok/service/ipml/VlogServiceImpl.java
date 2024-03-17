package com.tiktok.service.ipml;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.tiktok.base.BasePropertiesInfo;
import com.tiktok.bo.VlogBO;
import com.tiktok.enums.YesOrNoTypeEnum;
import com.tiktok.mapper.VlogCustomMapper;
import com.tiktok.mapper.VlogMapper;
import com.tiktok.pojo.Vlog;
import com.tiktok.service.FansService;
import com.tiktok.service.VlogService;
import com.tiktok.utils.PagedGridResult;
import com.tiktok.vo.IndexVlogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;

/**
 * 针对vlog表的service层实现.
 *
 * @author lzc
 * @version 1.0
 */
@Slf4j
@Service
public class VlogServiceImpl extends BasePropertiesInfo implements VlogService {

    @Resource
    private VlogMapper vlogMapper;

    @Resource
    private VlogCustomMapper vlogCustomMapper;

    @Resource
    private FansService fansService;
//    @Autowired
//    private MsgService msgService;

    @Override
    public Vlog getVlog(String vlogId) {
        return vlogMapper.selectById(vlogId);
    }

    @Transactional
    @Override
    public void createVlog(VlogBO vlogBO) {
        Vlog vlog = new Vlog();
        BeanUtils.copyProperties(vlogBO, vlog);
        vlog.setLikeCounts(0);
        vlog.setCommentsCounts(0);
        vlog.setIsPrivate(YesOrNoTypeEnum.NO.type);
        vlogMapper.insert(vlog);
    }

    @Override
    public PagedGridResult getIndexVlogList(String userId, String search, Integer page, Integer pageSize) {

        // 进行分页
        PageHelper.startPage(page, pageSize);
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(search)) {
            map.put("search", search);
        }
        List<IndexVlogVO> list = vlogCustomMapper.getIndexVlogList(map);
        for (IndexVlogVO indexVlog : list) {
            setterVO(indexVlog, userId);
        }
        return setterPagedGrid(list, page);
    }

    /**
     * 设置IndexVlogVO参数.
     *
     * @param v IndexVlogVO对象
     * @param userId 用户id
     */
    private void setterVO(IndexVlogVO v, String userId) {
        String vlogerId = v.getVlogerId();
        String vlogId = v.getVlogId();

        if (StringUtils.isNotBlank(userId)) {
            // 用户是否关注该博主
            boolean doIFollowVloger = fansService.queryDoIFollowVloger(userId, vlogerId);
            v.setDoIFollowVloger(doIFollowVloger);

            // 判断当前用户是否点赞过视频
            v.setDoILikeThisVlog(doILikeVlog(userId, vlogId));
        }
        // 获得当前视频被点赞过的总数
        v.setLikeCounts(getVlogBeLikedCounts(vlogId));
    }

    @Override
    public Integer getVlogBeLikedCounts(String vlogId) {
        String countsStr = redisOperator.get(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId);
        if (StringUtils.isBlank(countsStr)) {
            countsStr = "0";
        }
        return Integer.valueOf(countsStr);
    }

    @Override
    public boolean doILikeVlog(String myId, String vlogId) {
        String doILike = redisOperator.get(REDIS_USER_LIKE_VLOG + ":" + myId + ":" + vlogId);
        return StringUtils.isNotBlank(doILike) && doILike.equalsIgnoreCase("1");
    }

    @Override
    public IndexVlogVO getVlogDetailById(String userId, String vlogId) {
        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);
        List<IndexVlogVO> list = vlogCustomMapper.getVlogDetailById(map);
        if (list != null && !list.isEmpty()) {
            IndexVlogVO vlogVO = list.get(0);
            setterVO(vlogVO, userId);
            return vlogVO;
        }
        return null;
    }

    @Transactional
    @Override
    public void changeToPrivateOrPublic(String userId, String vlogId, Integer type) {
        LambdaQueryWrapper<Vlog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Vlog::getId, vlogId).eq(Vlog::getVlogerId, userId);
        Vlog pendingVlog = new Vlog();
        pendingVlog.setIsPrivate(type);
        vlogMapper.update(pendingVlog, lambdaQueryWrapper);
    }

    @Override
    public PagedGridResult queryMyVlogList(String userId, Integer type, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        if (type == 2) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            List<IndexVlogVO> list = vlogCustomMapper.getMyLikedVlogList(map);
            return setterPagedGrid(list, page);
        }
        LambdaQueryWrapper<Vlog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Vlog::getVlogerId, userId).eq(Vlog::getIsPrivate, type).orderByDesc(Vlog::getCreatedTime);
        List<Vlog> list = vlogMapper.selectList(lambdaQueryWrapper);
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult getMyFollowVlogList(String myId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        List<IndexVlogVO> list = vlogCustomMapper.getMyFollowVlogList(map);

        for (IndexVlogVO v : list) {
            String vlogId = v.getVlogId();
            if (StringUtils.isNotBlank(myId)) {
                v.setDoIFollowVloger(true);

                // 判断当前用户是否点赞过视频
                v.setDoILikeThisVlog(doILikeVlog(myId, vlogId));
            }
            // 获得当前视频被点赞过的总数
            v.setLikeCounts(getVlogBeLikedCounts(vlogId));
        }
        return setterPagedGrid(list, page);
    }

    @Override
    public PagedGridResult getMyFriendVlogList(String myId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("myId", myId);
        List<IndexVlogVO> list = vlogCustomMapper.getMyFriendVlogList(map);

        for (IndexVlogVO v : list) {
            String vlogId = v.getVlogId();
            if (StringUtils.isNotBlank(myId)) {
                v.setDoIFollowVloger(true);

                // 判断当前用户是否点赞过视频
                v.setDoILikeThisVlog(doILikeVlog(myId, vlogId));
            }
            // 获得当前视频被点赞过的总数
            v.setLikeCounts(getVlogBeLikedCounts(vlogId));
        }
        return setterPagedGrid(list, page);
    }
}