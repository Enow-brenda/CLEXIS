from typing import Optional
from pydantic import BaseModel,  Field
from enum import Enum

class ToolType(Enum):
    FLASHCARD = "FLASHCARD"
    QUIZ = "QUIZ"
    SUMMARY = "SUMMARY"
    TRANSCRIBE = "TRANSCRIBE"

class RequestResourceModel(BaseModel):
    title: str
    originalFilename: str
    originalFileUrl: str
    type: ToolType
    count: Optional[int]
    difficulty: Optional[int] = Field(...,gt=0,le=6)

class FlashCard(BaseModel):
    question: str
    answer: str

class MCQ(BaseModel):
    question: str
    option1: str
    option2: str
    option3: str
    option4: str
    correctOption: int = Field(...,gt=0,lt=5)


class Quiz(BaseModel):
    questions: list[MCQ]
    difficulty: int = Field(...,gt=0,le=6)

def ResponseModel(data: object,code: int, message: str,error: str = None):
    return {
        "data": data,
        "code": code,
        "message": message,
        "error": error
    }