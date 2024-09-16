package com.lts.domain.vo;

import com.lts.enumeration.RelationshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelationVO {
    private RelationshipStatus status;
}
