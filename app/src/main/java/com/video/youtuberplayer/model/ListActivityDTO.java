package com.video.youtuberplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.video.youtuberplayer.enums.ListType;

/**
 * Created by hoanghiep on 5/7/17.
 */

public class ListActivityDTO implements Parcelable {
  public static final Parcelable.Creator<ListActivityDTO> CREATOR = new Parcelable.Creator<ListActivityDTO>() {
    @Override
    public ListActivityDTO createFromParcel(Parcel source) {
      return new ListActivityDTO(source);
    }

    @Override
    public ListActivityDTO[] newArray(int size) {
      return new ListActivityDTO[size];
    }
  };
  private int id;
  private String nameActivity;
  private String subtitleActivity;
  private int layoutID;
  private ListType listType;

  public ListActivityDTO(int id, String nameActivity, int layoutID, ListType listType) {
    this(id, nameActivity, null, layoutID, listType);
  }

  public ListActivityDTO(int id, String nameActivity, String subtitleActivity, int layoutID, ListType listType) {
    this.id = id;
    this.nameActivity = nameActivity;
    this.layoutID = layoutID;
    this.subtitleActivity = subtitleActivity;
    this.listType = listType;
  }

  public ListActivityDTO(String nameActivity, String subtitleActivity, int layoutID, ListType listType) {
    this.nameActivity = nameActivity;
    this.layoutID = layoutID;
    this.subtitleActivity = subtitleActivity;
    this.listType = listType;
  }

  public ListActivityDTO(String nameActivity, int layoutID, ListType listType) {
    this.nameActivity = nameActivity;
    this.layoutID = layoutID;
    this.listType = listType;
  }

  protected ListActivityDTO(Parcel in) {
    this.id = in.readInt();
    this.nameActivity = in.readString();
    this.subtitleActivity = in.readString();
    int tmpMSortList = in.readInt();
    this.layoutID = in.readInt();
    int tmpListType = in.readInt();
    this.listType = tmpListType == -1 ? null : ListType.values()[tmpListType];
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getNameActivity() {
    return nameActivity;
  }

  public void setNameActivity(String nameActivity) {
    this.nameActivity = nameActivity;
  }

  public int getLayoutID() {
    return layoutID;
  }

  public void setLayoutID(int layoutID) {
    this.layoutID = layoutID;
  }

  public String getSubtitleActivity() {
    return subtitleActivity;
  }

  public void setSubtitleActivity(String subtitleActivity) {
    this.subtitleActivity = subtitleActivity;
  }

  public ListType getListType() {
    return listType;
  }

  public void setListType(ListType listType) {
    this.listType = listType;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.nameActivity);
    dest.writeString(this.subtitleActivity);
    dest.writeInt(this.layoutID);
    dest.writeInt(this.listType == null ? -1 : this.listType.ordinal());
  }
}
