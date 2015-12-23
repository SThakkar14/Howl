package boomer.com.howl.Objects;

import java.io.Serializable;

import boomer.com.howl.Objects.Pet;

public class HowlAttributes  implements Serializable {
    String message;
    Pet pet;

    public HowlAttributes() {

    }

    public HowlAttributes(String message, Pet pet) {
        this.message = message;
        this.pet = pet;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    @Override
    public String toString() {
        return "HowlAttributes{" +
                "message='" + message + '\'' +
                ", pet=" + pet +
                '}';
    }
}
