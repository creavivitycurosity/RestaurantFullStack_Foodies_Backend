package com.example.practice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {
    @Override
	public String toString() {
		return "UserDTO [email=" + email + "]";
	}

	private String email;

    @JsonCreator
    public UserDTO(@JsonProperty("email") String email) {
        this.email = email;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    public UserDTO() {
    }
}
//package com.example.practice.dto;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//public class UserDTO {
//    private Long id;
//    private String email;
//
//    @JsonCreator
//    public UserDTO(@JsonProperty("id") Long id, @JsonProperty("email") String email) {
//        this.id = id;
//        this.email = email;
//    }
//
//    @JsonProperty("id")
//    public Long getId() {
//        return id;
//    }
//
//    @JsonProperty("id")
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    @JsonProperty("email")
//    public String getEmail() {
//        return email;
//    }
//
//    @JsonProperty("email")
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public UserDTO() {
//    }
//}
//
////}