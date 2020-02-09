package com.learnreactivespring.repository;

import com.learnreactivespring.document.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

/**
 * Tailable cursor in oder to facilitate the stream kind of output to the endpoint
 * By default, MongoDB will automatically close a cursor when the client has exhausted all results in the cursor.
 * However, for capped collections you may use a Tailable Cursor that remains open after the client exhausts the
 * results in the initial cursor. Tailable cursors are conceptually equivalent to the tail Unix command with the
 * -f option (i.e. with “follow” mode). After clients insert new additional documents into a capped collection,
 * the tailable cursor will continue to retrieve documents.
 */
public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped, String> {

    @Tailable
    Flux<ItemCapped> findItemsBy();

}
