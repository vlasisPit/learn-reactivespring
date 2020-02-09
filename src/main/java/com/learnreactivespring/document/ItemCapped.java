package com.learnreactivespring.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Capped collection in MongoDB is a fixed-size collection  in MongoDB. It preserves the insertion order.
 * Capped collections work in a way similar to circular buffers: once a collection fills its allocated space,
 * it makes room for new documents by overwriting the oldest documents in the collection.
 * You cannot use capped collection for permanent storage, pecause it is of fixed size.
 */
@Document   //This is related to a @Entity of a relational database
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCapped {

    @Id
    private String id;      //unique Id
    private String description;
    private Double price;
}
