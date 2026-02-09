package com.jennifertellez.library.dto.jikan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JikanMangaResponse {

    @JsonProperty("data")
    private List<JikanMangaData> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JikanMangaData {

        @JsonProperty("mal_id")
        private Long malId;

        @JsonProperty("title")
        private String title;

        @JsonProperty("title_english")
        private String titleEnglish;

        @JsonProperty("title_japanese")
        private String titleJapanese;

        @JsonProperty("authors")
        private List<Author> authors;

        @JsonProperty("synopsis")
        private String synopsis;

        @JsonProperty("volumes")
        private Integer volumes;

        @JsonProperty("chapters")
        private Integer chapters;

        @JsonProperty("status")
        private String status;

        @JsonProperty("published")
        private Published published;

        @JsonProperty("score")
        private Double score;

        @JsonProperty("images")
        private Images images;

        @JsonProperty("genres")
        private List<Genre> genres;

        @JsonProperty("themes")
        private List<Theme> themes;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Author {
            @JsonProperty("name")
            private String name;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Published {
            @JsonProperty("from")
            private String from;

            @JsonProperty("to")
            private String to;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Images {
            @JsonProperty("jpg")
            private ImageUrls jpg;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class ImageUrls {
                @JsonProperty("image_url")
                private String imageUrl;

                @JsonProperty("large_image_url")
                private String largeImageUrl;
            }
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Genre {
            @JsonProperty("name")
            private String name;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Theme {
            @JsonProperty("name")
            private String name;
        }
    }
}
