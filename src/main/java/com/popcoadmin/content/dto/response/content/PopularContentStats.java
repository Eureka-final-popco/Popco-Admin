package com.popcoadmin.content.dto.response.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PopularContentStats {
    private long contentId;
    private String type;
    private long likeCount;
}
