package vn.tiendung.socialnetwork.Model;

import java.util.List;

public class PostRequest {
        private Content content;
        private String userid;

        public PostRequest(String caption, List<String> hashtags, List<String> pictures, String userid) {
            this.content = new Content(caption, hashtags, pictures);
            this.userid = userid;
        }

    public PostRequest() {
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


        public static class Content {
            private String caption;
            private List<String> hashtags;
            private List<String> pictures;

            public Content() {
            }

            public Content(String caption, List<String> hashtags, List<String> pictures) {
                this.caption = caption;
                this.hashtags = hashtags;
                this.pictures = pictures;
            }

            public String getCaption() {
                return caption;
            }

            public void setCaption(String caption) {
                this.caption = caption;
            }

            public List<String> getHashtags() {
                return hashtags;
            }

            public void setHashtags(List<String> hashtags) {
                this.hashtags = hashtags;
            }

            public List<String> getPictures() {
                return pictures;
            }

            public void setPictures(List<String> pictures) {
                this.pictures = pictures;
            }

        }
        // Getters và setters nếu cần
}


