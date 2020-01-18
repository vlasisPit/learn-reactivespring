package com.learnreactivespring.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document   //This is related to a @Entity of a relational database
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    private String id;      //unique Id
    private String description;
    private Double price;
}
