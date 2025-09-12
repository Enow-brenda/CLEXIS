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
Please summarize the following content well Make it well detailed keeping all the key points and facts:

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
    try:
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
            }),
            timeout=30  # Add timeout to prevent hanging
        )

        # Check for HTTP errors
        response.raise_for_status()

        resp_json = response.json()

        # Check for API errors in response
        if "error" in resp_json:
            raise ValueError(f"API Error: {resp_json['error']}")

        # Check if choices exists and has content
        if "choices" not in resp_json or not resp_json["choices"]:
            raise ValueError("No choices in API response")

        content = resp_json["choices"][0]["message"]["content"]
        return content

    except requests.exceptions.RequestException as e:
        raise Exception(f"HTTP request failed: {str(e)}")
    except KeyError as e:
        raise Exception(f"Malformed API response: {str(e)}")


def parse_response(task_type, response_text):
    max_retries = 3
    cleaned_text = response_text

    for attempt in range(max_retries):
        try:
            # Try to clean the JSON string
            cleaned_text = clean_json_string(response_text)

            # Parse the JSON
            data = json.loads(cleaned_text)

            # Validate the response structure based on task type
            if task_type == "SUMMARY":
                if not isinstance(data, dict) or "summary" not in data:
                    raise ValueError("Invalid summary response format")
                return data.get("summary")

            elif task_type == "FLASHCARD":
                if not isinstance(data, list):
                    raise ValueError("Flashcards response should be an array")
                for card in data:
                    if "question" not in card or "answer" not in card:
                        raise ValueError("Invalid flashcard format")
                return data

            elif task_type == "QUIZ":
                if not isinstance(data, list):
                    raise ValueError("Quiz response should be an array")
                for question in data:
                    if "question" not in question or "correctOption" not in question:
                        raise ValueError("Invalid quiz question format")
                return data

            else:
                raise ValueError(f"Invalid task type: {task_type}")

        except (json.JSONDecodeError, ValueError) as e:
            if attempt == max_retries - 1:
                raise ValueError(f"Failed to parse JSON after {max_retries} attempts: {str(e)}")

            # Try to extract JSON from malformed response
            json_match = re.search(r'(\[{.*}\]|{.*})', response_text, re.DOTALL)
            if json_match:
                response_text = json_match.group(1)
                continue

            raise


def clean_json_string(raw_text):
    # Remove Markdown code fences if present
    raw_text = re.sub(r"^```(?:json)?\s*|```$", "", raw_text.strip(), flags=re.MULTILINE).strip()

    # Handle both smart quotes and regular quotes
    raw_text = raw_text.replace("“", '"').replace("”", '"').replace("'", '"')

    # Fix escaped characters - this approach is problematic
    # Let's use a more robust method
    try:
        # First try to parse directly
        return raw_text
    except:
        # If that fails, try more aggressive cleaning
        raw_text = re.sub(r'(?<!\\)\\(?!["\\/bfnrt]|u[0-9a-fA-F]{4})', r'\\\\', raw_text)
        return raw_text


def getTheResource(task, content, count=None, difficulty=None):
    try:
        # Validate task type
        valid_tasks = ["SUMMARY", "FLASHCARD", "QUIZ"]
        if task not in valid_tasks:
            raise ValueError(f"Task must be one of {valid_tasks}")

        # Generate prompt
        prompt = generate_prompt(task, content, count or 10, difficulty or 2)

        # Call API
        raw_response = call_deepseek(prompt)

        # Parse response
        parsed = parse_response(task, raw_response)

        return {
            "code": 200,
            "message": "Success",
            "data": parsed
        }

    except Exception as e:
        # Log the full error for debugging
        print(f"Error in getTheResource: {str(e)}")

        return {
            "code": 500,
            "message": f"Error occurred while processing: {str(e)}",
            "error": "processing_error",
            "data": None
        }

# === Example usage ===
if __name__ == "__main__":
    content = "Photosynthesis is the process by which green plants convert sunlight into chemical energy..."
    task = "quiz"  # or "summary" or "flashcards"
    result = getTheResource(task, content, count=5, difficulty="hard")

