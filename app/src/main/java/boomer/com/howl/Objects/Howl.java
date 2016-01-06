package boomer.com.howl.Objects;

import java.io.Serializable;

public class Howl implements Serializable {
    String id;
    String comment_id;
    int parent;
    String zipcode;
    HowlAttributes attributes;
    String user_id;
    long created;
    String type;
    boolean following = false;

    public Howl() {

    }



    public Howl(String id, String comment_id, int parent, String zipcode, HowlAttributes attributes, String user_id, long created, String type , boolean following) {
        this.id = id;
        this.comment_id = comment_id;
        this.parent = parent;
        this.zipcode = zipcode;
        this.attributes = attributes;
        this.user_id = user_id;
        this.created = created;
        this.type = type;
        this.setFollowing(following);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public HowlAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(HowlAttributes attributes) {
        this.attributes = attributes;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    @Override
    public String toString() {
        return "Howl{" +
                "id='" + id + '\'' +
                ", comment_id='" + comment_id + '\'' +
                ", parent=" + parent +
                ", zipcode='" + zipcode + '\'' +
                ", attributes=" + attributes +
                ", user_id='" + user_id + '\'' +
                ", created=" + created +
                ", type='" + type + '\'' +
                ", following=" + following +
                '}';
    }
}
