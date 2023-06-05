package com.benyi.energy.service.impl;

import com.benyi.energy.domain.Feedback;
import com.benyi.energy.mapper.FeedbackMapper;
import com.benyi.energy.service.IFeebackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/3/16 02:07
 */
@Service
public class FeedbackServiceImpl implements IFeebackService {

    @Autowired
    FeedbackMapper feedbackMapper;

    @Override
    public int insertFeedback(Feedback feedback) {
        return feedbackMapper.insertFeedback(feedback);
    }
}
