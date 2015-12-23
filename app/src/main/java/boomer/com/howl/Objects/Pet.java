package boomer.com.howl.Objects;

import java.io.Serializable;

public class Pet implements Serializable{
    String id;
    String image_url;
    String name;
    PetFeatures features;
    String pet_type;
    String user_id;
    long created;

    public Pet() {

    }

    public Pet(String id, String image_url, String name, PetFeatures features, String pet_type, String user_id, long created) {
        this.id = id;
        this.image_url = image_url;
        this.name = name;
        this.features = features;
        this.pet_type = pet_type;
        this.user_id = user_id;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PetFeatures getFeatures() {
        return features;
    }

    public void setFeatures(PetFeatures features) {
        this.features = features;
    }

    public String getPet_type() {
        return pet_type;
    }

    public void setPet_type(String pet_type) {
        this.pet_type = pet_type;
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

    @Override
    public String toString() {
        return "Pet{" +
                "id='" + id + '\'' +
                ", image_url='" + image_url + '\'' +
                ", name='" + name + '\'' +
                ", features=" + features +
                ", pet_type='" + pet_type + '\'' +
                ", user_id='" + user_id + '\'' +
                ", created=" + created +
                '}';
    }

}


