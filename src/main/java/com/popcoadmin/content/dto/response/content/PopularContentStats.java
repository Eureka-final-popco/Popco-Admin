package com.popcoadmin.content.dto.response.content;

import com.popcoadmin.content.entity.Content;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PopularContentStats {
    private Content content;
    private long likeCount;
}
