package com.project.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Entity.ElasticEntity;
import com.project.Entity.ThresholdEntity;
import com.project.repository.ThresholdRepo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.SumAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;

@Service
public class ThresholdService {
	
	//@Autowired
	//private ThresholdRepo thresholdRepo;
	
	/*
	// thrashold 인덱스 조회
	public List<ThresholdEntity> getAllDocsByPattern(){
		return thresholdRepo.findAllByIndexPattern("thrashold");
	}
	
	public List<ThresholdEntity> getDatetime(String choiceDate, String choiceDateEnd){
		return thresholdRepo.findAllByDatetime(choiceDate, choiceDateEnd);
	}
	
	public ThresholdEntity getLatestTrafficByMinute() {
		return thresholdRepo.findTopByOrderByTimeDesc();
	}
	*/

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/*
	public String convertToJson(List<ElasticEntity> entities) {
        try {
            return objectMapper.writeValueAsString(entities);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }
    */
	
	private final ElasticsearchClient thresholdClient;
	
	@Autowired
    public ThresholdService(ElasticsearchClient thresholdClient) {
    	this.thresholdClient = thresholdClient;
    }
	
	public SearchResponse<?> searchDocuments(String startDate, String endDate) throws IOException {
		Query query;
    	if(startDate != null && endDate != null) {
    		// 시작 및 종료 시간을 KST로 변환
    		LocalDateTime startDateTime = LocalDateTime.parse(startDate); 
    		ZonedDateTime startDateKST = startDateTime.atZone(ZoneOffset.ofHours(9)); 

    		LocalDateTime endDateTime = LocalDateTime.parse(endDate);
    		ZonedDateTime endDateKST = endDateTime.atZone(ZoneOffset.ofHours(9));

    		query = Query.of(q -> q
        			.range(r -> r
        				.field("time")
        				.gte(JsonData.of(startDateKST.toString()))	// 시작시간
						.lte(JsonData.of(endDateKST.toString()))
    				)
    		);
    	}else {
    		query = Query.of(q -> q
        			.range(r -> r
        				.field("time")
        				.gte(JsonData.of("now-31m"))	// 시작시간
        				.lte(JsonData.of("now"))
    				)
    		);
    	}
    	
    	SearchRequest searchRequest = SearchRequest.of(s -> s
    			.index("thrashold")
    			.size(0)
    			.query(query)
    			.aggregations("per_minute", a -> a
    					.dateHistogram(DateHistogramAggregation.of(h -> h
    							.field("time")
    							.calendarInterval(CalendarInterval.Minute)
						))
    					.aggregations("total_threshold", agg -> agg
    							.sum(SumAggregation.of(sum -> sum.field("traffic")))
						)
				)
		);
    	return thresholdClient.search(searchRequest, Object.class);
    }
}
