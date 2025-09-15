package com.popcoadmin.review.gemini.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GeminiRequestDto {
    private List<Content> contents = new ArrayList<>();

    public GeminiRequestDto(String text) {
        addContent(text);
    }

    public void addContent(String text) {
        if (this.contents == null) {
            this.contents = new ArrayList<>();
        }
        Content content = new Content(text);
        contents.add(content);
    }

    @Data
    @NoArgsConstructor
    public static class Content {
        private List<Part> parts = new ArrayList<>();

        public Content(String text) {
            addPart(text);
        }

        public void addPart(String text) {
            if (this.parts == null) {
                this.parts = new ArrayList<>();
            }
            Part part = new Part(text);
            parts.add(part);
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Part {
            private String text;
        }
    }
}