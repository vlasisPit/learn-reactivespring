package com.learnreactivespring.controller.v1;

import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.learnreactivespring.constants.ItemConstants.ITEM_STREAM_END_POINT_V1;

@RestController
@Slf4j
public class ItemStreamController {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    /**
     * Use APPLICATION_STREAM_JSON_VALUE, if you want to stream the response
     * Run this from browser http://localhost:8080/v1/stream/items
     */
    @GetMapping(value = ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream() {
        return itemReactiveCappedRepository.findItemsBy();
    }
}
