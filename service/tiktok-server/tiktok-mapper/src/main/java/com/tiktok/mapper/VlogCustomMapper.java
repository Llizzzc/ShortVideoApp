package com.tiktok.mapper;

import com.tiktok.vo.IndexVlogVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 针对表vlog的数据库操作.
 *
 * @author lzc
 * @version 1.0
 */
public interface VlogCustomMapper {

    /**
     * 获取首页视频列表.
     *
     * @param map 搜索关键字
     * @return 视频列表
     */
    List<IndexVlogVO> getIndexVlogList(@Param("paramMap") Map<String, Object> map);

    /**
     * 根据视频id查询.
     *
     * @param map 视频id
     * @return 指定视频
     */
    List<IndexVlogVO> getVlogDetailById(@Param("paramMap")Map<String, Object> map);

    /**
     * 获取用户点赞的视频.
     *
     * @param map 用户id
     * @return 点赞视频列表
     */
    List<IndexVlogVO> getMyLikedVlogList(@Param("paramMap")Map<String, Object> map);

    /**
     * 获取关注视频列表.
     *
     * @param map 用户id
     * @return 分页信息
     */
    List<IndexVlogVO> getMyFollowVlogList(@Param("paramMap")Map<String, Object> map);

    /**
     * 获取朋友视频列表.
     *
     * @param map 用户id
     * @return 分页信息
     */
    List<IndexVlogVO> getMyFriendVlogList(@Param("paramMap")Map<String, Object> map);
}