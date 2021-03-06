package boomer.com.howl.Objects;

import java.io.Serializable;
import java.util.List;

public class ResponseStatus implements Serializable {
    private int status;
    private List<String> errors;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
