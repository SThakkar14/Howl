package boomer.com.howl.Objects;

import java.io.Serializable;
import java.util.List;

public class UserProfile implements Serializable {
    String id;
    List<String> devices;
    String email;
    long created;
    List<String> zipcodes;
    List<Pet> pets;
    List<Howl> howls;

    public UserProfile() {

    }

    public UserProfile(String id, List<String> devices, String email, long created, List<String> zipcodes, List<Pet> pets, List<Howl> howls) {
        this.id = id;
        this.devices = devices;
        this.email = email;
        this.created = created;
        this.zipcodes = zipcodes;
        this.pets = pets;
        this.howls = howls;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", devices=" + devices +
                ", email='" + email + '\'' +
                ", created=" + created +
                ", zipcodes=" + zipcodes +
                ", pets=" + pets +
                ", howls=" + howls +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public List<String> getZipcodes() {
        return zipcodes;
    }

    public void setZipcodes(List<String> zipcodes) {
        this.zipcodes = zipcodes;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public List<Howl> getHowls() {
        return howls;
    }

    public void setHowls(List<Howl> howls) {
        this.howls = howls;
    }
}
