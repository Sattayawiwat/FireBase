package com.example.sattayawiwat.firebasedatabase;

public class Person {

    private String name;
    private String email;
    private String id;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Person(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Person(String name, String email, String id, String url) {

        this.name = name;
        this.email = email;
        this.id = id;
        this.url = url;

    }
}
