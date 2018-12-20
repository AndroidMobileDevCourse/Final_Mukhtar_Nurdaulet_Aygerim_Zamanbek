package kz.iitu.cloudy.model;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by 1506k on 5/17/18.
 */

public class Hashtag implements SearchSuggestion {

    private String mName;
    private String mAuthor;
    private int mPhotoCount;

    public Hashtag() {
    }

    public Hashtag(String name, int photoCount) {
        mName = name;
        mPhotoCount = photoCount;
    }

    public Hashtag(String name, String author) {
        mName = name;
    mAuthor = author;
}

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPhotoCount() {
        return mPhotoCount;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public void setPhotoCount(int photoCount) {
        mPhotoCount = photoCount;
    }

    @Override
    public String getBody() {
        return "#" + mName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mAuthor);
        dest.writeInt(this.mPhotoCount);
    }

    @Override
    public String toString() {
        return "Hashtag{" +
                "mName='" + mName + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                '}';
    }

    protected Hashtag(Parcel in) {
        this.mName = in.readString();
        this.mAuthor = in.readString();
        this.mPhotoCount = in.readInt();
    }

    public static final Creator<Hashtag> CREATOR = new Creator<Hashtag>() {
        @Override
        public Hashtag createFromParcel(Parcel source) {
            return new Hashtag(source);
        }

        @Override
        public Hashtag[] newArray(int size) {
            return new Hashtag[size];
        }
    };
}
