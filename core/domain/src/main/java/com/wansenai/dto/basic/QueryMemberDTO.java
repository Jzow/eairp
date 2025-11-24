package com.wansenai.dto.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryMemberDTO {

    private String memberNumber;

    private String phoneNumber;

    private Long page;

    private Long pageSize;

    private String startDate;

    private String endDate;
}