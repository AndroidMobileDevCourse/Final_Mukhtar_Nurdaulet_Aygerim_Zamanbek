package kz.iitu.cloudy.model;

/**
 * Created by 1506k on 5/17/18.
 */

public class Photo {

    private String mId;
    private String mName;
    private String mUrl;

    public Photo() { }

    public Photo(String name, String url) {
        mName = name;
        mUrl = url;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
