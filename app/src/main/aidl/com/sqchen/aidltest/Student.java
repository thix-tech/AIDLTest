package com.sqchen.aidltest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Desc.
 *
 * @author chenxj(陈贤靖)
 * @date 2019/3/21
 */
public class Student implements Parcelable {

    private int id;

    private String name;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    public void readFromParcel(Parcel parcel) {
        this.id = parcel.readInt();
        this.name = parcel.readString();
    }

    public static Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[0];
        }
    };

    private Student(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
