package boomer.com.howl.Objects;

import java.io.Serializable;

public class PetFeatures  implements Serializable {
    int age;

    public PetFeatures() {

    }

    public PetFeatures(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "PetFeatures{" +
                "age=" + age +
                '}';
    }
}
