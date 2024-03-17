package com.tiktok.service.ipml;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.tiktok.base.BasePropertiesInfo;
import com.tiktok.bo.CommentBO;
import com.tiktok.enums.MessageTypeEnum;
import com.tiktok.enums.YesOrNoTypeEnum;
import com.tiktok.mapper.CommentCustomMapper;
import com.tiktok.mapper.CommentMapper;
import com.tiktok.pojo.Comment;
import com.tiktok.pojo.Vlog;
import com.tiktok.service.CommentService;
import com.tiktok.service.MsgService;
import com.tiktok.service.VlogService;
import com.tiktok.utils.PagedGridResult;
import com.tiktok.vo.CommentVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl extends BasePropertiesInfo implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private CommentCustomMapper commentCustomMapper;

    @Resource
    private VlogService vlogService;
    @Resource
    private MsgService msgService;

    @Override
    public CommentVO createComment(CommentBO commentBO) {

        Comment comment = new Comment();
        comment.setVlogId(commentBO.getVlogId());
        comment.setVlogerId(commentBO.getVlogerId());
        comment.setCommentUserId(commentBO.getCommentUserId());
        comment.setFatherCommentId(commentBO.getFatherCommentId());
        comment.setContent(commentBO.getContent());
        comment.setLikeCounts(0);
        commentMapper.insert(comment);
        redisOperator.increment(REDIS_VLOG_COMMENT_COUNTS + ":" + commentBO.getVlogId(), 1);

        // 留言后的最新评论需要返回给前端进行展示
        comment = getComment(comment.getId());
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);

        // 系统消息: 评论, 回复
        Vlog vlog = vlogService.getVlog(commentBO.getVlogId());
        Map msgContent = new HashMap();
        msgContent.put("vlogId", vlog.getId());
        msgContent.put("vlogCover", vlog.getCover());
        msgContent.put("commentId", comment.getId());
        msgContent.put("commentContent", commentBO.getContent());
        Integer type = MessageTypeEnum.COMMENT_VLOG.type;
        if (StringUtils.isNotBlank(commentBO.getFatherCommentId()) && !commentBO.getFatherCommentId().equalsIgnoreCase("0") ) {
            type = MessageTypeEnum.REPLY_YOU.type;
        }
        msgService.createMsg(commentBO.getCommentUserId(), commentBO.getVlogerId(), type, msgContent);
        return commentVO;
    }

    @Override
    public PagedGridResult queryVlogComments(String vlogId, String userId, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);
        PageHelper.startPage(page, pageSize);

        List<CommentVO> list = commentCustomMapper.getCommentList(map);

        for (CommentVO cv : list) {
            String commentId = cv.getCommentId();

            // 当前短视频的某个评论的点赞总数
            String countsStr = redisOperator.get(REDIS_VLOG_COMMENT_LIKED_COUNTS + ":" + commentId);
            Integer counts = 0;
            if (StringUtils.isNotBlank(countsStr)) {
                counts = Integer.valueOf(countsStr);
            }
            cv.setLikeCounts(counts);

            // 判断当前用户是否点赞过该评论
            String doILike = redisOperator.get(REDIS_USER_LIKE_COMMENT + ":" + userId + ":" + commentId);
            if (StringUtils.isNotBlank(doILike) && doILike.equalsIgnoreCase("1")) {
                cv.setIsLike(YesOrNoTypeEnum.YES.type);
            }
        }
        return setterPagedGrid(list, page);
    }

    @Override
    public void deleteComment(String commentUserId, String commentId, String vlogId) {
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getId, commentId).eq(Comment::getCommentUserId, commentUserId).eq(Comment::getVlogId, vlogId);
        commentMapper.delete(lambdaQueryWrapper);

        // 评论总数的累减
        redisOperator.decrement(REDIS_VLOG_COMMENT_COUNTS + ":" + vlogId, 1);
    }

    @Override
    public Comment getComment(String id) {
        return commentMapper.selectById(id);
    }
}