package boomer.com.howl.Objects;

public class HowlCommentBody {
    String zipcode;
    Attributes attributes;

    public HowlCommentBody(String zipcode, String message) {
        this.zipcode = zipcode;
        this.attributes = new Attributes(message);
    }

    @Override
    public String toString() {
        return "HowlCommentBody{" +
                "zipcode='" + zipcode + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    private class Attributes {
        String message;

        public Attributes(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Attributes{" +
                    "message='" + message + '\'' +
                    '}';
        }
    }
}
