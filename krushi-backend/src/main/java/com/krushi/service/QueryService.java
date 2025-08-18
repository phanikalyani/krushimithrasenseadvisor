package com.krushi.service;

import com.krushi.dto.QueryRequest;
import com.krushi.dto.QueryResponse;
import com.Entity.QueryEntity;
import com.krushi.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;

@Service
public class QueryService {
    private final QueryRepository repo;
    private final RestTemplate rt = new RestTemplate();
    private final String mlUrl;

    public QueryService(QueryRepository repo, @Value("${ML_SERVICE_URL:http://localhost:5001/api/answer}") String mlUrl){
        this.repo = repo;
        this.mlUrl = mlUrl;
    }

    public QueryResponse handle(QueryRequest req){
        // persist query
        QueryEntity q = new QueryEntity(req.text);
        q = repo.save(q);

        // call ML service
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String,Object> body = Map.of("text", req.text, "lang", req.lang == null ? "auto" : req.lang);
        HttpEntity<Map<String,Object>> ent = new HttpEntity<>(body, headers);

        ResponseEntity<QueryResponse> resp = rt.postForEntity(mlUrl, ent, QueryResponse.class);
        QueryResponse qr = resp.getBody();

        // save response text into DB
        if(qr != null){
            q.setResponse(qr.answer);
            repo.save(q);
        }

        return qr;
    }
}
