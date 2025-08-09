package com.brenda.clexis.clientGatewayService.service.interfaces;

import com.brenda.clexis.clientGatewayService.model.dto.DiscussionDto;
import com.brenda.clexis.clientGatewayService.model.dto.application.Review;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.BuddyProgram;
import com.brenda.clexis.clientGatewayService.model.entity.Discussion;
import org.springframework.http.ResponseEntity;

public interface SocialService {
    ResponseEntity<ResponseDto> addDiscussion(DiscussionDto discussionDto) ;

    ResponseEntity<ResponseDto> updateDiscussion(Discussion discussion);

    ResponseEntity<ResponseDto> joinProgram(String programId);

    ResponseEntity<ResponseDto> updateProgram(BuddyProgram buddyProgram);

    ResponseEntity<ResponseDto> getProgram(String id);

    ResponseEntity<ResponseDto> addProgram(BuddyProgram buddyProgram);

    ResponseEntity<ResponseDto> addResponse(Review review, String discussionId);

    ResponseEntity<ResponseDto> getDiscussion(String id);

    ResponseEntity<ResponseDto> getAllDiscussion();

    ResponseEntity<ResponseDto> getAllBuddyProgram();
}
