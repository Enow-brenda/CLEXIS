package com.example.clexis.models.entity;


import com.example.clexis.models.application.Flashcard;
import com.example.clexis.models.application.Mcq;
import com.example.clexis.models.application.Quiz;
import com.example.clexis.models.enums.ToolType;

import org.threeten.bp.LocalDate;

import java.util.Arrays;
import java.util.List;

import io.realm.RealmObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LearningAssets extends RealmObject {

    private String id;
    private String title;
    private String originalFilename;
    private String originalFileUrl;
    private ToolType type;
    private String summary;
    private String transcription;
    private List<Flashcard> flashcards;
    private Quiz quiz;
    private String userId;
    private LocalDate createdDate = LocalDate.now();
    
    public List<LearningAssets> getDefault(){
        return Arrays.asList(

                // 1. LearningAsset with Flashcards
                LearningAssets.builder()
                        .id("1")
                        .title("Java Basics Flashcards")
                        .originalFilename("java_basics.pdf")
                        .originalFileUrl("https://example.com/files/java_basics.pdf")
                        .type(ToolType.FLASHCARD)
                        .summary(null)
                        .transcription(null)
                        .flashcards(Arrays.asList(
                                new Flashcard("What is JVM?", "Java Virtual Machine."),
                                new Flashcard("What is JDK?", "Java Development Kit."),
                                new Flashcard("What is JRE?", "Java Runtime Environment.")
                        ))
                        .quiz(null)
                        .userId("user123")
                        .build(),

                // 2. LearningAsset with Quiz
                LearningAssets.builder()
                        .id("2")
                        .title("OOP Concepts Quiz")
                        .originalFilename("oop_quiz.docx")
                        .originalFileUrl("https://example.com/files/oop_quiz.docx")
                        .type(ToolType.QUIZ)
                        .summary(null)
                        .transcription(null)
                        .flashcards(null)
                        .quiz(
                                new Quiz(
                                        "QUIZ101",
                                        Arrays.asList(
                                                new Mcq("What is encapsulation?", "Hiding data", "Code reuse", "Polymorphism", "None", 1),
                                                new Mcq("Which OOP principle is method overloading?", "Encapsulation", "Polymorphism", "Abstraction", "Inheritance", 2),
                                                new Mcq("Which keyword is used for inheritance in Java?", "extends", "implements", "inherits", "super", 1),
                                                new Mcq("What does polymorphism allow?", "Multiple forms of objects", "Encapsulation of data", "Abstract methods only", "Single inheritance", 1),
                                                new Mcq("What is the purpose of an abstract class?", "Cannot be instantiated", "Stores data only", "Encapsulates variables", "Overloads methods", 1),
                                                new Mcq("Which OOP principle hides complexity?", "Encapsulation", "Inheritance", "Polymorphism", "Abstraction", 4),
                                                new Mcq("Which principle allows a class to acquire properties of another class?", "Inheritance", "Polymorphism", "Encapsulation", "Abstraction", 1),
                                                new Mcq("What is method overriding?", "Subclass provides specific implementation", "Changing method signature", "Hiding a method", "Accessing parent methods", 1),
                                                new Mcq("Which keyword refers to the parent class?", "super", "this", "parent", "extends", 1),
                                                new Mcq("Which OOP principle allows multiple objects to respond differently to the same method call?", "Polymorphism", "Encapsulation", "Inheritance", "Abstraction", 1)
                                        ),
                                        "Medium",
                                        0,
                                        0

                                )
                        )
                        .userId("user456")
                        .build(),

                // 3. LearningAsset with Summary
                LearningAssets.builder()
                        .id("3")
                        .title("Data Structures Summary")
                        .originalFilename("ds_summary.txt")
                        .originalFileUrl("https://example.com/files/ds_summary.txt")
                        .type(ToolType.SUMMARY)
                        .summary("This document summarizes key data structures: arrays, linked lists, stacks, queues, trees, and graphs, with their pros and cons.")
                        .transcription(null)
                        .flashcards(null)
                        .quiz(null)
                        .userId("user789")
                        .build(),

                // 4. LearningAsset with Transcription
                LearningAssets.builder()
                        .id("4")
                        .title("Lecture Transcription - AI Ethics")
                        .originalFilename("ai_ethics_audio.mp3")
                        .originalFileUrl("https://example.com/files/ai_ethics_audio.mp3")
                        .type(ToolType.TRANSCRIBE)
                        .summary(null)
                        .transcription("In this lecture, we discussed AI ethics, fairness, transparency, and accountability in AI systems.")
                        .flashcards(null)
                        .quiz(null)
                        .userId("user321")
                        .build(),

                // 5. Another Flashcard LearningAsset
                LearningAssets.builder()
                        .id("5")
                        .title("Korean Vocabulary Flashcards")
                        .originalFilename("korean_vocab.pdf")
                        .originalFileUrl("https://example.com/files/korean_vocab.pdf")
                        .type(ToolType.FLASHCARD)
                        .summary(null)
                        .transcription(null)
                        .flashcards(Arrays.asList(
                                new Flashcard("사랑 (Sarang)", "Love"),
                                new Flashcard("학교 (Hakgyo)", "School"),
                                new Flashcard("책 (Chaek)", "Book")
                        ))
                        .quiz(null)
                        .userId("user555")
                        .build()
        );
    }
}
