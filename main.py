# creating an entry point to run the app so once we run the main.py ,
# it runs the web server for the python project

import uvicorn

if __name__ == "__main__":
    uvicorn.run("server.app:app", host="0.0.0.0", port=8000, reload=True)