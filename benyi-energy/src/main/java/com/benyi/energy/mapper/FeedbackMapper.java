package com.benyi.energy.mapper;

import com.benyi.energy.domain.Feedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/3/16 02:09
 */

@Mapper
public interface FeedbackMapper {
    public int insertFeedback(Feedback feedback);
}
