package com.benyi.energy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/3/21 06:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedBackObj {
    String[] imgs;
    String suggest;
    String contact;
}
