import re
import requests
import json

apiKey = "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"

def difficulty_str_to_int(diff_str):
    mapping = {
        "easy": 1,
        "normal": 2,
        "medium": 2,
        "hard": 3,
        "very hard": 4
    }
    return mapping.get(diff_str.lower(), 2)

def generate_prompt(task_type, content, count=10, difficulty=2):
    # Cap count to 20
    count = min(count, 20) if count else 10
    if isinstance(difficulty, str):
        difficulty = difficulty_str_to_int(difficulty)

    if task_type == "SUMMARY":
        prompt = f"""
Please summarize the following content in {count} concise and well detailed paragraphs:

{content}

Respond ONLY with a JSON object:

{{
  "summary": "your summary text here"
}}
"""
    elif task_type == "FLASHCARD":
        prompt = f"""
Generate {count} flashcards of difficulty {difficulty} from the following content.

Each flashcard should be an object with these keys:  
- "question": question or term  
- "answer": answer or explanation

Respond ONLY with a JSON array of flashcard objects like this:

[
  {{
    "question": "What is photosynthesis?",
    "answer": "Photosynthesis is the process by which plants convert sunlight into chemical energy."
  }},
  ...
]

Content:
{content}
"""
    elif task_type == "QUIZ":
        prompt = f"""
Create a quiz of {count} multiple-choice questions with difficulty {difficulty} based on the following content.

Each question should have:  
- "question": the question text  
- "option1": a possible answer  
- "option2": a possible answer  
- "option3": a possible answer  
- "option4": a possible answer  
- "correctOption": (1-4) corresponding to the correct option number  
- "difficulty": a number from 1 to 4 representing difficulty level

Respond ONLY with a JSON array like this:

[
  {{
    "question": "What is the main pigment in photosynthesis?",
    "option1": "Chlorophyll",
    "option2": "Carotenoid",
    "option3": "Xanthophyll",
    "option4": "Enzymes",
    "correctOption": 1,
  }},
  ...
]

Content:
{content}
"""
    else:
        raise ValueError(f"Invalid task type. Choose from summary, flashcards, or quiz. you gave {task_type}")

    return prompt.strip()

def call_deepseek(prompt):
    response = requests.post(
        url="https://openrouter.ai/api/v1/chat/completions",
        headers={
            "Authorization": f"Bearer {apiKey}",
            "HTTP-Referer": "no url",
            "X-Title": "CLEXIS",
            "Content-Type": "application/json"
        },
        data=json.dumps({
            "model": "deepseek/deepseek-r1-0528:free",
            "messages": [
                {"role": "user", "content": prompt}
            ]
        })
    )

    resp_json = response.json()
    # Extract the message content
    content = resp_json["choices"][0]["message"]["content"]
    return content

def parse_response(task_type, response_text):
    try:
        cleaned = clean_json_string(response_text)
        data = json.loads(cleaned)
    except json.JSONDecodeError:
        raise ValueError("Failed to parse JSON from response")

    if task_type == "SUMMARY":
        return data.get("summary")
    elif task_type == "FLASHCARD":
        return data  # list of flashcard objects
    elif task_type == "QUIZ":
        return data  # list of quiz question objects
    else:
        raise ValueError("Invalid task type.")

def clean_json_string(raw_text):
    # Remove Markdown fences if present
    raw_text = re.sub(r"^```(?:json)?|```$", "", raw_text.strip(), flags=re.MULTILINE).strip()

    # Ensure all double quotes inside strings are valid
    raw_text = raw_text.replace("“", '"').replace("”", '"')

    # Fix raw newlines inside strings
    raw_text = re.sub(r'(?<!\\)\n', '\\n', raw_text)

    return raw_text

def getTheResource(task, content, count=None, difficulty=None):
    prompt = generate_prompt(task, content, count or 10, difficulty or 2)
    raw_response = call_deepseek(prompt)
    parsed = parse_response(task, raw_response)
    return parsed

# === Example usage ===
if __name__ == "__main__":
    content = "Photosynthesis is the process by which green plants convert sunlight into chemical energy..."
    task = "quiz"  # or "summary" or "flashcards"
    result = getTheResource(task, content, count=5, difficulty="hard")

