package org.healthmap.app.controller;

import lombok.RequiredArgsConstructor;
import org.healthmap.db.mongodb.model.Content;
import org.healthmap.db.mongodb.repository.ContentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// mongoDB 적용 되는지 확인용 controller
@RestController
@RequiredArgsConstructor
public class TestController {
    private final ContentRepository contentRepository;

    @GetMapping("/mongo/test")
    public Content contentTest(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "age") Integer age
    ) {
        contentRepository.save(new Content(id, name, age));
        return contentRepository.findById(id).orElseGet(() -> new Content(null, null, null));
    }
}
