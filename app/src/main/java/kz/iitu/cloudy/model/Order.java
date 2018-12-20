package kz.iitu.cloudy.model;

/**
 * Created by 1506k on 5/18/18.
 */

public class Order {

    private String mName;
    private String mPhone;
    private String mPhotoId;
    private String mHashtag;

    private String mPhotoUrl;

    public Order() {
    }

    public Order(String name, String phone, String photoId, String hashtag) {
        mName = name;
        mPhone = phone;
        mPhotoId = photoId;
        mHashtag = hashtag;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getPhotoId() {
        return mPhotoId;
    }

    public void setPhotoId(String photoId) {
        mPhotoId = photoId;
    }

    public String getHashtag() {
        return mHashtag;
    }

    public void setHashtag(String hashtag) {
        mHashtag = hashtag;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }

    public String formattedTag() {
        return "#" + mHashtag;
    }
}
