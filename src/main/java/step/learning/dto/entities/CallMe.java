package step.learning.dto.entities;

import java.util.Date;

public class CallMe {
    private long id;
    private String name;
    private String phone;
    private Date moment;

    public CallMe(long id, String name, String phone, Date moment) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.moment = moment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getMoment() {
        return moment;
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }
}
