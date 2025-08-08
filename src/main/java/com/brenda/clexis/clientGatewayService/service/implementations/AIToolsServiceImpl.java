package com.brenda.clexis.clientGatewayService.service.implementations;

import com.brenda.clexis.clientGatewayService.interfaces.interfaces.AiInterface;
import com.brenda.clexis.clientGatewayService.model.dto.LearningAssetDto;
import com.brenda.clexis.clientGatewayService.model.dto.RegenerateLearningAsset;
import com.brenda.clexis.clientGatewayService.model.dto.application.Flashcard;
import com.brenda.clexis.clientGatewayService.model.dto.application.Quiz;
import com.brenda.clexis.clientGatewayService.model.dto.response.MainResponse;
import com.brenda.clexis.clientGatewayService.model.dto.response.ResponseDto;
import com.brenda.clexis.clientGatewayService.model.entity.LearningAssets;
import com.brenda.clexis.clientGatewayService.repository.LearningAssetsRepository;
import com.brenda.clexis.clientGatewayService.service.interfaces.AIToolsService;
import com.brenda.clexis.clientGatewayService.service.interfaces.LearningPathService;
import com.brenda.clexis.clientGatewayService.utils.JWTUtils;
import com.brenda.clexis.clientGatewayService.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIToolsServiceImpl implements AIToolsService {

    private final AiInterface aiInterface;
    private final JWTUtils jWTUtils;
    private final LearningPathService learningPathService;
    private final Utils utils;
    private final LearningAssetsRepository learningAssetsRepository;

    @Override
    public ResponseEntity<ResponseDto> generateResource(LearningAssetDto learningAssetDto) {
        try{
            LearningAssets learningAssets = LearningAssets.builder()
                    .title(learningAssetDto.getTitle())
                    .originalFilename(learningAssetDto.getOriginalFilename())
                    .originalFileUrl(learningAssetDto.getOriginalFileUrl())
                    .userId(jWTUtils.extractUserId(learningPathService.getToken()))
                    .build();
            Object response = aiInterface.generateResource(learningAssetDto);
            if(response==null){
                return MainResponse.responseError("Network Error . Please try again Later");
            }
            switch(learningAssetDto.getType()){
                case FLASHCARD -> {
                   learningAssets.setFlashcards(utils.convertDataObjectFromHashMap(response, Flashcard.class));
                   break;
                }
                case SUMMARY -> {
                    learningAssets.setSummary(utils.convertDataObjectFromHashMap(response, String.class));
                    break;
                }
                case TRANSCRIBE -> {
                    learningAssets.setTranscription(utils.convertDataObjectFromHashMap(response, String.class));
                    break;
                }
                case QUIZ -> {
                    learningAssets.setQuiz(utils.convertDataObjectFromHashMap(response, Quiz.class));
                    break;
                }
                default -> {
                    return MainResponse.responseError("select a valid resource type");
                }
            }
            return MainResponse.responseOk(learningAssetsRepository.save(learningAssets));

        }catch(Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> getLearningResource() {
        try{
            List<LearningAssets> learningAssets = learningAssetsRepository.findLearningAssetsByUserId(jWTUtils.extractUserId(learningPathService.getToken()));
            return MainResponse.responseOk(learningAssets);
        }catch(Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> regenerateUserLearningAssets(RegenerateLearningAsset regenerateLearningAsset) {
        try{
            LearningAssets learningAssets = learningAssetsRepository.findById(regenerateLearningAsset.getAssetId()).orElse(null);
            if(learningAssets == null){
                return MainResponse.responseNotFound("Learning asset not found");
            }
            else if(learningAssets.getUserId().equals(jWTUtils.extractUserId(learningPathService.getToken())) ){
                return MainResponse.responseFailed("Access Denied","You do not have the right to do this");
            }
            var learningAssetDto = LearningAssetDto.builder()
                    .title(learningAssets.getTitle())
                    .originalFilename(learningAssets.getOriginalFilename())
                    .originalFileUrl(learningAssets.getOriginalFileUrl())
                    .count(regenerateLearningAsset.getCount())
                    .difficulty(regenerateLearningAsset.getDifficulty())
                    .type(learningAssets.getType())
                    .build();
            Object response = aiInterface.generateResource(learningAssetDto);
            if(response==null){
                return MainResponse.responseError("Network Error . Please try again Later");
            }
            switch(learningAssetDto.getType()){
                case FLASHCARD -> {
                    learningAssets.setFlashcards(utils.convertDataObjectFromHashMap(response, Flashcard.class));
                    break;
                }
                case SUMMARY -> {
                    learningAssets.setSummary(utils.convertDataObjectFromHashMap(response, String.class));
                    break;
                }
                case TRANSCRIBE -> {
                    learningAssets.setTranscription(utils.convertDataObjectFromHashMap(response, String.class));
                    break;
                }
                case QUIZ -> {
                    learningAssets.setQuiz(utils.convertDataObjectFromHashMap(response, Quiz.class));
                    break;
                }
                default -> {
                    return MainResponse.responseError("select a valid resource type");
                }
            }
            return MainResponse.responseOk(learningAssetsRepository.save(learningAssets));
        }catch(Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDto> deleteAsset(String id) {
        try{
            LearningAssets learningAssets = learningAssetsRepository.findById(id).orElse(null);
            if(learningAssets == null){
                return MainResponse.responseNotFound("Learning asset not found");
            }
            learningAssetsRepository.delete(learningAssets);
            return MainResponse.responseOk("Learning asset deleted successfully");
        }catch(Exception e){
            return MainResponse.responseError(e.getMessage());
        }
    }
}
