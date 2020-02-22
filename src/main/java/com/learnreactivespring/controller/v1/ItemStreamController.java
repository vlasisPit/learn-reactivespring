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
     * Use  curl --no-buffer http://localhost:8080/v1/stream/items
     * to access the data from curl. Browser does not buffer data.
     * From https://curl.haxx.se/docs/manpage.html ...
     * -N, --no-buffer
     * Disables the buffering of the output stream. In normal work situations, curl will use a standard buffered output
     * stream that will have the effect that it will output the data in chunks, not necessarily exactly when the data
     * arrives. Using this option will disable that buffering.
     * I use this option to process data while it arrives, so I don't have to wait until the request completes
     */
    @GetMapping(value = ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream() {
        return itemReactiveCappedRepository.findItemsBy();
    }
}
