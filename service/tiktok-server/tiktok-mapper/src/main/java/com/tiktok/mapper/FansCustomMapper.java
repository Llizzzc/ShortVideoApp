package com.tiktok.mapper;

import com.tiktok.vo.FansVO;
import com.tiktok.vo.VlogerVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

/**
 * 针对fans表的Mapper操作.
 *
 * @author lzc
 * @version 1.0
 */
@Repository
public interface FansCustomMapper {

    /**
     * 获取用户关注列表.
     *
     * @param map 用户id
     * @return 关注列表
     */
    List<VlogerVO> queryMyFollows(@Param("paramMap") Map<String, Object> map);

    /**
     * 获取用户粉丝列表.
     *
     * @param map 用户id
     * @return 粉丝列表
     */
    List<FansVO> queryMyFans(@Param("paramMap") Map<String, Object> map);
}