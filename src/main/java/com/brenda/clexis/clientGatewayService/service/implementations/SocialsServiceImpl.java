package com.brenda.clexis.clientGatewayService.service.implementations;

import com.brenda.clexis.clientGatewayService.model.dto.DiscussionDto;
import com.brenda.clexis.clientGatewayService.model.dto.application.BuddyScore;
import com.brenda.clexis.clientGatewayService.model.dto.application.Review;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.BuddyProgram;
import com.brenda.clexis.clientGatewayService.model.entity.Discussion;
import com.brenda.clexis.clientGatewayService.model.entity.Student;
import com.brenda.clexis.clientGatewayService.repository.BuddyProgramRepository;
import com.brenda.clexis.clientGatewayService.repository.DiscussionRepository;
import com.brenda.clexis.clientGatewayService.repository.StudentRepository;
import com.brenda.clexis.clientGatewayService.service.interfaces.LearningPathService;
import com.brenda.clexis.clientGatewayService.service.interfaces.SocialService;
import com.brenda.clexis.clientGatewayService.utils.JWTUtils;
import com.sun.tools.javac.Main;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocialsServiceImpl implements SocialService {
    private final JWTUtils jWTUtils;
    private final LearningPathService learningPathService;
    private final DiscussionRepository discussionRepository;
    private final BuddyProgramRepository buddyProgramRepository;
    private final StudentProfileService studentProfileService;
    private final StudentRepository studentRepository;

    @Override
    public ResponseEntity<ResponseDto> addDiscussion(DiscussionDto discussionDto) {
        try{
            Discussion discussion = Discussion.builder()
                    .date("date")
                    .discussionBody(discussionDto.getDiscussionBody())
                    .discussionTitle(discussionDto.getDiscussionTitle())
                    .userId(jWTUtils.extractUserId(learningPathService.getToken()))
                    .type(discussionDto.getType())
                    .responses(List.of())
                    .build();
            //notify others about this

//            studentProfileService.recordForumParticipation(discussion.getUserId());
            Student studen = studentRepository.findStudentByUserId(jWTUtils.extractUserId(learningPathService.getToken()));
            studen.setPoints(studen.getPoints() + 1);
            studentRepository.save(studen);
            return MainResponse.responseOk(discussionRepository.save(discussion));
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> updateDiscussion(Discussion discussion) {
        try{
            if(discussionRepository.existsById(discussion.getId())){
                return MainResponse.responseOk(discussionRepository.save(discussion));
            }
            return MainResponse.responseNotFound("Discussion Not Found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> joinProgram(String programId) {
        try{
            var program = buddyProgramRepository.findBuddyProgramsById(programId);
            if(program != null && program.isOpened() ){
               var buddies = program.getBuddies();
               var newBuddy = BuddyScore.builder()
                               .score(0)
                               .userId(jWTUtils.extractUserId(learningPathService.getToken()))
                               .build();
               buddies.add(newBuddy);
               program.setBuddies(buddies);

//               studentProfileService.recordStudyBuddySession(newBuddy.getUserId(), program.getTitle());
               //notify other buddies
               return MainResponse.responseOk("Welcome to the study program "+ program.getTitle());
            }
            return MainResponse.responseNotFound("Buddy Program closed or Not Found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> updateProgram(BuddyProgram buddyProgram) {
        try{
            if(buddyProgramRepository.existsById(buddyProgram.getId())){
                return MainResponse.responseOk(buddyProgramRepository.save(buddyProgram));
            }
            return MainResponse.responseNotFound("Program Not Found");
            
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getProgram(String id) {
        try{
            var program = buddyProgramRepository.findBuddyProgramsById(id);
            if(program != null){
                return MainResponse.responseOk(program);
            }
            return MainResponse.responseNotFound("Program Not Found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> addProgram(BuddyProgram buddyProgram) {
        try{
            String userId = jWTUtils.extractUserId(learningPathService.getToken());
            buddyProgram.setAuthorId(userId);
            Student student = studentRepository.findStudentByUserId(userId);
            buddyProgram.setAuthorName(student.getFullName());
            buddyProgram.getBuddies().add(new BuddyScore(userId,student.getFullName(),0));
            Student studen = studentRepository.findStudentByUserId(jWTUtils.extractUserId(learningPathService.getToken()));
            studen.setPoints(studen.getPoints() + 1);
            studentRepository.save(studen);
//            studentProfileService.recordStudyBuddySession(userId, buddyProgram.getTitle());
            if(buddyProgram.isOpened()){
                //notify that there is a new program
                System.out.println(buddyProgram.getTitle());
            }
            return MainResponse.responseOk(buddyProgramRepository.save(buddyProgram));
            
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> addResponse(Review review, String discussionId) {
        try{
            if(discussionRepository.existsById(discussionId)){
                var discussion = discussionRepository.findDiscussionById(discussionId);
                var reviews = discussion.getResponses();
                reviews.add(review);
                discussion.setResponses(reviews);
                studentProfileService.recordForumParticipation(discussion.getUserId());
                return MainResponse.responseOk(discussionRepository.save(discussion));
            }
            return MainResponse.responseNotFound("Discussion Not Found");
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getDiscussion(String id) {
        try{
            var discussion = discussionRepository.findDiscussionById(id);
            if(discussion != null){
                return MainResponse.responseOk(discussion);
            }
            return MainResponse.responseNotFound("Discussion Not Found");
            
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getAllDiscussion() {
        try{
            return MainResponse.responseOk(discussionRepository.findAll());
            
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getAllBuddyProgram() {
        try{
            return MainResponse.responseOk(buddyProgramRepository.findAll());
            
        }catch (Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }
}
