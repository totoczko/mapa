package com.example.martyna.mapa;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Martyna on 2018-01-13.
 */

public class Place implements Parcelable {
    public String id;
//    public LatLng latlng;
    public String name;
    public String desc;
    public String range;

    public Place() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Place(String id, String name, String desc, String range) {
        this.id = id;
//        this.latlng = latlng;
        this.name = name;
        this.desc = desc;
        this.range = range;
    }

    protected Place(Parcel in) {
        id = in.readString();
//        latlng = in.readParcelable(LatLng.class.getClassLoader());
        name = in.readString();
        desc = in.readString();
        range = in.readString();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public String toString() {
        return this.name + ": opis: " + this.desc + ", zasięg: " + this.range + "km";
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
//        dest.writeParcelable(this.latlng, flags);
        dest.writeString(desc);
        dest.writeString(range);
    }
}
