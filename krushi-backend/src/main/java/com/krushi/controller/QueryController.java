package com.krushi.controller;

import com.krushi.dto.QueryRequest;
import com.krushi.dto.QueryResponse;
import com.krushi.service.QueryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class QueryController {
    private final QueryService service;
    public QueryController(QueryService service){this.service = service;}

    @PostMapping("/query")
    public QueryResponse query(@RequestBody QueryRequest req){
        return service.handle(req);
    }
}
