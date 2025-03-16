package vn.tiendung.socialnetwork.Model;

public class Moment {
    private int imageResId;
    private String description;

    public Moment(int imageResId, String description) {
        this.imageResId = imageResId;
        this.description = description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getDescription() {
        return description;
    }
}

