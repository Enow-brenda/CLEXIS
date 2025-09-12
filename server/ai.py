import re
import requests
import json
import logging

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

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
    count = min(count, 5)  # Reduce for faster response
    if isinstance(difficulty, str):
        difficulty = difficulty_str_to_int(difficulty)

    # Truncate content to prevent long processing
    max_content = 800
    truncated_content = content[:max_content] + "..." if len(content) > max_content else content

    if task_type == "SUMMARY":
        prompt = f"""Summarize this content briefly:

{truncated_content}

Respond with JSON: {{"summary": "summary text"}}"""

    elif task_type == "FLASHCARD":
        prompt = f"""Create {count} flashcards. Be concise.

Content: {truncated_content}

Respond with JSON array: [{{"question": "Q?", "answer": "A."}}]"""

    elif task_type == "QUIZ":
        prompt = f"""Create {count} quiz questions. Be brief.

Content: {truncated_content}

Respond with JSON array: [{{"question": "Q?", "option1": "A", "option2": "B", "option3": "C", "option4": "D", "correctOption": 1}}]"""

    else:
        raise ValueError(f"Invalid task type: {task_type}")

    return prompt.strip()


def call_deepseek(prompt):
    """Call API with aggressive timeout for gateway compatibility"""
    try:
        # VERY short timeout - most important fix!
        response = requests.post(
            url="https://openrouter.ai/api/v1/chat/completions",
            headers={
                "Authorization": f"Bearer {apiKey}",
                "HTTP-Referer": "https://clexis.app",
                "X-Title": "CLEXIS",
                "Content-Type": "application/json"
            },
            json={
                "model": "deepseek/deepseek-r1-0528:free",
                "messages": [{"role": "user", "content": prompt}],
                "max_tokens": 300,  # Reduced output
                "temperature": 0.7
            },
            timeout=8  # VERY IMPORTANT: Short timeout for gateway
        )

        response.raise_for_status()
        resp_json = response.json()

        if "choices" not in resp_json or not resp_json["choices"]:
            raise Exception("No choices in response")

        return resp_json["choices"][0]["message"]["content"]

    except requests.exceptions.Timeout:
        raise Exception("AI service timeout - request took too long")
    except requests.exceptions.RequestException as e:
        raise Exception(f"Network error: {str(e)}")
    except (KeyError, IndexError) as e:
        raise Exception(f"Invalid response format: {str(e)}")


def clean_json_string(raw_text):
    cleaned = re.sub(r"```(?:json)?|```", "", raw_text)
    cleaned = cleaned.replace("“", '"').replace("”", '"')
    return cleaned.strip()


def parse_response(task_type, response_text):
    try:
        cleaned = clean_json_string(response_text)
        data = json.loads(cleaned)

        if task_type == "SUMMARY":
            return data.get("summary", "No summary generated")
        elif task_type in ["FLASHCARD", "QUIZ"]:
            return data if isinstance(data, list) else []
        else:
            raise ValueError(f"Unknown task type: {task_type}")

    except json.JSONDecodeError:
        return response_text  # Fallback to raw text


def getTheResource(task, content, count=None, difficulty=None):
    """Main function with timeout protection"""
    try:
        if not content or len(content.strip()) < 10:
            return {
                "code": 400,
                "message": "Content too short",
                "data": None
            }

        prompt = generate_prompt(task, content, count, difficulty)
        raw_response = call_deepseek(prompt)
        parsed = parse_response(task, raw_response)

        return {
            "code": 200,
            "message": "Success",
            "data": parsed
        }

    except Exception as e:
        return {
            "code": 500,
            "message": f"Error: {str(e)}",
            "data": None
        }

# === Example usage ===
if __name__ == "__main__":
    content = "Photosynthesis is the process by which green plants convert sunlight into chemical energy..."
    task = "quiz"  # or "summary" or "flashcards"
    result = getTheResource(task, content, count=5, difficulty="hard")

