package com.hexon.repository.model;

import java.io.Serializable;

/**
 * @author Hexon
 * @date 2019-08-29 11:15
 */
public class LiveNewsBean implements Serializable {
    private String id;
    private String type;
    private String text;
    private String url;
    private Image image;
    private long timestamp;
    private long created_at;
    private long updated_at;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setText(String text) {
        this.text = text;
    }
    public String getText() {
        return text;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    public Image getImage() {
        return image;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }
    public long getCreated_at() {
        return created_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }
    public long getUpdated_at() {
        return updated_at;
    }

    public class Image {

        private String thumbnail;
        private String original;
        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
        public String getThumbnail() {
            return thumbnail;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
        public String getOriginal() {
            return original;
        }

    }
}
